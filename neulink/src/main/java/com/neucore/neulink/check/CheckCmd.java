package com.neucore.neulink.check;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.Cmd;

public class CheckCmd extends Cmd {

    @SerializedName("objtype")
    private String objtype;

    @SerializedName("md5")
    private String md5;

    @SerializedName("timestamp")
    private long timestamp;

    @Override
    public String getCmdStr() {
        return cmdStr;
    }

    @Override
    public void setCmdStr(String cmdStr) {
        this.cmdStr = cmdStr;
    }

    public String getObjtype() {
        return objtype;
    }

    public void setObjtype(String objtype) {
        this.objtype = objtype;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
