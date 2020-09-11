package com.neucore.neulink.rmsg;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.CmdRes;

public class ShellCmdRes extends CmdRes {

    @SerializedName("shell_ret")
    private int shellRet;

    @SerializedName("stdout")
    private String stdout;

    public int getShellRet() {
        return shellRet;
    }

    public void setShellRet(int shellRet) {
        this.shellRet = shellRet;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }
}
