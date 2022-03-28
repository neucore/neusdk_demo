package com.neucore.neusdk_demo.neulink.extend.other;

import android.net.wifi.p2p.WifiP2pManager;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.impl.Cmd;
import com.neucore.neulink.util.IActionResult;

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
