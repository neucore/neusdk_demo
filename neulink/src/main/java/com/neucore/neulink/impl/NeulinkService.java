package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.cmd.msg.NeulinkZone;
import com.neucore.neulink.cmd.msg.ResRegist;
import com.neucore.neulink.impl.service.broadcast.UdpReceiveAndtcpSend;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.MD5Utils;
import com.neucore.neulink.util.NeuHttpHelper;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttReceivedMessage;

import java.io.EOFException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import cn.hutool.core.util.ObjectUtil;

public class NeulinkService {

    private static NeulinkService instance = new NeulinkService();
    private String TAG = "DeviceNeulinkService";

    private MqttService mqttService = null;
    private IMqttCallBack starMQTTCallBack;
    private Boolean inited = false;
    private Boolean connected = false;
    private Register register = null;
    private Boolean destroy = false;

    private NeulinkPublisherFacde publisherFacde;
    private NeulinkSubscriberFacde subscriberFacde;
    private String defaultServerUri,newServiceUri,registServer,neulinkServer;
    private UdpReceiveAndtcpSend udpReceiveAndtcpSend;
    public static NeulinkService getInstance(){
        return instance;
    }
    /**
     * 构建EasyMqttService对象
     */
    public void buildMqttService(String serverUri) {
        this.defaultServerUri = serverUri;
        Context context = ContextHolder.getInstance().getContext();
        udpReceiveAndtcpSend = new UdpReceiveAndtcpSend();
        register = new Register(context,this,serverUri);
        publisherFacde = new NeulinkPublisherFacde(context,this);
        subscriberFacde = new NeulinkSubscriberFacde(context,this);
    }

    void init(String serverUri,Context context){

        synchronized (inited){
            if(!inited){
                udpReceiveAndtcpSend.start();
                mqttService = new MqttService.Builder()
                        //设置自动重连
                        .autoReconnect(true)
                        //设置不清除回话session 可收到服务器之前发出的推送消息
                        .cleanSession(false)
                        //唯一标示 保证每个设备都唯一就可以 建议 imei
                        .clientId(DeviceUtils.getDeviceId(context))
                        //mqtt服务器地址 格式例如：tcp://10.0.261.159:1883
                        .serverUrl(serverUri)
                        //心跳包默认的发送间隔
                        .keepAliveInterval(20)
                        //设置发布和订阅回调接口
                        .mqttCallback(mqttCallback)
                        //设置连接或者发布动作侦听器
                        //.mqttActionListener(mqttActionListener)
                        //设置消息侦听器
                        //.mqttMessageListener(messageListener)
                        //构建出EasyMqttService 建议用application的context
                        .bulid(context);
                starMQTTCallBack = new NeulinkMsgCallBack(context,this);

                //this.connect();
                mqttService.connect();
                inited = true;
                new HouseKeeping().start();
            }
        }
    }

    public NeulinkPublisherFacde getPublisherFacde(){
        return publisherFacde;
    }

    /**
     * 连接Mqtt服务器
     */
    void connect() {
        //mqttService.connect();
    }

    IMqttCallBack getStarMQTTCallBack(){
        return starMQTTCallBack;
    }


    /**
     *
     * @param topic
     * @param qos
     * @param mqttMessageListener
     */
    protected void subscribeToTopic(final String topic, int qos,IMqttMessageListener mqttMessageListener){
        mqttService.subscribe(topic, qos,mqttMessageListener);
    }

    /**
     *
     * @param topicPrefix
     * @param version
     * @param payload
     * @param qos
     */
    protected void publishMessage(String topicPrefix,String version, String payload, int qos){

        publishMessage(topicPrefix,version, UUID.randomUUID().toString(),payload, qos);
    }

