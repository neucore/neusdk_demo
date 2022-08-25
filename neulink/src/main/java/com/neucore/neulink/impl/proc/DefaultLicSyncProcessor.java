package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.IBlib$ObjtypeProcessor;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.impl.cmd.rrpc.LicCmd;
import com.neucore.neulink.impl.cmd.rrpc.LicCmdRes;
import com.neucore.neulink.impl.cmd.rrpc.LicPkgActionResult;
import com.neucore.neulink.impl.cmd.rrpc.PkgActionResult;
import com.neucore.neulink.impl.cmd.rrpc.PkgCmd;
import com.neucore.neulink.impl.cmd.rrpc.PkgRes;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;

import java.util.List;

/**
 * 目标库处理器
 */
public final class DefaultLicSyncProcessor implements IBlib$ObjtypeProcessor<PkgCmd, PkgRes, PkgActionResult<List<String>>> {

    private String libDir;
    private Context context;
    protected String TAG = TAG_PREFIX+this.getClass().getSimpleName();
    public DefaultLicSyncProcessor(){
        Context context = ContextHolder.getInstance().getContext();
        libDir = DeviceUtils.getTmpPath(context)+"/licDir";
    }
    public DefaultLicSyncProcessor(Context context){
        this.context = context;
        libDir = DeviceUtils.getTmpPath(context)+"/licDir";
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

    public PkgRes responseWrapper(PkgCmd cmd, PkgActionResult<List<String>> result) {
        LicCmdRes res = new LicCmdRes();
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
        LicCmdRes res = new LicCmdRes();
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
        LicCmdRes res = new LicCmdRes();
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
        LicCmd licCmd = new LicCmd();
        return licCmd;
    }
}
