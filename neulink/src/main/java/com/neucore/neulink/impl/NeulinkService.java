package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.cfg.ConfigContext;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.HashAlgorithms;
import com.neucore.neulink.util.MD5Utils;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttReceivedMessage;

import java.util.UUID;

public class NeulinkService {

    private static NeulinkService instance = new NeulinkService();
    private String TAG = "DeviceNeulinkService";

    private MqttService mqttService = null;
    private IMqttCallBack starMQTTCallBack;
    private boolean inited = false;
    private NeulinkScheduledReport autoReporter = null;
    private NeulinkPublisherFacde publisherFacde;
    private NeulinkSubscriberFacde subscriberFacde;

    public static NeulinkService getInstance(){
        return instance;
    }
    /**
     * 构建EasyMqttService对象
     */
    public void buildMqttService(String serverUri) {
        if(!inited){
            Context context = ContextHolder.getInstance().getContext();
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

            autoReporter = new NeulinkScheduledReport(context,this);
            publisherFacde = new NeulinkPublisherFacde(context,this);
            subscriberFacde = new NeulinkSubscriberFacde(context,this);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    UserService.getInstance(ContextHolder.getInstance().getContext()).load();
//                }
//            }).start();
            inited = true;
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

        int deviceIdBKDRHash = HashAlgorithms.SDBMHash(DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext()));

        int partition = deviceIdBKDRHash %ConfigContext.getInstance().getConfig("Topic.Partition",8);
        String custcode = getCustCode();
        String topStr = topicPrefix+"/"+version+"/"+ reqId+"/"+md5+"/"+custcode+"/"+partition;

        Log.d(TAG,topStr);
        
        mqttService.publish(payload,topStr, qos, false);
    }

    private String getCustCode(){
        //@TODO 这个需要借助算法团队提供api接口
        return "0@Default";
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
            //连接成功
            //建议在这里执行订阅逻辑
            //如果cleanSession设置为false的话，不用每次启动app都订阅，第一次订阅后 后面只执行连接操作即可
            autoReporter.start();
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
