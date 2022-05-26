package com.neucore.neulink.impl.proc;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.neucore.neulink.IBlib$ObjtypeProcessor;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.cmd.rrpc.LicCmd;
import com.neucore.neulink.impl.cmd.rrpc.LicCmdRes;
import com.neucore.neulink.impl.cmd.rrpc.FaceData;
import com.neucore.neulink.impl.cmd.rrpc.LicPkgActionResult;
import com.neucore.neulink.impl.cmd.rrpc.LicCmd;
import com.neucore.neulink.impl.cmd.rrpc.LicCmdRes;
import com.neucore.neulink.impl.cmd.rrpc.LicPkgActionResult;
import com.neucore.neulink.impl.cmd.rrpc.SyncInfo;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.FileUtils;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.NeuHttpHelper;
import com.neucore.neulink.util.RequestContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 目标库处理器
 */
public class DefaultLicSyncProcessor implements IBlib$ObjtypeProcessor<LicCmd, LicCmdRes, LicPkgActionResult> {

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

    public LicCmdRes responseWrapper(LicCmd cmd, LicPkgActionResult result) {
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

    public LicCmdRes fail(LicCmd cmd, String message) {
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

    public LicCmdRes fail(LicCmd cmd, int code, String message) {
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
    public LicCmd buildPkg(LicCmd cmd) throws NeulinkException {
        //推送消息到达
        return cmd;
    }
}