    /**
     *
     * @param topicPrefix
     * @param version
     * @param reqId
     * @param payload
     * @param qos
     */
    protected void publishMessage(String topicPrefix, String version, String reqId, String payload, int qos){
        String md5 = MD5Utils.getInstance().getMD5String(payload);

//        int deviceIdBKDRHash = HashAlgorithms.SDBMHash(DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext()));
//        int partition = deviceIdBKDRHash %ConfigContext.getInstance().getConfig("Topic.Partition",8);

        String topStr = topicPrefix+"/"+version+"/"+ reqId+"/"+md5;

        int channel = ConfigContext.getInstance().getConfig(ConfigContext.UPLOAD_CHANNEL,0);

        Context context = ContextHolder.getInstance().getContext();

        if (channel==0){//向下兼容

            Log.d(TAG,"upload2cloud with mqtt");

            if(newServiceUri==null){
                newServiceUri = defaultServerUri;
            }
            init(newServiceUri,context);
            /**
             * MQTT机制
             */
            topStr = topStr+"/"+getCustId()+"/"+getStoreId()+"/"+getZoneId()+"/"+DeviceUtils.getDeviceId(context);
            Log.d(TAG,topStr);
            mqttService.publish(payload,topStr, qos, false);
        }
        else{


            /**
             * 设备注册：
             *
             */
            if(topStr.contains("msg/req/devinfo")){

                /**
                 * HTTP机制
                 */
                String registServer = ConfigContext.getInstance().getConfig(ConfigContext.REGIST_SERVER,"https://data.neuapi.com/v1/device/regist");
                Log.d(TAG,"registServer："+registServer);

                topStr = topStr+"/"+getCustId()+"/"+getStoreId()+"/"+getZoneId()+"/"+DeviceUtils.getDeviceId(context);
                Log.d(TAG,topStr);
                String response = null;
                try {
                    String topic = URLEncoder.encode(topStr,"UTF-8");
                    response = NeuHttpHelper.post(registServer+"?topic="+topic,payload,10,60,3);
                    Log.d(TAG,"设备注册响应："+response);
                } catch (UnsupportedEncodingException e) {
                }
                Log.d(TAG,"设备注册响应："+response);

                /**
                 * {
                 * 	"code": 200,
                 * 	"msg": "注册成功",
                 * 	"zone": {
                 * 		"zoneid": 3,
                 * 		"custcode": "saic",
                 * 		"placecode": "test",
                 * 		"server": "mqtt.neucore.com",
                 * 		"port": 1883,
                 * 	    "http.server":"https://data.neuapi.com/v1/smrtlibs/neulink/upload2cloud"
                 *   }
                 * }
                 */
                ResRegist resRegist = (ResRegist)JSonUtils.toObject(response, ResRegist.class);
                NeulinkZone zone = resRegist.getZone();
                custid = zone.getCustid();
                storeid = zone.getStoreid();
                zoneid = zone.getId();
                newServiceUri = "tcp://"+zone.getMqttServer()+":"+zone.getMqttPort();
                init(newServiceUri,context);
                /**
                 * upload.server 默认值
                 * https://data.neuapi.com/neulink/upload2cloud
                 */
                neulinkServer = zone.getUploadServer();
            }
            else {
                /**
                 * 设备端2cloud
                 */
                Log.d(TAG,"upload2cloud with http");
                try {
                    topStr = topStr+"/"+getCustId()+"/"+getStoreId()+"/"+getZoneId()+"/"+DeviceUtils.getDeviceId(context);
                    Log.d(TAG,topStr);
                    String topic = URLEncoder.encode(topStr,"UTF-8");
                    String response = NeuHttpHelper.post(neulinkServer+"?topic="+topic,payload,10,60,3);
                    Log.d(TAG,"设备upload2cloud响应："+response);
                } catch (Exception e) {
                    Log.d(TAG,"upload2cloud error with: "+e.getMessage());
                }
            }
        }
    }

    public void publishConnect(Integer flg){
        Context context = ContextHolder.getInstance().getContext();
        String sccperId = ConfigContext.getInstance().getConfig("ScopeId","yeker");
        mqttService.publish(String.valueOf(flg),"$EDC/"+sccperId+"/"+DeviceUtils.getDeviceId(context)+"/MQTT/CONNECT", 1, true);
//        mqttService.publish("1","$share/will_test/"+sccperId+"/"+DeviceUtils.getDeviceId(context)+"/MQTT/CONNECT", 1, true);
    }

