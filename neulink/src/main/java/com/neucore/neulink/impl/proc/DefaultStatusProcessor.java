package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.cmd.rmsg.StatusCmd;
import com.neucore.neulink.impl.cmd.rmsg.StatusCmdRes;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.util.JSonUtils;

import java.util.Map;

public final class DefaultStatusProcessor extends GProcessor<StatusCmd, StatusCmdRes, ActionResult<Map<String, String>>> {

    public DefaultStatusProcessor(Context context){
        super(context);
    }

    @Override
    public StatusCmd parser(String payload) {
        return JSonUtils.toObject(payload, StatusCmd.class);
    }

    @Override
    protected StatusCmdRes responseWrapper(StatusCmd cmd, ActionResult<Map<String, String>> actionResult) {
        StatusCmdRes res = new StatusCmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        return res;
    }

    @Override
    protected StatusCmdRes fail(StatusCmd cmd, String error) {
        StatusCmdRes res = new StatusCmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }

    @Override
    protected StatusCmdRes fail(StatusCmd cmd,int code, String error) {
        StatusCmdRes res = new StatusCmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }
}
