package com.neucore.neulink.impl.proc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.cmd.rmsg.AwakenCmd;
import com.neucore.neulink.cmd.rmsg.AwakenRes;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.impl.ArgCmd;
import com.neucore.neulink.impl.CmdRes;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.JSonUtils;

import java.util.HashMap;
import java.util.Map;

public class AwakenProcessor extends GProcessor<AwakenCmd, AwakenRes,Map<String,String>> {

    private PowerManager pm =null;
    PowerManager.WakeLock wakeLock = null;

    @SuppressLint("InvalidWakeLockTag")
    public AwakenProcessor(Context context){
        super(context);
    }
    @Override
    public Map<String,String> process(NeulinkTopicParser.Topic topic, AwakenCmd cmd) {
        try {

            ICmdListener listener = getListener();
            if(listener==null){
                throw new NeulinkException(STATUS_404,"awaken Listener does not implemention");
            }
            listener.doAction(new NeulinkEvent(cmd));
            return new HashMap<String,String>();
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
        return (AwakenCmd) JSonUtils.toObject(payload, AwakenCmd.class);
    }

    @Override
    protected AwakenRes responseWrapper(AwakenCmd cmd, Map<String, String> result) {
        AwakenRes res = new AwakenRes();
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        return res;
    }

    @Override
    protected AwakenRes fail(AwakenCmd cmd, String error) {
        AwakenRes res = new AwakenRes();
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(error);
        return res;
    }

    @Override
    protected AwakenRes fail(AwakenCmd cmd,int code, String error) {
        AwakenRes res = new AwakenRes();
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(error);
        return res;
    }

    @Override
    protected ICmdListener getListener() {
        return ListenerFactory.getInstance().getAwakenListener();
    }
}
