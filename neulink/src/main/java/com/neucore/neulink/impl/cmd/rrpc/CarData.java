package com.neucore.neulink.impl.cmd.rrpc;

import com.google.gson.annotations.SerializedName;

public class CarData extends PkgData{

    @SerializedName("org")
    private String org;

    @SerializedName("name")
    private String name;

    @SerializedName("car")
    private String car;

    @SerializedName("lic")
    private String lic;

    @SerializedName("ext_info")
    private KVPair[] extInfo = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getLic() {
        return lic;
    }

    public void setLic(String lic) {
        this.lic = lic;
    }

    /**
     * {"key":"type","value":"1"},//人脸名单类型：value="1":黑名单
     * {"key":"period_start","value":"2132132132"},//unix_timestamp 【新增：临时访客有效期限开始时间】
     * {"key":"period_end","value":"345433243243"}//unix_timestamp 结束时间【新增：临时访客有效期限结束时间】
     * @return
     */
    public KVPair[] getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(KVPair[] extInfo) {
        this.extInfo = extInfo;
    }
}
