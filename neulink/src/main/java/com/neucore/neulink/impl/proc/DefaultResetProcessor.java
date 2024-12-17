package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.Cmd;
import com.neucore.neulink.impl.CmdRes;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.listener.DefaultResetCmdListener;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.util.JSonUtils;

public final class DefaultResetProcessor extends GProcessor<Cmd, CmdRes, ActionResult> {
    public DefaultResetProcessor(Context context){
        super(context);
        ListenerRegistry.getInstance().setExtendListener("reset",new DefaultResetCmdListener());
    }
    @Override
    protected Cmd parser(String payload) {
        return JSonUtils.toObject(payload,Cmd.class);
    }

    @Override
    protected CmdRes responseWrapper(Cmd cmd, ActionResult actionResult) {
        CmdRes res = new CmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        res.setData(actionResult.getData());
        return res;
    }

    @Override
    protected CmdRes fail(Cmd cmd, String error) {
        CmdRes res = new CmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }

    @Override
    protected CmdRes fail(Cmd cmd, int code, String error) {
        CmdRes res = new CmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }
}
