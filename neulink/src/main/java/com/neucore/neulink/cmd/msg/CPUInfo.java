package com.neucore.neulink.cmd.msg;

import com.google.gson.annotations.SerializedName;

public class CPUInfo {

    @SerializedName("temp")
    private double temp;//cpu 温度

    @SerializedName("use")
    private double used;

    public double getUsed() {
        return used;
    }

    public void setUsed(double used) {
        this.used = used;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }
}
