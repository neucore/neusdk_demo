package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.cmd.bak.BackupItem;
import com.neucore.neulink.cmd.recv.RecoverCmd;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.Result;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.NeuHttpHelper;

import java.io.IOException;

public class RecoverActionListener implements ICmdListener<Result,RecoverCmd>, NeulinkConst {
    @Override
    public Result doAction(NeulinkEvent<RecoverCmd> event) {
        /**
         * 最新下载的算法文件
         */
        RecoverCmd cmd = event.getSource();

        String url = cmd.getUrl();
        try {
            String json  = NeuHttpHelper.dldFile2String(url,3);
            BackupItem[] items = (BackupItem[]) JSonUtils.toObject(json, BackupItem[].class);
            int len = items==null?0:items.length;
            for(int i=0;i<len;i++){
                String obj = items[i].getObj();
                if(Backup_Obj_Cfg.equalsIgnoreCase(obj)){
                    //@TODO 实现应用配置文件恢复操作
                }
                else if(Backup_Obj_Syscfg.equalsIgnoreCase(obj)){
                    //@TODO 实现系统配置文件恢复操作
                }
                else if(Backup_Obj_Data.equalsIgnoreCase(obj)){
                    //@TODO 实现数据库文件恢复操作
                }
            }

        } catch (IOException e) {
            throw new NeulinkException(500,e.getMessage());
        }

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
