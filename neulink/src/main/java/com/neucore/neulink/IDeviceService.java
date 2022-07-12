package com.neucore.neulink;

import com.neucore.neulink.impl.cmd.msg.DeviceInfo;
import com.neucore.neulink.impl.cmd.msg.HeatbeatInfo;
import com.neucore.neulink.impl.cmd.msg.RuntimeInfo;
import com.neucore.neulink.impl.service.LWTPayload;
import com.neucore.neulink.impl.service.LWTTopic;

import java.util.Locale;

public interface IDeviceService {
    /**
     * 设备序列号，每台设备必须固定且唯一
     * @return
     */
    String getExtSN();

    DeviceInfo getInfo();

    /**
     * enable.heartbeat;默认关闭：即不上报
     * @return
     */
    HeatbeatInfo heatbeat();
    /**
     * enable.runtime；默认关闭：即不上报
     * @return
     */
    RuntimeInfo runtime();

    Locale getLocale();

    boolean regist(DeviceInfo deviceInfo);

    void connect();

    void disconnect();

    LWTTopic lwtTopic();

    LWTPayload lwtPayload();
}
