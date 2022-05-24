package com.neucore.neusdk_demo.neulink.extend.other;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.cmd.rmsg.RebootCmd;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.ShellExecutor;

import java.util.Map;

public class SampleRebootCmdListener implements ICmdListener<ActionResult<Map<String,String>>, RebootCmd> {
    @Override
    public ActionResult<Map<String, String>> doAction(NeulinkEvent<RebootCmd> event) {
        /**
         * @TODO: 业务实现
         */
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
