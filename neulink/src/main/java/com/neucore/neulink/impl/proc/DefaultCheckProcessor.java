package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.cmd.check.CheckCmd;
import com.neucore.neulink.cmd.check.CheckCmdRes;
import com.neucore.neulink.extend.QueryActionResult;
import com.neucore.neulink.extend.ServiceRegistrator;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.ListenerRegistrator;
import com.neucore.neulink.impl.listener.DefaultFaceCheckListener;
import com.neucore.neulink.util.JSonUtils;

import java.util.Map;

public class DefaultCheckProcessor extends GProcessor<CheckCmd, CheckCmdRes, QueryActionResult<Map<String,Object>>> {

    public DefaultCheckProcessor(Context context) {
        super(context);
        ListenerRegistrator.getInstance().setExtendListener("check",new DefaultFaceCheckListener());
    }

    @Override
    public CheckCmd parser(String payload) {
        return JSonUtils.toObject(payload, CheckCmd.class);
    }

    @Override
    protected CheckCmdRes responseWrapper(CheckCmd t, QueryActionResult<Map<String,Object>> result) {
        CheckCmdRes cmdRes = new CheckCmdRes();
        cmdRes.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        cmdRes.setCmdStr(t.getCmdStr());
        cmdRes.setCode(STATUS_200);
        cmdRes.setMsg(MESSAGE_SUCCESS);
        cmdRes.setObjtype(t.getObjtype());
        cmdRes.setDatas((String)result.getData().get("card_ids"));
        cmdRes.setData(result.getData());
        return cmdRes;
    }

    @Override
    protected CheckCmdRes fail(CheckCmd t, String error) {
        CheckCmdRes cmdRes = new CheckCmdRes();
        cmdRes.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        cmdRes.setCmdStr(t.getCmdStr());
        cmdRes.setCode(STATUS_500);
        cmdRes.setMsg(MESSAGE_FAILED);
        cmdRes.setObjtype(t.getObjtype());
        cmdRes.setData(error);
        return cmdRes;
    }

    @Override
    protected CheckCmdRes fail(CheckCmd t, int code, String error) {
        CheckCmdRes cmdRes = new CheckCmdRes();
        cmdRes.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        cmdRes.setCmdStr(t.getCmdStr());
        cmdRes.setCode(code);
        cmdRes.setMsg(MESSAGE_FAILED);
        cmdRes.setObjtype(t.getObjtype());
        cmdRes.setData(error);
        return cmdRes;
    }
}
