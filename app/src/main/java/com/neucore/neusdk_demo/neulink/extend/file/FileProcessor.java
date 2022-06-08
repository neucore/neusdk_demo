package com.neucore.neusdk_demo.neulink.extend.file;

import android.content.Context;

import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neusdk_demo.neulink.extend.file.listener.FileActionResult;
import com.neucore.neusdk_demo.neulink.extend.file.request.FileSyncCmd;
import com.neucore.neusdk_demo.neulink.extend.file.response.FileSyncCmdRes;


/**
 * 设备添加文件请求下发
 * **SyncCmd:请求对象，
 * **SyncCmdRes：响应对象
 * **ActionResult:actionListener的返回类型
 */
public class FileProcessor extends GProcessor<FileSyncCmd, FileSyncCmdRes, FileActionResult> {

    public FileProcessor() {
        this(ContextHolder.getInstance().getContext());
    }

    public FileProcessor(Context context) {
        super(context);
    }

    @Override
    public FileSyncCmd parser(String payload) {
//        LogUtils.setLog(TAG, "FileProcessor..parser()..payload:" + payload);
        return JSonUtils.toObject(payload, FileSyncCmd.class);
    }

    @Override
    protected FileSyncCmdRes responseWrapper(FileSyncCmd fileSyncCmd, FileActionResult fileActionResult) {
//        LogUtils.setLog(TAG, "FileSyncCmdRes()..responseWrapper");
        FileSyncCmdRes res = new FileSyncCmdRes();
        res.setCmdStr(fileSyncCmd.getCmdStr());
        res.setCode(fileActionResult.getCode());
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setMsg(fileActionResult.getMessage());
        return res;
    }

    @Override
    protected FileSyncCmdRes fail(FileSyncCmd fileSyncCmd, String s) {
//        LogUtils.setLog(TAG, "FileSyncCmdRes()..fail");
        FileSyncCmdRes res = new FileSyncCmdRes();
        res.setCmdStr(fileSyncCmd.getCmdStr());
        res.setCode(500);
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setData(s);
        res.setMsg("失败");
        return res;
    }

    @Override
    protected FileSyncCmdRes fail(FileSyncCmd fileSyncCmd, int i, String s) {
//        LogUtils.setLog(TAG, "FileSyncCmdRes fail " + s);
        FileSyncCmdRes res = new FileSyncCmdRes();
        res.setCmdStr(fileSyncCmd.getCmdStr());
        res.setCode(i);
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setData(s);
        res.setMsg("失败");
        return res;
    }
}
