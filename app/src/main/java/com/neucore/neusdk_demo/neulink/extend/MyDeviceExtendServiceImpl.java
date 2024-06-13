package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.impl.cmd.msg.DeviceInfo;
import com.neucore.neulink.impl.service.device.DefaultDeviceServiceImpl;
import com.neucore.neulink.impl.service.device.DeviceInfoDefaultBuilder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.SecuretSign;

import java.util.Locale;

/**
 * 设备服务扩展实现
 */
public class MyDeviceExtendServiceImpl extends DefaultDeviceServiceImpl {

    @Override
    public String getExtSN() {
        /**
         * 需要获取设备唯一标识【自定义，eg：YekerID@MacAddress】
         * TODO 需要从设备生产时烧入的位置读取的:椰壳Id@mac信息
         */
        return MyDeviceExtendInfoCallBack.DimSystemVer.getInstance().getYekerId()+"@"+ DeviceUtils.getMacAddress();
    }
    @Override
    public String getProductKey(){
        /**
         * TODO 需要从设备生产时烧入的位置读取的:椰壳Id@mac信息,没有时返回为空
         */
        return MyDeviceExtendInfoCallBack.DimSystemVer.getInstance().getProductKey();
    }
    @Override
    public String getDeviceId(){
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
    @Override
    public SecuretSign sign(){
        SecuretSign securetSign = new SecuretSign(getProductKey(),getDeviceId(),getDeviceSecret(),DeviceUtils.getMacAddress(),String.valueOf(System.currentTimeMillis()));
        return securetSign;
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
