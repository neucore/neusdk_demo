package com.neucore.neulink.impl.cmd.rrpc;

import com.google.gson.annotations.SerializedName;

public class LicData extends PkgData{

    @SerializedName("org")
    private String org;

    @SerializedName("name")
    private String name;

    @SerializedName("lic")
    private String lic;

    @SerializedName("ext_info")
    private KVPair[] extInfo = null;

    /**
     * 访客规则：用逗号连接；eg："V1,中控系统号码"
     * 员工规则：中控系统号码
     * @return
     */

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
