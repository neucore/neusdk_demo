package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.cmd.rmsg.app.AlogUpgrCmd;
import com.neucore.neulink.impl.cmd.rmsg.app.AlogUpgrRes;
import com.neucore.neulink.impl.listener.DefaultAlogCmdListener;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.util.JSonUtils;

public final class DefaultALogProcessor extends GProcessor<AlogUpgrCmd,AlogUpgrRes, ActionResult<String>> {

    public DefaultALogProcessor(Context context){
        super(context);
    }
    
    public AlogUpgrCmd parser(String payload){
        return JSonUtils.toObject(payload, AlogUpgrCmd.class);
    }

    public AlogUpgrRes responseWrapper(AlogUpgrCmd cmd, ActionResult<String> actionResult) {
        AlogUpgrRes res = new AlogUpgrRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        res.setVinfo(cmd.getVinfo());
        res.setData(actionResult.getData());
        return res;
    }

    public AlogUpgrRes fail(AlogUpgrCmd cmd, String message) {
        AlogUpgrRes res = new AlogUpgrRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCode(STATUS_500);
        res.setMsg(MESSAGE_FAILED);
        res.setVinfo(cmd.getVinfo());
        res.setData(message);
        return res;
    }
    public AlogUpgrRes fail(AlogUpgrCmd cmd, int code, String message) {
        AlogUpgrRes res = new AlogUpgrRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCode(code);
        res.setMsg(MESSAGE_FAILED);
        res.setVinfo(cmd.getVinfo());
        res.setData(message);
        return res;
    }
}
