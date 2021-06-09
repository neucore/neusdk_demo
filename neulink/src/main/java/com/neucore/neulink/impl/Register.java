package com.neucore.neulink.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.neucore.neulink.IProcessor;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.cmd.msg.DeviceInfo;
import com.neucore.neulink.cmd.msg.MiscInfo;
import com.neucore.neulink.cmd.msg.SoftVInfo;
import com.neucore.neulink.util.AppUtils;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.MacHelper;
import com.neucore.neulink.util.JSonUtils;

public class Register extends BroadcastReceiver {

    private String TAG = "Register";
    private Context context;
    private  NeulinkService service;
    private Boolean registed = false;
    private NeulinkScheduledReport autoReporter = null;

    public Register(Context context, NeulinkService service,String serviceUrl) {
        this.context = context;
        this.service = service;
        registerReceiver(this);
        autoReporter = new NeulinkScheduledReport(context,service);
        int channel = ConfigContext.getInstance().getConfig(ConfigContext.UPLOAD_CHANNEL,0);
        if(channel==1){//http注册方式&上报方式
            Log.d(TAG,"start http register");
            regist();
            autoReporter.start();
        }
        else{//mqtt注册方式&上报方式
            service.init(serviceUrl,context);
        }
    }

    private void registerReceiver(BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("MqttService.callbackToActivity.v0");
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

        DeviceInfo deviceInfo = new DeviceInfo();
        /**
         * cpu_sn@@ext_sn@@device_type
         */
        String devId = DeviceUtils.getDeviceId(context)+"@@"+ ListenerFactory.getInstance().getDeviceService().getSN()+"@@"+ConfigContext.getInstance().getConfig(ConfigContext.DEVICE_TYPE,0);
        deviceInfo.setDeviceId(devId);

        String mac = MacHelper.getWifiMac(context);
        deviceInfo.setMac(mac);

        deviceInfo.setTag(AppUtils.getVersionName(context));
        MiscInfo miscInfo = new MiscInfo();
        miscInfo.setLocalIp(DeviceUtils.getIpAddress(context));
        miscInfo.setDescription("Jeff@amlogic");

        deviceInfo.setMiscInfo(miscInfo);

        SoftVInfo vInfo = new SoftVInfo();

        vInfo.setOsName(android.os.Build.MANUFACTURER+"@"+android.os.Build.PRODUCT);
        vInfo.setOsVersion(android.os.Build.VERSION.RELEASE);
        vInfo.setReportName("NeuSDK");
        vInfo.setReportVersion(AppUtils.getVersionName(context));
        vInfo.setAlogVersion("1.0");
        deviceInfo.setSoftVInfo(vInfo);

        deviceInfo.setCpuMode(android.os.Build.CPU_ABI);

        String[] funList = {"face"};//人脸识别
        deviceInfo.setFunList(funList);

        String payload = JSonUtils.toString(deviceInfo);
        String devinfo_topic = "msg/req/devinfo";
        service.publishMessage(devinfo_topic, IProcessor.V1$0, payload, 0);
    }

    private static final String Action_Name="com.intel.unit.Clipy";
    @Override
    public void onReceive(Context context, Intent intent) {
        int channel = ConfigContext.getInstance().getConfig(ConfigContext.UPLOAD_CHANNEL,0);
        if(channel==0 && !registed){
            Log.d(TAG,"start mqtt register");
            regist();
            registed=true;
            autoReporter.start();
        }
    }
}
