package com.neucore.neulink.impl;

import android.content.Context;

import com.google.gson.JsonObject;
import com.neucore.neulink.IMessage;
import com.neucore.neulink.impl.adapter.NeulinkActionListenerAdapter;
import com.neucore.neulink.impl.adapter.RegistCallback;
import com.neucore.neulink.impl.registry.CallbackRegistry;
import com.neucore.neulink.impl.service.LWTPayload;
import com.neucore.neulink.impl.service.LWTTopic;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.IProcessor;
import com.neucore.neulink.IResCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.cmd.msg.DeviceInfo;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.impl.service.MyMqttService;
import com.neucore.neulink.impl.service.NeulinkSecurity;
import com.neucore.neulink.impl.service.broadcast.UdpReceiveAndtcpSend;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DatesUtil;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.HttpParamWrapper;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.MD5Utils;
import com.neucore.neulink.util.NeuHttpHelper;
import com.neucore.neulink.util.HeadersUtil;
import com.neucore.neulink.util.RequestContext;
import com.neucore.neulink.util.SecuretSign;

import org.eclipse.paho.mqttv5.client.MqttActionListener;
import org.eclipse.paho.mqttv5.client.MqttClientException;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.packet.MqttReturnCode;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;

public class NeulinkService implements NeulinkConst{

    private String TAG = TAG_PREFIX+"Service";

    private static NeulinkService instance = new NeulinkService();

    protected IResCallback defaultResCallback = ConfigContext.getInstance().getDefaultResCallback();
    private MyMqttService myMqttService = null;
    private RegistCallback registCallback = new RegistCallback();
    private List<IMqttCallBack> mqttCallBacks = new ArrayList<>();
    private Boolean neulinkServiceInited = false;
    private Boolean mqttInited = false;
    private Boolean registCalled = false,registed=false;
    private Boolean mqttConnCalled=false, mqttConnSuccessed = false;
    private Throwable failException;
    private Boolean closed=false, destroyed = false;

