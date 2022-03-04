package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.cmd.msg.NeulinkZone;
import com.neucore.neulink.cmd.msg.ResRegist;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.extend.NeulinkSecurity;
import com.neucore.neulink.extend.ServiceFactory;
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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import cn.hutool.core.util.ObjectUtil;

public class NeulinkService {

    private static NeulinkService instance = new NeulinkService();
    private String TAG = NeulinkConst.TAG_PREFIX+"Service";

    private MyMqttService myMqttService = null;
    private List<IMqttCallBack> mqttCallBacks = new ArrayList<>();
    private ILoginCallback loginCallback;
    private Boolean inited = false;
    private Boolean mqttConnSuccessed = false;
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
        NeulinkMsgCallBack defaultMqttCallBack = new NeulinkMsgCallBack(context,this);
        mqttCallBacks.add(defaultMqttCallBack);
        publisherFacde = new NeulinkPublisherFacde(context,this);
        subscriberFacde = new NeulinkSubscriberFacde(context,this);
        register = new Register(context,this,serverUri);
        udpReceiveAndtcpSend = new UdpReceiveAndtcpSend();
        udpReceiveAndtcpSend.start();
    }

    void init(String serverUri,Context context){
        Log.i(TAG,String.format("inited %s",inited));
        synchronized (inited){
            if(!inited){
                myMqttService = new MyMqttService.Builder()
                        //设置自动重连
                        .autoReconnect(true)
                        //设置不清除回话session 可收到服务器之前发出的推送消息
                        .cleanSession(false)
                        //唯一标示 保证每个设备都唯一就可以 建议 imei
                        .clientId(ServiceFactory.getInstance().getDeviceService().getExtSN())
                        //mqtt服务器地址 格式例如：tcp://10.0.261.159:1883
                        .serverUrl(serverUri)
                        //心跳包默认的发送间隔
                        .keepAliveInterval(20)
                        //设置发布和订阅回调接口
                        .mqttCallback(mqttCallback)
                        //设置连接或者发布动作侦听器
                        .mqttActionListener(mqttActionListener)
                        //设置消息侦听器
                        //.mqttMessageListener(messageListener)
                        //构建出EasyMqttService 建议用application的context
                        .bulid(context);
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
        if(!mqttConnSuccessed){
            myMqttService.connect();
        }
    }

    public void addMQTTCallBack(IMqttCallBack mqttCallBack){
        if(mqttCallBack!=null){
            this.mqttCallBacks.add(mqttCallBack);
        }
    }

    public void setLoginCallback(ILoginCallback loginCallback) {
        this.loginCallback = loginCallback;
    }

    /**
     *
     * @param topic
     * @param qos
     * @param mqttMessageListener
     */
    protected void subscribeToTopic(final String topic, int qos,IMqttMessageListener mqttMessageListener){
        myMqttService.subscribe(topic, qos,mqttMessageListener);
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
        publishMessage(topicPrefix,version,reqId,payload,qos,false);
    }

    protected void publishMessage(String topicPrefix, String version, String reqId, String payload, int qos,boolean retained){
        String md5 = MD5Utils.getInstance().getMD5String(payload);

        String topStr = topicPrefix+"/"+version+"/"+ reqId+"/"+md5;

        int channel = ConfigContext.getInstance().getConfig(ConfigContext.UPLOAD_CHANNEL,0);

        Context context = ContextHolder.getInstance().getContext();

        if (channel==0){//向下兼容

            Log.d(TAG,"upload2cloud with mqtt");

            if(newServiceUri==null){
                newServiceUri = defaultServerUri;
            }
            init(newServiceUri,context);

            if(topStr.contains("msg/req/devinfo")){
                Log.d(TAG,"start mqtt regist");
            }
            /**
             * MQTT机制
             */
            topStr = topStr+"/"+getCustId()+"/"+getStoreId()+"/"+getZoneId()+"/"+ServiceFactory.getInstance().getDeviceService().getExtSN();
            myMqttService.publish(payload,topStr, qos, retained);
        }
        else{

            String token = NeulinkSecurity.getInstance().getToken();
            Map<String,String> params = new HashMap<>();
            if(token!=null){
                int index = token.indexOf(" ");
                if(index!=-1){
                    token = token.substring(index+1);
                }
                params.put("Authorization","Bearer "+token);
            }

            /**
             * 设备注册：
             *
             */
            if(topStr.contains("msg/req/devinfo")){

                Log.d(TAG,"start http regist");

                /**
                 * HTTP机制
                 */
                String registServer = ConfigContext.getInstance().getConfig(ConfigContext.REGIST_SERVER,"https://dev.neucore.com/api/neulink/upload2cloud");
                Log.d(TAG,"registServer："+registServer);

                topStr = topStr+"/"+getCustId()+"/"+getStoreId()+"/"+getZoneId()+"/"+ServiceFactory.getInstance().getDeviceService().getExtSN();
                Log.d(TAG,topStr);
                String response = null;
                try {
                    String topic = URLEncoder.encode(topStr,"UTF-8");
                    response = NeuHttpHelper.post(registServer+"?topic="+topic,payload,params,10,60,3);
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
                 * 	    "http.server":"https://dev.neucore.com/api/neulink/upload2cloud"
                 *   }
                 * }
                 */
                ResRegist resRegist = (ResRegist)JSonUtils.toObject(response, ResRegist.class);
                NeulinkZone zone = resRegist.getZone();
                custid = zone.getCustid();
                storeid = zone.getStoreid();
                zoneid = zone.getId();

                /**
                 * upload.server 默认值
                 * https://dev.neucore.com/api/neulink/upload2cloud
                 */
                neulinkServer = zone.getUploadServer();
            }
            else {
                /**
                 * 设备端2cloud
                 */
                Log.d(TAG,"upload2cloud with http");
                try {
                    topStr = topStr+"/"+getCustId()+"/"+getStoreId()+"/"+getZoneId()+"/"+ServiceFactory.getInstance().getDeviceService().getExtSN();
                    Log.d(TAG,topStr);
                    String topic = URLEncoder.encode(topStr,"UTF-8");

                    String response = NeuHttpHelper.post(neulinkServer+"?topic="+topic,payload,params,10,60,3);
                    Log.d(TAG,"设备upload2cloud响应："+response);
                } catch (Exception e) {
                    Log.d(TAG,"upload2cloud error with: "+e.getMessage());
                }
            }
        }
    }

    public void publishConnect(Integer flg){
        String manualReport = ConfigContext.getInstance().getConfig(ConfigContext.STATUS_MANUAL_REPORT,"true");
        if("true".equalsIgnoreCase(manualReport)){
            String payload = "{\"dev_id\",:\""+ServiceFactory.getInstance().getDeviceService().getExtSN()+"\",\"status\":1}";
            publishMessage("msg/req/connect","v1.0",UUID.randomUUID().toString(),payload,1,true);
        }
    }

    public void publishDisConnect(Integer flg){

        String manualReport = ConfigContext.getInstance().getConfig(ConfigContext.STATUS_MANUAL_REPORT,"true");
        if("true".equalsIgnoreCase(manualReport)){
            String payload = "{\"dev_id\",:\""+ServiceFactory.getInstance().getDeviceService().getExtSN()+"\",\"status\":0}";
            publishMessage("msg/req/disconnect","v1.0",UUID.randomUUID().toString(),payload,1,true);
        }
    }

    public void destroy(){
        if(!destroy && !ObjectUtil.isEmpty(myMqttService)){
            myMqttService.disconnect();
            destroy = true;
            Log.i(TAG,"断开Mqtt Service");
        }
    }

    public Boolean getDestroy() {
        return destroy;
    }

    private String custid="notimpl";
    private String getCustId(){
        String sccperId = ConfigContext.getInstance().getConfig("ScopeId","yeker");
        if(ObjectUtil.isNotEmpty(sccperId)){
            return sccperId;
        }
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

    boolean getMqttConnSuccessed(){
        return mqttConnSuccessed;
    }

    private ReentrantLock reentrantLock = new ReentrantLock();
    /**
     * MQTT是否连接成功
     */
    private IMqttActionListener mqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "onSuccess ");
            mqttConnSuccessed = true;
            if (mqttCallBacks != null) {
                for (IMqttCallBack callback: mqttCallBacks) {
                    callback.connectSuccess(arg0);
                }
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            if (mqttCallBacks != null) {
                for (IMqttCallBack callback: mqttCallBacks) {
                    callback.connectFailed(arg0, arg1);
                }
            }
        }
    };

    // MQTT监听并且接受消息
    private MqttCallback mqttCallback = new MqttCallbackExtended() {

        private String TAG = NeulinkConst.TAG_PREFIX+"MqttCallback";

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            try {
                mqttConnSuccessed = true;
                Log.i(TAG, "connectComplete ");
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

            if (mqttCallBacks != null) {
                for (IMqttCallBack callback: mqttCallBacks) {
                    callback.connectComplete(reconnect, serverURI);
                }
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

            if (mqttCallBacks != null) {
                for (IMqttCallBack callback: mqttCallBacks) {
                    callback.messageArrived(topic, msgContent, receivedMessage.getQos());
                }
            }

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            Log.i(TAG, "deliveryComplete");

            if (mqttCallBacks != null) {
                for (IMqttCallBack callback: mqttCallBacks) {
                    callback.deliveryComplete(arg0);
                }
            }
        }

        @Override
        public void connectionLost(Throwable arg0) {

            Log.i(TAG, "connectionLost");
            mqttConnSuccessed = false;
            publishDisConnect(1);
            if (mqttCallBacks != null) {
                for (IMqttCallBack callback: mqttCallBacks) {
                    callback.connectionLost(arg0);
                }
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
