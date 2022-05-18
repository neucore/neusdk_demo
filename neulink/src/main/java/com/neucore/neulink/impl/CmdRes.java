package com.neucore.neulink.impl;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class CmdRes<T> {

    @SerializedName("heads")
    private Map<String,String> headers;

    @SerializedName("mode")
    protected String cmdStr;

    @SerializedName("code")
    private int code;

    @SerializedName("msg")
    private String msg;

    @SerializedName("dev_id")
    private String deviceId;

    @SerializedName("data")
    private T data;

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
