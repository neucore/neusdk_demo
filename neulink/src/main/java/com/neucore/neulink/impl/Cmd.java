package com.neucore.neulink.impl;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.NeulinkConst;

public class Cmd extends GCmd{

    public String getBiz() {
        return this.getHeaders().get(NeulinkConst.NEULINK_HEADERS_BIZ);
    }

    protected void setBiz(String biz) {
        this.getHeaders().put(NeulinkConst.NEULINK_HEADERS_BIZ,biz);
    }

    @SerializedName("mode")
    protected String cmdStr;

    public String getReqNo() {
        return this.getHeaders().get(NeulinkConst.NEULINK_HEADERS_REQNO);
    }
    protected void setReqNo(String reqNo) {
        this.getHeaders().put(NeulinkConst.NEULINK_HEADERS_REQNO,reqNo);
    }

    private String version;

    public String getVersion() {
        return this.getHeaders().get(NeulinkConst.NEULINK_HEADERS_VERSION);
    }

    protected void setVersion(String version) {
        this.getHeaders().put(NeulinkConst.NEULINK_HEADERS_VERSION,version);
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
