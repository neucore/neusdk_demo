package com.neucore.neulink.impl;

import com.google.gson.annotations.SerializedName;

public class ArgCmd extends Cmd{

    @SerializedName("args")
    private String[] args;

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String[] toArrays(){
        String[] args = getArgs();
        int alen = args == null ? 0 : args.length;
        String[] cmds = new String[alen + 1];
        cmds[0] = getCmdStr();
        for (int i = 1; i < alen + 1; i++) {
            cmds[i] = args[i - 1];
        }
        return cmds;
    }

    public String toString(){
        String[] args = toArrays();
        return array2Str(args);
    }

    public String array2Str(String[] args){
        int len = args==null?0:args.length;
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<len;i++){
            sb.append(args[i]+" ");
        }
        return sb.toString();
    }
}
