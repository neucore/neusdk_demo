package com.neucore.neulink.cmd.rmsg.log;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.extend.ActionResult;

public class LogActionResult extends ActionResult {

    private int code;
    private String msg;

    @SerializedName("pages")
    private long pages;

    @SerializedName("offset")
    private long offset;

    @SerializedName("url")
    private String url;

    @SerializedName("md5")
    private String md5;

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
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
