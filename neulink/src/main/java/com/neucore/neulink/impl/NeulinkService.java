package com.neucore.neulink.impl;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.IProcessor;
import com.neucore.neulink.IResCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.impl.adapter.NeulinkMsgCallBackAdapter;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.cmd.msg.DeviceInfo;
import com.neucore.neulink.impl.cmd.msg.NeulinkZone;
import com.neucore.neulink.impl.cmd.msg.ResRegist;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.impl.service.MyMqttService;
import com.neucore.neulink.impl.service.NeulinkSecurity;
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
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttReceivedMessage;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;

public class NeulinkService implements NeulinkConst{

    private String TAG = TAG_PREFIX+"Service";

    private static NeulinkService instance = new NeulinkService();

    protected IResCallback defaultResCallback = new ResCallback2Log();
    private MyMqttService myMqttService = null;
    private List<IMqttCallBack> mqttCallBacks = new ArrayList<>();
    private Boolean neulinkServiceInited = false;
    private Boolean mqttInited = false;
    private Boolean mqttConnSuccessed = false;
    private Throwable failException;
    private Register register = null;
    private Boolean destroy = false;

    private NeulinkPublisherFacde publisherFacde;
    private NeulinkSubscriberFacde subscriberFacde;
    private NeulinkScheduledReport autoReporter = null;
    private String mqttServiceUri, httpServiceUri;
    private UdpReceiveAndtcpSend udpReceiveAndtcpSend;
    private IDeviceService deviceService;
    public static NeulinkService getInstance(){
        return instance;
    }
    final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
    /**
     * 构建EasyMqttService对象
     */
    public void init() {
        Context context = ContextHolder.getInstance().getContext();
        NeulinkMsgCallBackAdapter defaultMqttCallBack = new NeulinkMsgCallBackAdapter(context,this);
        deviceService = ServiceRegistry.getInstance().getDeviceService();
        mqttCallBacks.add(defaultMqttCallBack);
        publisherFacde = new NeulinkPublisherFacde(context,this);
        subscriberFacde = new NeulinkSubscriberFacde(context,this);
        register = new Register(context,this);
        udpReceiveAndtcpSend = new UdpReceiveAndtcpSend();
        udpReceiveAndtcpSend.start();
    }

