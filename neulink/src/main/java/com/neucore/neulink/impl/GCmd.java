package com.neucore.neulink.impl;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.extend.annotation.IgnoreProp;

import org.greenrobot.greendao.annotation.Transient;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;

public abstract class GCmd {

    @SerializedName("headers")
    protected Map<String,String> headers;

    @IgnoreProp
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

    public String getHeader(String key){
        return getProp(key);
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
        setProp(NeulinkConst.NEULINK_HEADERS_GROUP,group);
    }

    public String getCmdType(){
        return getProp(NeulinkConst.NEULINK_HEADERS_REQ$RES);
    }

    public void setCmdType(String cmdType){
        setProp(NeulinkConst.NEULINK_HEADERS_REQ$RES,cmdType);
    }

    /**
     * replace getMode
     * @return
     */
    @Deprecated
    public String getCmd(){
        return getProp(NeulinkConst.NEULINK_HEADERS_MODE);
    }

    /**
     * replace setMode
     * @param cmd
     * @Deprecated
     */
    
    public void setCmd(String cmd){
        setProp(NeulinkConst.NEULINK_HEADERS_MODE,cmd);
    }
    
    public String getMode(){
        return getProp(NeulinkConst.NEULINK_HEADERS_MODE);
    }

    public void setMode(String mode){
        setProp(NeulinkConst.NEULINK_HEADERS_MODE,mode);
    }

    public String getBiz() {
        return getProp(NeulinkConst.NEULINK_HEADERS_BIZ);
    }

    public void setBiz(String biz) {
        setProp(NeulinkConst.NEULINK_HEADERS_BIZ,biz);
    }

    public String getReqNo() {
        return getProp(NeulinkConst.NEULINK_HEADERS_REQNO);
    }

    public void setReqNo(String reqNo) {
        setProp(NeulinkConst.NEULINK_HEADERS_REQNO,reqNo);
    }

    public String getMd5(){
        return getProp(NeulinkConst.NEULINK_HEADERS_MD5);
    }

    public void setMd5(String md5){
        setProp(NeulinkConst.NEULINK_HEADERS_MD5,md5);
    }

    public String getVersion() {
        return getProp(NeulinkConst.NEULINK_HEADERS_VERSION);
    }
    
    public void setVersion(String version) {
        setProp(NeulinkConst.NEULINK_HEADERS_VERSION,version);
    }
    
    private String getProp(String key){
        if(ObjectUtil.isNull(headers)){
            return null;
        }
        return this.getHeaders().get(key);
    }
    
    private void setProp(String key,String value){
        if(ObjectUtil.isNotEmpty(value)){
            if(ObjectUtil.isNull(headers)){
                headers = new HashMap<>();
            }
        }
        else if(ObjectUtil.isNotEmpty(key)){
            if(ObjectUtil.isNull(headers)){
                headers = new HashMap<>();
            }
        }
        this.getHeaders().put(key,value);
    }
}
