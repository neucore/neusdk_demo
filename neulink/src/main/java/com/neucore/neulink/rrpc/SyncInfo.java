package com.neucore.neulink.rrpc;

import com.google.gson.annotations.SerializedName;

public class SyncInfo {

    @SerializedName("url")
    private String fileUrl;

    @SerializedName("md5")
    private String md5;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
