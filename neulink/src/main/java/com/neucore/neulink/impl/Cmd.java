package com.neucore.neulink.impl;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Cmd {

    @SerializedName("heads")
    private Map<String,String> headers;

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    private String biz;

    public String getBiz() {
        return biz;
    }

    protected void setBiz(String biz) {
        this.biz = biz;
    }

    @SerializedName("mode")
    protected String cmdStr;

    private String reqId;

    public String getReqId() {
        return reqId;
    }

    protected void setReqId(String reqId) {
        this.reqId = reqId;
    }

    private String version;

    public String getVersion() {
        return version;
    }

    protected void setVersion(String version) {
        this.version = version;
    }

    public String getCmdStr() {
        return cmdStr;
    }

    public void setCmdStr(String cmdStr) {
        this.cmdStr = cmdStr;
    }

    @SerializedName("time_stamp")
    private long reqtime;

    public long getReqtime() {
        return reqtime;
    }

    protected void setReqtime(long reqtime) {
        this.reqtime = reqtime;
    }
}
