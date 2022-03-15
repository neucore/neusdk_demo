package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.IPublishCallback;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.cmd.msg.NeulinkZone;
import com.neucore.neulink.cmd.msg.ResRegist;
import com.neucore.neulink.extend.NeulinkSecurity;
import com.neucore.neulink.extend.Result;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import cn.hutool.core.util.ObjectUtil;

public class NeulinkService implements NeulinkConst{

    private static NeulinkService instance = new NeulinkService();
    private String TAG = TAG_PREFIX+"Service";

    private MyMqttService myMqttService = null;
    private List<IMqttCallBack> mqttCallBacks = new ArrayList<>();
    private Boolean neulinkServiceInited = false;
    private Boolean mqttInited = false;
    private Boolean mqttConnSuccessed = false;
    private Register register = null;
    private Boolean destroy = false;

    private NeulinkPublisherFacde publisherFacde;
    private NeulinkSubscriberFacde subscriberFacde;
    private String mqttServiceUri, httpServiceUri;
    private UdpReceiveAndtcpSend udpReceiveAndtcpSend;
    public static NeulinkService getInstance(){
        return instance;
    }
    /**
     * 构建EasyMqttService对象
     */
    public void init() {
        Context context = ContextHolder.getInstance().getContext();
        NeulinkMsgCallBack defaultMqttCallBack = new NeulinkMsgCallBack(context,this);
        mqttCallBacks.add(defaultMqttCallBack);
        publisherFacde = new NeulinkPublisherFacde(context,this);
        subscriberFacde = new NeulinkSubscriberFacde(context,this);
        register = new Register(context,this);
        udpReceiveAndtcpSend = new UdpReceiveAndtcpSend();
        udpReceiveAndtcpSend.start();
    }

