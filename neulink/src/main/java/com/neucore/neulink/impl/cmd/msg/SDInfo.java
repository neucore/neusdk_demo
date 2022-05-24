package com.neucore.neulink.impl.cmd.msg;

import com.google.gson.annotations.SerializedName;

public class SDInfo {

    @SerializedName("tot")
    private long total;

    @SerializedName("use")
    private long used;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getUsed() {
        return used;
    }

    public void setUsed(long used) {
        this.used = used;
    }
}
