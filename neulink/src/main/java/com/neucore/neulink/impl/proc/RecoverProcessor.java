package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.cmd.bak.BackupItem;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.cmd.recv.RecoverCmd;
import com.neucore.neulink.cmd.recv.RecoverCmdRes;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.impl.ListenerRegistrator;
import com.neucore.neulink.extend.ServiceRegistrator;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.NeuHttpHelper;

import java.io.IOException;

public class RecoverProcessor extends GProcessor<RecoverCmd, RecoverCmdRes, ActionResult<String>> implements NeulinkConst {

    public RecoverProcessor(Context context) {
        super(context);
    }

    @Override
    public ActionResult<String> process(NeulinkTopicParser.Topic topic, RecoverCmd cmd) {
        String url = cmd.getUrl();
        try {
            String json  = NeuHttpHelper.dldFile2String(url,3);
            BackupItem[] items = (BackupItem[]) JSonUtils.toObject(json, BackupItem[].class);
            int len = items==null?0:items.length;
            for(int i=0;i<len;i++){
                String obj = items[i].getObj();
                if(Backup_Obj_Cfg.equalsIgnoreCase(obj)){
                    ConfigContext.getInstance().store(topic.getReqId(),items[i].getUrl());
                }
                else if(Backup_Obj_Syscfg.equalsIgnoreCase(obj)){

                }
                else if(Backup_Obj_Data.equalsIgnoreCase(obj)){
//                    DaoManager.getInstance().store(this.getContext(),topic.getReqId(),items[i].getUrl());
                }
            }

        } catch (IOException e) {
            throw new NeulinkException(STATUS_500,e.getMessage());
        }
        return null;
    }

    @Override
    public RecoverCmd parser(String payload) {
        return (RecoverCmd) JSonUtils.toObject(payload, RecoverCmd.class);
    }

    @Override
    protected RecoverCmdRes responseWrapper(RecoverCmd t, ActionResult<String> result) {
        RecoverCmdRes recoverCmdRes = new RecoverCmdRes();
        recoverCmdRes.setCmdStr(t.getCmdStr());
        recoverCmdRes.setCode(STATUS_200);
        recoverCmdRes.setMsg(MESSAGE_SUCCESS);
        recoverCmdRes.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        return recoverCmdRes;
    }

    @Override
    protected RecoverCmdRes fail(RecoverCmd t, String error) {

        RecoverCmdRes recoverCmdRes = new RecoverCmdRes();
        recoverCmdRes.setCmdStr(t.getCmdStr());
        recoverCmdRes.setCode(STATUS_500);
        recoverCmdRes.setMsg(error);
        recoverCmdRes.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        return recoverCmdRes;
    }

    @Override
    protected RecoverCmdRes fail(RecoverCmd t, int code, String error) {
        RecoverCmdRes recoverCmdRes = new RecoverCmdRes();
        recoverCmdRes.setCmdStr(t.getCmdStr());
        recoverCmdRes.setCode(code);
        recoverCmdRes.setMsg(error);
        recoverCmdRes.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        return recoverCmdRes;
    }

    @Override
    protected ICmdListener getListener() {
        return ListenerRegistrator.getInstance().getRecoverListener();
    }
}
