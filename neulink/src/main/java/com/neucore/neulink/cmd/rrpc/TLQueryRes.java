package com.neucore.neulink.cmd.rrpc;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.CmdRes;

public class TLQueryRes extends CmdRes {

    @SerializedName("objtype")
    private String objtype;

    @SerializedName("total")
    private long total;

    @SerializedName("pages")
    private long pages;

    @SerializedName("offset")
    private long offset;

    @SerializedName("url")
    private String url;

    @SerializedName("md5")
    private String md5;

    public TLQueryRes(){
        cmdStr = "query";
    }

    public String getObjtype() {
        return objtype;
    }

    public void setObjtype(String objtype) {
        this.objtype = objtype;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

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
