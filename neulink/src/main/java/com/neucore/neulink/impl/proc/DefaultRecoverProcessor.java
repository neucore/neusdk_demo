package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.cmd.recv.RecoverCmd;
import com.neucore.neulink.cmd.recv.RecoverCmdRes;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.ServiceRegistrator;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.ListenerRegistrator;
import com.neucore.neulink.impl.listener.DefaultRecoverCmdListener;
import com.neucore.neulink.util.JSonUtils;

public class DefaultRecoverProcessor extends GProcessor<RecoverCmd, RecoverCmdRes, ActionResult<String>> implements NeulinkConst {

    public DefaultRecoverProcessor(Context context) {
        super(context);
        ListenerRegistrator.getInstance().setExtendListener("recover",new DefaultRecoverCmdListener());
    }

    @Override
    public RecoverCmd parser(String payload) {
        return JSonUtils.toObject(payload, RecoverCmd.class);
    }

    @Override
    protected RecoverCmdRes responseWrapper(RecoverCmd t, ActionResult<String> result) {
        RecoverCmdRes recoverCmdRes = new RecoverCmdRes();
        recoverCmdRes.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        recoverCmdRes.setCmdStr(t.getCmdStr());
        recoverCmdRes.setCode(STATUS_200);
        recoverCmdRes.setMsg(MESSAGE_SUCCESS);

        return recoverCmdRes;
    }

    @Override
    protected RecoverCmdRes fail(RecoverCmd t, String error) {
        RecoverCmdRes recoverCmdRes = new RecoverCmdRes();
        recoverCmdRes.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        recoverCmdRes.setCmdStr(t.getCmdStr());
        recoverCmdRes.setCode(STATUS_500);
        recoverCmdRes.setMsg(MESSAGE_FAILED);
        recoverCmdRes.setData(error);
        return recoverCmdRes;
    }

    @Override
    protected RecoverCmdRes fail(RecoverCmd t, int code, String error) {
        RecoverCmdRes recoverCmdRes = new RecoverCmdRes();
        recoverCmdRes.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        recoverCmdRes.setCmdStr(t.getCmdStr());
        recoverCmdRes.setCode(code);
        recoverCmdRes.setMsg(MESSAGE_FAILED);
        recoverCmdRes.setData(error);
        return recoverCmdRes;
    }
}
