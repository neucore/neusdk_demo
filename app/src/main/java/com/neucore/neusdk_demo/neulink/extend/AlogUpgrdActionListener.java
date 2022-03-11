package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.NeulinkEvent;

import java.io.File;

public class AlogUpgrdActionListener implements ICmdListener<ActionResult, File> {
    @Override
    public ActionResult doAction(NeulinkEvent<File> event) {
        /**
         * 最新下载的算法文件
         */
        File alogFile = (File)event.getSource();
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
