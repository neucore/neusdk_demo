package com.neucore.neulink.cmd.rmsg;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.CmdRes;

public class UpgrRes extends CmdRes {

    @SerializedName("vinfo")
    private String vinfo;

    @SerializedName("progress")
    private Integer progress;

    public UpgrRes(){
        this.cmdStr = "upgrade";
    }

    public String getVinfo() {
        return vinfo;
    }

    public void setVinfo(String vinfo) {
        this.vinfo = vinfo;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }
}
