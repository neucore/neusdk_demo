package com.neucore.neulink.cmd.msg;

import com.google.gson.annotations.SerializedName;

public class SubApp {

    @SerializedName("rptn")
    private String reportName="NeuSDK";
    @SerializedName("rptv")
    private String reportVersion;
    @SerializedName("alogv")
    private String alogVersion;

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
