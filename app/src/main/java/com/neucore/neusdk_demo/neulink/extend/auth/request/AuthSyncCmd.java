package com.neucore.neusdk_demo.neulink.extend.auth.request;

import com.neucore.neulink.impl.Cmd;

/**
 * 授权下发 请求
 */
public class AuthSyncCmd extends Cmd {
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
