package com.neucore.neulink.impl.proc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

import com.neucore.neulink.cmd.rmsg.RebootCmd;
import com.neucore.neulink.cmd.rmsg.RebootRes;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.ServiceRegistrator;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.ListenerRegistrator;
import com.neucore.neulink.impl.listener.DefaultRebootCmdListener;
import com.neucore.neulink.util.JSonUtils;

import java.util.Map;

public class DefaultRebootProcessor extends GProcessor<RebootCmd, RebootRes, ActionResult<Map<String,String>>> {

    private PowerManager pm =null;
    PowerManager.WakeLock wakeLock = null;

    @SuppressLint("InvalidWakeLockTag")
    public DefaultRebootProcessor(Context context){
        super(context);
        ListenerRegistrator.getInstance().setExtendListener("reboot",new DefaultRebootCmdListener());
    }

    @Override
    public RebootCmd parser(String payload) {
        return JSonUtils.toObject(payload, RebootCmd.class);
    }

    @Override
    protected RebootRes responseWrapper(RebootCmd cmd, ActionResult<Map<String,String>>  result) {
        RebootRes res = new RebootRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        return res;
    }

    @Override
    protected RebootRes fail(RebootCmd cmd, String error) {
        RebootRes res = new RebootRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }

    @Override
    protected RebootRes fail(RebootCmd cmd,int code, String error) {
        RebootRes res = new RebootRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }
}