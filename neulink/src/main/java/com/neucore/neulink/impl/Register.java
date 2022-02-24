package com.neucore.neulink.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.neucore.neulink.IProcessor;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.cmd.msg.DeviceInfo;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.impl.service.device.IDeviceService;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.NetworkHelper;

import cn.hutool.core.util.ObjectUtil;

public class Register extends BroadcastReceiver {

    private String TAG = "NeulinkRegister";
    private Context context;
    private  NeulinkService service;
    private NeulinkScheduledReport autoReporter = null;
    private boolean initMqttService = false;
    private boolean mqttServiceReady =false;
    private boolean networkReady = false;
    private boolean initRegistService = false;
    private String serviceUrl;
    private final NetworkHelper networkHelper = NetworkHelper.getInstance();

    public Register(final Context context, final NeulinkService service, final String serviceUrl) {
        this.context = context;
        this.service = service;
        this.serviceUrl = serviceUrl;

        registerReceiver(this);

        networkHelper.addListener(new NetworkHelper.Listener() {
            @Override
            public void onConnectivityChange(boolean connect) {
                if(connect){
                    networkReady = connect;
                    initRegistService("onConnectivityChange");
                }
            }
        });

        networkHelper.onStart();

        if(networkHelper.getNetworkConnected()){
            initRegistService("getNetworkConnected");
        }

        autoReporter = new NeulinkScheduledReport(context,service);
    }

    private void initRegistService(String from){
        Log.i(TAG,String.format("from=%s,networkReady=%s,mqttServiceReady=%s",from,networkReady, mqttServiceReady));
        int channel = ConfigContext.getInstance().getConfig(ConfigContext.UPLOAD_CHANNEL,0);
        if(networkReady){
            if(channel==0 && !initMqttService ){//http注册方式&上报方式
                Log.d(TAG,"start mqtt register");
                service.init(serviceUrl,context);
                initMqttService = true;
            }
        }
        if((channel==0
                && initMqttService
                && mqttServiceReady
                && !initRegistService)
           || (channel ==1
                && networkReady
                && !initRegistService)){
            Log.d(TAG,"start "+(channel==0?"mqtt":"http")+ " register");
            regist();
            autoReporter.start();
            initRegistService = true;
        }
    }

    private void registerReceiver(BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("MqttService.callbackToActivity.v0");
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
    }
    /**
     * 设备注册 msg/req/devinfo/v1.0/${req_no}[/${md5}], qos=0
     */
    void regist() {
        /**
         * 确保有license的设备先激活，后注册
         */
        try {
            Thread.sleep(15);
        }
        catch (Exception ex){}
        IDeviceService deviceService = ServiceFactory.getInstance().getDeviceService();
        DeviceInfo deviceInfo = deviceService.getInfo();
        if(ObjectUtil.isEmpty(deviceInfo)){
            throw new RuntimeException("设备服务 getInfo没有实现。。。");
        }
        String devId = DeviceUtils.getDeviceId(context)+"@@"+ deviceService.getExtSN()+"@@"+ ConfigContext.getInstance().getConfig(ConfigContext.DEVICE_TYPE,0);
        deviceInfo.setDeviceId(devId);

        String payload = JSonUtils.toString(deviceInfo);
        String devinfo_topic = "msg/req/devinfo";
        service.publishMessage(devinfo_topic, IProcessor.V1$0, payload, 0);
    }

    private static final String Action_Name="com.intel.unit.Clipy";
    @Override
    public void onReceive(Context context, Intent intent) {
        mqttServiceReady = true;
        initRegistService("onReceive");
    }
}
