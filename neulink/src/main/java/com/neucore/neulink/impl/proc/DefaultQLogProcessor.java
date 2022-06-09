package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.impl.cmd.rmsg.log.DnloadCmd;
import com.neucore.neulink.impl.cmd.rmsg.log.DnloadRes;
import com.neucore.neulink.impl.cmd.rmsg.log.LogActionResult;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.impl.listener.DefaultQLogCmdListener;
import com.neucore.neulink.util.JSonUtils;

public final class DefaultQLogProcessor extends GProcessor<DnloadCmd, DnloadRes, LogActionResult> {

    public DefaultQLogProcessor(Context context){
        super(context);
        ListenerRegistry.getInstance().setExtendListener("qlog",new DefaultQLogCmdListener());
    }

    @Override
    public DnloadCmd parser(String payload) {
        return JSonUtils.toObject(payload, DnloadCmd.class);
    }

    @Override
    protected DnloadRes responseWrapper(DnloadCmd cmd, LogActionResult result) {
        DnloadRes res = new DnloadRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setType(cmd.getType());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        res.setPages(result.getPages());
        res.setOffset(result.getOffset());
        res.setUrl(result.getUrl());
        res.setMd5(result.getMd5());

        return res;
    }

    @Override
    protected DnloadRes fail(DnloadCmd cmd, String error) {
        DnloadRes res = new DnloadRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setType(cmd.getType());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }

    @Override
    protected DnloadRes fail(DnloadCmd cmd, int code, String error) {
        DnloadRes res = new DnloadRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setType(cmd.getType());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }
}
