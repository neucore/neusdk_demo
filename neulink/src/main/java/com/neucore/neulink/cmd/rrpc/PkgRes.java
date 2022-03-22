package com.neucore.neulink.cmd.rrpc;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.CmdRes;

import java.util.List;

public class PkgRes extends CmdRes {

    @SerializedName("objtype")
    private String objtype;

    @SerializedName("total")
    private long total;

    @SerializedName("pages")
    private long pages;

    @SerializedName("offset")
    private long offset;

    @SerializedName("failed")
    private List<String> failed;

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

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public List<String> getFailed() {
        return failed;
    }

    public void setFailed(List<String> failed) {
        this.failed = failed;
    }
}
