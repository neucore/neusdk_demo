package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.recv.RecoverCmd;
import com.neucore.neulink.impl.cmd.recv.RecoverCmdRes;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.listener.DefaultRecoverCmdListener;
import com.neucore.neulink.util.JSonUtils;

public class DefaultRecoverProcessor extends GProcessor<RecoverCmd, RecoverCmdRes, ActionResult<String>> implements NeulinkConst {

    public DefaultRecoverProcessor(Context context) {
        super(context);
        ListenerRegistry.getInstance().setExtendListener("recover",new DefaultRecoverCmdListener());
    }

    @Override
    public RecoverCmd parser(String payload) {
        return JSonUtils.toObject(payload, RecoverCmd.class);
    }

    @Override
    protected RecoverCmdRes responseWrapper(RecoverCmd t, ActionResult<String> result) {
        RecoverCmdRes recoverCmdRes = new RecoverCmdRes();
        recoverCmdRes.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        recoverCmdRes.setCmdStr(t.getCmdStr());
        recoverCmdRes.setCode(STATUS_200);
        recoverCmdRes.setMsg(MESSAGE_SUCCESS);

        return recoverCmdRes;
    }

    @Override
    protected RecoverCmdRes fail(RecoverCmd t, String error) {
        RecoverCmdRes recoverCmdRes = new RecoverCmdRes();
        recoverCmdRes.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        recoverCmdRes.setCmdStr(t.getCmdStr());
        recoverCmdRes.setCode(STATUS_500);
        recoverCmdRes.setMsg(MESSAGE_FAILED);
        recoverCmdRes.setData(error);
        return recoverCmdRes;
    }

    @Override
    protected RecoverCmdRes fail(RecoverCmd t, int code, String error) {
        RecoverCmdRes recoverCmdRes = new RecoverCmdRes();
        recoverCmdRes.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        recoverCmdRes.setCmdStr(t.getCmdStr());
        recoverCmdRes.setCode(code);
        recoverCmdRes.setMsg(MESSAGE_FAILED);
        recoverCmdRes.setData(error);
        return recoverCmdRes;
    }
}
