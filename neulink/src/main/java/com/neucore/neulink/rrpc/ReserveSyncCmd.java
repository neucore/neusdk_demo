package com.neucore.neulink.rrpc;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.ArgCmd;

public class ReserveSyncCmd extends ArgCmd {

    public ReserveSyncCmd(){
        cmdStr = "sync";
    }

    @SerializedName("data")
    private String data;

    @SerializedName("time_stamp")
    private long time_stamp;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }
}
