package com.neucore.neulink.msg;

import com.google.gson.annotations.SerializedName;

public class DeviceInfo {

    @SerializedName("dev_id")
    private String deviceId;

    @SerializedName("mac")
    private String mac;

    @SerializedName("tag")
    private String tag;

    @SerializedName("misc")
    private MiscInfo miscInfo;

    @SerializedName("soft")
    private SoftVInfo softVInfo;

    @SerializedName("cpumd")
    private String cpuMode;

    @SerializedName("fun_list")
    private String funList[];

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public MiscInfo getMiscInfo() {
        return miscInfo;
    }

    public void setMiscInfo(MiscInfo miscInfo) {
        this.miscInfo = miscInfo;
    }

    public SoftVInfo getSoftVInfo() {
        return softVInfo;
    }

    public void setSoftVInfo(SoftVInfo softVInfo) {
        this.softVInfo = softVInfo;
    }

    public String getCpuMode() {
        return cpuMode;
    }

    public void setCpuMode(String cpuMode) {
        this.cpuMode = cpuMode;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String[] getFunList() {
        return funList;
    }

    public void setFunList(String[] funList) {
        this.funList = funList;
    }
}
