package com.neucore.neusdk_demo.neulink.extend.bind.request;

import com.neucore.neulink.impl.Cmd;

public class BindSyncCmd extends Cmd {
    private String user;
    private Integer role;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }
}