    public void initMqttService(String mqttServiceUri, String userName, String password){
        LogUtils.iTag(TAG,"initMqttService");
        if(!mqttInited){
            createMqttService(mqttServiceUri,userName,password);
            int cnt = 0;
            while (!getMqttConnSuccessed()){
                try {
                    LogUtils.iTag(TAG,"start connectMqtt");
                    connectMqtt();
                    LogUtils.iTag(TAG,"end connectMqtt");
                }
                catch (Exception ex){
                    LogUtils.eTag(TAG,"连接失败：",ex);
                }
                finally {
                    cnt++;
                    LogUtils.iTag(TAG,"try "+cnt+"次连接。。。。");
                    if(!getMqttConnSuccessed()){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        if(isFailed()){
                            Throwable throwable = getFailException();
                            if(throwable instanceof MqttException){
                                int code = ((MqttException)throwable).getReasonCode();
                                if(code == MqttException.REASON_CODE_NOT_AUTHORIZED
                                        ||code ==MqttException.REASON_CODE_FAILED_AUTHENTICATION
                                        || code == MqttException.REASON_CODE_INVALID_CLIENT_ID){
                                    Result result = new Result();
                                    result.setReqId(UUID.fastUUID().toString());
                                    result.setCode(STATUS_403);
                                    result.setMsg(throwable.getMessage());
                                    defaultResCallback.onFinished(result);
                                    LogUtils.eTag(TAG,"Mqtt鉴权失败："+ getFailException().getMessage());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            LogUtils.iTag(TAG,"mqttInited");
        }
    }

    private void createMqttService(String serverUri, String userName, String password){
        LogUtils.iTag(TAG,String.format("createMqttService inited %s", mqttInited));
        synchronized (mqttInited){
            if(!mqttInited){
                Context context = ContextHolder.getInstance().getContext();
                myMqttService = new MyMqttService.Builder()
                        .serverUrl(serverUri)
                        .userName(userName)
                        .passWord(password)
                        //唯一标示 保证每个设备都唯一就可以 建议 imei
                        .clientId(ServiceRegistry.getInstance().getDeviceService().getExtSN())
                        //设置自动重连
                        .autoReconnect(ConfigContext.getInstance().getConfig(ConfigContext.AUTO_RECONNECT,true))
                        //最大延时时间
                        .maxReconnectDelay(ConfigContext.getInstance().getConfig(ConfigContext.MAX_RECONNECT_DELAY,30000))
                        //设置不清除回话session 可收到服务器之前发出的推送消息
                        .cleanSession(ConfigContext.getInstance().getConfig(ConfigContext.CLEAN_SESSION,false))
                        //mqtt服务器地址 格式例如：tcp://10.0.261.159:1883
                        //心跳包默认的发送间隔
                        .keepAliveInterval(ConfigContext.getInstance().getConfig(ConfigContext.KEEP_ALIVE_INTERVAL,60))
                        //超时
                        .connectTimeout(ConfigContext.getInstance().getConfig(ConfigContext.CONNECT_TIMEOUT,30))
                        //执行超时
                        .executorServiceTimeout(ConfigContext.getInstance().getConfig(ConfigContext.EXECUTOR_SERVICE_TIMEOUT,1))
                        //设置发布和订阅回调接口
                        .mqttCallback(defaultMqttCallback)
                        //设置连接或者发布动作侦听器
                        .mqttActionListener(defaultMqttActionListener)
                        //设置消息侦听器
                        //.mqttMessageListener(messageListener)
                        //构建出EasyMqttService 建议用application的context
                        .bulid(context);
                mqttInited = true;
                new HouseKeeping().start();
                LogUtils.iTag(TAG,String.format("end createMqttService inited %s", mqttInited));
            }
        }
    }

    public IResCallback getDefaultResCallback() {
        return defaultResCallback;
    }

    public void setDefaultResCallback(IResCallback defaultResCallback) {
        this.defaultResCallback = defaultResCallback;
    }

    public NeulinkPublisherFacde getPublisherFacde(){
        return publisherFacde;
    }

    public IDeviceService getDeviceService() {
        return deviceService;
    }

    public void setDeviceService(IDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * 连接Mqtt服务器
     */
    private void connectMqtt() {
        if(!mqttConnSuccessed){
            myMqttService.connect();
        }
    }

    public void destroy(){
        if(!destroy && !ObjectUtil.isEmpty(myMqttService)){
            myMqttService.disconnect();
            destroy = true;
            LogUtils.iTag(TAG,"断开Mqtt Service");
        }
    }

    public Boolean getDestroy() {
        return destroy;
    }

    public void addMQTTCallBack(IMqttCallBack mqttCallBack){
        if(mqttCallBack!=null){
            this.mqttCallBacks.add(mqttCallBack);
        }
    }

    public boolean regist(DeviceInfo deviceInfo){
        String payload = JSonUtils.toString(deviceInfo);
        String devinfo_topic = "msg/req/devinfo";
        publishMessage(devinfo_topic, IProcessor.V1$0, payload, ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1));
        return true;
    }

    public void connect(Integer flg){
        String manualReport = ConfigContext.getInstance().getConfig(ConfigContext.STATUS_MANUAL_REPORT,"true");
        if("true".equalsIgnoreCase(manualReport)){
            String payload = "{\"dev_id\":\""+ ServiceRegistry.getInstance().getDeviceService().getExtSN()+"\",\"status\":1}";
            publishMessage("msg/req/connect","v1.0",UUID.fastUUID().toString(),payload,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1),ConfigContext.getInstance().getConfig(ConfigContext.MQTT_RETAINED,false));
        }
    }

    public void disconnect(Integer flg){

        String manualReport = ConfigContext.getInstance().getConfig(ConfigContext.STATUS_MANUAL_REPORT,"true");
        if("true".equalsIgnoreCase(manualReport)){
            String payload = "{\"dev_id\":\""+ ServiceRegistry.getInstance().getDeviceService().getExtSN()+"\",\"status\":0}";
            publishMessage("msg/req/disconnect","v1.0",UUID.fastUUID().toString(),payload,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1),ConfigContext.getInstance().getConfig(ConfigContext.MQTT_RETAINED,false));
        }
    }

    public LWTInfo lwt(){
        LWTInfo info = new LWTInfo();
        info.setTopicPrefix("msg/req/lwt/v1.0");
        String payload = "{\"dev_id\":\""+ServiceRegistry.getInstance().getDeviceService().getExtSN()+"\",\"status\":-1}";
        info.setPayload(payload);
        return info;
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

    /**
     *
     * @param topicPrefix
     * @param version
     * @param payload
     * @param qos
     * @param callback
     */
    protected void publishMessage(String topicPrefix, String version, String payload, int qos, IResCallback callback){

        publishMessage(topicPrefix,version, UUID.fastUUID().toString(),payload, qos,callback);
    }

    /**
     *
     * @param topicPrefix
     * @param version
     * @param payload
     * @param qos
     * @param retained
     * @param callback
     */
    protected void publishMessage(String topicPrefix, String version, String payload, int qos,boolean retained, IResCallback callback){
        publishMessage(topicPrefix,version, UUID.fastUUID().toString(),payload, qos,retained,callback);
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
        publishMessage(topicPrefix,version,reqId,payload,qos,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_RETAINED,false),null);
    }

    protected void publishMessage(String topicPrefix, String version, String reqId, String payload, int qos, IResCallback callback){
        publishMessage(topicPrefix,version,reqId,payload,qos,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_RETAINED,false),callback);
    }

    protected void publishMessage(String topicPrefix, String version, String reqId, String payload, int qos,boolean retained){
        publishMessage(topicPrefix,version,reqId,payload,qos,retained,null);
    }

    protected void publishMessage(final String topicPrefix, String version, final String reqId, final String payload, final int qos, final boolean retained, final IResCallback callback){

        int channel = ConfigContext.getInstance().getConfig(ConfigContext.UPLOAD_CHANNEL,0);

        String md5 = MD5Utils.getInstance().getMD5String(payload);

        final String topic = buildTopic(topicPrefix,version,reqId,md5);

        LogUtils.dTag(TAG,"upload2cloud with "+(channel==0?"mqtt topic: ":"http topic: ")+topic);

        if (channel==0){//向下兼容
            if(topic.startsWith("msg/req/devinfo")) {
                regist(reqId,topic,payload,qos,retained,null);
            }
            else{
                publish(reqId,topic,payload,qos,retained,null,callback);
            }
        }
        else{

            Map<String,String> params = getParams();
            /**
             * 设备注册：
             *
             */
            if(topic.startsWith("msg/req/devinfo")){
                regist(reqId,topic,payload,qos,retained,params);
            }
            else {
                publish(reqId,topic,payload,qos,retained,params,callback);
            }
        }
    }

    private String buildTopic(String topicPrefix,String version,String reqId,String md5){

        StringBuffer stringBuffer = new StringBuffer(topicPrefix).append("/").append(version).append("/").append(reqId).append("/").append(md5);

        stringBuffer.append("/").append(getCustId()).append("/").append(getStoreId()).append("/").append(getZoneId()).append("/").append(ServiceRegistry.getInstance().getDeviceService().getExtSN());

        return stringBuffer.toString();
    }

    private void regist(String reqId,String topStr, String payload, int qos, Boolean retained, Map<String,String> params){
        fixedThreadPool.execute(new AsyncRegistor(ContextHolder.getInstance().getContext(),reqId,topStr,payload,qos,retained,params));
    }

    private void publish(String reqId,String payload,String topStr,Integer qos,boolean retained,Map<String,String> params,IResCallback callback){

        if(ObjectUtil.isEmpty(callback)){
            LogUtils.iTag(TAG,"没有设置IResCallback，走系统默认回调，日志输出回调结果");
            callback = defaultResCallback;
        }

        if(!neulinkServiceInited){
            if(callback!=null){
                Result result = Result.fail(STATUS_503,"SDK还没初始化完成");
                result.setReqId(reqId);
                callback.onFinished(result);
            }
        }
        else{
            fixedThreadPool.execute(new AsynPublisher(reqId,payload,topStr,qos,retained,params,callback));
        }
    }

    private Map<String,String> getParams(){
        String token = NeulinkSecurity.getInstance().getToken();
        final Map<String,String> params = new HashMap<>();
        if(token!=null){
            int index = token.indexOf(" ");
            if(index!=-1){
                token = token.substring(index+1);
            }
            params.put("Authorization","Bearer "+token);
        }
        IDeviceService deviceService = ServiceRegistry.getInstance().getDeviceService();
        if(ObjectUtil.isNotEmpty(deviceService)){
            Locale locale = deviceService.getLocale();
            if(ObjectUtil.isEmpty(locale)){
                locale = Locale.getDefault();
            }
            params.put("Accept-Language",locale.getLanguage()+"-"+locale.getCountry());
        }
        return params;
    }

    private String custid="notimpl";
    public String getCustId(){
        String sccperId = ConfigContext.getInstance().getConfig("ScopeId","yeker");
        if(ObjectUtil.isNotEmpty(sccperId)){
            return sccperId;
        }
        return custid;
    }
    private String storeid="notimpl";
    public String getStoreId(){
        if(ObjectUtil.isNotEmpty(storeid)){
            return storeid;
        }
        return "notimpl";
    }
    private String zoneid="0";
    public String getZoneId(){
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
    boolean isFailed(){
        return ObjectUtil.isNotEmpty(failException);
    }

    Throwable getFailException(){
        return failException;
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
    private IMqttActionListener defaultMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            LogUtils.iTag(TAG, "onSuccess ");
            failException = null;
            if (mqttCallBacks != null) {
                for (IMqttCallBack callback: mqttCallBacks) {
                    try {
                        callback.connectSuccess(arg0);
                    }
                    catch (Exception ex){
                        LogUtils.eTag(TAG,ex.getMessage());
                    }
                }
            }
            setMqttConnSuccessed(true);
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            failException = arg1;
            if (mqttCallBacks != null) {
                for (IMqttCallBack callback: mqttCallBacks) {
                    try {
                        callback.connectFailed(arg0, arg1);
                    }
                    catch (Exception ex){
                        LogUtils.eTag(TAG,ex.getMessage());
                    }
                }
            }
        }
    };

    public List<IMqttCallBack> getMqttCallBacks() {
        return mqttCallBacks;
    }

    // MQTT监听并且接受消息
    private MqttCallback defaultMqttCallback = new MqttCallbackExtended() {

        private String TAG = TAG_PREFIX+"MqttCallback";

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            try {

                LogUtils.iTag(TAG, "connectComplete ");
                reentrantLock.lock();
                subscriberFacde.subAll();
                deviceService.connect();
                LogUtils.dTag(TAG, "Server:" + mqttServiceUri + " ,connectComplete reconnect:" + reconnect);
                if (mqttCallBacks != null) {
                    for (IMqttCallBack callback: mqttCallBacks) {
                        try {
                            callback.connectComplete(reconnect, serverURI);
                        }
                        catch (Exception ex){
                            LogUtils.eTag(TAG,ex.getMessage());
                        }
                    }
                }
                setMqttConnSuccessed(true);
            }
            finally {
                reentrantLock.unlock();
            }
        }


        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {

            MqttReceivedMessage receivedMessage = (MqttReceivedMessage)message;

            int messageId = receivedMessage.getMessageId();
            String detailLog = topic + ";qos:" + receivedMessage.getQos() + ";retained:" + receivedMessage.isRetained() + "messageId:"+messageId;
            String msgContent = new String(receivedMessage.getPayload());
            LogUtils.iTag(TAG, "messageArrived:" + msgContent);
            LogUtils.iTag(TAG, detailLog);

            if (mqttCallBacks != null) {
                for (IMqttCallBack callback: mqttCallBacks) {
                    try {
                        callback.messageArrived(topic, msgContent, receivedMessage.getQos());
                    }
                    catch (Exception ex){
                        LogUtils.eTag(TAG,ex.getMessage());
                    }
                }
            }

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {

            if (mqttCallBacks != null) {
                for (IMqttCallBack callback: mqttCallBacks) {
                    try {
                        callback.deliveryComplete(arg0);
                    }
                    catch (Exception ex){
                        LogUtils.eTag(TAG,ex.getMessage());
                    }
                }
            }
        }

        @Override
        public void connectionLost(Throwable arg0) {

            LogUtils.iTag(TAG, "connectionLost");
            deviceService.disconnect();
            if (mqttCallBacks != null) {
                for (IMqttCallBack callback: mqttCallBacks) {
                    try {
                        callback.connectionLost(arg0);
                    }
                    catch (Exception ex){
                        LogUtils.eTag(TAG,ex.getMessage());
                    }
                }
            }
            // 失去连接，重连
        }
    };

    /**
     * 日志周期清理服务
     */
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
                            LogUtils.iTag(TAG,file.getAbsolutePath()+"清除掉");
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

    /**
     * 异步注册器
     */
    class AsyncRegistor implements Runnable {

        private String reqId;
        private String topStr;
        private String payload;
        private int qos;
        private Boolean retained;
        private Map<String,String> params;
        private Context context;
        public AsyncRegistor(Context context,String reqId,String topStr, String payload, int qos, Boolean retained, Map<String,String> params){
            this.context = context;
            this.reqId = reqId;
            this.topStr = topStr;
            this.payload = payload;
            this.qos = qos;
            this.retained = retained;
            this.params = params;

        }

        @Override
        public void run() {
            int channel = ConfigContext.getInstance().getConfig(ConfigContext.UPLOAD_CHANNEL,0);
            while (!neulinkServiceInited){
                try {
                    if(channel==0){
                        /**
                         * MQTT机制
                         */
                        if(mqttServiceUri ==null){
                            mqttServiceUri = ConfigContext.getInstance().getConfig(ConfigContext.MQTT_SERVER,"tcp://dev.neucore.com:1883");
                        }
                        String userName = ConfigContext.getInstance().getConfig(ConfigContext.USERNAME,"admin");
                        String password = ConfigContext.getInstance().getConfig(ConfigContext.PASSWORD,"password");

                        initMqttService(mqttServiceUri,userName,password);
                        /**
                         * MQTT机制
                         */
                        myMqttService.publish(reqId,payload,topStr, qos, retained,null);

                        neulinkServiceInited = true;
                    }
                    else{
                        /**
                         * HTTP机制
                         */
                        Context context = ContextHolder.getInstance().getContext();
                        String registServer = ConfigContext.getInstance().getConfig(ConfigContext.REGIST_SERVER,"https://dev.neucore.com/api/neulink/upload2cloud");
                        LogUtils.dTag(TAG,"registServer："+registServer);

                        String response = null;

                        String topic = URLEncoder.encode(topStr,"UTF-8");
                        response = NeuHttpHelper.post(registServer+"?topic="+topic,payload,params,10,60,1);

                        LogUtils.dTag(TAG,"设备注册响应："+response);

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
                        userName = userName==null?ConfigContext.getInstance().getConfig(ConfigContext.USERNAME,"admin"):userName;
                        password = password==null?ConfigContext.getInstance().getConfig(ConfigContext.PASSWORD,"password"):password;
                        //tcp://dev.neucore.com:1883
                        mqttServiceUri = String.format("tcp://%s:%s",mqttHost,port);

                        initMqttService(mqttServiceUri,userName,password);

                        neulinkServiceInited = true;
                    }
                }
                catch (NeulinkException e) {
                    LogUtils.eTag(TAG,"注册失败",e);
                    Result result = new Result();
                    result.setReqId(reqId);
                    result.setCode(e.getCode());
                    result.setMsg(e.getMsg());
                    defaultResCallback.onFinished(result);
                }
                catch (Exception ex){
                    LogUtils.eTag(TAG,"注册失败",ex);
                    Result result = new Result();
                    result.setReqId(reqId);
                    result.setCode(STATUS_500);
                    result.setMsg(ex.getMessage());
                    defaultResCallback.onFinished(result);

                }
                finally {
                    if(!neulinkServiceInited){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                        }
                    }
                    else{
                        autoReporter = new NeulinkScheduledReport(context, instance);
                        autoReporter.start();
                    }
                }
            }
        }
    }

    /**
     * 异步发布器
     */
    class AsynPublisher implements Runnable{
        private String reqId;
        private String topStr;
        private String payload;
        private Integer qos;
        private Boolean retained;
        private Map<String,String> params;
        private IResCallback callback;
        public AsynPublisher(String reqId, String topStr, String payload, int qos, Boolean retained, Map<String,String> params, IResCallback callback){
            this.reqId = reqId;
            this.topStr = topStr;
            this.payload = payload;
            this.qos = qos;
            this.retained = retained;
            this.params = params;
            this.callback = callback;
        }
        @Override
        public void run() {
            /**
             * End2Cloud
             */
            int channel = ConfigContext.getInstance().getConfig(ConfigContext.UPLOAD_CHANNEL,0);
            if(channel==0){
                myMqttService.publish(reqId,payload,topStr, qos, retained,callback);
            }
            else{
                /**
                 * HTTP机制
                 */
                httpServiceUri = ConfigContext.getInstance().getConfig(ConfigContext.REGIST_SERVER, httpServiceUri);
                Boolean done = false;
                int count = 0;
                while(!done && count<3){
                    try {
                        String topicStr = URLEncoder.encode(topStr,"UTF-8");
                        String response = NeuHttpHelper.post(httpServiceUri +"?topic="+topicStr,payload,params,10,60,1);
                        LogUtils.dTag(TAG,"设备upload2cloud请求："+payload);
                        LogUtils.dTag(TAG,"响应topic:"+topStr);
                        LogUtils.dTag(TAG,"设备upload2cloud响应："+response);
                        if(ObjectUtil.isNotEmpty(callback)){
                            Result result = JSonUtils.toObject(response,Result.class);
                            result.setReqId(reqId);
                            callback.onFinished(result);
                            LogUtils.dTag(TAG,"onFinished");
                        }
                        done = true;
                    }
                    catch (NeulinkException e) {
                        if(e.getCode()==401||e.getCode()==403){

                            Result result = Result.fail(e.getCode(),e.getMessage());
                            result.setReqId(reqId);
                            result.setCode(e.getCode());
                            result.setMsg("token过期");
                            callback.onFinished(result);

                            ILoginCallback loginCallback = ServiceRegistry.getInstance().getLoginCallback();
                            if(ObjectUtil.isNotEmpty(loginCallback)){
                                String token = loginCallback.login();
                                if(ObjectUtil.isNotEmpty(token)){
                                    LogUtils.iTag(TAG,"token过期，重新登录成功");
                                    NeulinkSecurity.getInstance().setToken(token);
                                }
                                else{
                                    result = Result.fail(e.getCode(),e.getMessage());
                                    result.setReqId(reqId);
                                    result.setCode(e.getCode());
                                    result.setMsg("token过期，重新登录失败");
                                    callback.onFinished(result);
                                }
                            }
                            else{
                                LogUtils.eTag(TAG,"没有实现ILoginCallback");
                                result = Result.fail(e.getCode(),e.getMessage());
                                result.setReqId(reqId);
                                result.setCode(e.getCode());
                                result.setMsg("没有实现ILoginCallback");
                                callback.onFinished(result);
                            }
                            count++;
                        }
                        else {
                            if(ObjectUtil.isNotEmpty(callback)){
                                Result result = Result.fail(e.getCode(),e.getMessage());
                                result.setReqId(reqId);
                                callback.onFinished(result);
                            }
                            done = true;
                        }
                    }
                    catch (Exception e) {
                        LogUtils.dTag(TAG,"upload2cloud error with: "+e.getMessage());
                        if(ObjectUtil.isNotEmpty(callback)){
                            Result result = Result.fail(STATUS_500,e.getMessage());
                            result.setReqId(reqId);
                            callback.onFinished(result);
                        }
                        done = true;
                    }
                }
            }
        }
    }

    public Boolean isNeulinkServiceInited() {
        return neulinkServiceInited;
    }
}
