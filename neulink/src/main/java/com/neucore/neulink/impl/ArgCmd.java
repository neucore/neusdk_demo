package com.neucore.neulink.impl;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class ArgCmd<T> extends Cmd<T>{

    @SerializedName("args")
    private String[] args;

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public Map<String,String> argsToMap(){
        Map<String,String> map = new HashMap<>();
        String[] args = getArgs();
        if(args!=null&&args.length>0){
            int i = 0;
            for (String arg:args) {
                int idx = arg.indexOf("=");
                if(idx!=-1){
                    String key = arg.substring(0,idx);
                    String value = arg.substring(idx+1);
                    map.put(key,value);
                }
                else{
                    map.put(String.valueOf(i),arg);
                    i++;
                }
            }
        }
        return map;
    }
}
