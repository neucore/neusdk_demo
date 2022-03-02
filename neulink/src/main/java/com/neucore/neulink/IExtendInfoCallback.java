package com.neucore.neulink;

import com.neucore.neulink.cmd.msg.SoftVInfo;

import java.util.List;
import java.util.Map;

public interface IExtendInfoCallback {

    String getImei();

    String getImsi();

    String getIccid();

    String getWifiModel();

    String getScreenSize();

    String getScreenInterface();

    String getScreenResolution();

    String getBiosVersion();

    String getJvmVersion();

    String getNpuModel();

    List<SoftVInfo> getSubApps();

    List<Map<String,String>> getAttrs();
}
