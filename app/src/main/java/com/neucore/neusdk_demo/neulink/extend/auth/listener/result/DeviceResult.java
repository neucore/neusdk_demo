package com.neucore.neusdk_demo.neulink.extend.auth.listener.result;

import com.google.gson.annotations.SerializedName;

public class DeviceResult {
    private String mode;
    @SerializedName("device_id")
    private String deviceId;
    private Integer code;
    @SerializedName("msg")
    private String message;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
