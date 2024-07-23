package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.impl.cmd.msg.DeviceInfo;
import com.neucore.neulink.impl.service.device.DefaultDeviceServiceImpl;
import com.neucore.neulink.impl.service.device.DeviceInfoDefaultBuilder;
import com.neucore.neulink.util.AppUtils;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;

import java.util.Locale;

/**
 * 设备服务扩展实现
 *
 */
public class MyDeviceExtendServiceImpl extends DefaultDeviceServiceImpl {

    @Override
    public String getMacAddress(){
        return DeviceUtils.getMacAddress().replace(":","").toUpperCase();
    }

    @Override
    public String getMqttServer(){
        /**
         * TODO 需要从设备生产时烧入的位置读取的:productId
         */
        return MyDeviceExtendInfoCallBack.DimSystemVer.getInstance().getMqttServer();
    }

    @Override
    public String getProductKey(){
        /**
         * TODO 需要从设备生产时烧入的位置读取的:productId
         */
        return MyDeviceExtendInfoCallBack.DimSystemVer.getInstance().getProductKey();
    }

    @Override
    public String getDeviceName(){
        /**
         * 读取设备烧录的授权ID【椰壳Id，即：设备Id】
         * TODO 需要从设备生产时烧入的位置读取的:椰壳Id
         */
        return MyDeviceExtendInfoCallBack.DimSystemVer.getInstance().getYekerId();
    }

    @Override
    public String getDeviceSecret(){
        /**
         * 读取设备烧录的设备密钥
         * TODO 需要从设备生产时烧入的位置读取的:设备密钥
         */
        return MyDeviceExtendInfoCallBack.DimSystemVer.getInstance().getDeviceSecret();
    }

    /**
     * 获取操作系统名称
     * @return
     */
    public String getOsName(){
        //os.name
        return MyDeviceExtendInfoCallBack.DimSystemVer.getInstance().getOsName();//System.getProperty("os.name","");
    }


    public String getOsVersion() {
        //os.version
        return MyDeviceExtendInfoCallBack.DimSystemVer.getInstance().getOsVersion();
    }


    /**
     * 固件名称
     * @return
     */
    @Override
    public String getFirName() {
        return MyDeviceExtendInfoCallBack.DimSystemVer.getInstance().getFirName();  // 获取固件名称
    }

    /**
     * 固件版本
     * @return
     */
    @Override
    public String getFirVersion() {
        return MyDeviceExtendInfoCallBack.DimSystemVer.getInstance().getFirVersion();  // 获取Android版本号
    }

    @Override
    public String getApkName(){
        return AppUtils.getApkName(ContextHolder.getInstance().getContext());
    }

    @Override
    public String getApkVersion(){
        return AppUtils.getVersionName(ContextHolder.getInstance().getContext());
    }
    
    @Override
    public Locale getLocale(){
        /**
         * 需要读取apk国际化设置后的值
         */
        return Locale.getDefault();
    }
    @Override
    public DeviceInfo getInfo() {
        /**
         * 需要上报应用列表【名称及其相关版本；】
         * OTA升级文件规则
         *
         * ota_[sys|apk|app]_设备硬件型号_设备产品型号(对应neulink的cpu型号)_产品当前版本识别号，其中设备硬件型号和设备产品型号，以及产品当前版本识别号不能有下划线。
         *
         * ota升级文件包的【设备产品型号】字段需要和neulink内的 -- cpumd 进行一致；
         */
        return DeviceInfoDefaultBuilder.getInstance().build(new MyDeviceExtendInfoCallBack());
    }
}
