package com.neucore.neulink.cfg;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.Cmd;

public class CfgCmd extends Cmd {

    @SerializedName("data")
    private CfgItem[] data;

    public CfgItem[] getData() {
        return data;
    }

    public void setData(CfgItem[] data) {
        this.data = data;
    }
}
