package com.neucore.neulink.rmsg;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.ArgCmd;

public class ShellCmd extends ArgCmd {

    @SerializedName("timestamp")
    private Long timestamp;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
