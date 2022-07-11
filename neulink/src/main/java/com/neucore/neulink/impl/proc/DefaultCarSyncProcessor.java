package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.IBlib$ObjtypeProcessor;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.impl.cmd.rrpc.CarCmd;
import com.neucore.neulink.impl.cmd.rrpc.CarCmdRes;
import com.neucore.neulink.impl.cmd.rrpc.CarPkgActionResult;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.util.ContextHolder;

/**
 * 目标库处理器
 */
public final class DefaultCarSyncProcessor implements IBlib$ObjtypeProcessor<CarCmd, CarCmdRes, CarPkgActionResult> {

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

    public String getBiz(){
        return NEULINK_BIZ_BLIB;
    }

    public String getObjType(){
        return NEULINK_BIZ_BLIB_FACE;
    }

    public CarCmdRes responseWrapper(CarCmd cmd, CarPkgActionResult result) {
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

    public CarCmdRes fail(CarCmd cmd, String message) {
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

    public CarCmdRes fail(CarCmd cmd, int code, String message) {
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
    public CarCmd buildPkg(CarCmd cmd) throws NeulinkException {
        //推送消息到达
        return cmd;
    }
}
