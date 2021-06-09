package com.neucore.neulink.cmd.rmsg;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.CmdRes;

public class UpgrRes extends CmdRes {

    @SerializedName("vinfo")
    private String vinfo;

    public UpgrRes(){
        this.cmdStr = "upgrade";
    }

    public String getVinfo() {
        return vinfo;
    }

    public void setVinfo(String vinfo) {
        this.vinfo = vinfo;
    }
}
