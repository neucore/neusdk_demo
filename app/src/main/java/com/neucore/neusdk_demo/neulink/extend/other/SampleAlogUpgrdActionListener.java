package com.neucore.neusdk_demo.neulink.extend.other;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.NeulinkEvent;

import java.io.File;

/**
 * 算法升级
 */
public class SampleAlogUpgrdActionListener implements ICmdListener<ActionResult, File> {
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
