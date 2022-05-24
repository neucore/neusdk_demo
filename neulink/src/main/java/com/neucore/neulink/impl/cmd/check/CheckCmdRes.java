package com.neucore.neulink.impl.cmd.check;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.CmdRes;

public class CheckCmdRes extends CmdRes {

    @SerializedName("objtype")
    private String objtype;

    @SerializedName("datas")
    private String datas;

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

    public String getDatas() {
        return datas;
    }

    public void setDatas(String datas) {
        this.datas = datas;
    }
}
