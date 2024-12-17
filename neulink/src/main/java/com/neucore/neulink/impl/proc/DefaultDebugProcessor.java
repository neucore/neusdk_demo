package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.cmd.rmsg.app.DebugCmd;
import com.neucore.neulink.impl.cmd.rmsg.app.DebugRes;
import com.neucore.neulink.impl.listener.DefaultDebugCmdListener;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.util.JSonUtils;

public final class DefaultDebugProcessor extends GProcessor<DebugCmd, DebugRes, ActionResult<String>> {

    public DefaultDebugProcessor(Context context){
        super(context);
        ListenerRegistry.getInstance().setExtendListener("debug",new DefaultDebugCmdListener());
    }

    public DebugCmd parser(String payload){
        return JSonUtils.toObject(payload, DebugCmd.class);
    }

    public DebugRes responseWrapper(DebugCmd cmd, ActionResult<String> result) {
        DebugRes res = new DebugRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        res.setData(result.getData());
        return res;
    }

    public DebugRes fail(DebugCmd cmd, String message) {
        DebugRes res = new DebugRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(MESSAGE_FAILED);
        res.setData(cmd.argsToMap());
        return res;
    }
    public DebugRes fail(DebugCmd cmd, int code, String message) {
        DebugRes res = new DebugRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(MESSAGE_FAILED);
        res.setData(cmd.argsToMap());
        return res;
    }
}
