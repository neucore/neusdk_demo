package com.neucore.neulink.cmd.msg;

import com.google.gson.annotations.SerializedName;

public class ResRegist {
    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("zone")
    private NeulinkZone zone;

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

    public NeulinkZone getZone() {
        return zone;
    }

    public void setZone(NeulinkZone zone) {
        this.zone = zone;
    }
}
