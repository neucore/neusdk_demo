package com.neucore.neulink.impl;

import com.google.gson.annotations.SerializedName;

public class Cmd {

    @SerializedName("mode")
    protected String cmdStr;

    private String reqId;

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
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

    public void setReqtime(long reqtime) {
        this.reqtime = reqtime;
    }
}
