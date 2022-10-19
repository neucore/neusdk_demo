package com.neucore.neulink.impl.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.cmd.rmsg.app.DebugCmd;

import java.util.Map;

import cn.hutool.core.util.ObjectUtil;

public class DefaultDebugCmdListener implements ICmdListener<ActionResult<Map<String,String>>, DebugCmd> {

    @Override
    public ActionResult doAction(NeulinkEvent<DebugCmd> event) {
        String[] cmds = null;
        try {
            DebugCmd cmd = event.getSource();
            Map<String,String> argsMap = cmd.argsToMap();
            if(ObjectUtil.isEmpty(argsMap)){
                ConfigContext.getInstance().update(NeulinkConst.SYSTEM_DEBUG_KEY,DEBUG_OFF);
                System.getenv().put(NeulinkConst.SYSTEM_DEBUG_KEY,DEBUG_OFF);
            }
            else{
                System.getenv().putAll(argsMap);
            }
            ActionResult<Map<String,String>> result = new ActionResult<>();
            result.setData(argsMap);
            return result;
        }
        catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }
}
