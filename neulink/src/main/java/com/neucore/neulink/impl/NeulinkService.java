package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.cfg.ConfigContext;
import com.neucore.neulink.msg.NeulinkZone;
import com.neucore.neulink.msg.ResRegist;
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

import java.net.URLEncoder;
import java.util.UUID;

public class NeulinkService {

    private static NeulinkService instance = new NeulinkService();
    private String TAG = "DeviceNeulinkService";

    private MqttService mqttService = null;
    private IMqttCallBack starMQTTCallBack;
    private Boolean inited = false;
    private Register register = null;

    private NeulinkPublisherFacde publisherFacde;
    private NeulinkSubscriberFacde subscriberFacde;
    private String defaultServerUri,newServiceUri,registServer,neulinkServer;
    public static NeulinkService getInstance(){
        return instance;
    }
    /**
     * 构建EasyMqttService对象
     */
    public void buildMqttService(String serverUri) {
        this.defaultServerUri = serverUri;
        Context context = ContextHolder.getInstance().getContext();
        register = new Register(context,this,serverUri);
        publisherFacde = new NeulinkPublisherFacde(context,this);
        subscriberFacde = new NeulinkSubscriberFacde(context,this);
    }

    void init(String serverUri,Context context){
        synchronized (inited){
            if(!inited){
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
                        .mqttActionListener(mqttActionListener)
                        //设置消息侦听器
                        //.mqttMessageListener(messageListener)
                        //构建出EasyMqttService 建议用application的context
                        .bulid(context);
                starMQTTCallBack = new NeulinkMsgCallBack(context,this);

                //this.connect();
                mqttService.connect();
                inited = true;
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
                String response = NeuHttpHelper.post(registServer,payload,10,60,3);
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

    /**
     * MQTT是否连接成功
     */
    private IMqttActionListener mqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "onSuccess");

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

            subscriberFacde.subAll();

            if(reconnect){
                Log.d(TAG,"Server:"+ serverURI+",reconnect:"+reconnect);
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
            if (starMQTTCallBack != null) {
                starMQTTCallBack.connectionLost(arg0);
            }

            // 失去连接，重连
        }
    };

}
