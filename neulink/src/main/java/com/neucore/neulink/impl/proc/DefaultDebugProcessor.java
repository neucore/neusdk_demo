package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.cmd.rmsg.app.DebugCmd;
import com.neucore.neulink.cmd.rmsg.app.DebugRes;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.ServiceRegistrator;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.ListenerRegistrator;
import com.neucore.neulink.impl.listener.DefaultDebugCmdListener;
import com.neucore.neulink.util.JSonUtils;

public class DefaultDebugProcessor extends GProcessor<DebugCmd, DebugRes, ActionResult<String>> {

    public DefaultDebugProcessor(Context context){
        super(context);
        ListenerRegistrator.getInstance().setExtendListener("debug",new DefaultDebugCmdListener());
    }

    public DebugCmd parser(String payload){
        return JSonUtils.toObject(payload, DebugCmd.class);
    }

    public DebugRes responseWrapper(DebugCmd cmd, ActionResult<String> result) {
        DebugRes res = new DebugRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        res.setData(result.getData());
        return res;
    }

    public DebugRes fail(DebugCmd cmd, String message) {
        DebugRes res = new DebugRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(MESSAGE_FAILED);
        res.setData(message);
        return res;
    }
    public DebugRes fail(DebugCmd cmd, int code, String message) {
        DebugRes res = new DebugRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(MESSAGE_FAILED);
        res.setData(message);
        return res;
    }
}
