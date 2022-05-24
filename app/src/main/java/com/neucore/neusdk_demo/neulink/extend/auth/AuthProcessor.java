package com.neucore.neusdk_demo.neulink.extend.auth;

import android.content.Context;

import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neusdk_demo.neulink.extend.auth.request.AuthSyncCmd;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.AuthActionResult;
import com.neucore.neusdk_demo.neulink.extend.auth.response.AuthSyncCmdRes;

/**
 * 设备授权下发
 * AuthSyncCmd:请求对象，
 * AuthSyncCmdRes：响应对象
 * AuthActionResult:actionListener的返回类型
 */
public class AuthProcessor  extends GProcessor<AuthSyncCmd, AuthSyncCmdRes, AuthActionResult> {

    public AuthProcessor(){
        this(ContextHolder.getInstance().getContext());
    }

    public AuthProcessor(Context context) {
        super(context);
    }

    @Override
    public AuthSyncCmd parser(String payload) {
        return JSonUtils.toObject(payload, AuthSyncCmd.class);
    }

    /**
     *
     * @param t 同步请求
     * @param result listener.doAction 的返回值
     * @return
     */
    @Override
    protected AuthSyncCmdRes responseWrapper(AuthSyncCmd t, AuthActionResult result) {
        AuthSyncCmdRes res = new AuthSyncCmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(t.getCmdStr());
        res.setCode(result.getCode());
        res.setMsg(result.getMessage());
        res.setData(result.getData());
        return res;
    }

    @Override
    protected AuthSyncCmdRes fail(AuthSyncCmd t, String error) {
        AuthSyncCmdRes res = new AuthSyncCmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(t.getCmdStr());
        res.setCode(500);
        res.setMsg("失败");
        res.setData(error);
        return res;
    }

    @Override
    protected AuthSyncCmdRes fail(AuthSyncCmd t, int code, String error) {
        AuthSyncCmdRes res = new AuthSyncCmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(t.getCmdStr());
        res.setCode(code);
        res.setMsg("失败");
        res.setData(error);
        return res;
    }
}
