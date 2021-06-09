package com.neucore.neulink.cmd.rrpc;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.ArgCmd;

public class TLibQueryCmd extends ArgCmd {

    @SerializedName("objtype")
    private String objtype;

    @SerializedName("cnd")
    private QCond[] conds;

    public String getObjtype() {
        return objtype;
    }

    public void setObjtype(String objtype) {
        this.objtype = objtype;
    }

    public QCond[] getConds() {
        return conds;
    }

    public void setConds(QCond[] conds) {
        this.conds = conds;
    }
}
