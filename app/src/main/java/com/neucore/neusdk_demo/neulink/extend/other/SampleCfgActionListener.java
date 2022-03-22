package com.neucore.neusdk_demo.neulink.extend.other;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.cmd.cfg.CfgCmd;
import com.neucore.neulink.cmd.cfg.CfgItem;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.NeulinkEvent;

public class SampleCfgActionListener implements ICmdListener<ActionResult,CfgCmd>, NeulinkConst {
    @Override
    public ActionResult doAction(NeulinkEvent<CfgCmd> event) {
        CfgCmd cmd = event.getSource();
        CfgItem[] items = cmd.getData();
        int size = items==null?0:items.length;
        /**
         * @TODO: 配置项管理
         */
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
        return new ActionResult();
    }
}
