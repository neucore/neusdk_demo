package com.neucore.neulink.impl.service;

import android.content.Context;

import com.neucore.neulink.IResCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.impl.adapter.PublishActionListenerAdapter;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.MessageUtil;

import org.eclipse.paho.mqttv5.client.MqttActionListener;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import java.nio.charset.StandardCharsets;

import cn.hutool.core.util.ObjectUtil;

public class MyMqttService implements NeulinkConst{

    private final String TAG = TAG_PREFIX+"MyMqttService";
    private MqttAsyncClient client;
    private MqttConnectionOptions conOpt;

    private Context context;
    private String serverUrl;
    private String userName;
    private String passWord;
    private String clientId;
    private Integer connectTimeout;
    private Integer executorServiceTimeout;
    private Integer keepAliveInterval;
    private Integer maxReconnectDelay;
    //客户端掉线后是否清楚客户端session
    private Boolean cleanSession;
    private Boolean autoReconnect;

    private boolean close = false;
    private boolean disconnect = false;
    private MqttCallback mqttCallback = null;
    private MqttActionListener mqttActionListener;

    /**
     * builder设计模式
     *
     * @param builder
     */
    private MyMqttService(Builder builder) {
        this.context = builder.context;
        this.serverUrl = builder.serverUrl;
        this.userName = builder.userName;
        this.passWord = builder.passWord;
        this.clientId = builder.clientId;
        this.connectTimeout = builder.connectTimeout;
        this.executorServiceTimeout = builder.executorServiceTimeout;
        this.keepAliveInterval = builder.keepAliveInterval;
        this.cleanSession = builder.cleanSession;
        this.autoReconnect = builder.autoReconnect;
        this.maxReconnectDelay = builder.maxReconnectDelay;
        this.mqttCallback = builder.mqttCallback;
        this.mqttActionListener = builder.mqttActionListener;
        init();
    }
    public String toString(){
        return String.format("clientId=%s,\nserverUrl=%s,\nuserName=%s,\npassWord=%s,\ncleanSession=%s,\nautoReconnect=%s,\nmaxReconnectDelay=%s,\nconnectTimeout=%s,\nkeepAliveInterval=%s",clientId,serverUrl,userName,passWord,cleanSession,autoReconnect,maxReconnectDelay,connectTimeout,keepAliveInterval);
    }
    /**
     * Builder 构造类
     */
    public static final class Builder {

        private Context context;
        private String serverUrl;
        private String userName;
        private String passWord;
        private String clientId;
        private Integer connectTimeout;
        private Integer executorServiceTimeout;
        private Integer keepAliveInterval;
        private Boolean cleanSession;
        private Boolean autoReconnect;
        private Integer maxReconnectDelay;

        private MqttCallback mqttCallback = null;
        private MqttActionListener mqttActionListener;

