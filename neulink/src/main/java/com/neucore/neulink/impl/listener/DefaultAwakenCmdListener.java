package com.neucore.neulink.impl.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.cmd.rmsg.AwakenCmd;
import com.neucore.neulink.cmd.rmsg.app.AlogUpgrCmd;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.impl.ArgCmd;

import java.util.HashMap;
import java.util.Map;

public class DefaultAwakenCmdListener implements ICmdListener<ActionResult, AwakenCmd> {
    @Override
    public ActionResult doAction(NeulinkEvent<AwakenCmd> event) {
        AwakenCmd cmd = event.getSource();
        /**
         * 此处实现系统休眠操作
         */
        //@TODO
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
