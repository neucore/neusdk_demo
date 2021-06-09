package com.neucore.neulink.cmd.rrpc;

import com.google.gson.annotations.SerializedName;

public class QCond {

    @SerializedName("name")
    private String name;

    @SerializedName("opt")
    private String opt;

    @SerializedName("val")
    private String val;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
