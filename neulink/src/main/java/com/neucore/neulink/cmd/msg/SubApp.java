package com.neucore.neulink.cmd.msg;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubApp {

    @SerializedName("rptn")
    private String reportName="NeuSDK";
    @SerializedName("rptv")
    private String reportVersion;
    @SerializedName("alogv")
    private List<Alog> alog;

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

    public List<Alog> getAlog() {
        return alog;
    }

    public void setAlog(List<Alog> alog) {
        this.alog = alog;
    }
}
