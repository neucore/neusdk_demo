package com.neucore.neulink.impl.service;

import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.neucore.neulink.IResCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.impl.LWTInfo;
import com.neucore.neulink.impl.adapter.MqttActionListenerAdapter;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.util.ContextHolder;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import cn.hutool.core.util.ObjectUtil;

public class MyMqttService implements NeulinkConst{

    private final String TAG = TAG_PREFIX+"MyMqttService";
    private boolean canDoConnect = true;

    private MqttAsyncClient client;
    private MqttConnectOptions conOpt;

    private Context context;
    private String serverUrl;
    private String userName;
    private String passWord;
    private String clientId;
    private Integer connectTimeout;
    private Integer executorServiceTimeout;
    private Integer keepAliveInterval;
    //客户端掉线后是否清楚客户端session
    private Boolean cleanSession;
    private Boolean autoReconnect;

    private boolean close = false;
    private boolean disconnect = false;
    private MqttCallback mqttCallback = null;
    private IMqttActionListener mqttActionListener;

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
        this.mqttCallback = builder.mqttCallback;
        this.mqttActionListener = builder.mqttActionListener;
        init();
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

        private MqttCallback mqttCallback = null;
        private IMqttActionListener mqttActionListener;

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

        public Builder mqttActionListener(IMqttActionListener mqttActionListener){
            this.mqttActionListener = mqttActionListener;
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
    public void publish(String reqId,String msg, String topic, int qos, boolean retained, IResCallback iResCallback) {
        try {
            if(ObjectUtil.isNotEmpty(iResCallback)){
                MqttActionListenerAdapter myPublishAction = new MqttActionListenerAdapter(reqId, iResCallback);
                client.publish(topic, msg.getBytes(), qos, retained, ContextHolder.getInstance().getContext(),myPublishAction);
            }
            else{
                client.publish(topic, msg.getBytes(), qos, retained);
            }

        } catch (Exception e) {
            LogUtils.eTag(TAG, "publish: "+e.toString(),e);
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

    private MqttAndroidClient androidClient;

    private void init() {
        // 服务器地址（协议+地址+端口号）
        MemoryPersistence memoryPersistence = new MemoryPersistence();
        try {
            LogUtils.iTag(TAG,String.format("init ClientId: %s",clientId));
            client = new MqttAsyncClient(serverUrl, clientId, memoryPersistence);
            // 设置MQTT监听并且接受消息
            client.setCallback(mqttCallback);

            conOpt = new MqttConnectOptions();
            // 清除缓存
            conOpt.setCleanSession(cleanSession);
            // 设置连接超时时间，单位：秒
            conOpt.setConnectionTimeout(connectTimeout);
            //设置执行服务超时时间，单位秒
            conOpt.setExecutorServiceTimeout(executorServiceTimeout);
            // 心跳包发送间隔，单位：秒
            conOpt.setKeepAliveInterval(keepAliveInterval);
            // 用户名
            if(ObjectUtil.isEmpty(userName)){
                throw new NeulinkException(408,"用户名为空");
            }
            conOpt.setUserName(userName);
            // 密码
            if(ObjectUtil.isEmpty(passWord)){
                throw new NeulinkException(408,"密码为空");
            }
            conOpt.setPassword(passWord.toCharArray());
            // 自动重连
            conOpt.setAutomaticReconnect(autoReconnect);
            /**
             *
             */
            conOpt.setMaxReconnectDelay(Integer.MAX_VALUE);
            // 监控Client的状态 $share/{ShareName}/{filter}
            String sccperId = ConfigContext.getInstance().getConfig("ScopeId", "yeker");
            LWTInfo info = ServiceRegistry.getInstance().getDeviceService().lwt();
            String payload = info.getPayload();
            int qos = ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,info.getQos());
            boolean retained = ConfigContext.getInstance().getConfig(ConfigContext.MQTT_RETAINED,info.getRetained());
            conOpt.setWill(info.getTopicPrefix()+"/" + sccperId + "/" + clientId, payload.getBytes(), qos, retained);
            LogUtils.iTag(TAG,String.format("end init: %s",clientId));
//        conOpt.setWill("$share/will_test/"+sccperId+"/"+clientId+"/MQTT/DISCONNECT","1".getBytes(),1,true);
        }
        catch (MqttException ex){
            LogUtils.eTag(TAG,"MQTT Init failed",ex);
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
                LogUtils.iTag(TAG,"MQTT Closed");
            }
        } catch (Exception e) {
            LogUtils.eTag(TAG, e.toString());
        }
    }

    public void disconnect(){
        try {
            if(!disconnect && !ObjectUtil.isEmpty(client)){
                client.disconnect();
                close();
                disconnect = true;
                LogUtils.iTag(TAG,"MQTT Disconnect");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    /**
     * 连接MQTT服务器
     */
    public void connect() {
        if (canDoConnect && !client.isConnected()) {
            try {
                LogUtils.iTag(TAG,String.format("connect by %s",clientId));
                client.connect(conOpt, null, mqttActionListener);
                LogUtils.iTag(TAG,"connected");
            } catch (Exception e) {
                LogUtils.eTag(TAG, e.toString());
            }
        }
    }

    public void subscribe(String topic, int qos,IMqttMessageListener mqttMessageListener) {
        try {
            // 订阅topic话题
            LogUtils.iTag(TAG, "execute subscribe -- topic = " + topic + ",qos = " + qos);
            client.subscribe(topic, qos,mqttMessageListener);
        } catch (Exception e) {
            LogUtils.eTag(TAG, e.toString());
        }
    }

    /**
     * 判断连接是否断开
     */
    public boolean isConnected() {
        try {
            return client.isConnected();
        } catch (Exception e) {
            LogUtils.eTag(TAG, e.toString());
        }
        return false;
    }

}
