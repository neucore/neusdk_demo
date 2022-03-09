package com.neucore.neusdk_demo.neulink.extend.auth.response;

import com.google.gson.annotations.SerializedName;

public class LinkResult {
    private String mode;
    @SerializedName("srcDevId")
    private String srcDevId;

    @SerializedName("destDevId")
    private String destDevId;
    private Integer code;
    @SerializedName("msg")
    private String message;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getSrcDevId() {
        return srcDevId;
    }

    public void setSrcDevId(String srcDevId) {
        this.srcDevId = srcDevId;
    }

    public String getDestDevId() {
        return destDevId;
    }

    public void setDestDevId(String destDevId) {
        this.destDevId = destDevId;
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
