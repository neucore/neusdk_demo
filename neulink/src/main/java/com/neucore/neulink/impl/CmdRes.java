package com.neucore.neulink.impl;

import com.google.gson.annotations.SerializedName;

/**
 * replace by NewCmdRes
 * @param <T>
 */
@Deprecated
public class CmdRes<T> extends NewCmdRes<T> {

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
    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public void setCode(Integer code) {
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