        public Builder serverUrl(String serverUrl) {
            this.serverUrl = serverUrl;
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder passWord(String passWord) {
            this.passWord = passWord;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder executorServiceTimeout(int executorServiceTimeout){
            this.executorServiceTimeout = executorServiceTimeout;
            return this;
        }

        public Builder keepAliveInterval(int keepAliveInterval) {
            this.keepAliveInterval = keepAliveInterval;
            return this;
        }

        public Builder autoReconnect(boolean autoReconnect) {
            this.autoReconnect = autoReconnect;
            return this;
        }

        public Builder cleanSession(boolean cleanSession) {
            this.cleanSession = cleanSession;
            return this;
        }

        public Builder mqttCallback(MqttCallback mqttCallback){
            this.mqttCallback = mqttCallback;
            return this;
        }

        public Builder mqttActionListener(MqttActionListener mqttActionListener){
            this.mqttActionListener = mqttActionListener;
            return this;
        }

        public Builder maxReconnectDelay(Integer maxReconnectDelay){
            this.maxReconnectDelay = maxReconnectDelay;
            return this;
        }

        public MyMqttService bulid(Context context) {
            this.context = context;
            return new MyMqttService(this);
        }
    }


    /**
     * 发布消息
     *
     * @param msg
     * @param topic
     * @param qos
     * @param retained
     */
    public void publish(boolean debug,String reqId, String msg, String topic, int qos, boolean retained, IResCallback iResCallback) {
        try {

            byte[] compress= MessageUtil.encode(debug,topic,msg);
            if(ObjectUtil.isNotEmpty(iResCallback)){
                PublishActionListenerAdapter myPublishAction = new PublishActionListenerAdapter(reqId,msg, iResCallback);
                client.publish(topic, compress, qos, retained, ContextHolder.getInstance().getContext(),myPublishAction);
            }
            else{
                client.publish(topic, compress, qos, retained);
            }

        } catch (Exception e) {
            NeuLogUtils.eTag(TAG, "publish: "+e.toString(),e);
        }
    }

    /**
     * 获取mqtt客户端
     *
     * @return
     */
    public MqttAsyncClient getMqttClient() {
        return client;
    }

    private void init() {
        // 服务器地址（协议+地址+端口号）
        MemoryPersistence memoryPersistence = new MemoryPersistence();
        try {
            NeuLogUtils.iTag(TAG,String.format("init ClientId: %s",clientId));

            client = new MqttAsyncClient(serverUrl, clientId, memoryPersistence);

            conOpt = new MqttConnectionOptions();

            String[] serverUrls = serverUrl.split(",");
            if(serverUrls.length>1){
                conOpt.setServerURIs(serverUrls);
            }

            // 设置MQTT监听并且接受消息
            client.setCallback(mqttCallback);
            /**
             * mqtt 5.0 lwt 消息必须设置sessionExpiryInterval，否则不会触发 lwt事件
             */
            conOpt.setSessionExpiryInterval(0L);
            // 清除缓存
            conOpt.setCleanStart(cleanSession);
            // 设置连接超时时间，单位：秒
            conOpt.setConnectionTimeout(connectTimeout);
            /**
             * 自动连接延时after first
             */
            conOpt.setAutomaticReconnectDelay(1,4);
            //设置执行服务超时时间，单位秒
            conOpt.setExecutorServiceTimeout(executorServiceTimeout);
            // 心跳包发送间隔，单位：秒
            conOpt.setKeepAliveInterval(keepAliveInterval);
            // 用户名
            if(ObjectUtil.isEmpty(userName)){
                throw new NeulinkException(STATUS_402,"用户名为空");
            }
            conOpt.setUserName(userName);
            // 密码
            if(ObjectUtil.isEmpty(passWord)){
                throw new NeulinkException(STATUS_402,"密码为空");
            }
            conOpt.setPassword(passWord.getBytes(StandardCharsets.UTF_8));
            // 自动重连
            conOpt.setAutomaticReconnect(autoReconnect);
            //最大重连间隔
            conOpt.setMaxReconnectDelay(maxReconnectDelay);

            // 监控Client的状态 $share/{ShareName}/{filter}
            String sccperId = ConfigContext.getInstance().getConfig("ScopeId", "yeker");
            LWTTopic lwtTopic = ServiceRegistry.getInstance().getDeviceService().lwtTopic();
            LWTPayload payloadInfo = ServiceRegistry.getInstance().getDeviceService().lwtPayload();

            payloadInfo.setHeader("version","v1.0");
            payloadInfo.setHeader("biz","lwt");
            payloadInfo.setHeader("devid", ServiceRegistry.getInstance().getDeviceService().getExtSN());
            payloadInfo.setHeader("custid", NeulinkService.getInstance().getCustId());
            payloadInfo.setHeader("storeid",NeulinkService.getInstance().getStoreId());
            payloadInfo.setHeader("zoneid",NeulinkService.getInstance().getZoneId());

            String payload = JSonUtils.toString(payloadInfo);
            int qos = ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,lwtTopic.getQos());
            boolean retained = ConfigContext.getInstance().getConfig(ConfigContext.MQTT_RETAINED,lwtTopic.getRetained());
            byte[] encoded = MessageUtil.encode(false,lwtTopic.getTopic(),payload);
            MqttMessage mqttMessage = new MqttMessage(encoded,qos,retained,null);
            conOpt.setWill(lwtTopic.getTopic(),mqttMessage);
            NeuLogUtils.iTag(TAG,String.format("end init with : \n%s",toString()));
        }
        catch (MqttException ex){
            NeuLogUtils.eTag(TAG,"MQTT Init failed",ex);
        }
    }

    /**
     * 关闭客户端
     */
    public void close() {
        try {
            if(!close && !ObjectUtil.isEmpty(client)){
//                client.unregisterResources();
                client.close();
                close = true;
                NeuLogUtils.iTag(TAG,"MQTT Closed");
            }
        } catch (Exception e) {
            NeuLogUtils.eTag(TAG, e.toString());
        }
    }

    public void destory(){
        try {
            if(!disconnect && !ObjectUtil.isEmpty(client)){
                client.disconnect();
                close();
                disconnect = true;
                NeuLogUtils.iTag(TAG,"MQTT Disconnect");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    /**
     * 连接MQTT服务器
     */
    public void connect() throws MqttException{
        if (!client.isConnected()) {
            try {
                NeuLogUtils.iTag(TAG,String.format("connect by %s",clientId));
                client.connect(conOpt, null, mqttActionListener);
                NeuLogUtils.iTag(TAG,"connected");
            }
            catch (MqttException ex){
                throw ex;
            }
        }
    }

    public void subscribe(String topic, int qos, MqttActionListener mqttMessageListener) {
        try {
            // 订阅topic话题
            NeuLogUtils.iTag(TAG, "execute subscribe -- topic = " + topic + ",qos = " + qos);
            client.subscribe(topic, qos,context,mqttMessageListener);
        } catch (Exception e) {
            NeuLogUtils.eTag(TAG, e.toString());
        }
    }

    public void subscribe(String[] topics, int[] qoss,MqttActionListener mqttMessageListeners) {
        try {
            // 订阅topic话题
            client.subscribe(topics, qoss,context,mqttMessageListeners);
        } catch (Exception e) {
            NeuLogUtils.eTag(TAG, e.toString());
        }
    }

    /**
     * 判断连接是否断开
     */
    public boolean isConnected() {
        try {
            return client.isConnected();
        } catch (Exception e) {
            NeuLogUtils.eTag(TAG, e.toString());
        }
        return false;
    }

}
