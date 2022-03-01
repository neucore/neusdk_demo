package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.Result;
import com.neucore.neulink.cmd.upd.UgrdeCmd;

import java.io.File;
import java.util.Map;

public class ApkUpgrdActionListener implements ICmdListener<Result> {
    @Override
    public Result doAction(NeulinkEvent event) {
        UgrdeCmd cmd = (UgrdeCmd)event.getSource();

        Map<String,String> argMaps = cmd.argsToMap();
        /**
         * 最新下载的apk文件
         */
        File file = cmd.getLocalFile();
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
