package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.cmd.bak.BackupCmd;
import com.neucore.neulink.impl.cmd.bak.BackupCmdRes;
import com.neucore.neulink.impl.listener.DefaultBackupCmdListener;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.util.JSonUtils;

import java.util.Map;

public final class DefaultBackupProcessor extends GProcessor<BackupCmd, BackupCmdRes, ActionResult<Map<String,String>>> implements NeulinkConst {

    public DefaultBackupProcessor(Context context) {
        super(context);
        ListenerRegistry.getInstance().setExtendListener("backup",new DefaultBackupCmdListener());
    }

    @Override
    public BackupCmd parser(String payload) {
        return JSonUtils.toObject(payload, BackupCmd.class);
    }

    @Override
    protected BackupCmdRes responseWrapper(BackupCmd cmd, ActionResult<Map<String,String>> result) {
        BackupCmdRes cmdRes = new BackupCmdRes();
        cmdRes.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        cmdRes.setCmdStr(cmd.getCmdStr());
        cmdRes.setCode(STATUS_200);
        cmdRes.setMsg(MESSAGE_SUCCESS);
        cmdRes.setUrl(result.getData().get("url"));
        cmdRes.setData(result.getData());
        return cmdRes;
    }

    @Override
    protected BackupCmdRes fail(BackupCmd cmd, String error) {
        BackupCmdRes cmdRes = new BackupCmdRes();
        cmdRes.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        cmdRes.setCmdStr(cmd.getCmdStr());
        cmdRes.setCode(STATUS_500);
        cmdRes.setMsg(MESSAGE_FAILED);
        cmdRes.setData(error);
        return cmdRes;
    }

    @Override
    protected BackupCmdRes fail(BackupCmd cmd, int code, String error) {
        BackupCmdRes cmdRes = new BackupCmdRes();
        cmdRes.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        cmdRes.setCmdStr(cmd.getCmdStr());
        cmdRes.setCode(code);
        cmdRes.setMsg(MESSAGE_FAILED);
        cmdRes.setData(error);
        return cmdRes;
    }
}
