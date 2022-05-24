package com.neucore.neulink.impl.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.impl.Cmd;

public class DefaultResetCmdListener implements ICmdListener<ActionResult, Cmd> {
    @Override
    public ActionResult doAction(NeulinkEvent<Cmd> event) {
        ActionResult result = new ActionResult();
        result.setCode(STATUS_200);
        result.setMessage(MESSAGE_SUCCESS);
        return result;
    }
}