    private void initMqtt(String serverUri, Context context){
        Log.i(TAG,String.format("inited %s", mqttInited));
        synchronized (mqttInited){
            if(!mqttInited){
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
                mqttInited = true;
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

        publishMessage(topicPrefix,version, payload, qos,null);
    }

    protected void publishMessage(String topicPrefix, String version, String payload, int qos, IPublishCallback callback){

        publishMessage(topicPrefix,version, UUID.randomUUID().toString(),payload, qos,callback);
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
        publishMessage(topicPrefix,version,reqId,payload,qos,false,null);
    }

    protected void publishMessage(String topicPrefix, String version, String reqId, String payload, int qos,IPublishCallback callback){
        publishMessage(topicPrefix,version,reqId,payload,qos,false,callback);
    }

    protected void publishMessage(String topicPrefix, String version, String reqId, String payload, int qos,boolean retained){
        publishMessage(topicPrefix,version,reqId,payload,qos,retained,null);
    }

    protected void publishMessage(String topicPrefix, String version, String reqId, String payload, int qos,boolean retained,IPublishCallback callback){

        int channel = ConfigContext.getInstance().getConfig(ConfigContext.UPLOAD_CHANNEL,0);

        String md5 = MD5Utils.getInstance().getMD5String(payload);

        String topStr = topicPrefix+"/"+version+"/"+ reqId+"/"+md5;

        topStr = topStr+"/"+getCustId()+"/"+getStoreId()+"/"+getZoneId()+"/"+ServiceFactory.getInstance().getDeviceService().getExtSN();

        Log.d(TAG,"upload2cloud with "+(channel==0?"mqtt topic: ":"http topic: ")+topStr);

        Context context = ContextHolder.getInstance().getContext();

        if (channel==0){//向下兼容

            if(mqttServiceUri ==null){
                mqttServiceUri = ConfigContext.getInstance().getConfig(ConfigContext.MQTT_SERVER);
            }

            connect(mqttServiceUri,context);
            /**
             * MQTT机制
             */
            myMqttService.publish(reqId,payload,topStr, qos, retained,context,callback);

            neulinkServiceInited = true;
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

                /**
                 * HTTP机制
                 */
                String registServer = ConfigContext.getInstance().getConfig(ConfigContext.REGIST_SERVER,"https://dev.neucore.com/api/neulink/upload2cloud");
                Log.d(TAG,"registServer："+registServer);

                String response = null;
                try {
                    String topic = URLEncoder.encode(topStr,"UTF-8");
                    response = NeuHttpHelper.post(registServer+"?topic="+topic,payload,params,10,60,1);

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
                    ResRegist resRegist = JSonUtils.toObject(response, ResRegist.class);
                    NeulinkZone zone = resRegist.getZone();
                    custid = zone.getCustid();
                    storeid = zone.getStoreid();
                    zoneid = zone.getId();

                    httpServiceUri = zone.getUploadServer();
                    String mqttHost = zone.getMqttServer();
                    Integer port = zone.getMqttPort();

                    //tcp://dev.neucore.com:1883
                    mqttServiceUri = String.format("tcp://%s:%s",mqttHost,port);

                    connect(mqttServiceUri,context);

                    neulinkServiceInited = true;

                } catch (Exception e) {
                    Log.e(TAG,e.getMessage(),e);
                }
            }
            else {
                /**
                 * 设备端2cloud
                 */
                httpServiceUri = ConfigContext.getInstance().getConfig(ConfigContext.REGIST_SERVER, httpServiceUri);
                Boolean done = false;
                int count = 0;
                while(!done && count<3){
                    try {
                        Log.d(TAG,topStr);
                        String topic = URLEncoder.encode(topStr,"UTF-8");
                        String response = NeuHttpHelper.post(httpServiceUri +"?topic="+topic,payload,params,10,60,1);
                        Log.d(TAG,"设备upload2cloud响应："+response);
                        if(ObjectUtil.isNotEmpty(callback)){
                            Class cls = callback.getResultType();
                            Result result = (Result) JSonUtils.toObject(response,cls);
                            result.setReqId(reqId);
                            callback.onFinished(result);
                        }
                        done = true;
                    }
                    catch (NeulinkException e) {
                        if(e.getCode()==401||e.getCode()==403){
                            Log.i(TAG,"token过期，重新登录");
                            token = ServiceFactory.getInstance().getLoginCallback().login();
                            if(ObjectUtil.isNotEmpty(token)){
                                Log.i(TAG,"token过期，重新登录成功");
                                NeulinkSecurity.getInstance().setToken(token);
                            }
                            count++;
                        }
                        else {
                            done = true;
                        }
                    }
                    catch (Exception e) {
                        Log.d(TAG,"upload2cloud error with: "+e.getMessage());
                        done = true;
                    }
                }
            }
        }
    }

    private void connect(String mqttServiceUri,Context context){
        if(!mqttInited){
            initMqtt(mqttServiceUri,context);
            int cnt = 0;
            while (!getMqttConnSuccessed()){
                try {
                    connect();
                    cnt++;
                    Thread.sleep(1000);
                    Log.i(TAG,"try "+cnt+"次连接。。。。");
                }
                catch (Exception ex){}
            }
        }
    }

    protected void publishConnect(Integer flg){
        String manualReport = ConfigContext.getInstance().getConfig(ConfigContext.STATUS_MANUAL_REPORT,"true");
        if("true".equalsIgnoreCase(manualReport)){
            String payload = "{\"dev_id\":\""+ServiceFactory.getInstance().getDeviceService().getExtSN()+"\",\"status\":1}";
            publishMessage("msg/req/connect","v1.0",UUID.randomUUID().toString(),payload,1,true);
        }
    }

    protected void publishDisConnect(Integer flg){

        String manualReport = ConfigContext.getInstance().getConfig(ConfigContext.STATUS_MANUAL_REPORT,"true");
        if("true".equalsIgnoreCase(manualReport)){
            String payload = "{\"dev_id\":\""+ServiceFactory.getInstance().getDeviceService().getExtSN()+"\",\"status\":0}";
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

    public List<IMqttCallBack> getMqttCallBacks() {
        return mqttCallBacks;
    }

    // MQTT监听并且接受消息
    private MqttCallback mqttCallback = new MqttCallbackExtended() {

        private String TAG = TAG_PREFIX+"MqttCallback";

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            try {
                mqttConnSuccessed = true;
                Log.i(TAG, "connectComplete ");
                reentrantLock.lock();
                subscriberFacde.subAll();
                publishConnect(1);
                Log.d(TAG, "Server:" + mqttServiceUri + " ,connectComplete reconnect:" + reconnect);
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

    public Boolean isNeulinkServiceInited() {
        return neulinkServiceInited;
    }
}
