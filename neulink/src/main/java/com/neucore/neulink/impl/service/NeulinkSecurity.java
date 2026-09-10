package com.neucore.neulink.impl.service;

import com.neucore.neulink.LoginUser;

public class NeulinkSecurity {

    private static NeulinkSecurity instance = new NeulinkSecurity();

    public static NeulinkSecurity getInstance(){
        return instance;
    }
    private LoginUser loginUser;

    public LoginUser getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(LoginUser loginUser) {
        this.loginUser = loginUser;
    }

    public String getToken() {
        return loginUser.getAccessToken();
    }

    public String getRefreshToken() {
        return loginUser.getRefreshToken();
    }

}
