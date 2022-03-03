package com.neucore.neulink.cmd.msg;

import com.google.gson.annotations.SerializedName;

public class SoftVInfo extends SubApp{

    @SerializedName("bios_version")
    private String biosVersion;

    @SerializedName("osn")
    private String osName;
    @SerializedName("osv")
    private String osVersion;

    @SerializedName("firn")
    private String firName;

    @SerializedName("firv")
    private String firVersion;

    @SerializedName("jvm_version")
    private String jvmVersion;

    /**
     * 固件名称
     * @return
     */
    public String getOsName() {
        return osName;
    }

    /**
     * 固件名称
     * @param osName
     */
    public void setOsName(String osName) {
        this.osName = osName;
    }

    /**
     * 固件版本
     * @return
     */
    public String getOsVersion() {
        return osVersion;
    }

    /**
     * 固件版本
     * @param osVersion
     */
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getFirName() {
        return firName;
    }

    public void setFirName(String firName) {
        this.firName = firName;
    }

    public String getFirVersion() {
        return firVersion;
    }

    public void setFirVersion(String firVersion) {
        this.firVersion = firVersion;
    }

    public String getBiosVersion() {
        return biosVersion;
    }

    public void setBiosVersion(String biosVersion) {
        this.biosVersion = biosVersion;
    }

    public String getJvmVersion() {
        return jvmVersion;
    }

    public void setJvmVersion(String jvmVersion) {
        this.jvmVersion = jvmVersion;
    }
}
