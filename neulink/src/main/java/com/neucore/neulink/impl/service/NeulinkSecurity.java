package com.neucore.neulink.impl.service;

public class NeulinkSecurity {

    private static NeulinkSecurity instance = new NeulinkSecurity();

    public static NeulinkSecurity getInstance(){
        return instance;
    }

    private String token;
    public void setToken(String token){
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
