package com.neucore.neulink.cmd.msg;

import com.google.gson.annotations.SerializedName;

public class SoftVInfo {

    @SerializedName("osn")
    private String osName;
    @SerializedName("osv")
    private String osVersion;
    @SerializedName("rptn")
    private String reportName="NeuSDK";
    @SerializedName("rptv")
    private String reportVersion;
    @SerializedName("alogv")
    private String alogVersion;

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

    /**
     * 应用程序名称【apk应用名称】
     * @return
     */
    public String getReportName() {
        return reportName;
    }

    /**
     * 应用程序名称【apk应用名称】
     * @param reportName
     */
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    /**
     * 应用程序版本【apk应用程序版本】
     * @return
     */
    public String getReportVersion() {
        return reportVersion;
    }

    /**
     * 应用程序版本【apk应用程序版本】
     * @param reportVersion
     */
    public void setReportVersion(String reportVersion) {
        this.reportVersion = reportVersion;
    }

    /**
     * 算法版本
     * @return
     */
    public String getAlogVersion() {
        return alogVersion;
    }

    /**
     * 算法版本
     * @param alogVersion
     */
    public void setAlogVersion(String alogVersion) {
        this.alogVersion = alogVersion;
    }
}
