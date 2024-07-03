package com.neucore.neulink;

import com.neucore.neulink.impl.cmd.msg.DeviceInfo;
import com.neucore.neulink.impl.cmd.msg.HeatbeatInfo;
import com.neucore.neulink.impl.cmd.msg.RuntimeInfo;
import com.neucore.neulink.impl.service.LWTPayload;
import com.neucore.neulink.impl.service.LWTTopic;
import com.neucore.neulink.impl.service.device.LocalTimezone;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.SecuretSign;

import java.util.Locale;

import cn.hutool.core.util.ObjectUtil;

public interface IDeviceService {

    /**
     * 设备序列号，每台设备必须固定且唯一
     * @return
     */
    default String getExtSN() {
        /**
         * 默认实现
         * 每台设备固定不变【必须和设备出厂时的设备序列号一致，当不一致的时候设备将无法使用neucore云管理设备】
         * 这个主要时提供给中小企业不想建立云平台，想使用neucore云服务
         */
        if(ObjectUtil.isEmpty(getDeviceName())){
            return DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext());
        }
        else{
            return getDeviceName()+"@"+ DeviceUtils.getMacAddress();
        }
    }
    String getMqttServer();
    /**
     * 获取授权设备所属产品Id
     * @return
     */
    String getProductKey();

    /**
     * 设备id【设备授权id：椰壳Id】
     * @return
     */
    String getDeviceName();

    /**
     * 设备密钥
     * @return
     */
    String getDeviceSecret();

    /**
     * 是否是新版本
     * @return
     */
    default boolean newVersion(){
        return ObjectUtil.isNotEmpty(getProductKey()) &&ObjectUtil.isNotEmpty(getDeviceName()) && ObjectUtil.isNotEmpty(getDeviceSecret());
    }

    /**
     * 获取签名
     * @return
     */
    default SecuretSign sign(){
        SecuretSign securetSign = new SecuretSign(getProductKey(),getDeviceName(),getDeviceSecret(), DeviceUtils.getMacAddress(),String.valueOf(System.currentTimeMillis()));
        return securetSign;
    }
    /**
     *
     * @return
     * @Deprecated
     */
    @Deprecated
    default String getDeviceId(){
        return getDeviceName();
    }

    /**
     *
     * @return
     */
    @Deprecated
    default String getDevId(){
        return getDeviceName();
    }
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
