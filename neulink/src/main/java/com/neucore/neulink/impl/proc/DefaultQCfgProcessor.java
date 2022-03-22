package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.cmd.cfg.CfgCmd;
import com.neucore.neulink.cmd.cfg.CfgItem;
import com.neucore.neulink.cmd.cfg.CfgQueryCmdRes;
import com.neucore.neulink.cmd.cfg.QCfgCmd;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.ServiceRegistrator;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.ListenerRegistrator;
import com.neucore.neulink.impl.listener.DefaultQCfgCmdListener;
import com.neucore.neulink.util.JSonUtils;

public class DefaultQCfgProcessor extends GProcessor<QCfgCmd, CfgQueryCmdRes, ActionResult<CfgItem[]>> {


    public DefaultQCfgProcessor(Context context){
        super(context);
        ListenerRegistrator.getInstance().setExtendListener("qcfg",new DefaultQCfgCmdListener());
    }

    public QCfgCmd parser(String payload){
        return JSonUtils.toObject(payload, QCfgCmd.class);
    }

    public CfgQueryCmdRes responseWrapper(QCfgCmd cmd, ActionResult<CfgItem[]> actionResult) {
        CfgQueryCmdRes res = new CfgQueryCmdRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        res.setData(actionResult.getData());
        return res;
    }

    public CfgQueryCmdRes fail(QCfgCmd cmd,String message) {
        CfgQueryCmdRes res = new CfgQueryCmdRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(MESSAGE_FAILED);
        res.setData(message);
        return res;
    }
    public CfgQueryCmdRes fail(QCfgCmd cmd,int code,String message) {
        CfgQueryCmdRes res = new CfgQueryCmdRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(MESSAGE_FAILED);
        res.setData(message);
        return res;
    }
}
