package com.neucore.neusdk_demo.neulink.extend.other;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.cmd.rmsg.ShellCmd;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.ShellExecutor;

import java.util.Map;

/**
 * 远程下发shell命令，并执行shell命令
 */
public class SampleShellCmdListener implements ICmdListener<ActionResult<Map<String, String>>,ShellCmd> {

    @Override
    public ActionResult<Map<String, String>> doAction(NeulinkEvent<ShellCmd> event) {
        /**
         * @TODO: 业务实现
         */
        String[] cmds = null;
        Map<String, String> resultMap = null;
        try {
            ShellCmd cmd = event.getSource();
            String cmdA = cmd.toString();
            resultMap = ShellExecutor.execute(ContextHolder.getInstance().getContext(), cmdA);
            ActionResult<Map<String, String>> actionResult = new ActionResult<>();
            actionResult.setData(resultMap);
            return actionResult;
        }
        catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }
}
