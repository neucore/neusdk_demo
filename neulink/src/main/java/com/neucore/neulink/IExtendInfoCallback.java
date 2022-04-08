package com.neucore.neulink;

import com.neucore.neulink.cmd.msg.SoftVInfo;
import com.neucore.neulink.cmd.msg.SubApp;

import java.util.List;
import java.util.Map;

public interface IExtendInfoCallback {

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

//    String getBiosVersion();
//
//    String getOsName();
//
//    String getOsVersion();
//
//    String getFirName();
//
//    String getFirVersion();
//
//    String getMainAppName();
//
//    String getMainAppVersion();
//
//    String getJvmVersion();

    String getNpuModel();

    SoftVInfo getMain();

    List<SubApp> getSubApps();

    List<Map<String,String>> getAttrs();
}
