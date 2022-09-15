package com.neucore.neulink.impl;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.NeulinkConst;

import cn.hutool.core.util.ObjectUtil;

public class NewCmdRes<T> extends GCmd{

    public Integer getCode() {
        String codeStr = getHeader(NeulinkConst.NEULINK_HEADERS_CODE);
        if(ObjectUtil.isEmpty(codeStr)){
            return null;
        }
        return Integer.valueOf(codeStr);
    }

    public void setCode(Integer code) {
        setHeader(NeulinkConst.NEULINK_HEADERS_CODE,String.valueOf(code));
    }

    public String getMsg() {
        return getHeader(NeulinkConst.NEULINK_HEADERS_MSG);
    }

    public void setMsg(String msg) {
        setHeader(NeulinkConst.NEULINK_HEADERS_MSG,msg);
    }

    @SerializedName("data")
    private T data;

    public T getData(){
        return data;
    }

    public void setData(T data){
        this.data = data;
    }
}
