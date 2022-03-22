package com.neucore.neulink.impl.proc;

import android.annotation.SuppressLint;
import android.content.Context;

import com.neucore.neulink.cmd.rmsg.HibrateCmd;
import com.neucore.neulink.cmd.rmsg.HibrateRes;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.ServiceRegistrator;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.ListenerRegistrator;
import com.neucore.neulink.impl.listener.DefaultHibrateCmdListener;
import com.neucore.neulink.util.JSonUtils;

import java.util.Map;

public class DefaultHibrateProcessor extends GProcessor<HibrateCmd, HibrateRes, ActionResult<Map<String,String>>> {

    @SuppressLint("InvalidWakeLockTag")
    public DefaultHibrateProcessor(Context context){
        super(context);
        ListenerRegistrator.getInstance().setExtendListener("hibrate",new DefaultHibrateCmdListener());
    }
   
    @Override
    public HibrateCmd parser(String payload) {
        return JSonUtils.toObject(payload, HibrateCmd.class);
    }

    @Override
    protected HibrateRes responseWrapper(HibrateCmd cmd, ActionResult<Map<String, String>> actionResult) {
        HibrateRes res = new HibrateRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        return res;
    }

    @Override
    protected HibrateRes fail(HibrateCmd cmd, String error) {
        HibrateRes res = new HibrateRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }

    @Override
    protected HibrateRes fail(HibrateCmd cmd,int code, String error) {
        HibrateRes res = new HibrateRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }

}
