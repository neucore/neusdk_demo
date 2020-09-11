package com.neucore.neulink.cfg;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.CmdRes;

public class CfgQueryCmdRes extends CmdRes {

    @SerializedName("data")
    private CfgItem[] data;

    public CfgQueryCmdRes(){
        this.cmdStr = "query";
    }

    public CfgItem[] getData() {
        return data;
    }

    public void setData(CfgItem[] data) {
        this.data = data;
    }
}
