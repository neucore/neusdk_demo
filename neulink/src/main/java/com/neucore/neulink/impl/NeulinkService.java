package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.IResCallback;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.cmd.msg.NeulinkZone;
import com.neucore.neulink.cmd.msg.ResRegist;
import com.neucore.neulink.extend.NeulinkSecurity;
import com.neucore.neulink.extend.Result;
import com.neucore.neulink.extend.ServiceRegistrator;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
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

    private void initMqtt(String serverUri,String userName,String password, Context context){
        Log.i(TAG,String.format("inited %s", mqttInited));
        synchronized (mqttInited){
            if(!mqttInited){
                myMqttService = new MyMqttService.Builder()
                        //设置自动重连
                        .autoReconnect(true)
                        //设置不清除回话session 可收到服务器之前发出的推送消息
                        .cleanSession(false)
                        //唯一标示 保证每个设备都唯一就可以 建议 imei
                        .clientId(ServiceRegistrator.getInstance().getDeviceService().getExtSN())
                        //mqtt服务器地址 格式例如：tcp://10.0.261.159:1883
                        .serverUrl(serverUri)
                        .userName(userName)
                        .passWord(password)
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

    protected void publishMessage(String topicPrefix, String version, String payload, int qos, IResCallback callback){

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

    protected void publishMessage(String topicPrefix, String version, String reqId, String payload, int qos, IResCallback callback){
        publishMessage(topicPrefix,version,reqId,payload,qos,false,callback);
    }

    protected void publishMessage(String topicPrefix, String version, String reqId, String payload, int qos,boolean retained){
        publishMessage(topicPrefix,version,reqId,payload,qos,retained,null);
    }

    protected void publishMessage(final String topicPrefix, String version, final String reqId, final String payload, final int qos, final boolean retained, final IResCallback callback){

        int channel = ConfigContext.getInstance().getConfig(ConfigContext.UPLOAD_CHANNEL,0);

        String md5 = MD5Utils.getInstance().getMD5String(payload);

        String topStr = topicPrefix+"/"+version+"/"+ reqId+"/"+md5;

        topStr = topStr+"/"+getCustId()+"/"+getStoreId()+"/"+getZoneId()+"/"+ ServiceRegistrator.getInstance().getDeviceService().getExtSN();

        final String topic = topStr;

        Log.d(TAG,"upload2cloud with "+(channel==0?"mqtt topic: ":"http topic: ")+topStr);

        if (channel==0){//向下兼容

            mqttPublish(reqId,payload,topic,qos,retained,callback);

        }
        else{

            String token = NeulinkSecurity.getInstance().getToken();
            final Map<String,String> params = new HashMap<>();
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
                httpRegist(reqId,topic,payload,params);
            }
            else {
                if(!neulinkServiceInited){
                    throw new NeulinkException(STATUS_503,"SDK还没初始化完成");
                }
                httpPublish(reqId,topic,payload,params,callback);
            }
        }
    }
    private void mqttPublish(String reqId,String payload,String topStr,Integer qos,boolean retained,IResCallback callback){
        fixedThreadPool.execute(new MqttPublisher(reqId,payload,topStr,qos,retained,callback));
    }

    private void httpRegist(String reqId,String topStr,String payload,Map<String,String> params){
        fixedThreadPool.execute(new HttpRegistor(reqId,payload,topStr,params));
    }

    private void httpPublish(String reqId,String topStr,String payload,Map<String,String> params,IResCallback callback){
        fixedThreadPool.execute(new HttpPublisher(reqId,topStr,payload,params,callback));
    }

    private void connect(String mqttServiceUri,String userName,String password,Context context){
        if(!mqttInited){
            initMqtt(mqttServiceUri,userName,password,context);
            int cnt = 0;
            while (!getMqttConnSuccessed()){
                try {
                    connect();
                    cnt++;
                    Thread.sleep(5000);
                    Log.i(TAG,"try "+cnt+"次连接。。。。");
                }
                catch (Exception ex){
                    Log.e(TAG,"连接失败：",ex);
                }
            }
        }
    }

    protected void publishConnect(Integer flg){
        String manualReport = ConfigContext.getInstance().getConfig(ConfigContext.STATUS_MANUAL_REPORT,"true");
        if("true".equalsIgnoreCase(manualReport)){
            String payload = "{\"dev_id\":\""+ ServiceRegistrator.getInstance().getDeviceService().getExtSN()+"\",\"status\":1}";
            publishMessage("msg/req/connect","v1.0",UUID.randomUUID().toString(),payload,1,true);
        }
    }

    protected void publishDisConnect(Integer flg){

        String manualReport = ConfigContext.getInstance().getConfig(ConfigContext.STATUS_MANUAL_REPORT,"true");
        if("true".equalsIgnoreCase(manualReport)){
            String payload = "{\"dev_id\":\""+ ServiceRegistrator.getInstance().getDeviceService().getExtSN()+"\",\"status\":0}";
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
        if(ObjectUtil.isNotEmpty(storeid)){
            return storeid;
        }
        return "notimpl";
    }
    private String zoneid="0";
    private String getZoneId(){
        if(ObjectUtil.isNotEmpty(zoneid)){
            return zoneid;
        }
        return "notimpl";
    }

    boolean getMqttConnSuccessed(){
        synchronized (this){
            return mqttConnSuccessed;
        }
    }

    void setMqttConnSuccessed(Boolean connSuccessed){
        synchronized (this) {
            this.mqttConnSuccessed = connSuccessed;
        }
    }

    private ReentrantLock reentrantLock = new ReentrantLock();
    /**
     * MQTT是否连接成功
     */
    private IMqttActionListener mqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "onSuccess ");
            setMqttConnSuccessed(true);
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
                setMqttConnSuccessed(true);
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

    class MqttPublisher implements Runnable{
        private String reqId;
        private String payload;
        private String topStr;
        private Integer qos;
        private boolean retained;
        private IResCallback callback;
        public MqttPublisher(String reqId,String payload,String topStr,Integer qos,boolean retained,IResCallback callback){
            this.reqId = reqId;
            this.payload = payload;
            this.topStr = topStr;
            this.qos = qos;
            this.retained = retained;
            this.callback = callback;
        }
        @Override
        public void run() {
            if(mqttServiceUri ==null){
                mqttServiceUri = ConfigContext.getInstance().getConfig(ConfigContext.MQTT_SERVER);
            }
            String userName = ConfigContext.getInstance().getConfig(ConfigContext.USERNAME);
            String password = ConfigContext.getInstance().getConfig(ConfigContext.PASSWORD);
            Context context = ContextHolder.getInstance().getContext();
            connect(mqttServiceUri,userName,password,context);
            /**
             * MQTT机制
             */
            myMqttService.publish(reqId,payload,topStr, qos, retained,context,callback);

            neulinkServiceInited = true;
        }
    }

    class HttpRegistor implements Runnable{
        private String reqId;
        private String payload;
        private String topStr;
        private Map<String,String> params;

        public HttpRegistor(String reqId,String payload,String topStr,Map<String,String> params){
            this.reqId = reqId;
            this.payload = payload;
            this.topStr = topStr;
            this.params = params;
        }
        @Override
        public void run() {
            /**
             * HTTP机制
             */
            Context context = ContextHolder.getInstance().getContext();
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
                String userName = zone.getMqttUserName();
                String password = zone.getMqttPassword();
                //tcp://dev.neucore.com:1883
                mqttServiceUri = String.format("tcp://%s:%s",mqttHost,port);

                connect(mqttServiceUri,userName,password,context);

                neulinkServiceInited = true;

            } catch (Exception e) {
                Log.e(TAG,e.getMessage(),e);
            }
        }
    }

    class HttpPublisher implements Runnable{
        private String reqId;
        private String topStr;
        private String payload;
        private Map<String,String> params;
        private IResCallback callback;
        public HttpPublisher(String reqId,String topStr,String payload,Map<String,String> params,IResCallback callback){
            this.reqId = reqId;
            this.topStr = topStr;
            this.payload = payload;
            this.params = params;
            this.callback = callback;
        }
        @Override
        public void run() {
            /**
             * 设备端2cloud
             */
            httpServiceUri = ConfigContext.getInstance().getConfig(ConfigContext.REGIST_SERVER, httpServiceUri);
            Boolean done = false;
            int count = 0;
            while(!done && count<3){
                try {
                    Log.d(TAG,topStr);
                    String topicStr = URLEncoder.encode(topStr,"UTF-8");
                    String response = NeuHttpHelper.post(httpServiceUri +"?topic="+topicStr,payload,params,10,60,1);
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
                        String token = ServiceRegistrator.getInstance().getLoginCallback().login();
                        if(ObjectUtil.isNotEmpty(token)){
                            Log.i(TAG,"token过期，重新登录成功");
                            NeulinkSecurity.getInstance().setToken(token);
                        }
                        count++;
                    }
                    else {
                        if(ObjectUtil.isNotEmpty(callback)){
                            Class cls = callback.getResultType();
                            Result result = Result.fail(e.getCode(),e.getMessage());
                            result.setReqId(reqId);
                            callback.onFinished(result);
                        }
                        done = true;
                    }
                }
                catch (Exception e) {
                    Log.d(TAG,"upload2cloud error with: "+e.getMessage());
                    if(ObjectUtil.isNotEmpty(callback)){
                        Class cls = callback.getResultType();
                        Result result = Result.fail(STATUS_500,e.getMessage());
                        result.setReqId(reqId);
                        callback.onFinished(result);
                    }
                    done = true;
                }
            }
        }
    }

    public Boolean isNeulinkServiceInited() {
        return neulinkServiceInited;
    }
}
