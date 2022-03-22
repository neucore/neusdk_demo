package com.neucore.neusdk_demo.neulink.extend.other;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.cmd.upd.UgrdeCmd;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.NeulinkEvent;

import java.io.File;
import java.util.Map;

public class SampleFirewareUpgrdActionListener implements ICmdListener<ActionResult,UgrdeCmd> {
    @Override
    public ActionResult doAction(NeulinkEvent<UgrdeCmd> event) {
        /**
         * @TODO: 业务实现
         */
        UgrdeCmd cmd = event.getSource();

        Map<String,String> argMaps = cmd.argsToMap();
        /**
         * 最新下载的apk文件
         */
        File file = cmd.getLocalFile();
        /**
         * 此处实现apk文件安装操作
         */
        //@TODO apk｜固件文件安装

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
