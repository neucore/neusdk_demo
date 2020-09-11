package com.neucore.neulink.rmsg;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.ArgCmd;

public class FirwareUpgr extends ArgCmd {
    @SerializedName("vinfo")
    private String vinfo;


    public FirwareUpgr(){
        this.cmdStr = "upgrade";
    }

    public String getVinfo() {
        return vinfo;
    }

    public void setVinfo(String vinfo) {
        this.vinfo = vinfo;
    }
}
