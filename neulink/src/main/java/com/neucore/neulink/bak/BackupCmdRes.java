package com.neucore.neulink.bak;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.CmdRes;

public class BackupCmdRes extends CmdRes {

    @SerializedName("url")
    private String url;

    @SerializedName("md5")
    private String md5;

    @Override
    public String getCmdStr() {
        return cmdStr;
    }

    @Override
    public void setCmdStr(String cmdStr) {
        this.cmdStr = cmdStr;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
