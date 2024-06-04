package com.neucore.neulink;

import com.neucore.neulink.impl.cmd.msg.DeviceInfo;
import com.neucore.neulink.impl.cmd.msg.HeatbeatInfo;
import com.neucore.neulink.impl.cmd.msg.RuntimeInfo;
import com.neucore.neulink.impl.service.LWTPayload;
import com.neucore.neulink.impl.service.LWTTopic;
import com.neucore.neulink.impl.service.device.LocalTimezone;

import java.util.Locale;

public interface IDeviceService {
    /**
     * [${productId}|]${extSn}
     * @return
     */
    String clientId();

    /**
     * 设备序列号，每台设备必须固定且唯一
     * @return
     */
    String getExtSN();

    /**
     * 获取授权设备所属产品Id
     * @return
     */
    String getProductId();
    /**
     * 设备id【设备授权id：椰壳Id】
     * @return
     */
    String getDeviceId();

    /**
     * 设备密钥
     * @return
     */
    String getDeviceSecret();

    /**
     * 获取签名
     * @return
     */
    String sign(String devId,String deviceSecret);

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

    LocalTimezone getTimezone();

    boolean regist(DeviceInfo deviceInfo);

    void connect();

    void disconnect();

    LWTTopic lwtTopic();

    LWTPayload lwtPayload();
}
