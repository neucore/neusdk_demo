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
    private Long reqtime;

    public Long getReqtime() {
        return reqtime;
    }

    public void setReqtime(Long reqtime) {
        this.reqtime = reqtime;
    }
}
