package com.neucore.neusdk_demo.neulink.extend.other;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.impl.ArgCmd;

public class SampleHibrateActionListener implements ICmdListener<ActionResult,ArgCmd> {
    @Override
    public ActionResult doAction(NeulinkEvent<ArgCmd> event) {
        /**
         * @TODO: 业务实现
         */
        ArgCmd cmd = event.getSource();

        ActionResult actionResult = new ActionResult();
        /**
         * 200表示成功 500：表示错误
         */
        actionResult.setCode(200);
        /**
         * 错误信息
         */
        actionResult.setMessage("success");
        return actionResult;
    }
}