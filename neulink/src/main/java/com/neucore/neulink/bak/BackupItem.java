package com.neucore.neulink.bak;

import com.google.gson.annotations.SerializedName;

public class BackupItem {
    @SerializedName("obj")
    private String obj;
    @SerializedName("url")
    private String url;
    @SerializedName("md5")
    private String md5;

    public String getObj() {
        return obj;
    }

    public void setObj(String obj) {
        this.obj = obj;
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
