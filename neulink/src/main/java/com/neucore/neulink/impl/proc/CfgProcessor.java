package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.cmd.cfg.CfgCmd;
import com.neucore.neulink.cmd.cfg.CfgCmdRes;
import com.neucore.neulink.cmd.cfg.CfgItem;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.ServiceRegistrator;
import com.neucore.neulink.impl.ListenerRegistrator;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.JSonUtils;

public class CfgProcessor extends GProcessor<CfgCmd, CfgCmdRes, ActionResult<String>> {

    public CfgProcessor(Context context){
        super(context);
    }
    @Override
    public ActionResult<String> process(NeulinkTopicParser.Topic topic, CfgCmd cmd) {


        try {
            CfgItem[] items = cmd.getData();
            NeulinkEvent<CfgCmd> event = new NeulinkEvent(cmd);
            ICmdListener listener = getListener();
            listener.doAction(event);
            ActionResult<String> result = new ActionResult<>();
            result.setData(MESSAGE_SUCCESS);
            return result;
        }
        catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    public CfgCmd parser(String payload){
        return (CfgCmd) JSonUtils.toObject(payload, CfgCmd.class);
    }

    public CfgCmdRes responseWrapper(CfgCmd cmd,ActionResult<String> result) {
        CfgCmdRes res = new CfgCmdRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCode(STATUS_200);
        res.setMsg(result.getData());
        return res;
    }

    public CfgCmdRes fail(CfgCmd cmd,String message) {
        CfgCmdRes res = new CfgCmdRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCode(STATUS_500);
        res.setMsg(message);
        return res;
    }

    public CfgCmdRes fail(CfgCmd cmd,int code,String message) {
        CfgCmdRes res = new CfgCmdRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCode(code);
        res.setMsg(message);
        return res;
    }

    @Override
    protected ICmdListener getListener() {
        return ListenerRegistrator.getInstance().getCfgListener();
    }
}
