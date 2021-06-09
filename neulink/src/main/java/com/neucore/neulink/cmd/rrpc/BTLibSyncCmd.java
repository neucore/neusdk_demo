package com.neucore.neulink.cmd.rrpc;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.Cmd;

public class BTLibSyncCmd extends Cmd {

    public BTLibSyncCmd(){
        cmdStr = "sync";
    }

    @SerializedName("objtype")
    private String objtype;

    @SerializedName("total")
    private long total;

    @SerializedName("pages")
    private long pages;

    @SerializedName("offset")
    private int offset;

    @SerializedName("data_url")
    private String dataUrl;

    @SerializedName("md5")
    private String md5;

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

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
