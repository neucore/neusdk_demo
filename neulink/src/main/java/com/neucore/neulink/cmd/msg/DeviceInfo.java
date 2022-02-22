package com.neucore.neulink.cmd.msg;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @SerializedName("npumd")
    private String npuMode;

    @SerializedName("fun_list")
    private String funList[];

    @SerializedName("sku_token")
    private String skuToken;

    @SerializedName("subapps")
    private List<SoftVInfo> subApps = new ArrayList<>();

    @SerializedName("attrs")
    private List<Map<String,String>> attrs = new ArrayList();

    public String getDeviceId() {
        return deviceId;
    }

    /**
     * deviceId = cpusn@@extSn@@deviceType
     * @param deviceId
     */
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

    public String getNpuMode() {
        return npuMode;
    }

    public void setNpuMode(String npuMode) {
        this.npuMode = npuMode;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }


    public String getSkuToken() {
        return skuToken;
    }

    public void setSkuToken(String skuToken) {
        this.skuToken = skuToken;
    }

    public String[] getFunList() {
        return funList;
    }

    public void setFunList(String[] funList) {
        this.funList = funList;
    }

    public List<SoftVInfo> getSubApps() {
        return subApps;
    }

    public void setSubApps(List<SoftVInfo> subApps) {
        this.subApps = subApps;
    }

    public List<Map<String, String>> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<Map<String, String>> attrs) {
        this.attrs = attrs;
    }
}
