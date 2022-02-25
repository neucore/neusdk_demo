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
        return DeviceInfoBuilder.getInstance().build();
    }
}
