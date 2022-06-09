package com.neucore.neulink.impl.proc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

import com.neucore.neulink.impl.cmd.rmsg.AwakenCmd;
import com.neucore.neulink.impl.cmd.rmsg.AwakenRes;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.listener.DefaultAwakenCmdListener;
import com.neucore.neulink.util.JSonUtils;

import java.util.Map;

public final class DefaultAwakenProcessor extends GProcessor<AwakenCmd, AwakenRes, ActionResult<Map<String,String>>> {

    private PowerManager pm =null;
    PowerManager.WakeLock wakeLock = null;

    @SuppressLint("InvalidWakeLockTag")
    public DefaultAwakenProcessor(Context context){
        super(context);
        ListenerRegistry.getInstance().setExtendListener("awaken",new DefaultAwakenCmdListener());
    }

    @Override
    public AwakenCmd parser(String payload) {
        return JSonUtils.toObject(payload, AwakenCmd.class);
    }

    @Override
    protected AwakenRes responseWrapper(AwakenCmd cmd, ActionResult<Map<String, String>> actionResult) {
        AwakenRes res = new AwakenRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        res.setData(actionResult.getData());
        return res;
    }

    @Override
    protected AwakenRes fail(AwakenCmd cmd, String error) {
        AwakenRes res = new AwakenRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }

    @Override
    protected AwakenRes fail(AwakenCmd cmd,int code, String error) {
        AwakenRes res = new AwakenRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }
}
