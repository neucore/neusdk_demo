package com.neucore.neulink.impl.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.cmd.bak.BackupItem;
import com.neucore.neulink.impl.cmd.recv.RecoverCmd;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.NeuHttpHelper;

public class DefaultRecoverCmdListener implements ICmdListener<ActionResult<String>, RecoverCmd> {
    @Override
    public ActionResult<String> doAction(NeulinkEvent<RecoverCmd> event) {
        /**
         * 最新下载的算法文件
         */
        RecoverCmd cmd = event.getSource();

        String url = cmd.getUrl();
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
