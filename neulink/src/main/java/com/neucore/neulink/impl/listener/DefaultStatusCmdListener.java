package com.neucore.neulink.impl.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.Cmd;
import com.neucore.neulink.impl.NeulinkEvent;

/**
 * 设备状态查询
 */
public class DefaultStatusCmdListener implements ICmdListener<ActionResult, Cmd> {
    @Override
    public ActionResult doAction(NeulinkEvent<Cmd> event) {
        ActionResult result = new ActionResult();
        result.setCode(STATUS_200);
        result.setMessage(MESSAGE_SUCCESS);
        return result;
    }
}
