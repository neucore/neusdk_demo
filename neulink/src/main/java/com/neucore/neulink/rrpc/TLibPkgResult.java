package com.neucore.neulink.rrpc;

public class TLibPkgResult {

    private int code;
    private String msg;
    private long total;
    private long pages;
    private long offset;
    /**
     * ext_id:message
     */
    private String[] failed;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    public String[] getFailed() {
        return failed;
    }

    public void setFailed(String[] failed) {
        this.failed = failed;
    }
}
