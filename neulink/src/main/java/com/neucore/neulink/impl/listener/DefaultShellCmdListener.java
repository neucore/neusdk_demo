package com.neucore.neulink.impl.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.cmd.rmsg.ShellCmd;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.ShellExecutor;

import java.util.Map;

public class DefaultShellCmdListener implements ICmdListener<ActionResult<Map<String, String>>,ShellCmd> {

    @Override
    public ActionResult<Map<String, String>> doAction(NeulinkEvent<ShellCmd> event) {
        String[] cmds = null;
        try {
            ShellCmd cmd = event.getSource();
            String cmdStr = cmd.toString();
            Map<String, String> resultMap = ShellExecutor.execute(ContextHolder.getInstance().getContext(), cmdStr);
            ActionResult<Map<String, String>> actionResult = new ActionResult<>();
            actionResult.setData(resultMap);
            return actionResult;
        }
        catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }
}
