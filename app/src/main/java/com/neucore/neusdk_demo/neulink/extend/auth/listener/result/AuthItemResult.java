package com.neucore.neusdk_demo.neulink.extend.auth.listener.result;

import com.google.gson.annotations.SerializedName;

public class AuthItemResult {
    private String mode;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("unlock_device")
    private String unlockDevice;
    private Integer code;
    @SerializedName("msg")
    private String message;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUnlockDevice() {
        return unlockDevice;
    }

    public void setUnlockDevice(String unlockDevice) {
        this.unlockDevice = unlockDevice;
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
