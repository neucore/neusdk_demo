package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.IClib$ObjtypeProcessor;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.impl.QueryActionResult;
import com.neucore.neulink.impl.cmd.check.CheckCmd;
import com.neucore.neulink.impl.cmd.check.CheckCmdRes;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.util.ContextHolder;

import java.util.Map;

public final class DefaultLicCheckProcessor implements IClib$ObjtypeProcessor<CheckCmd, CheckCmdRes, QueryActionResult<Map<String,Object>>> {
    private Context context;
    public DefaultLicCheckProcessor() {
        this.context = ContextHolder.getInstance().getContext();
    }
    public DefaultLicCheckProcessor(Context context) {
        this.context = context;
    }

    @Override
    public CheckCmdRes responseWrapper(CheckCmd t, QueryActionResult<Map<String,Object>> result) {
        CheckCmdRes cmdRes = new CheckCmdRes();
        cmdRes.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        cmdRes.setCmdStr(t.getCmdStr());
        cmdRes.setCode(STATUS_200);
        cmdRes.setMsg(MESSAGE_SUCCESS);
        cmdRes.setObjtype(t.getObjtype());
        cmdRes.setDatas((String)result.getData().get("ids"));
        cmdRes.setData(result.getData());
        return cmdRes;
    }

    @Override
    public CheckCmdRes fail(CheckCmd t, String error) {
        CheckCmdRes cmdRes = new CheckCmdRes();
        cmdRes.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        cmdRes.setCmdStr(t.getCmdStr());
        cmdRes.setCode(STATUS_500);
        cmdRes.setMsg(MESSAGE_FAILED);
        cmdRes.setObjtype(t.getObjtype());
        cmdRes.setData(error);
        return cmdRes;
    }

    @Override
    public CheckCmdRes fail(CheckCmd t, int code, String error) {
        CheckCmdRes cmdRes = new CheckCmdRes();
        cmdRes.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        cmdRes.setCmdStr(t.getCmdStr());
        cmdRes.setCode(code);
        cmdRes.setMsg(MESSAGE_FAILED);
        cmdRes.setObjtype(t.getObjtype());
        cmdRes.setData(error);
        return cmdRes;
    }

    @Override
    public CheckCmd buildPkg(CheckCmd cmd) throws NeulinkException {
        return cmd;
    }
}
