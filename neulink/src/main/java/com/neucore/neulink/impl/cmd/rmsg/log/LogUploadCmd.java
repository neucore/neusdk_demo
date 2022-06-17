package com.neucore.neulink.impl.cmd.rmsg.log;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.Cmd;

public class LogUploadCmd extends Cmd {
    @SerializedName("dev_id")
    private String deviceId;

    @SerializedName("req_no")
    private String reqId;

    @SerializedName("msg")
    private String msg;

    @SerializedName("time")
    private String time;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getReqNo() {
        return reqId;
    }

    public void setReqNo(String reqNo) {
        this.reqId = reqNo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
