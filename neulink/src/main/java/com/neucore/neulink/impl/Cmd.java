package com.neucore.neulink.impl;

import com.google.gson.annotations.SerializedName;

/**
 * replace by NewCmd
 * @param <T>
 */
@Deprecated
public class Cmd<T> extends NewCmd<T>{

    @SerializedName("mode")
    protected String cmdStr;

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
