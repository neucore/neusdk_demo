package com.neucore.neulink.impl;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;

public abstract class GCmd {

    @SerializedName("headers")
    private Map<String,String> headers;

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setHeader(String key, String value){
        if(ObjectUtil.isEmpty(headers)){
            headers = new HashMap<>();
        }
        headers.put(key,value);
    }
}
