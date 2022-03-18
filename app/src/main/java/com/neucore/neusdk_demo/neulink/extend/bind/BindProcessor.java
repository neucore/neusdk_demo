package com.neucore.neusdk_demo.neulink.extend.bind;

import android.content.Context;

import com.neucore.neulink.extend.ServiceRegistrator;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.JSonUtils;

import com.neucore.neusdk_demo.neulink.extend.bind.listener.BindActionResult;
import com.neucore.neusdk_demo.neulink.extend.bind.request.BindSyncCmd;
import com.neucore.neusdk_demo.neulink.extend.bind.response.BindSyncCmdRes;

/**
 * 绑定下发
 * **SyncCmd:请求对象，
 * **SyncCmdRes：响应对象
 * **ActionResult:actionListener的返回类型
 */
public class BindProcessor extends GProcessor<BindSyncCmd, BindSyncCmdRes, BindActionResult> {

    public BindProcessor(){
        this(ContextHolder.getInstance().getContext());
    }

    public BindProcessor(Context context) {
        super(context);
    }


    @Override
    public BindSyncCmd parser(String payload) {
        return JSonUtils.toObject(payload, BindSyncCmd.class);
    }

    @Override
    protected BindSyncCmdRes responseWrapper(BindSyncCmd bindSyncCmd, BindActionResult result) {
        BindSyncCmdRes res = new BindSyncCmdRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(bindSyncCmd.getCmdStr());
        res.setCode(result.getCode());
        res.setMsg(result.getMessage());
        res.setData(result.getData());

        return res;
    }

    @Override
    protected BindSyncCmdRes fail(BindSyncCmd bindSyncCmd, String s) {
        BindSyncCmdRes res = new BindSyncCmdRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(bindSyncCmd.getCmdStr());
        res.setCode(500);
        res.setMsg("失败");
        res.setData(s);
        return res;
    }

    @Override
    protected BindSyncCmdRes fail(BindSyncCmd bindSyncCmd, int i, String s) {
        BindSyncCmdRes res = new BindSyncCmdRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(bindSyncCmd.getCmdStr());
        res.setCode(i);
        res.setMsg("失败");
        res.setData(s);
        return res;
    }
}