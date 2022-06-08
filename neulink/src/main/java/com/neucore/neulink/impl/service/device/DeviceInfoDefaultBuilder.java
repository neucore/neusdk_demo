package com.neucore.neulink.impl.service.device;

import android.content.Context;
import android.os.Build;

import com.neucore.neulink.impl.cmd.msg.DeviceInfo;
import com.neucore.neulink.impl.cmd.msg.MiscInfo;
import com.neucore.neulink.impl.cmd.msg.SoftVInfo;
import com.neucore.neulink.IDeviceExtendInfoCallback;
import com.neucore.neulink.util.AppUtils;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.MacHelper;

import cn.hutool.core.util.ObjectUtil;

public class DeviceInfoDefaultBuilder {

    private static DeviceInfoDefaultBuilder instance = new DeviceInfoDefaultBuilder();

    public static DeviceInfoDefaultBuilder getInstance(){
        return instance;
    }

    public DeviceInfo build(){
        DeviceInfo deviceInfo = new DeviceInfo();
        /**
         * cpu_sn@@ext_sn@@device_type
         */
        Context context = ContextHolder.getInstance().getContext();

        String mac = DeviceUtils.getMacAddress();
        deviceInfo.setMac(mac);

        deviceInfo.setTag(AppUtils.getVersionName(context));
        MiscInfo miscInfo = new MiscInfo();
        miscInfo.setLocalIp(DeviceUtils.getIpAddress(context));
        miscInfo.setDescription("Jeff@amlogic");

        deviceInfo.setMiscInfo(miscInfo);

        SoftVInfo vInfo = new SoftVInfo();

        vInfo.setOsName(android.os.Build.MANUFACTURER+"@"+android.os.Build.PRODUCT);
        /**
         * 固件版本
         */
        vInfo.setOsVersion(DeviceUtils.getSystemProperties("ro.product.releaseversion","V0.0.0"));
        /**
         * apk名称
         */
        vInfo.setReportName(AppUtils.getApkName(context));
        /**
         * apk版本信息
         */
        vInfo.setReportVersion(AppUtils.getVersionName(context));

        deviceInfo.setSoftVInfo(vInfo);

        deviceInfo.setCpuMode(Build.MODEL);
        deviceInfo.setNpuMode(DeviceUtils.getNpuMode(context));

        String[] funList = {"face"};//人脸识别
        deviceInfo.setFunList(funList);
        deviceInfo.setSkuToken(DeviceUtils.getSkuToken());

        return deviceInfo;
    }

    /**
     *
     * @param callback
     * @return
     */
    public DeviceInfo build(IDeviceExtendInfoCallback callback){
        DeviceInfo deviceInfo = build();
        if(callback!=null){

            deviceInfo.setLat(callback.getLat());

            deviceInfo.setLng(callback.getLng());

            deviceInfo.setModel(callback.getModel());
            deviceInfo.setImei(callback.getImei());
            deviceInfo.setImsi(callback.getImsi());
            deviceInfo.setIccid(callback.getIccid());
            SoftVInfo main = callback.getMain();
            if(ObjectUtil.isNotEmpty(main)){

                deviceInfo.getSoftVInfo().setBiosVersion(main.getBiosVersion());

                if(ObjectUtil.isNotEmpty(main.getOsName())){
                    deviceInfo.getSoftVInfo().setOsName(main.getOsName());
                }

                if(ObjectUtil.isNotEmpty(main.getOsVersion())){
                    deviceInfo.getSoftVInfo().setOsVersion(main.getOsVersion());
                }

                deviceInfo.getSoftVInfo().setJvmVersion(main.getJvmVersion());

                if(ObjectUtil.isNotEmpty(main.getReportName())) {
                    deviceInfo.getSoftVInfo().setReportName(main.getReportName());
                }

                if(ObjectUtil.isNotEmpty(main.getReportVersion())){
                    deviceInfo.getSoftVInfo().setReportVersion(main.getReportVersion());
                }

                deviceInfo.getSoftVInfo().setFirName(main.getFirName());

                deviceInfo.getSoftVInfo().setFirVersion(main.getFirVersion());

                deviceInfo.getSoftVInfo().setAlog(main.getAlog());
            }
            if(ObjectUtil.isNotEmpty(callback.getInterface())){
                deviceInfo.setInterface(callback.getInterface());
            }
            if(ObjectUtil.isNotEmpty(callback.getWifiModel())){
                deviceInfo.setWifiModel(callback.getWifiModel());
            }

            if(ObjectUtil.isNotEmpty(callback.getCpuModel())){
                deviceInfo.setCpuMode(callback.getCpuModel());
            }
            if(ObjectUtil.isNotEmpty(callback.getNpuModel())){
                deviceInfo.setNpuMode(callback.getNpuModel());
            }
            if(ObjectUtil.isNotEmpty(callback.getScreenSize())){
                deviceInfo.setScreenSize(callback.getScreenSize());
            }
            if(ObjectUtil.isNotEmpty(callback.getScreenInterface())){
                deviceInfo.setScreenInterface(callback.getScreenInterface());
            }
            if(ObjectUtil.isNotEmpty(callback.getScreenResolution())){
                deviceInfo.setScreenResolution(callback.getScreenResolution());
            }
            if(ObjectUtil.isNotEmpty(callback.getBno())){
                deviceInfo.setBno(callback.getBno());
            }

            deviceInfo.setSubApps(callback.getSubApps());

            deviceInfo.setAttrs(callback.getAttrs());

        }
        return deviceInfo;
    }
}
