package com.neucore.neusdk_demo.neulink.extend.file.response;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.CmdRes;

public class FileSyncCmdRes extends CmdRes {

    @SerializedName("id")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

