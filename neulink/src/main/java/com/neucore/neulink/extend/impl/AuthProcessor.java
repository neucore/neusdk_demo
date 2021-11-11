package com.neucore.neulink.extend.impl;

import android.content.Context;

import com.neucore.neulink.cmd.rrpc.AuthSyncCmd;
import com.neucore.neulink.cmd.rrpc.AuthSyncCmdRes;
import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;

/**
 * 设备授权下发
 * AuthSyncCmd:请求对象，
 * AuthSyncCmdRes：响应对象
 * String:actionListener的返回类型
 */
public class AuthProcessor  extends GProcessor<AuthSyncCmd, AuthSyncCmdRes,String> {

    public AuthProcessor(){
        this(ContextHolder.getInstance().getContext());
    }

    public AuthProcessor(Context context) {
        super(context);
    }

    @Override
    public AuthSyncCmd parser(String payload) {
        return (AuthSyncCmd) JSonUtils.toObject(payload, AuthSyncCmd.class);
    }

    @Override
    protected AuthSyncCmdRes responseWrapper(AuthSyncCmd t, String result) {
        AuthSyncCmdRes res = new AuthSyncCmdRes();
        res.setCmdStr(t.getCmdStr());
        res.setCode(200);
        res.setDeviceId(DeviceUtils.getDeviceId(getContext()));
        res.setData(result);
        res.setMsg("成功");
        return res;
    }

    @Override
    protected AuthSyncCmdRes fail(AuthSyncCmd t, String error) {
        AuthSyncCmdRes res = new AuthSyncCmdRes();
        res.setCmdStr(t.getCmdStr());
        res.setCode(500);
        res.setDeviceId(DeviceUtils.getDeviceId(getContext()));
        res.setData(error);
        res.setMsg("失败");
        return res;
    }

    @Override
    protected AuthSyncCmdRes fail(AuthSyncCmd t, int code, String error) {
        AuthSyncCmdRes res = new AuthSyncCmdRes();
        res.setCmdStr(t.getCmdStr());
        res.setCode(code);
        res.setDeviceId(DeviceUtils.getDeviceId(getContext()));
        res.setData(error);
        res.setMsg("失败");
        return res;
    }
}
