package com.neucore.neulink.impl.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.cmd.cfg.CfgItem;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.cmd.cfg.QCfgCmd;
import com.neucore.neulink.cmd.rmsg.HibrateCmd;
import com.neucore.neulink.cmd.upd.UgrdeCmd;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.NeulinkEvent;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DefaultQCfgCmdListener implements ICmdListener<ActionResult<CfgItem[]>, QCfgCmd> {
    @Override
    public ActionResult<CfgItem[]> doAction(NeulinkEvent<QCfgCmd> event) {
        try{
            QCfgCmd cmd = event.getSource();
            CfgItem[] retItems = null;
            if("query".equalsIgnoreCase(cmd.getCmdStr())) {
                Properties configs = ConfigContext.getInstance().getConfigs();
                Iterator<String> keys = configs.stringPropertyNames().iterator();
                List<CfgItem> result = new ArrayList<CfgItem>();
                while (keys.hasNext()) {
                    String key = keys.next();
                    CfgItem item = new CfgItem();
                    item.setKeyName(key);
                    item.setValue(ConfigContext.getInstance().getConfig(key));
                    result.add(item);
                }
                retItems = new CfgItem[result.size()];
                result.toArray(retItems);
            }
            ActionResult<CfgItem[]> actionResult = new ActionResult<>();
            actionResult.setData(retItems);
            return actionResult;
        }
        catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }
}
