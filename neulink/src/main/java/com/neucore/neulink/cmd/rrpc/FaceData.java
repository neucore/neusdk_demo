package com.neucore.neulink.cmd.rrpc;

import com.google.gson.annotations.SerializedName;

public class FaceData {
    /**
     * 访客规则：xxxxx,中控系统号码
     * 正式员工规则：yyyyy 卡号
     */
    @SerializedName("ext_id")
    private String extId;

    @SerializedName("org")
    private String org;

    @SerializedName("name")
    private String name;

    @SerializedName("face")
    private String face;

    @SerializedName("face_mask")
    private String faceMask;

    @SerializedName("ext_info")
    private KVPair[] extInfo = null;

    /**
     * 访客规则：用逗号连接；eg："V1,中控系统号码"
     * 员工规则：中控系统号码
     * @return
     */
    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }


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

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getFaceMask() {
        return faceMask;
    }

    public void setFaceMask(String faceMask) {
        this.faceMask = faceMask;
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
