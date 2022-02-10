package com.neucore.neulink.impl.service.device;

public interface IDeviceService {
    /**
     * 设备序列号，每台设备必须固定且唯一
     * @return
     */
    String getSN();
}
