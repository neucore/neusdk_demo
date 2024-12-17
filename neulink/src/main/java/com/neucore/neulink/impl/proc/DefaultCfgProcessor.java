package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.cmd.cfg.CfgCmd;
import com.neucore.neulink.impl.cmd.cfg.CfgCmdRes;
import com.neucore.neulink.impl.listener.DefaultCfgCmdListener;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.util.JSonUtils;

public final class DefaultCfgProcessor extends GProcessor<CfgCmd, CfgCmdRes, ActionResult<String>> {

    public DefaultCfgProcessor(Context context){
        super(context);
        ListenerRegistry.getInstance().setExtendListener("cfg",new DefaultCfgCmdListener());
    }

    public CfgCmd parser(String payload){
        return JSonUtils.toObject(payload, CfgCmd.class);
    }

    public CfgCmdRes responseWrapper(CfgCmd cmd,ActionResult<String> result) {
        CfgCmdRes res = new CfgCmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        res.setData(result.getData());
        return res;
    }

    public CfgCmdRes fail(CfgCmd cmd,String message) {
        CfgCmdRes res = new CfgCmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(MESSAGE_FAILED);
        res.setData(message);
        return res;
    }

    public CfgCmdRes fail(CfgCmd cmd,int code,String message) {
        CfgCmdRes res = new CfgCmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(MESSAGE_FAILED);
        res.setData(message);
        return res;
    }
}
