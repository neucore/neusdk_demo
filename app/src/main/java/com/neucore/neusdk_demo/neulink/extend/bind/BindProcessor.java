package com.neucore.neusdk_demo.neulink.extend.bind;

import android.content.Context;

import com.neucore.neulink.extend.ServiceFactory;
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
        //LogUtil.setLog(TAG, "BindProcessor..parser=" + payload);
        return (BindSyncCmd) JSonUtils.toObject(payload, BindSyncCmd.class);
    }

    @Override
    protected BindSyncCmdRes responseWrapper(BindSyncCmd bindSyncCmd, BindActionResult bindActionResult) {
//        LogUtilil.setLog(TAG, "BindSyncCmdRes responseWrapper" );
        BindSyncCmdRes res = new BindSyncCmdRes();
        res.setCmdStr(bindSyncCmd.getCmdStr());
        res.setCode(200);
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setData(bindActionResult);
        res.setMsg("成功");
        return res;
    }

    @Override
    protected BindSyncCmdRes fail(BindSyncCmd bindSyncCmd, String s) {
//        LogUtil.setLog(TAG, "BindSyncCmdRes fail" );
        BindSyncCmdRes res = new BindSyncCmdRes();
        res.setCmdStr(bindSyncCmd.getCmdStr());
        res.setCode(500);
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setData(s);
        res.setMsg("失败");
        return res;
    }

    @Override
    protected BindSyncCmdRes fail(BindSyncCmd bindSyncCmd, int i, String s) {
//        LogUtil.setLog(TAG, "BindSyncCmdRes fail+"+i );
        BindSyncCmdRes res = new BindSyncCmdRes();
        res.setCmdStr(bindSyncCmd.getCmdStr());
        res.setCode(i);
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setData(s);
        res.setMsg("失败");
        return res;
    }
}