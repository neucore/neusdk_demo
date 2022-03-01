package com.neucore.neulink.impl.proc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.ICmdListener;
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

public class AwakenProcessor extends GProcessor<ArgCmd, CmdRes,Map<String,String>> {

    private PowerManager pm =null;
    PowerManager.WakeLock wakeLock = null;

    @SuppressLint("InvalidWakeLockTag")
    public AwakenProcessor(Context context){
        super(context);
    }
    @Override
    public Map<String,String> process(NeulinkTopicParser.Topic topic, ArgCmd cmd) {
        try {

            ICmdListener listener = getListener();
            if(listener==null){
                throw new NeulinkException(404,"awaken Listener does not implemention");
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
    public ArgCmd parser(String payload) {
        return (ArgCmd) JSonUtils.toObject(payload, ArgCmd.class);
    }

    @Override
    protected CmdRes responseWrapper(ArgCmd cmd, Map<String, String> result) {
        CmdRes res = new CmdRes();
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(200);
        res.setMsg("success");
        return res;
    }

    @Override
    protected CmdRes fail(ArgCmd cmd, String error) {
        CmdRes res = new CmdRes();
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(500);
        res.setMsg(error);
        return res;
    }

    @Override
    protected CmdRes fail(ArgCmd cmd,int code, String error) {
        CmdRes res = new CmdRes();
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
