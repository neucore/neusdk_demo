package com.neucore.neulink.impl.cmd.rrpc;

import com.neucore.neulink.IActionResult;
import com.neucore.neulink.impl.ActionResult;

public class PkgActionResult<T> extends ActionResult<T> implements IActionResult {

    private Long total;
    private Long pages;
    private Long offset;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPages() {
        return pages;
    }

    public void setPages(Long pages) {
        this.pages = pages;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }
}
