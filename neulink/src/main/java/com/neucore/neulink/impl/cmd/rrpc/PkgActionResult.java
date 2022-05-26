package com.neucore.neulink.impl.cmd.rrpc;

import com.neucore.neulink.IActionResult;
import com.neucore.neulink.impl.ActionResult;

public class PkgActionResult<T> extends ActionResult<T> implements IActionResult {

    private long total;
    private long pages;
    private long offset;

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
}
