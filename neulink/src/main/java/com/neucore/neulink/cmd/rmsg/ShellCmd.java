package com.neucore.neulink.cmd.rmsg;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.ArgCmd;

public class ShellCmd extends ArgCmd {

    @SerializedName("timestamp")
    private Long timestamp;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
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

    public String array2Str(String[] args){
        int len = args==null?0:args.length;
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<len;i++){
            sb.append(args[i]+" ");
        }
        return sb.toString();
    }

    public String toString(){
        String[] args = toArrays();
        return array2Str(args);
    }

}
