package com.neucore.neulink.recv;

import com.neucore.neulink.impl.CmdRes;

public class RecoverCmdRes extends CmdRes {

    @Override
    public String getCmdStr() {
        return cmdStr;
    }

    @Override
    public void setCmdStr(String cmdStr) {
        this.cmdStr = cmdStr;
    }
}
