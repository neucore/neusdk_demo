package com.neucore.neulink.impl;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.extend.annotation.IgnoreProp;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.annotation.PropIgnore;
import cn.hutool.core.util.ObjectUtil;

public abstract class GCmd {

    @SerializedName("headers")
    protected Map<String,String> headers;

    @IgnoreProp
    private boolean debug=false;

    @IgnoreProp
    private Long reqtime;

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setHeader(String key, String value){
        if(ObjectUtil.isNotEmpty(key)){
            if(ObjectUtil.isEmpty(headers)){
                headers = new HashMap<>();
            }
            this.getHeaders().put(key,value);
        }
    }

    public String getHeader(String key){
        if(ObjectUtil.isNull(headers)){
            return null;
        }
        return this.getHeaders().get(key);
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public Long getReqtime() {
        return reqtime;
    }

    public void setReqtime(Long reqtime) {
        this.reqtime = reqtime;
    }

    public String getGroup() {
        return this.getHeaders().get(NeulinkConst.NEULINK_HEADERS_GROUP);
    }

    public void setGroup(String group) {
        setHeader(NeulinkConst.NEULINK_HEADERS_GROUP,group);
    }

    public String getCmdType(){
        return getHeader(NeulinkConst.NEULINK_HEADERS_REQ$RES);
    }

    public void setCmdType(String cmdType){
        setHeader(NeulinkConst.NEULINK_HEADERS_REQ$RES,cmdType);
    }

    /**
     * replace getMode
     * @return
     */
    @Deprecated
    public String getCmd(){
        return getHeader(NeulinkConst.NEULINK_HEADERS_MODE);
    }

    /**
     * replace setMode
     * @param cmd
     * @Deprecated
     */
    
    public void setCmd(String cmd){
        setHeader(NeulinkConst.NEULINK_HEADERS_MODE,cmd);
    }
    
    public String getMode(){
        return getHeader(NeulinkConst.NEULINK_HEADERS_MODE);
    }

    public void setMode(String mode){
        setHeader(NeulinkConst.NEULINK_HEADERS_MODE,mode);
    }

    public String getBiz() {
        return getHeader(NeulinkConst.NEULINK_HEADERS_BIZ);
    }

    public void setBiz(String biz) {
        setHeader(NeulinkConst.NEULINK_HEADERS_BIZ,biz);
    }

    public String getReqNo() {
        return getHeader(NeulinkConst.NEULINK_HEADERS_REQNO);
    }

    public void setReqNo(String reqNo) {
        setHeader(NeulinkConst.NEULINK_HEADERS_REQNO,reqNo);
    }

    public String getMd5(){
        return getHeader(NeulinkConst.NEULINK_HEADERS_MD5);
    }

    public void setMd5(String md5){
        setHeader(NeulinkConst.NEULINK_HEADERS_MD5,md5);
    }

    public String getVersion() {
        return getHeader(NeulinkConst.NEULINK_HEADERS_VERSION);
    }
    
    public void setVersion(String version) {
        setHeader(NeulinkConst.NEULINK_HEADERS_VERSION,version);
    }
}
