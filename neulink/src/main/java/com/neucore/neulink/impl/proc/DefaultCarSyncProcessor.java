package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.IBlib$ObjtypeProcessor;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.impl.cmd.rrpc.CarCmd;
import com.neucore.neulink.impl.cmd.rrpc.CarCmdRes;
import com.neucore.neulink.impl.cmd.rrpc.CarPkgActionResult;
import com.neucore.neulink.impl.cmd.rrpc.LicCmd;
import com.neucore.neulink.impl.cmd.rrpc.PkgActionResult;
import com.neucore.neulink.impl.cmd.rrpc.PkgCmd;
import com.neucore.neulink.impl.cmd.rrpc.PkgRes;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.util.ContextHolder;

import java.util.List;

/**
 * 目标库处理器
 */
public final class DefaultCarSyncProcessor implements IBlib$ObjtypeProcessor<PkgCmd, PkgRes, PkgActionResult<List<String>>> {

    private String libDir;
    private Context context;
    protected String TAG = TAG_PREFIX+this.getClass().getSimpleName();
    public DefaultCarSyncProcessor(){
        Context context = ContextHolder.getInstance().getContext();
    }
    public DefaultCarSyncProcessor(Context context){
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public PkgRes responseWrapper(PkgCmd cmd, PkgActionResult<List<String>> result) {
        CarCmdRes res = new CarCmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(result.getCode());
        res.setMsg(result.getMessage());
        res.setObjtype(cmd.getObjtype());
        res.setTotal(cmd.getTotal());
        res.setPages(cmd.getPages());
        res.setOffset(result.getOffset());
        res.setFailed(result.getData());
        return res;
    }

    public PkgRes fail(PkgCmd cmd, String message) {
        CarCmdRes res = new CarCmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(message);
        res.setObjtype(cmd.getObjtype());
        res.setTotal(cmd.getTotal());
        res.setPages(cmd.getPages());
        res.setOffset(cmd.getOffset());
        return res;
    }

    public PkgRes fail(PkgCmd cmd, int code, String message) {
        CarCmdRes res = new CarCmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(message);
        res.setObjtype(cmd.getObjtype());
        res.setTotal(cmd.getTotal());
        res.setPages(cmd.getPages());
        res.setOffset(cmd.getOffset());
        return res;
    }
    @Override
    public PkgCmd buildPkg(PkgCmd cmd) throws NeulinkException {
        String cmdStr = cmd.getCmdStr();
        String jsonUrl = cmd.getDataUrl();
        long offset = cmd.getOffset();
        //TODO 实现
        CarCmd licCmd = new CarCmd();
        return licCmd;
    }
}
