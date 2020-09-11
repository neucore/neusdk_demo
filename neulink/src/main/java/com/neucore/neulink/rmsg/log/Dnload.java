package com.neucore.neulink.rmsg.log;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.Cmd;

public class Dnload extends Cmd {

    @SerializedName("type")
    private String type;

    @SerializedName("start")
    private String start;
    @SerializedName("end")
    private String end;

    public Dnload(){
        this.cmdStr = "download";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
