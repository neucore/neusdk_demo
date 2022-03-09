package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.cmd.rmsg.app.AlogUpgrCmd;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.Result;

import java.io.File;

public class AlogUpgrdActionListener implements ICmdListener<Result, File> {
    @Override
    public Result doAction(NeulinkEvent<File> event) {
        /**
         * 最新下载的算法文件
         */
        File alogFile = (File)event.getSource();
        /**
         * 此处实现算法文件安装操作
         */
        //@TODO 算法文件安装

        Result result = new Result();
        /**
         * 200表示成功 500：表示错误
         */
        result.setCode(200);
        /**
         * 错误信息
         */
        result.setMessage("success");
        return result;
    }
}
