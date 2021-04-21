package com.neucore.neulink.impl;

import android.content.Context;

import com.neucore.neulink.IProcessor;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.msg.DeviceInfo;
import com.neucore.neulink.msg.MiscInfo;
import com.neucore.neulink.msg.SoftVInfo;
import com.neucore.neulink.util.AppUtils;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;

public class Register {
    private Context context;
    private  NeulinkService service;
    private Boolean started = false;

    public Register(Context context, NeulinkService service) {
        this.context = context;
        this.service = service;
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
        deviceInfo.setDeviceId(DeviceUtils.getDeviceId(context)+"@@"+ ListenerFactory.getInstance().getDeviceService().getSN());
        deviceInfo.setMac(DeviceUtils.getMacAddress());

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
}
