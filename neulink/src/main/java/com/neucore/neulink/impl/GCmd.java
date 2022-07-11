package com.neucore.neulink.impl;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.NeulinkConst;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;

public abstract class GCmd {

    @SerializedName("headers")
    private Map<String,String> headers;

    @SerializedName("debug")
    private boolean debug=false;

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

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getGroup() {
        return this.getHeaders().get(NeulinkConst.NEULINK_HEADERS_GROUP);
    }

    public void setGroup(String group) {
        this.getHeaders().put(NeulinkConst.NEULINK_HEADERS_GROUP,group);
    }

    public String getCmd(){
        return this.getHeaders().get(NeulinkConst.NEULINK_HEADERS_REQ$RES);
    }
    public void setCmd(String cmd){
        this.getHeaders().put(NeulinkConst.NEULINK_HEADERS_REQ$RES,cmd);
    }
    public String getBiz() {
        return this.getHeaders().get(NeulinkConst.NEULINK_HEADERS_BIZ);
    }

    public void setBiz(String biz) {
        this.getHeaders().put(NeulinkConst.NEULINK_HEADERS_BIZ,biz);
    }

    public String getReqNo() {
        return this.getHeaders().get(NeulinkConst.NEULINK_HEADERS_REQNO);
    }
    public void setReqNo(String reqNo) {
        this.getHeaders().put(NeulinkConst.NEULINK_HEADERS_REQNO,reqNo);
    }public String getMd5(){
        return this.getHeaders().get(NeulinkConst.NEULINK_HEADERS_MD5);
    }
    public void setMd5(String md5){
        this.getHeaders().put(NeulinkConst.NEULINK_HEADERS_MD5,md5);
    }

    public String getVersion() {
        return this.getHeaders().get(NeulinkConst.NEULINK_HEADERS_VERSION);
    }

    public void setVersion(String version) {
        this.getHeaders().put(NeulinkConst.NEULINK_HEADERS_VERSION,version);
    }

}
