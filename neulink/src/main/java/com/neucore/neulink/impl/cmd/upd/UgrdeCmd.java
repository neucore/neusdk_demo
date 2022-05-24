package com.neucore.neulink.impl.cmd.upd;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.ArgCmd;

import java.io.File;

public class UgrdeCmd extends ArgCmd {

    @SerializedName("vinfo")
    private String vinfo;

    @SerializedName("url")
    private String url;

    @SerializedName("md5")
    private String md5;

    private File localFile;

    public String getVinfo() {
        return vinfo;
    }

    public void setVinfo(String vinfo) {
        this.vinfo = vinfo;
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

    public File getLocalFile() {
        return localFile;
    }

    public void setLocalFile(File localFile) {
        this.localFile = localFile;
    }
}
