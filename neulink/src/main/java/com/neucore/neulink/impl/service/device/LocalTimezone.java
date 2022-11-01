package com.neucore.neulink.impl.service.device;

import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;

public class LocalTimezone implements NeulinkConst {

    private String id = ConfigContext.getInstance().getConfig(TimeZoneId,TimeZoneId_Asia$ShangHai);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
