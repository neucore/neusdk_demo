package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.Result;

import java.io.File;

public class ApkUpgrdActionListener implements ICmdListener<Result> {
    @Override
    public Result doAction(NeulinkEvent event) {
        /**
         * 最新下载的apk文件
         */
        File apkFile = (File)event.getSource();
        /**
         * 此处实现apk文件安装操作
         */
        //@TODO apk文件安装

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
