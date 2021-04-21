package com.neucore.neulink.proc;

import android.content.Context;

import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.bak.BackupCmd;
import com.neucore.neulink.bak.BackupCmdRes;
import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.QueryResult;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeuLinkConstant;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;

import java.util.Map;

public class BackupProcessor extends GProcessor<BackupCmd, BackupCmdRes,String> implements NeuLinkConstant {

    public BackupProcessor(Context context) {
        super(context);
    }

    @Override
    public String process(NeulinkTopicParser.Topic topic, BackupCmd payload) {
        NeulinkEvent event = new NeulinkEvent(payload);
        ICmdListener<QueryResult> listener = getListener();
        if(listener==null){
            throw new NeulinkException(404,"backup Listener does not implemention");
        }
        QueryResult<Map<String,String>> result = listener.doAction(event);
        return result.getData().get("url");
    }

    @Override
    public BackupCmd parser(String payload) {
        return (BackupCmd) JSonUtils.toObject(payload, BackupCmd.class);
    }

    @Override
    protected BackupCmdRes responseWrapper(BackupCmd cmd, String result) {
        BackupCmdRes cmdRes = new BackupCmdRes();
        cmdRes.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        cmdRes.setCmdStr(cmd.getCmdStr());
        cmdRes.setCode(200);
        cmdRes.setMsg("success");
        cmdRes.setUrl(result);
        cmdRes.setMd5("fdafdsa");
        return cmdRes;
    }

    @Override
    protected BackupCmdRes fail(BackupCmd cmd, String error) {
        BackupCmdRes cmdRes = new BackupCmdRes();
        cmdRes.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        cmdRes.setCmdStr(cmd.getCmdStr());
        cmdRes.setCode(500);
        cmdRes.setMsg(error);
        return cmdRes;
    }

    @Override
    protected BackupCmdRes fail(BackupCmd cmd, int code, String error) {
        BackupCmdRes cmdRes = new BackupCmdRes();
        cmdRes.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        cmdRes.setCmdStr(cmd.getCmdStr());
        cmdRes.setCode(code);
        cmdRes.setMsg(error);
        return cmdRes;
    }

    @Override
    protected String resTopic(){
        return "rrpc/res/backup";
    }

    @Override
    protected ICmdListener<QueryResult> getListener() {
        return ListenerFactory.getInstance().getBackupListener();
    }
}
