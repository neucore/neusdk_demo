package com.neucore.neusdk_demo.neulink.extend.other;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.cmd.cfg.CfgItem;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.cmd.cfg.QCfgCmd;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.NeulinkEvent;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * 配置查询
 */
public class SampleQCfgCmdListener implements ICmdListener<ActionResult<CfgItem[]>, QCfgCmd> {
    @Override
    public ActionResult<CfgItem[]> doAction(NeulinkEvent<QCfgCmd> event) {
        /**
         * @TODO: 业务实现
         */
        try{
            QCfgCmd cmd = event.getSource();
            CfgItem[] retItems = null;
            if("query".equalsIgnoreCase(cmd.getCmdStr())) {
                Properties configs = ConfigContext.getInstance().getConfigs();
                Enumeration keys = configs.keys();
                List<CfgItem> result = new ArrayList<CfgItem>();
                while (keys.hasMoreElements()) {
                    String key = (String) keys.nextElement();
                    CfgItem item = new CfgItem();
                    item.setKeyName(key);
                    item.setValue(configs.getProperty(key));
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
