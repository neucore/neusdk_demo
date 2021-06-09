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

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportVersion() {
        return reportVersion;
    }

    public void setReportVersion(String reportVersion) {
        this.reportVersion = reportVersion;
    }

    public String getAlogVersion() {
        return alogVersion;
    }

    public void setAlogVersion(String alogVersion) {
        this.alogVersion = alogVersion;
    }
}
