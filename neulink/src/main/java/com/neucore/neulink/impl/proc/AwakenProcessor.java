package com.neucore.neulink.impl.proc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.cmd.rmsg.AwakenCmd;
import com.neucore.neulink.cmd.rmsg.AwakenRes;
import com.neucore.neulink.extend.ServiceRegistrator;
import com.neucore.neulink.impl.ListenerRegistrator;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.JSonUtils;

import java.util.HashMap;
import java.util.Map;

public class AwakenProcessor extends GProcessor<AwakenCmd, AwakenRes, ActionResult<Map<String,String>>> {

    private PowerManager pm =null;
    PowerManager.WakeLock wakeLock = null;

    @SuppressLint("InvalidWakeLockTag")
    public AwakenProcessor(Context context){
        super(context);
    }
    @Override
    public ActionResult<Map<String,String>> process(NeulinkTopicParser.Topic topic, AwakenCmd cmd) {
        try {

            ICmdListener listener = getListener();
            if(listener==null){
                throw new NeulinkException(STATUS_404,"awaken Listener does not implemention");
            }
            listener.doAction(new NeulinkEvent(cmd));
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
    public AwakenCmd parser(String payload) {
        return JSonUtils.toObject(payload, AwakenCmd.class);
    }

    @Override
    protected AwakenRes responseWrapper(AwakenCmd cmd, ActionResult<Map<String, String>> actionResult) {
        AwakenRes res = new AwakenRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        res.setData(actionResult.getData());
        return res;
    }

    @Override
    protected AwakenRes fail(AwakenCmd cmd, String error) {
        AwakenRes res = new AwakenRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }

    @Override
    protected AwakenRes fail(AwakenCmd cmd,int code, String error) {
        AwakenRes res = new AwakenRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }

    @Override
    protected ICmdListener getListener() {
        return ListenerRegistrator.getInstance().getAwakenListener();
    }
}
