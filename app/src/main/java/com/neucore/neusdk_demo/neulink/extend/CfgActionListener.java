package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.cmd.cfg.CfgCmd;
import com.neucore.neulink.cmd.cfg.CfgItem;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.Result;

public class CfgActionListener implements ICmdListener<Result>, NeulinkConst {
    @Override
    public Result doAction(NeulinkEvent event) {
        CfgCmd cmd = (CfgCmd) event.getSource();
        CfgItem[] items = cmd.getData();
        int size = items==null?0:items.length;

        if ("add".equalsIgnoreCase(cmd.getCmdStr())) {
            ConfigContext.getInstance().add(items);
        }
        else if("update".equalsIgnoreCase(cmd.getCmdStr())){
            ConfigContext.getInstance().update(items);
        }
        else if("del".equalsIgnoreCase(cmd.getCmdStr())){
            ConfigContext.getInstance().delete(items);
        }
        else if("sync".equalsIgnoreCase(cmd.getCmdStr())){
            ConfigContext.getInstance().sync(items);
        }
        return new Result();
    }
}
