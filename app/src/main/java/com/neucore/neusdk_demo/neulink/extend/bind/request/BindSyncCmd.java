package com.neucore.neusdk_demo.neulink.extend.bind.request;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.Cmd;

public class BindSyncCmd extends Cmd {

    @SerializedName("user")
    private String userId;

    @SerializedName("name")
    private String userName;

    private Integer role;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String user) {
        this.userId = user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }
}
