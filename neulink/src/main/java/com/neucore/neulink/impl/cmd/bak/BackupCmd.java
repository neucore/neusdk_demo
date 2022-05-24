package com.neucore.neulink.impl.cmd.bak;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.Cmd;

public class BackupCmd extends Cmd {

    @SerializedName("objs")
    private String[] objs;

    @SerializedName("args")
    private String[] args;

    @Override
    public String getCmdStr() {
        return cmdStr;
    }

    @Override
    public void setCmdStr(String cmdStr) {
        this.cmdStr = cmdStr;
    }

    public String[] getObjs() {
        return objs;
    }

    public void setObjs(String[] objs) {
        this.objs = objs;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
