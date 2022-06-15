package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.impl.cmd.msg.DeviceInfo;
import com.neucore.neulink.impl.service.device.DefaultDeviceServiceImpl;
import com.neucore.neulink.impl.service.device.DeviceInfoDefaultBuilder;
import com.neucore.neulink.util.DeviceUtils;

import java.util.Locale;

public class MyDeviceServiceImpl extends DefaultDeviceServiceImpl {
    @Override
    public String getExtSN() {
        /**
         * 需要获取设备唯一标识【自定义，eg：YekerID@MacAddress】
         */
        return MyExtendInfoCallBack.DimSystemVer.getInstance().getYekerId()+"@"+ DeviceUtils.getMacAddress();
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
        return DeviceInfoDefaultBuilder.getInstance().build(new MyExtendInfoCallBack());
    }
}
