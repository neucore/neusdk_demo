package com.neucore.neulink.impl.proc;

import android.annotation.SuppressLint;
import android.content.Context;

import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.cmd.rmsg.HibrateCmd;
import com.neucore.neulink.cmd.rmsg.HibrateRes;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.impl.ListenerRegistrator;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.JSonUtils;

import java.util.HashMap;
import java.util.Map;

public class HibrateProcessor extends GProcessor<HibrateCmd, HibrateRes, ActionResult<Map<String,String>>> {

    @SuppressLint("InvalidWakeLockTag")
    public HibrateProcessor(Context context){
        super(context);
    }
    @Override
    public ActionResult<Map<String,String>> process(NeulinkTopicParser.Topic topic, HibrateCmd cmd) {
        try {
            ICmdListener<ActionResult,HibrateCmd> listener = getListener();
            if(listener==null){
                throw new NeulinkException(STATUS_404,"Hibrate Listener does not implemention");
            }
            listener.doAction(new NeulinkEvent<HibrateCmd>(cmd));
            Map<String,String> data = new HashMap<String,String>();
            ActionResult<Map<String,String>> actionResult = new ActionResult<>();
            actionResult.setData(data);
            return actionResult;
        }
        catch (NeulinkException ex){
            throw ex;
        }
        catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public HibrateCmd parser(String payload) {
        return (HibrateCmd) JSonUtils.toObject(payload, HibrateCmd.class);
    }

    @Override
    protected HibrateRes responseWrapper(HibrateCmd cmd, ActionResult<Map<String, String>> actionResult) {
        HibrateRes res = new HibrateRes();
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        return res;
    }

    @Override
    protected HibrateRes fail(HibrateCmd cmd, String error) {
        HibrateRes res = new HibrateRes();
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(error);
        return res;
    }

    @Override
    protected HibrateRes fail(HibrateCmd cmd,int code, String error) {
        HibrateRes res = new HibrateRes();
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(error);
        return res;
    }

    @Override
    protected ICmdListener getListener() {
        return ListenerRegistrator.getInstance().getHibrateListener();
    }
}
