package com.neucore.neulink.impl.service.device;

import android.content.Context;
import android.os.Build;

import com.neucore.neulink.cmd.msg.DeviceInfo;
import com.neucore.neulink.cmd.msg.MiscInfo;
import com.neucore.neulink.cmd.msg.SoftVInfo;
import com.neucore.neulink.util.AppUtils;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.MacHelper;

import cn.hutool.core.util.ObjectUtil;

public class DeviceServiceImpl implements IDeviceService {

    @Override
    public String getExtSN() {
        /**
         * 默认实现
         * 每台设备固定不变【必须和设备出厂时的设备序列号一致，当不一致的时候设备将无法使用neucore云管理设备】
         * 这个主要时提供给中小企业不想建立云平台，想使用neucore云服务
         */
        return DeviceUtils.getCPUSN(ContextHolder.getInstance().getContext());
    }



    @Override
    public DeviceInfo getInfo() {

        DeviceInfo deviceInfo = new DeviceInfo();
        /**
         * cpu_sn@@ext_sn@@device_type
         */
        Context context = ContextHolder.getInstance().getContext();

        String mac = MacHelper.getEthernetMac();
        if(ObjectUtil.isEmpty(mac)){
            mac = MacHelper.getWifiMac(context);
        }

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
        vInfo.setAlogVersion("1.0");
        deviceInfo.setSoftVInfo(vInfo);

        deviceInfo.setCpuMode(Build.MODEL);
        deviceInfo.setNpuMode(DeviceUtils.getNpuMode(context));

        String[] funList = {"face"};//人脸识别
        deviceInfo.setFunList(funList);
        deviceInfo.setSkuToken(DeviceUtils.getSkuToken());
        return deviceInfo;
    }
}
