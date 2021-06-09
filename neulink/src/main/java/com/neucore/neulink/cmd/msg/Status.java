package com.neucore.neulink.cmd.msg;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.util.DatesUtil;

public class Status {

    @SerializedName("dev_id")
    private String deviceId;

    @SerializedName("status")
    private String status="active";

    @SerializedName("timestamp")
    private long timestamp = DatesUtil.getNowTimeStamp();

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