    public void publishDisConnect(Integer flg){
        Context context = ContextHolder.getInstance().getContext();
        String sccperId = ConfigContext.getInstance().getConfig("ScopeId","yeker");
        mqttService.publish(String.valueOf(flg),"$EDC/"+sccperId+"/"+DeviceUtils.getDeviceId(context)+"/MQTT/DISCONNECT", 1, true);
//        mqttService.publish("1","$share/will_test/"+sccperId+"/"+DeviceUtils.getDeviceId(context)+"/MQTT/CONNECT", 1, true);
    }

    public void destroy(){
        if(!destroy && !ObjectUtil.isEmpty(mqttService)){
            mqttService.disconnect();
            destroy = true;
            Log.i(TAG,"断开Mqtt Service");
        }
    }

    public Boolean getDestroy() {
        return destroy;
    }

    private String custid="notimpl";
    private String getCustId(){
        return custid;
    }
    private String storeid="notimpl";
    private String getStoreId(){
        return storeid;
    }
    private String zoneid="0";
    private String getZoneId(){
        return zoneid;
    }
    boolean isConnected(){
        return true;//mqttService.isConnected();
    }

    private ReentrantLock reentrantLock = new ReentrantLock();
    /**
     * MQTT是否连接成功
     */
    private IMqttActionListener mqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {

            if (starMQTTCallBack != null) {
                starMQTTCallBack.connectSuccess(arg0);
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            Log.i(TAG, "onFailure ");
            if (starMQTTCallBack != null) {
                starMQTTCallBack.connectFailed(arg0, arg1);
            }
        }
    };

    // MQTT监听并且接受消息
    private MqttCallback mqttCallback = new MqttCallbackExtended() {

        private String TAG = "MqttCallback";

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            try {
                reentrantLock.lock();
                if(reconnect){
                    subscriberFacde.subAll();
                    publishConnect(1);
                    Log.d(TAG, "Server:" + defaultServerUri + " ,connectComplete reconnect:" + reconnect);
                }
            }
            finally {
                reentrantLock.unlock();
            }
            if (starMQTTCallBack != null) {
                starMQTTCallBack.connectComplete(reconnect, serverURI);
            }
        }


        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {

            MqttReceivedMessage receivedMessage = (MqttReceivedMessage)message;

            int messageId = receivedMessage.getMessageId();
            String detailLog = topic + ";qos:" + receivedMessage.getQos() + ";retained:" + receivedMessage.isRetained() + "messageId:"+messageId;
            String msgContent = new String(receivedMessage.getPayload());
            Log.i(TAG, "messageArrived:" + msgContent);
            Log.i(TAG, detailLog);

            if (starMQTTCallBack != null) {
                starMQTTCallBack.messageArrived(topic, msgContent, receivedMessage.getQos());
            }

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            Log.i(TAG, "deliveryComplete");
            if (starMQTTCallBack != null) {
                starMQTTCallBack.deliveryComplete(arg0);
            }
        }

        @Override
        public void connectionLost(Throwable arg0) {

            Log.i(TAG, "connectionLost");
            connected = false;
            publishDisConnect(1);
            if (starMQTTCallBack != null) {
                starMQTTCallBack.connectionLost(arg0);
            }
            // 失去连接，重连
        }
    };

    class HouseKeeping extends Thread{
        public void run(){
            while (!destroy){
                String tempDir = DeviceUtils.getTmpPath(ContextHolder.getInstance().getContext());
                File[] files = new File(tempDir).listFiles();
                /**
                 * 三小时之前的数据
                 */
                long time = System.currentTimeMillis()-3*60*60*1000;
                if(files!=null){
                    for (File file:files) {
                        if(file.lastModified()>=time){
                            file.delete();
                            Log.i(TAG,file.getAbsolutePath()+"清除掉");
                        }
                    }
                }
                try {
                    Thread.currentThread().sleep(60*60*1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

}
