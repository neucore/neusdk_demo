package com.neucore.neulink.impl.proc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

import com.neucore.neulink.cmd.rmsg.AwakenCmd;
import com.neucore.neulink.cmd.rmsg.AwakenRes;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.ServiceRegistrator;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.ListenerRegistrator;
import com.neucore.neulink.impl.listener.DefaultAwakenCmdListener;
import com.neucore.neulink.util.JSonUtils;

import java.util.Map;

public class DefaultAwakenProcessor extends GProcessor<AwakenCmd, AwakenRes, ActionResult<Map<String,String>>> {

    private PowerManager pm =null;
    PowerManager.WakeLock wakeLock = null;

    @SuppressLint("InvalidWakeLockTag")
    public DefaultAwakenProcessor(Context context){
        super(context);
        ListenerRegistrator.getInstance().setExtendListener("awaken",new DefaultAwakenCmdListener());
    }

    @Override
    public AwakenCmd parser(String payload) {
        return JSonUtils.toObject(payload, AwakenCmd.class);
    }

    @Override
    protected AwakenRes responseWrapper(AwakenCmd cmd, ActionResult<Map<String, String>> actionResult) {
        AwakenRes res = new AwakenRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        res.setData(actionResult.getData());
        return res;
    }

    @Override
    protected AwakenRes fail(AwakenCmd cmd, String error) {
        AwakenRes res = new AwakenRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }

    @Override
    protected AwakenRes fail(AwakenCmd cmd,int code, String error) {
        AwakenRes res = new AwakenRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }
}