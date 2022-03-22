package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.cmd.upd.UgrdeCmd;
import com.neucore.neulink.cmd.upd.UgrdeCmdRes;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.ServiceRegistrator;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.ListenerRegistrator;
import com.neucore.neulink.impl.listener.DefaultFirewareCmdListener;
import com.neucore.neulink.util.JSonUtils;

/**
 * NeuSDK升级/或者固件升级
 * @deprecated
 */
public class DefaultFirewareProcessor extends GProcessor<UgrdeCmd, UgrdeCmdRes, ActionResult<String>> {

    public DefaultFirewareProcessor(Context context){
        super(context);
        ListenerRegistrator.getInstance().setExtendListener("fireware",new DefaultFirewareCmdListener());
    }

    @Override
    public UgrdeCmd parser(String payload) {
        return JSonUtils.toObject(payload, UgrdeCmd.class);
    }

    @Override
    protected UgrdeCmdRes responseWrapper(UgrdeCmd cmd, ActionResult<String> result) {
        UgrdeCmdRes res = new UgrdeCmdRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        res.setData(result.getData());
        return res;
    }

    @Override
    protected UgrdeCmdRes fail(UgrdeCmd cmd, String error) {
        UgrdeCmdRes res = new UgrdeCmdRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }

    @Override
    protected UgrdeCmdRes fail(UgrdeCmd cmd,int code, String error) {
        UgrdeCmdRes res = new UgrdeCmdRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }
}
