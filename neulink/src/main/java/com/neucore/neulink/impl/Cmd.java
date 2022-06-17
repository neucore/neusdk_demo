package com.neucore.neulink.impl;

import com.google.gson.annotations.SerializedName;

public class Cmd extends GCmd{

    private String biz;

    public String getBiz() {
        return biz;
    }

    protected void setBiz(String biz) {
        this.biz = biz;
    }

    @SerializedName("mode")
    protected String cmdStr;

    private String reqNo;

    public String getReqNo() {
        return reqNo;
    }
    protected void setReqNo(String reqNo) {
        this.reqNo = reqNo;
    }

    @Deprecated
    public String getReqId() {
        return reqNo;
    }

    @Deprecated
    protected void setReqId(String reqNo) {
        this.reqNo = reqNo;
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
