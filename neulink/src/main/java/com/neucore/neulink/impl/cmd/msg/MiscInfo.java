package com.neucore.neulink.impl.cmd.msg;

import com.google.gson.annotations.SerializedName;

public class MiscInfo {
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @SerializedName("description")
    private String description;
    @SerializedName("local_ip")
    private String localIp;

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }


}
