package com.neucore.neulink.impl;

import com.google.gson.annotations.SerializedName;

public class CmdRes {

    @SerializedName("mode")
    protected String cmdStr;

    @SerializedName("code")
    private int code;

    @SerializedName("msg")
    private String msg;

    @SerializedName("dev_id")
    private String deviceId;

    public String getCmdStr() {
        return cmdStr;
    }

    public void setCmdStr(String cmdStr) {
        this.cmdStr = cmdStr;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
