package com.neucore.neusdk_demo.neulink.extend.other;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.cmd.cfg.CfgItem;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.cmd.rmsg.app.DebugCmd;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.NeulinkEvent;

public class SampleDebugCmdListener implements ICmdListener<ActionResult, DebugCmd> {
    @Override
    public ActionResult doAction(NeulinkEvent<DebugCmd> event) {
        String[] cmds = null;
        try {
            /**
             * @TODO: debug开关管理实现可替换
             */
            String[] args = event.getSource().getArgs();
            if(args==null||args.length==0||(!"on".equalsIgnoreCase(args[0]) && !"off".equalsIgnoreCase(args[0]))){
                throw new RuntimeException("参数只接受[on或者off]");
            }
            else {
                CfgItem item = new CfgItem();
                item.setKeyName("Debug");
                item.setValue(args[0]);
                CfgItem[] items = new CfgItem[]{item};
                ConfigContext.getInstance().update(items);
                ActionResult<String> result = new ActionResult<>();
                result.setData(MESSAGE_SUCCESS);
                return result;
            }
        }
        catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }
}
