package com.neucore.neulink.cmd.cfg;

import com.google.gson.annotations.SerializedName;

public class CfgItem {

    @SerializedName("key")
    private String keyName;

    @SerializedName("value")
    private String value;

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}