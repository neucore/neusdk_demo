package com.neucore.neulink.tmptr;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.ArgCmd;
import com.neucore.neulink.util.DatesUtil;

public class FaceTempCmd extends ArgCmd {
    @SerializedName("dev_id")
    private String deviceId;
    @SerializedName("data")
    private FaceTemp[] data;

    @SerializedName("timestamp")
    private long timestamp = DatesUtil.getNowTimeStamp();

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public FaceTemp[] getData() {
        return data;
    }

    public void setData(FaceTemp[] data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
