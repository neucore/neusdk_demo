package com.neucore.neusdk_demo.neulink.extend.auth.listener.result;

import com.google.gson.annotations.SerializedName;

public class DomainResult {

    private String mode;
    @SerializedName("domain_id")
    private String domainId;
    private Integer code;
    @SerializedName("msg")
    private String message;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
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
