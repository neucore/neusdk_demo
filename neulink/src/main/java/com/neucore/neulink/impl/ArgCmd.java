package com.neucore.neulink.impl;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class ArgCmd extends Cmd{

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
            for (String arg:args) {
                String[] kv = arg.split("=");
                if(kv.length>1){
                    map.put(kv[0],kv[1]);
                }
                else{
                    map.put(kv[0],kv[0]);
                }
            }
        }
        return map;
    }
}
