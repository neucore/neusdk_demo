package com.neucore.neulink.impl.service.device;

import com.neucore.neulink.cmd.msg.DeviceInfo;

public interface IDeviceService {
    /**
     * 设备序列号，每台设备必须固定且唯一
     * @return
     */
    String getSN();

    DeviceInfo getInfo();
}