    private NeulinkPublisherFacde publisherFacde;
    private NeulinkSubscriberFacde subscriberFacde;
    private NeulinkActionListenerAdapter neulinkActionListenerAdapter;
    private NeulinkScheduledReport autoReporter = null;
    private String mqttServiceUri, httpServiceUri, userName,password;
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
        deviceService = ServiceRegistry.getInstance().getDeviceService();
        publisherFacde = new NeulinkPublisherFacde(context,this);
        subscriberFacde = new NeulinkSubscriberFacde(context,this);
        neulinkActionListenerAdapter = new NeulinkActionListenerAdapter(context,this);
        new RegisterAdapter();
        udpReceiveAndtcpSend = new UdpReceiveAndtcpSend();
        udpReceiveAndtcpSend.start();
    }

    public synchronized void initMqttService(String mqttServiceUri, String userName, String password) throws MqttException{
        createMqttService(mqttServiceUri,userName,password);
        int count = 1;
        while (!isMqttConnSuccessed()){
            try {
                NeuLogUtils.iTag(TAG,String.format("try %s 次 start connectMqtt。。。。",count));
                connect();
            }
            catch (MqttException ex){
                NeuLogUtils.eTag(TAG,"连接失败",ex);
            }
            finally {
                if(!isMqttConnSuccessed()){
                    if(isFailed()){
                        NeuLogUtils.eTag(TAG,"连接失败：",failException);
                        if(failException instanceof MqttException){
                            throw (MqttException)failException;
                        }
                    }
                    try {
                        Thread.sleep(1000*count);
                        if(count<30){
                            count++;
                        }
                    } catch (InterruptedException e) {
                    }
                }
                NeuLogUtils.iTag(TAG,String.format("end %s 次 connectMqtt",count));
            }
        }
        NeuLogUtils.iTag(TAG,String.format("end connect success=%s with 第 %s 次",isMqttConnSuccessed(),count));
    }

    private void createMqttService(String serverUri, String userName, String password){
        //唯一标示 保证每个设备都唯一就可以 建议 imei

        synchronized (this){
            if(!mqttInited){
                NeuLogUtils.iTag(TAG,String.format("createMqttService inited %s", mqttInited));
                Context context = ContextHolder.getInstance().getContext();
                myMqttService = new MyMqttService.Builder()
                        .serverUrl(serverUri)
                        .userName(userName)
                        .passWord(password)
                        .clientId(ConfigContext.getInstance().getConfig(ConfigContext.MQTT_CLIENT_ID,deviceService.getExtSN()))
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
                        .mqttCallback(neulinkActionListenerAdapter)
                        //设置连接或者发布动作侦听器
                        .mqttActionListener(neulinkActionListenerAdapter)
                        //设置消息侦听器
                        //.mqttMessageListener(messageListener)
                        //构建出EasyMqttService 建议用application的context
                        .bulid(context);
                mqttInited = true;
                new HouseKeeping().start();
                NeuLogUtils.iTag(TAG,String.format("end createMqttService inited %s,serverUri=%s,userName=%s,password=%s", mqttInited,serverUri,userName,password));
            }
        }
    }

    public MyMqttService getMqttService(){
        return myMqttService;
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

    public NeulinkSubscriberFacde getSubscriberFacde(){
        return subscriberFacde;
    }

    public NeulinkActionListenerAdapter getNeulinkActionListenerAdapter(){
        return neulinkActionListenerAdapter;
    }

    public IDeviceService getDeviceService() {
        return deviceService;
    }

    /**
     * 连接Mqtt服务器
     */
    private synchronized void connect() throws MqttException{
        if (!mqttConnCalled
                && !mqttConnSuccessed) {
            myMqttService.connect();
            mqttConnCalled = true;
            NeuLogUtils.iTag(TAG,String.format("connect mqttConnCalled=%s",mqttConnCalled));
        }
    }

    private synchronized void resetMqttConnCalled(){
        mqttConnCalled = false;
        failException = null;
        NeuLogUtils.iTag(TAG,"resetMqttConnCalled");
    }

    public void destroy(){
        if(!destroyed
                && !ObjectUtil.isEmpty(myMqttService)){
            myMqttService.destory();
            destroyed = true;
            mqttConnCalled = false;
            NeuLogUtils.iTag(TAG,"destroy");
        }
    }

    public void close(){
        if(!closed
                && !ObjectUtil.isEmpty(myMqttService)){
            myMqttService.close();
            closed = true;
            mqttConnCalled = false;
            NeuLogUtils.iTag(TAG,"close");
        }
    }

    public Boolean getDestroyed() {
        return destroyed;
    }

    public void addMQTTCallBack(IMqttCallBack mqttCallBack){
        if(mqttCallBack!=null){
            this.mqttCallBacks.add(mqttCallBack);
        }
    }

    public boolean regist(DeviceInfo deviceInfo){
        String payload = JSonUtils.toString(deviceInfo);
        String devinfo_topic = "msg/req/devinfo";
        NeuLogUtils.iTag(TAG,"regist");
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

    public LWTTopic lwtTopic(){
        long resTime = DatesUtil.getNowTimeStamp();//msg.getReqtime();
        LWTTopic info = new LWTTopic();
        String topic = "msg/req/lwt";
        String productId = deviceService.getProductKey();
        if(ObjectUtil.isNotEmpty(productId)){
            topic = productId+"/"+topic;
        }
        info.setTopic(topic);
        info.setRetained(true);
        info.setQos(1);
        return info;
    }

    public LWTPayload lwtPayload(){
        long resTime = DatesUtil.getNowTimeStamp();//msg.getReqtime();
        LWTPayload info = new LWTPayload();
        info.setHeader("version","v1.0");
        info.setHeader("biz","lwt");
        info.setHeader("devid", ServiceRegistry.getInstance().getDeviceService().getExtSN());
        info.setHeader("custid", NeulinkService.getInstance().getCustId());
        info.setHeader("storeid",NeulinkService.getInstance().getStoreId());
        info.setHeader("zoneid",NeulinkService.getInstance().getZoneId());
        info.setHeader("time",String.valueOf(resTime));
        info.setStatus(-1);
        return info;
    }

    /**
     *
     * @param topic
     * @param qos
     * @param mqttMessageListener
     */
    protected void subscribeToTopic(final String topic, int qos, MqttActionListener mqttMessageListener){
        myMqttService.subscribe(topic, qos,mqttMessageListener);
    }

    /**
     *
     * @param topic
     * @param qos
     * @param mqttMessageListener
     */
    protected void subscribeToTopic(final String topic[], int qos[],MqttActionListener mqttMessageListener){
        myMqttService.subscribe(topic, qos,mqttMessageListener);
    }

    /**
     * 0
     * @param topicPrefix
     * @param version
     * @param payload
     * @param qos
     */
    protected void publishMessage(String topicPrefix,String version, String payload, int qos){
        publishMessage(topicPrefix,version, payload, qos,null);
    }
    /**
     * 1
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
     * 2
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
     * 3
     * @param debug
     * @param topicPrefix
     * @param version
     * @param reqId
     * @param payload
     * @param qos
     */
    protected void publishMessage(boolean debug,String topicPrefix, String version, String reqId, String payload, int qos){
        publishMessage(debug,topicPrefix,version,reqId,payload,qos,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_RETAINED,false),null);
    }

    /**
     * 4
     * @param topicPrefix
     * @param version
     * @param reqId
     * @param payload
     * @param qos
     * @param callback
     */
    protected void publishMessage(String topicPrefix, String version, String reqId, String payload, int qos, IResCallback callback){
        publishMessage(topicPrefix,version,reqId,payload,qos,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_RETAINED,false),callback);
    }

    /**
     * 5
     * @param topicPrefix
     * @param version
     * @param reqId
     * @param payload
     * @param qos
     * @param retained
     */
    protected void publishMessage(String topicPrefix, String version, String reqId, String payload, int qos,boolean retained){
        publishMessage(topicPrefix,version,reqId,payload,qos,retained,null);
    }

    /**
     * 6
     * @param topicPrefix
     * @param version
     * @param reqId
     * @param payload
     * @param qos
     * @param retained
     * @param callback
     */
    protected void publishMessage(final String topicPrefix, String version, final String reqId, final String payload, final int qos, final boolean retained, final IResCallback callback){
        publishMessage(false,topicPrefix,version,reqId,payload,qos,retained,callback);
    }

    /**
     *
     * @param debug
     * @param qos
     * @param retained
     * @param topicPrefix
     * @param version
     * @param reqId
     * @param clientId
     * @param message
     */
    public void response(boolean debug,int qos,boolean retained,String topicPrefix, String version, String reqId,String clientId, IMessage message){
        /**
         * @TODO
         */
        message.getPayload();
    }

    /**
     *
     * @param debug
     * @param qos
     * @param retained
     * @param topicPrefix
     * @param version
     * @param reqId
     * @param res
     * @param callback
     */
    public void response(boolean debug, int qos,boolean retained,String topicPrefix, String version,String reqId,String clientId, CmdRes res,IResCallback callback){
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        String payloadStr = JSonUtils.toString(res);
        response(debug,qos,retained,topicPrefix,version,reqId,clientId,payloadStr,callback);
    }

    /**
     * 响应服务端请求
     * @param debug
     * @param qos
     * @param retained
     * @param topicPrefix
     * @param biz
     * @param version
     * @param reqId
     * @param clientId
     * @param mode
     * @param code
     * @param message
     * @param heads
     */
    public void response(boolean debug,int qos,boolean retained,String topicPrefix, String biz, String version, String reqId,String clientId, String mode, Integer code, String message, Map<String,String> heads){
        CmdRes res = new CmdRes();
        res.setHeaders(heads);
        res.setCode(code);
        res.setMsg(message);
        res.setCmdStr(mode);
        IResCallback resCallback = CallbackRegistry.getInstance().getResCallback(biz.toLowerCase());
        response(debug,qos,retained,topicPrefix,version,reqId,clientId,res, resCallback);
    }

    /**
     * 响应服务端请求
     * @param debug
     * @param qos
     * @param retained
     * @param biz
     * @param topicPrefix
     * @param version
     * @param reqId
     * @param payload
     */
    public void response(boolean debug,int qos,boolean retained,String topicPrefix, String biz,String version,String reqId,String clientId, String payload){
        IResCallback callback = CallbackRegistry.getInstance().getResCallback(biz.toLowerCase());
        response(debug,qos,retained,topicPrefix,version,reqId,clientId,payload,callback);
    }

    /**
     * 响应服务端请求
     * @param debug
     * @param qos
     * @param retained
     * @param topicPrefix
     * @param version
     * @param reqId
     * @param clientId
     * @param payload
     * @param resCallback
     */
    public void response(boolean debug,int qos,boolean retained,String topicPrefix, String version,String reqId,String clientId, String payload,IResCallback resCallback){

        String md5 = MD5Utils.getInstance().getMD5String(payload);
        /**
         * [rmsg|rrpc|upld]/[res|req]/biz/version/reqId/md5/custid/storeid/zoneid/devId
         */
        String topic = buildTopic(topicPrefix,version,reqId,md5);

        publish(debug,reqId,topic,clientId,payload,qos,retained,resCallback);
    }
    /**
     *
     * @param debug
     * @param topicPrefix
     * @param version
     * @param reqId
     * @param payload
     * @param qos
     * @param retained
     * @param callback
     */
    protected void publishMessage(boolean debug,final String topicPrefix, String version, final String reqId, final String payload, final int qos, final boolean retained, final IResCallback callback){

        String md5 = MD5Utils.getInstance().getMD5String(payload);

        final String topic = buildTopic(topicPrefix,version,reqId,md5);
        if(topic.toLowerCase().startsWith("msg/req/devinfo")){
            regist(reqId,topic,payload,qos,retained);
        }
        else{
            publish(debug,reqId,topic,payload,qos,retained,callback);
        }
    }

    /**
     * upld/req|res/biz
     * @param topicPrefix [rmsg|rrpc|upld]/[res|req]/biz
     * @param version
     * @param reqId
     * @param md5
     * @return [rmsg|rrpc|upld]/[res|req]/biz/version/reqId/md5/custid/storeid/zoneid/devId
     */
    private String buildTopic(String topicPrefix,String version,String reqId,String md5){

        StringBuffer stringBuffer = new StringBuffer(topicPrefix).append("/").append(version).append("/").append(reqId).append("/").append(md5);

        stringBuffer.append("/").append(getCustId()).append("/").append(getStoreId()).append("/").append(getZoneId()).append("/").append(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        String topic = stringBuffer.toString();
        return topic;
    }

    private void regist(String reqId,String topStr, String payload, int qos, Boolean retained){
        synchronized (this){
            if(!registCalled){
                fixedThreadPool.execute(new AsyncRegistor(ContextHolder.getInstance().getContext(),reqId,topStr,payload,qos,retained));
                registCalled = true;
            }
        }
    }

    private void publish(boolean debug,String reqId,String payload,String topStr,Integer qos,boolean retained,IResCallback callback){

        if(ObjectUtil.isEmpty(callback)){
            NeuLogUtils.iTag(TAG,"没有设置IResCallback，走系统默认回调，日志输出回调结果");
            callback = defaultResCallback;
        }

        if(!neulinkServiceInited){
            if(callback!=null){
                Result result = Result.fail(STATUS_503,"SDK还没初始化完成");
                result.setReqId(reqId);
                result.setData(payload);
                callback.onFinished(result);
            }
        }
        else{
            fixedThreadPool.execute(new AsynPublisher(debug,reqId,payload,topStr,qos,retained,callback));
        }
    }

    private void publish(boolean debug,String reqId,String payload,String topStr,String clientId,Integer qos,boolean retained,IResCallback callback){

        if(ObjectUtil.isEmpty(callback)){
            NeuLogUtils.iTag(TAG,"没有设置IResCallback，走系统默认回调，日志输出回调结果");
            callback = defaultResCallback;
        }

        if(!neulinkServiceInited){
            if(callback!=null){
                Result result = Result.fail(STATUS_503,"SDK还没初始化完成");
                result.setReqId(reqId);
                result.setData(payload);
                callback.onFinished(result);
            }
        }
        else{
            fixedThreadPool.execute(new AsynPublisher(debug,reqId,payload,topStr,clientId,qos,retained,callback));
        }
    }

    public String getCustId(){
        String scopeId = ConfigContext.getInstance().getConfig(ConfigContext.SCOPEID,"1");
        return scopeId;
    }

    public String getStoreId(){
        String storeid = ConfigContext.getInstance().getConfig(ConfigContext.STOREID,"notimpl");
        return storeid;
    }

    public String getZoneId(){
        String zoneid = ConfigContext.getInstance().getConfig(ConfigContext.ZONEID,"notimpl");
        return zoneid;
    }

    public boolean isMqttConnSuccessed(){
        synchronized (mqttConnSuccessed) {
            return mqttConnSuccessed;
        }
    }
    public synchronized boolean isFailed(){
        synchronized (mqttConnSuccessed){
            return ObjectUtil.isNotEmpty(failException);
        }

    }

    public void setFailException(Throwable failException){
        synchronized (mqttConnSuccessed){
            if(ObjectUtil.isEmpty(failException)){
                mqttConnSuccessed = true;
            }
            else{
                mqttConnSuccessed = false;
            }
            this.failException = failException;
        }
    }

    public RegistCallback getRegistCallback() {
        return registCallback;
    }

    public List<IMqttCallBack> getMqttCallBacks() {
        return mqttCallBacks;
    }



    /**
     * 日志周期清理服务
     */
    class HouseKeeping extends Thread{
        public HouseKeeping(){
            super("HouseKeeping");
        }
        public void run(){
            while (!destroyed){
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
                            NeuLogUtils.iTag(TAG,file.getAbsolutePath()+"清除掉");
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
        private Context context;
        public AsyncRegistor(Context context,String reqId,String topStr, String payload, int qos, Boolean retained){
            this.context = context;
            this.reqId = reqId;
            this.topStr = topStr;
            this.payload = payload;
            String mode = ConfigContext.getInstance().getConfig(ConfigContext.TOPIC_MODE,ConfigContext.TOPIC_SHORT);
            if(ConfigContext.TOPIC_SHORT.equals(mode)){
                String topStrTemp = topStr;
                JsonObject jsonObject = JSonUtils.toObject(payload,JsonObject.class);
                /**
                 * 绑定Head
                 */
                HeadersUtil.registBinding(jsonObject,topStr,qos);
                this.payload = jsonObject.toString();
                String[] temps = topStrTemp.split("/");
                int len = temps.length;
                String group = null;
                String req$res = null;
                String biz = null;
                String version = null;
                if(len>0){
                    group = temps[0];
                }
                if(len>1){
                    req$res = temps[1];
                }
                if(len>2){
                    biz = temps[2];
                }
                if(len>3){
                    version = temps[3];
                }
                this.topStr = String.format("%s/%s/%s/%s",group,req$res,biz,version);
                String productId = deviceService.getProductKey();
                if(ObjectUtil.isNotEmpty(productId)){
                    this.topStr = productId+"/"+this.topStr;
                }
            }
            this.qos = qos;
            this.retained = retained;
        }

        @Override
        public void run() {

            RequestContext.setId(reqId);

            int channel = ConfigContext.getInstance().getConfig(ConfigContext.UPLOAD_CHANNEL,0);

            int trys = 1;

            NeuLogUtils.iTag(TAG,"开始建立Mqtt连接");

            while (!neulinkServiceInited){
                try{
                    /**
                     * MQTT机制
                     */
                    NeuLogUtils.iTag(TAG,"第"+trys+"次Mqtt连接");
                    if(mqttServiceUri ==null){
                        mqttServiceUri = ConfigContext.getInstance().getConfig(ConfigContext.MQTT_SERVER,"tcp://dev.neucore.com:1883");
                    }
                    String userName = ConfigContext.getInstance().getConfig(ConfigContext.MQTT_USERNAME,"admin");
                    String password = ConfigContext.getInstance().getConfig(ConfigContext.MQTT_PASSWORD,"password");
                    NeuLogUtils.iTag(TAG,"MQTT 初始化");
                    initMqttService(mqttServiceUri,userName,password);
                    neulinkServiceInited = true;
                }
                catch (MqttException ex){
                    int code = ex.getReasonCode();
                    NeuLogUtils.eTag(TAG,"MQTT 初始化异常",ex);
                    Result result = new Result();
                    result.setReqId(UUID.fastUUID().toString());
                    result.setCode(STATUS_403);
                    result.setMsg(ex.getMessage());
                    registCallback.onFinished(result);

                    if(code == MqttReturnCode.RETURN_CODE_SERVER_UNAVAILABLE
                            || code == MqttReturnCode.RETURN_CODE_UNSPECIFIED_ERROR
                            || code == MqttReturnCode.RETURN_CODE_SERVER_BUSY
                            || code == MqttReturnCode.RETURN_CODE_SERVER_SHUTTING_DOWN
                            || code == MqttReturnCode.RETURN_CODE_SERVER_MOVED
                            || code == MqttReturnCode.RETURN_CODE_KEEP_ALIVE_TIMEOUT
                            || code == MqttClientException.REASON_CODE_CLIENT_TIMEOUT
                            || code == MqttClientException.REASON_CODE_SERVER_CONNECT_ERROR
                            || code == MqttClientException.REASON_CODE_CLIENT_NOT_CONNECTED
                    ){
                        /**
                         * 未知错误、网络异常、服务器故障、服务重启中需要重试
                         */
                        resetMqttConnCalled();
                        continue;
                    }
                    else{
                        /**
                         * 非：未知错误、网络异常、服务器故障、服务重启中直接跳出循环
                         * eg：clientId非法
                         */
                        NeuLogUtils.eTag(TAG,"MQTT 初始化异常,跳出注册："+ ex.getMessage());
                        break;
                    }
                }
                finally {
                    if(!neulinkServiceInited){
                        try {
                            Thread.sleep(1000*trys);
                            if(trys<30){
                                trys++;
                            }
                        } catch (InterruptedException interruptedException) {
                        }
                    }
                }
            }

            NeuLogUtils.iTag(TAG,String.format("开始异步注册：neulinkServiceInited=%s,registed=%s",neulinkServiceInited,registed));

            trys = 1;
            while (neulinkServiceInited && !registed){
                try {

                    if(isMqttConnSuccessed()){
                        if(channel==0){
                            NeuLogUtils.iTag(TAG,"第"+trys+"次MQTT通道注册");
                            myMqttService.publish(false,reqId,payload,topStr, qos, retained,registCallback);
                            registed = true;
                        }
                        else{
                            Map<String,String> headers = HttpParamWrapper.getParams();
                            String response = null;
                            NeuLogUtils.iTag(TAG,"第"+trys+"次Http通道注册");
                            String registServer = ConfigContext.getInstance().getConfig(ConfigContext.HTTP_UPLOAD_SERVER,"https://dev.neucore.com/api/neulink/upload2cloud");
                            NeuLogUtils.dTag(TAG,"registServer："+registServer);

                            String topic = URLEncoder.encode(topStr,"UTF-8");
                            response = NeuHttpHelper.post(true,false,registServer+"?topic="+topic,payload,headers,10,60,1);

                            NeuLogUtils.dTag(TAG,"设备注册响应："+response);
                            getRegistCallback().onFinished(Result.ok());
                            registed = true;
                        }
                    }
                }
                catch (UnsupportedEncodingException ex){
                    NeuLogUtils.eTag(TAG,"注册失败",ex);
                    Result result = new Result();
                    result.setReqId(reqId);
                    result.setCode(STATUS_500);
                    result.setMsg(ex.getMessage());
                    registCallback.onFinished(result);
                }
                catch (NeulinkException e) {
                    NeuLogUtils.eTag(TAG,"注册失败",e);
                    Result result = new Result();
                    result.setReqId(reqId);
                    result.setCode(e.getCode());
                    result.setMsg(e.getMsg());
                    registCallback.onFinished(result);
                }
                finally {
                    if(!registed){
                        try {
                            Thread.sleep(1000*trys);
                            if(trys<30){
                                trys++;
                            }
                        } catch (InterruptedException interruptedException) {
                        }
                    }
                    else{
                        autoReporter = new NeulinkScheduledReport(context, instance);
                        autoReporter.start();
                    }
                }
            }
            RequestContext.removeId();
        }
    }

    /**
     * 异步发布器
     */
    class AsynPublisher implements Runnable{
        boolean debug;
        private String reqId;
        private String topStr;
        private String clientId;
        private String payload;
        private Integer qos;
        private Boolean retained;
        private IResCallback callback;
        private Map<String,String> headers;
        public AsynPublisher(boolean debug,String reqId, String topStr, String payload, int qos, Boolean retained, IResCallback callback){
            this(debug,reqId,topStr,null,payload,qos,retained,callback);
        }
        public AsynPublisher(boolean debug,String reqId, String topStr, String clientId,String payload, int qos, Boolean retained, IResCallback callback){
            this.debug = debug;
            this.reqId = reqId;
            this.topStr = topStr;
            this.clientId = clientId;
            this.payload = payload;
            this.payload = payload;
            String mode = ConfigContext.getInstance().getConfig(ConfigContext.TOPIC_MODE,ConfigContext.TOPIC_SHORT);
            if(ConfigContext.TOPIC_SHORT.equals(mode)){
                String topStrTemp = topStr;
                JsonObject jsonObject = JSonUtils.toObject(payload,JsonObject.class);
                /**
                 * 绑定Head
                 */
                HeadersUtil.binding(jsonObject,topStr,qos);
                this.payload = jsonObject.toString();
                String[] temps = topStrTemp.split("/");
                int len = temps.length;
                String group = null;
                String req$res = null;
                String biz = null;
                String version = null;
                if(len>0){
                    group = temps[0];
                }
                if(len>1){
                    req$res = temps[1];
                }
                if(len>2){
                    biz = temps[2];
                }
                if(len>3){
                    version = temps[3];
                }
                this.topStr = String.format("%s/%s/%s/%s",group,req$res,biz,version);
                if(ObjectUtil.isNotEmpty(clientId)){
                    this.topStr = this.topStr+"/"+clientId;
                }
                if(debug){
                    this.topStr = this.topStr+"/debug";
                }
                String productId = deviceService.getProductKey();
                if(ObjectUtil.isNotEmpty(productId)){
                    this.topStr = productId+"/"+this.topStr;
                }
            }

            this.qos = qos;
            this.retained = retained;
            this.callback = callback;
        }
        public AsynPublisher(boolean debug,String reqId, String topStr, String payload, int qos, Boolean retained,Map<String,String> headers, IResCallback callback){
            this.headers = headers;
        }
        @Override
        public void run() {

            RequestContext.setDebug(debug);
            RequestContext.setId(reqId);

            /**
             * End2Cloud
             */
            NeuLogUtils.dTag(TAG,"响应topic:"+topStr);
            NeuLogUtils.dTag(TAG,"设备upload2cloud请求："+payload);

            int channel = ConfigContext.getInstance().getConfig(ConfigContext.UPLOAD_CHANNEL,0);
            if(channel==0){

                myMqttService.publish(debug,reqId,payload,topStr, qos, retained,callback);
            }
            else{
                /**
                 * HTTP机制
                 */
                httpServiceUri = ConfigContext.getInstance().getConfig(ConfigContext.HTTP_UPLOAD_SERVER, httpServiceUri);
                Boolean done = false;
                int count = 0;
                while(!done && count<3){
                    try {
                        String topicStr = URLEncoder.encode(topStr,"UTF-8");
                        Map<String,String> params = HttpParamWrapper.getParams();
                        String response = NeuHttpHelper.post(true,debug,httpServiceUri +"?topic="+topicStr,payload,params,10,60,1);
                        NeuLogUtils.dTag(TAG,"设备upload2cloud响应："+response);
                        if(ObjectUtil.isNotEmpty(callback)){
                            Result result = JSonUtils.toObject(response,Result.class);
                            result.setReqId(reqId);
                            callback.onFinished(result);
                            NeuLogUtils.dTag(TAG,"onFinished");
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
                                    NeuLogUtils.iTag(TAG,"token过期，重新登录成功");
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
                                NeuLogUtils.eTag(TAG,"没有实现ILoginCallback");
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
                        NeuLogUtils.dTag(TAG,"upload2cloud error with: "+e.getMessage());
                        if(ObjectUtil.isNotEmpty(callback)){
                            Result result = Result.fail(STATUS_500,e.getMessage());
                            result.setReqId(reqId);
                            callback.onFinished(result);
                        }
                        done = true;
                    }
                }
            }
            RequestContext.removeId();
        }
    }
}
