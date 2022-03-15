package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IPublishCallback;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.extend.MyPublishListener;

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
    private String serverUrl = "";
    private String userName = "admin";
    private String passWord = "password";
    private String clientId = "";
    private int timeOut = 10;
    private int keepAliveInterval = 60;
    private boolean retained = false;
    //客户端掉线后是否清楚客户端session
    private boolean cleanSession = false;
    private boolean close = false;
    private boolean disconnect = false;
    private boolean autoReconnect = true;
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
        this.timeOut = builder.timeOut;
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
        private String userName = "admin";
        private String passWord = "password";
        private String clientId;
        private int timeOut = 10;
        private int keepAliveInterval = 10;
        private boolean retained = false;
        private boolean cleanSession = false;
        private boolean autoReconnect = false;
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

        public Builder timeOut(int timeOut) {
            this.timeOut = timeOut;
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
    public void publish(String reqId,String msg, String topic, int qos, boolean retained, Context context, IPublishCallback iPublishCallback) {
        try {
            if(ObjectUtil.isNotEmpty(iPublishCallback)){
                MyPublishListener myPublishAction = new MyPublishListener(reqId,iPublishCallback);
                client.publish(topic, msg.getBytes(), qos, retained,context,myPublishAction);
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
            // 设置超时时间，单位：秒
            conOpt.setConnectionTimeout(timeOut);
            // 心跳包发送间隔，单位：秒
            conOpt.setKeepAliveInterval(keepAliveInterval);
            // 用户名
            conOpt.setUserName(ConfigContext.getInstance().getConfig(ConfigContext.USERNAME, "admin"));
            // 密码
            conOpt.setPassword(ConfigContext.getInstance().getConfig(ConfigContext.PASSWORD, "password").toCharArray());
            conOpt.setAutomaticReconnect(autoReconnect);
            // 监控Client的状态 $share/{ShareName}/{filter}
            String sccperId = ConfigContext.getInstance().getConfig("ScopeId", "yeker");
            String payload = "{\"dev_id\":\""+clientId+"\",\"status\":-1}";
            conOpt.setWill("msg/req/lwt/v1.0/" + sccperId + "/" + clientId, payload.getBytes(), 1, true);
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
