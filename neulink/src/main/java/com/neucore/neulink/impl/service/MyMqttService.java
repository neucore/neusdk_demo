package com.neucore.neulink.impl.service;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IResCallback;
import com.neucore.neulink.NeulinkConst;
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
    private Boolean retained;
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
        this.retained = builder.retained;
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
        private Boolean retained;
        private Boolean cleanSession;
        private Boolean autoReconnect;

        private MqttCallback mqttCallback = null;
        private IMqttActionListener mqttActionListener;
        private IMqttMessageListener mqttMessageListener;

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

        public Builder retained(boolean retained) {
            this.retained = retained;
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

        public Builder mqttMessageListener(IMqttMessageListener mqttMessageListener){
            this.mqttMessageListener = mqttMessageListener;
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
            Log.e(TAG, "publish: "+e.toString(),e);
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
            Log.i(TAG,String.format("init ClientId: %s",clientId));
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
            conOpt.setUserName(userName);
            // 密码
            conOpt.setPassword(passWord.toCharArray());
            conOpt.setAutomaticReconnect(autoReconnect);
            // 监控Client的状态 $share/{ShareName}/{filter}
            String sccperId = ConfigContext.getInstance().getConfig("ScopeId", "yeker");
            LWTInfo info = ServiceRegistry.getInstance().getDeviceService().lwt();
            String payload = info.getPayload();
            conOpt.setWill(info.getTopicPrefix()+"/" + sccperId + "/" + clientId, payload.getBytes(), 1, true);
//        conOpt.setWill("$share/will_test/"+sccperId+"/"+clientId+"/MQTT/DISCONNECT","1".getBytes(),1,true);
        }
        catch (MqttException ex){
            Log.e(TAG,"MQTT Init failed",ex);
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
                Log.i(TAG,"MQTT Closed");
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void disconnect(){
        try {
            if(!disconnect && !ObjectUtil.isEmpty(client)){
                client.disconnect();
                close();
                disconnect = true;
                Log.i(TAG,"MQTT Disconnect");
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
                Log.i(TAG,String.format("connect by %s",clientId));
                client.connect(conOpt, null, mqttActionListener);
                Log.i(TAG,"connected");
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    public void subscribe(String topic, int qos,IMqttMessageListener mqttMessageListener) {
        try {
            // 订阅topic话题
            Log.i(TAG, "execute subscribe -- topic = " + topic + ",qos = " + qos);
            client.subscribe(topic, qos,mqttMessageListener);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * 判断连接是否断开
     */
    public boolean isConnected() {
        try {
            return client.isConnected();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return false;
    }

}
