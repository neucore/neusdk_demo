package com.neucore.neulink.impl;

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

import com.neucore.neulink.log.LogUtils;
import com.neucore.neulink.IExtendCallback;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.IMessageService;
import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.IResCallback;
import com.neucore.neulink.app.CarshHandler;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.impl.service.LogService;
import com.neucore.neulink.impl.service.OnNetStatusListener;
import com.neucore.neulink.impl.service.device.DefaultDeviceServiceImpl;
import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.impl.service.resume.IFileService;
import com.neucore.neulink.util.ContextHolder;

import java.util.Properties;

import cn.hutool.core.util.ObjectUtil;

public class SampleConnector implements NeulinkConst{
    private String TAG = TAG_PREFIX+"SampleConnector";
    private Application application;
    private IMessageService messageService;
    private ILoginCallback loginCallback;
    private IDeviceService deviceService = new DefaultDeviceServiceImpl();
    private IExtendCallback defaultExtendCallback = new DefaultExtendCallback();
    private IMqttCallBack mqttCallBack;
    private IExtendCallback extendCallback;
    private IFileService fileService;
    private IResCallback defaultResCallback;

    private Properties extConfig;
    private Handler tHandler;
    private Boolean started = false;
    private boolean networkReady = false;
    private boolean initMqttService = false;
    private boolean mqttServiceReady =false;

    @Deprecated
    public SampleConnector(Application application, IExtendCallback callback){
        this(application,callback,new Properties());
    }

    @Deprecated
    public SampleConnector(Application application, IExtendCallback callback, Properties extConfig){
        this(application,extConfig,null,callback);
    }

    @Deprecated
    public SampleConnector(Application application, Properties extConfig, ILoginCallback loginCallback, IExtendCallback callback){
        this.loginCallback = loginCallback;
        this.application = application;
        this.extendCallback = callback;
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

    public void setDeviceService(IDeviceService deviceService){
        this.deviceService = deviceService;
    }

    public void setMessageService(IMessageService messageService){
        this.messageService = messageService;
    }

    public void setFileService(IFileService fileService) {
        this.fileService = fileService;
    }

    public void setDefaultResCallback(IResCallback defaultResCallback) {
        this.defaultResCallback = defaultResCallback;
    }

    /**
     * 必须先设置相关属性，然后再调用start
     * 不然不起效果
     */
    public void start(){

        if(!started){

            NetBroadcastReceiver netBroadcastReceiver = new NetBroadcastReceiver();

            NetBroadcastReceiver.setOnNetListener(new OnNetStatusListener());

            registerNetworkReceiver(netBroadcastReceiver);

            NeulinkService service = NeulinkService.getInstance();

            ServiceRegistry.getInstance().setLoginCallback(loginCallback);

            if(ObjectUtil.isNotEmpty(deviceService)){
                ServiceRegistry.getInstance().setDeviceService(deviceService);
            }
            ServiceRegistry.getInstance().setMessageService(messageService);
            ServiceRegistry.getInstance().setFileService(fileService);
            ConfigContext.getInstance().setExtConfig(extConfig);

            if(mqttCallBack!=null){
                service.addMQTTCallBack(mqttCallBack);
            }
            if(defaultResCallback!=null){
                service.setDefaultResCallback(defaultResCallback);
            }
            /**
             * 默认实现
             */
            defaultExtendCallback.onCallBack();
            /**
             * 注册扩展实现
             */
            if(extendCallback!=null){
                this.extendCallback.onCallBack();
                LogUtils.iTag(TAG,"success regist extend implmention");
            }
            else{
                LogUtils.iTag(TAG,"success regist 默认 implmention");
            }
            init(service);
        }
        started = true;
    }

    private void init(NeulinkService service){

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
                    LogUtils.iTag(TAG,"Build.VERSION.SDK_INT:"+ Build.VERSION.SDK_INT);
                    LogUtils.iTag(TAG,"Build.VERSION_CODES.O:"+Build.VERSION_CODES.O);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LogUtils.iTag(TAG,"startForegroundService");
                        application.startForegroundService(intent);
                    } else {
                        LogUtils.iTag(TAG,"startService");
                        application.startService(intent);
                    }
                    LogUtils.iTag(TAG,"success startLogService");
                    return true;
                }
            });
        }

        LogServiceThread logServiceThread = new LogServiceThread();

        logServiceThread.start();

        //处理初始化应用carsh
        CarshHandler crashHandler = CarshHandler.getIntance();
        crashHandler.init();
        LogUtils.iTag(TAG,"success regist crashHandler");

        /**
         * 初始化MQTT
         */
        long start = System.currentTimeMillis();

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        service.init();

        LogUtils.iTag(TAG,"success start Mqtt service timeused: "+(System.currentTimeMillis()-start));
    }
    /**
     * 网络恢复事件侦听器
     * @param receiver
     */

    private void registerNetworkReceiver(BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        /**
         * 网络恢复事件侦听器
         */
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        LocalBroadcastManager.getInstance(application).registerReceiver(receiver, filter);
    }

    private void registerMqttServiceReceiver(BroadcastReceiver receiver) {

        IntentFilter filter = new IntentFilter();
        /**
         * 网络恢复事件侦听器
         */
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        /**
         * MqttService事件侦听器
         */
        filter.addAction("MyMqttService.callbackToActivity.v0");

        LocalBroadcastManager.getInstance(application).registerReceiver(receiver, filter);
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
}
