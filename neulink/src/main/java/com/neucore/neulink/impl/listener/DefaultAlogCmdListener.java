package com.neucore.neulink.impl.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.cmd.rmsg.app.AlogUpgrCmd;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.NeulinkEvent;

public class DefaultAlogCmdListener implements ICmdListener<ActionResult, AlogUpgrCmd> {
    @Override
    public ActionResult doAction(NeulinkEvent<AlogUpgrCmd> event) {
        /**
         * 最新下载的算法文件
         */
        String alogFile = event.getSource().getUrl();
        /**
         * 此处实现算法文件安装操作
         */
        //@TODO 算法文件安装

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
