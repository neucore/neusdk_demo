package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.cmd.cfg.CfgCmd;
import com.neucore.neulink.cmd.cfg.CfgCmdRes;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.ServiceRegistrator;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.ListenerRegistrator;
import com.neucore.neulink.impl.listener.DefaultCfgCmdListener;
import com.neucore.neulink.util.JSonUtils;

public class DefaultCfgProcessor extends GProcessor<CfgCmd, CfgCmdRes, ActionResult<String>> {

    public DefaultCfgProcessor(Context context){
        super(context);
        ListenerRegistrator.getInstance().setExtendListener("cfg",new DefaultCfgCmdListener());
    }

    public CfgCmd parser(String payload){
        return JSonUtils.toObject(payload, CfgCmd.class);
    }

    public CfgCmdRes responseWrapper(CfgCmd cmd,ActionResult<String> result) {
        CfgCmdRes res = new CfgCmdRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        res.setData(result.getData());
        return res;
    }

    public CfgCmdRes fail(CfgCmd cmd,String message) {
        CfgCmdRes res = new CfgCmdRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(MESSAGE_FAILED);
        res.setData(message);
        return res;
    }

    public CfgCmdRes fail(CfgCmd cmd,int code,String message) {
        CfgCmdRes res = new CfgCmdRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(MESSAGE_FAILED);
        res.setData(message);
        return res;
    }
}
