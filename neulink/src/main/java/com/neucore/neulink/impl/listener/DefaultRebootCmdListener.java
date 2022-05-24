package com.neucore.neulink.impl.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.cmd.rmsg.RebootCmd;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.ShellExecutor;

import java.util.Map;

public class DefaultRebootCmdListener implements ICmdListener<ActionResult<Map<String,String>>, RebootCmd> {

    @Override
    public ActionResult<Map<String, String>> doAction(NeulinkEvent<RebootCmd> event) {
        try {
            RebootCmd cmd = event.getSource();
            Map<String,String> stringStringMap = ShellExecutor.run(ContextHolder.getInstance().getContext(), cmd.toArrays());
            ActionResult<Map<String,String>> actionResult = new ActionResult<>();
            actionResult.setData(stringStringMap);
            return actionResult;
        }
        catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }
}
