package com.neucore.neulink.cmd.rmsg.app;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.CmdRes;

public class AlogUpgrRes extends CmdRes {
    
    @SerializedName("vinfo")
    private String vinfo;

    public AlogUpgrRes(){
        cmdStr = "upgrade";
    }

    public String getVinfo() {
        return vinfo;
    }

    public void setVinfo(String vinfo) {
        this.vinfo = vinfo;
    }
}
