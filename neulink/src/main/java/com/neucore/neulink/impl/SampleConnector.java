package com.neucore.neulink.impl;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.IDownloder;
import com.neucore.neulink.IExtendCallback;
import com.neucore.neulink.IFileService;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.IMessageService;
import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.IPermissionChecker;
import com.neucore.neulink.IResCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.app.CarshHandler;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.impl.service.OnNetStatusListener;
import com.neucore.neulink.impl.service.device.DefaultDeviceServiceImpl;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.util.ContextHolder;

import org.conscrypt.Conscrypt;

import java.security.Security;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import cn.hutool.core.util.ObjectUtil;

public class SampleConnector implements NeulinkConst{

    private String TAG = TAG_PREFIX+"SampleConnector";
    private Application application;
    private IPermissionChecker permissionChecker;
    private IMessageService messageService;
    private ILoginCallback loginCallback;
    private IDeviceService deviceService = new DefaultDeviceServiceImpl();
    private IExtendCallback defaultExtendCallback = new DefaultExtendCallback();
    private IMqttCallBack mqttCallBack;
    private IExtendCallback extendCallback;
    private IResCallback defaultResCallback;
    private IDownloder downloder;

    private Properties extConfig;
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

    public void setPermissionChecker(IPermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
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

    public void setDefaultResCallback(IResCallback defaultResCallback) {
        this.defaultResCallback = defaultResCallback;
    }

    public void setDownloder(IDownloder downloder) {
        this.downloder = downloder;
    }

    /**
     * 必须先设置相关属性，然后再调用start
     * 不然不起效果
     */
    public void start(){

        Security.insertProviderAt(Conscrypt.newProvider(), 1);

        if(!started){

            new Thread("SampleConnector"){
                @Override
                public void run() {

                    boolean allow = false;
                    /**
                     * onPermissionsGranted之后调用
                     */
                    while(ObjectUtil.isEmpty(permissionChecker)){
                        try {
                            Thread.sleep(1000);
                            Log.i(TAG,"IPermissionChecker has Not Found !!!");
                        } catch (InterruptedException e) {
                        }
                    }

                    while (!(allow= permissionChecker.has())){
                        try {
                            Thread.sleep(1000);
                            Log.i(TAG,"Permissions Not Granted,Please Do Grant!!!");
                        } catch (InterruptedException e) {
                        }
                    }

                    PropChgWatcher propChgWatcher = new PropChgWatcher();

                    NetBroadcastReceiver netBroadcastReceiver = new NetBroadcastReceiver();

                    NetBroadcastReceiver.setOnNetListener(new OnNetStatusListener());

                    registerNetworkReceiver(netBroadcastReceiver);

                    NeulinkService service = NeulinkService.getInstance();

                    ServiceRegistry.getInstance().setLoginCallback(loginCallback);

                    if(ObjectUtil.isNotEmpty(deviceService)){
                        ServiceRegistry.getInstance().setDeviceService(deviceService);
                    }

                    ServiceRegistry.getInstance().setMessageService(messageService);

                    ServiceRegistry.getInstance().setDownloder(downloder);

                    NeuLogUtils.iTag(TAG,"startBuild...");
                    NeuLogUtils.iTag(TAG,"配置项：\n"+extConfig);

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
                        extendCallback.onCallBack();
                        NeuLogUtils.iTag(TAG,"success regist extend implmention");
                    }
                    else{
                        NeuLogUtils.iTag(TAG,"success regist 默认 implmention");
                    }
                    init(service);

                }
            }.start();
        }
        started = true;
    }

    private void init(NeulinkService service){

        /**
         * 集成Neulink
         */
        ContextHolder.getInstance().setContext(application);

        //处理初始化应用carsh
        CarshHandler crashHandler = CarshHandler.getIntance();
        crashHandler.init();
        NeuLogUtils.iTag(TAG,"success regist crashHandler");

        /**
         * 初始化MQTT
         */
        long start = System.currentTimeMillis();

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        service.init();

        NeuLogUtils.iTag(TAG,"success start Mqtt service timeused: "+(System.currentTimeMillis()-start));
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
}
