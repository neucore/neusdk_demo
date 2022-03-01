package com.neucore.neulink.extend;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.neucore.neulink.IExtendCallback;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.IMessageService;
import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.IUserService;
import com.neucore.neulink.app.CarshHandler;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.LogService;
import com.neucore.neulink.impl.NetBroadcastReceiver;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.impl.service.OnNetStatusListener;
import com.neucore.neulink.impl.service.device.IDeviceService;
import com.neucore.neulink.util.ContextHolder;

import java.util.Properties;

public class SampleConnector {
    private String TAG = NeulinkConst.TAG_PREFIX+"SampleConnector";
    private Application application;
    private NeulinkService neulinkService;
    private IUserService userService;
    private IMessageService messageService;
    private ILoginCallback loginCallback;
    private IDeviceService deviceService;
    private IMqttCallBack mqttCallBack;
    private IExtendCallback extendCallback;
    private Properties extConfig;
    private Handler tHandler;
    private Boolean started = false;

    @Deprecated
    public SampleConnector(Application application, IExtendCallback callback, IUserService service){
        this(application,callback,service,new Properties());
    }

    @Deprecated
    public SampleConnector(Application application, IExtendCallback callback, IUserService service, Properties extConfig){
        this(application,service,extConfig,null,callback);
    }

    @Deprecated
    public SampleConnector(Application application, IUserService service, Properties extConfig, ILoginCallback loginCallback, IExtendCallback callback){
        this.loginCallback = loginCallback;
        this.application = application;
        this.extendCallback = callback;
        this.userService = service;
        this.extConfig = extConfig;
        start();
    }

    public SampleConnector(Application application,Properties extConfig){
        this.application = application;
        this.extConfig = extConfig;
    }


    public void setLoginCallback(ILoginCallback loginCallback) {
        this.loginCallback = loginCallback;
    }

    public void setMqttCallBack(IMqttCallBack mqttCallBack) {
        this.mqttCallBack = mqttCallBack;
    }

    public void setExtendCallback(IExtendCallback callback) {
        this.extendCallback = callback;
    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    public void setDeviceService(IDeviceService deviceService){
        this.deviceService = deviceService;
    }

    public void setMessageService(IMessageService messageService){
        this.messageService = messageService;
    }

    /**
     * 必须先设置相关属性，然后再调用start
     * 不然不起效果
     */
    public void start(){
        if(!started){
            ServiceFactory.getInstance().setLoginCallback(loginCallback);
            ServiceFactory.getInstance().setUserService(userService);
            ServiceFactory.getInstance().setDeviceService(deviceService);
            ServiceFactory.getInstance().setMessageService(messageService);
            init();
        }
        started = true;
    }

    private void init(){

        /**
         * 注册扩展实现
         */
        if(extendCallback!=null){
            this.extendCallback.onCallBack();
            Log.i(TAG,"success regist extend implmention");
        }
        else{
            Log.i(TAG,"success regist 默认 implmention");
        }

        /**
         * 集成Neulink
         */
        ContextHolder.getInstance().setContext(application);

        /**
         * 异步开启LogService
         */
        if(tHandler==null){
            tHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    Intent intent = new Intent(application, LogService.class);
                    Log.i(TAG,"Build.VERSION.SDK_INT:"+ Build.VERSION.SDK_INT);
                    Log.i(TAG,"Build.VERSION_CODES.O:"+Build.VERSION_CODES.O);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Log.i(TAG,"startForegroundService");
                        application.startForegroundService(intent);
                    } else {
                        Log.i(TAG,"startService");
                        application.startService(intent);
                    }
                    Log.i(TAG,"success startLogService");
                    return true;
                }
            });
        }

        LogServiceThread logServiceThread = new LogServiceThread();

        logServiceThread.start();

        //处理初始化应用carsh
        CarshHandler crashHandler = CarshHandler.getIntance();
        crashHandler.init();
        Log.i(TAG,"success regist crashHandler");

        /**
         * 加载人脸到内存
         */
        if(ServiceFactory.getInstance().getUserService()!=null){
            ServiceFactory.getInstance().getUserService().load();
            Log.i(TAG,"success load user info 2 mem");
        }

        ConfigContext.getInstance().setExtConfig(extConfig);

        NetBroadcastReceiver netBroadcastReceiver = new NetBroadcastReceiver();
        NetBroadcastReceiver.setOnNetListener(new OnNetStatusListener());
        registerReceiver(netBroadcastReceiver);

        /**
         * 初始化MQTT
         */
        long start = System.currentTimeMillis();
        neulinkService = deviceMqttServiceInit();
        Log.i(TAG,"success start Mqtt service timeused: "+(System.currentTimeMillis()-start));
    }
    /**
     * 网络恢复事件侦听器
     * @param receiver
     */
    private void registerReceiver(BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        LocalBroadcastManager.getInstance(ContextHolder.getInstance().getContext()).registerReceiver(receiver, filter);
    }

    class LogServiceThread extends Thread {
        public LogServiceThread(){
        }
        public void run() {
            Looper.prepare();
            tHandler.sendEmptyMessage(1);
            Looper.loop();  //looper开始处理消息。
        }
    }

    private NeulinkService deviceMqttServiceInit(){

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        NeulinkService service = NeulinkService.getInstance();
        service.buildMqttService(ConfigContext.getInstance().getConfig(ConfigContext.MQTT_SERVER));//tcp://10.18.9.99:1883"));
        if(mqttCallBack!=null){
            service.addMQTTCallBack(mqttCallBack);
        }
        return service;
    }
}
