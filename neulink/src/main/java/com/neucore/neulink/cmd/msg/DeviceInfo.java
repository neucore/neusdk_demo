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

    @SerializedName("imei")
    private String imei;

    public String getImei(){
        return imei;
    }
    @SerializedName("imsi")
    private String imsi;

    public String getImsi(){
        return imsi;
    }
    @SerializedName("iccid")
    private String iccid;
    public String getIccid(){
        return iccid;
    }
    @SerializedName("wifi_model")
    private String wifiModel;
    public String getWifiModel(){
        return wifiModel;
    }
    @SerializedName("screen_size")
    private String screenSize;
    public String getScreenSize(){
        return screenSize;
    }
    @SerializedName("screen_interface")
    private String screenInterface;
    public String getScreenInterface(){
        return screenInterface;
    }
    @SerializedName("screen_resolution")
    private String screenResolution;
    public String getScreenResolution(){
        return screenResolution;
    }
    @SerializedName("bios_version")
    private String biosVersion;
    public String getBiosVersion(){
        return biosVersion;
    }
    @SerializedName("jvm_version")
    private String jvmVersion;
    public String getJvmVersion(){
        return jvmVersion;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public void setWifiModel(String wifiModel) {
        this.wifiModel = wifiModel;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    public void setScreenInterface(String screenInterface) {
        this.screenInterface = screenInterface;
    }

    public void setScreenResolution(String screenResolution) {
        this.screenResolution = screenResolution;
    }

    public void setBiosVersion(String biosVersion) {
        this.biosVersion = biosVersion;
    }

    public void setJvmVersion(String jvmVersion) {
        this.jvmVersion = jvmVersion;
    }

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
