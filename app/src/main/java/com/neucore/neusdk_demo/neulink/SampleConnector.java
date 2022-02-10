package com.neucore.neusdk_demo.neulink;

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
import com.neucore.neulink.IOnNetStatusListener;
import com.neucore.neulink.IUserService;
import com.neucore.neulink.app.CarshHandler;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.LogService;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.impl.NetBroadcastReceiver;
import com.neucore.neulink.impl.service.OnNetStatusListener;
import com.neucore.neulink.util.ContextHolder;

import java.util.Properties;

public class SampleConnector {
    private String TAG = "SampleConnector";
    private Application application;
    private IUserService userService;
    private IExtendCallback callback;
    private Properties extConfig;

    public SampleConnector(Application application, IExtendCallback callback, IUserService service){
        this(application,callback,service,new Properties());
    }

    public SampleConnector(Application application, IExtendCallback callback, IUserService service, Properties extConfig){
        this.application = application;
        this.callback = callback;
        this.userService = service;
        this.extConfig = extConfig;

        init();
    }
    private void init(){

        /**
         * 注册扩展实现
         */
        Log.i(TAG,"success regist extend implmention");
        this.callback.onCallBack();

        /**
         * 集成Neulink
         */
        ContextHolder.getInstance().setContext(application);

        //处理初始化应用carsh
        CarshHandler crashHandler = CarshHandler.getIntance();
        crashHandler.init();
        Log.i(TAG,"success regist crashHandler");

        /**
         * 加载人脸到内存
         */
        userService.load();
        Log.i(TAG,"success load user info 2 mem");

        ConfigContext.getInstance().setExtConfig(extConfig);

        /**
         * 异步开启LogService
         */
        new LogServiceThread().start();

        NetBroadcastReceiver netBroadcastReceiver = new NetBroadcastReceiver();
        NetBroadcastReceiver.setOnNetListener(new OnNetStatusListener());
        registerReceiver(netBroadcastReceiver);

        /**
         * 初始化MQTT
         */
        long start = System.currentTimeMillis();
        deviceMqttServiceInit();
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
    Handler tHandler = new Handler(new Handler.Callback() {
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

    class LogServiceThread extends Thread {
        public void run() {
            Looper.prepare();
            tHandler.sendEmptyMessage(1);
            Looper.loop();  //looper开始处理消息。
        }
    }

    private NeulinkService deviceMqttServiceInit(){

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        NeulinkService service = NeulinkService.getInstance();
        service.buildMqttService(ConfigContext.getInstance().getConfig(ConfigContext.MQTT_SERVER));//tcp://10.18.9.99:1883"));
        return service;
    }
}
