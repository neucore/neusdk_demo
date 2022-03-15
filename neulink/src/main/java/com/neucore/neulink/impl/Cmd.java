package com.neucore.neulink.impl;

import com.google.gson.annotations.SerializedName;

public class Cmd {

    private String biz;

    public String getBiz() {
        return biz;
    }

    void setBiz(String biz) {
        this.biz = biz;
    }

    @SerializedName("mode")
    protected String cmdStr;

    private String reqId;

    public String getReqId() {
        return reqId;
    }

    void setReqId(String reqId) {
        this.reqId = reqId;
    }

    private String version;

    public String getVersion() {
        return version;
    }

    void setVersion(String version) {
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

    void setReqtime(long reqtime) {
        this.reqtime = reqtime;
    }
}
