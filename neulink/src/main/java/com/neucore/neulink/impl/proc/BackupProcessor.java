package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.cmd.bak.BackupCmd;
import com.neucore.neulink.cmd.bak.BackupCmdRes;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.QueryActionResult;
import com.neucore.neulink.extend.ServiceRegistrator;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.ListenerRegistrator;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.JSonUtils;

import java.util.Map;

public class BackupProcessor extends GProcessor<BackupCmd, BackupCmdRes, QueryActionResult<Map<String,String>>> implements NeulinkConst {

    public BackupProcessor(Context context) {
        super(context);
    }

    @Override
    public QueryActionResult<Map<String,String>> process(NeulinkTopicParser.Topic topic, BackupCmd payload) {
        NeulinkEvent event = new NeulinkEvent(payload);
        ICmdListener<QueryActionResult,BackupCmd> listener = getListener();
        if(listener==null){
            throw new NeulinkException(STATUS_404,"backup Listener does not implemention");
        }
        QueryActionResult<Map<String,String>> result = listener.doAction(event);

        return result;
    }

    @Override
    public BackupCmd parser(String payload) {
        return JSonUtils.toObject(payload, BackupCmd.class);
    }

    @Override
    protected BackupCmdRes responseWrapper(BackupCmd cmd, QueryActionResult<Map<String,String>> result) {
        BackupCmdRes cmdRes = new BackupCmdRes();
        cmdRes.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
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
        cmdRes.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        cmdRes.setCmdStr(cmd.getCmdStr());
        cmdRes.setCode(STATUS_500);
        cmdRes.setMsg(MESSAGE_FAILED);
        cmdRes.setData(error);
        return cmdRes;
    }

    @Override
    protected BackupCmdRes fail(BackupCmd cmd, int code, String error) {
        BackupCmdRes cmdRes = new BackupCmdRes();
        cmdRes.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        cmdRes.setCmdStr(cmd.getCmdStr());
        cmdRes.setCode(code);
        cmdRes.setMsg(MESSAGE_FAILED);
        cmdRes.setData(error);
        return cmdRes;
    }

    @Override
    protected ICmdListener getListener() {
        return ListenerRegistrator.getInstance().getBackupListener();
    }
}
