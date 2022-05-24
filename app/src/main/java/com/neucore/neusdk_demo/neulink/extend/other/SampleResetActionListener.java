package com.neucore.neusdk_demo.neulink.extend.other;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.impl.Cmd;

public class SampleResetActionListener implements ICmdListener<ActionResult, Cmd> {
    @Override
    public ActionResult doAction(NeulinkEvent<Cmd> neulinkEvent) {
        /**
         * @TODO: reset app
         */
        ActionResult result = new ActionResult();
        result.setCode(STATUS_200);
        result.setMessage(MESSAGE_SUCCESS);
        return result;
    }
}
