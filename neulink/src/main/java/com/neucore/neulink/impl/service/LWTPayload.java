package com.neucore.neulink.impl.service;

import com.neucore.neulink.impl.GCmd;

public class LWTPayload extends GCmd {
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
