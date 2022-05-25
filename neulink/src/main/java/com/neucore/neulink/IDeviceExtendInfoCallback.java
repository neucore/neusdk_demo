package com.neucore.neulink;

import com.neucore.neulink.impl.cmd.msg.SoftVInfo;
import com.neucore.neulink.impl.cmd.msg.SubApp;

import java.util.List;
import java.util.Map;

/**
 * 设备扩展信息回调接口
 */
public interface IDeviceExtendInfoCallback {

    String getModel();

    String getImei();

    String getImsi();

    String getIccid();

    String getLat();

    String getLng();

    String getInterface();

    String getWifiModel();

    String getScreenSize();

    String getScreenInterface();

    String getScreenResolution();

    String getBno();

    String getCpuModel();

    String getNpuModel();

    SoftVInfo getMain();

    List<SubApp> getSubApps();

    List<Map<String,String>> getAttrs();
}
