package com.neucore.neulink.extend.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.google.gson.Gson;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.impl.CmdRes;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.rrpc.ReserveSyncCmd;
import com.neucore.neulink.util.JSonUtils;

import java.util.HashMap;
import java.util.Map;

public class ReserveProcessor extends GProcessor<ReserveSyncCmd, CmdRes,Map<String,String>> {

    final private String ADD = "add",DEL = "del",UPDATE = "update",SYNC = "sync",PUSH = "push";
    private PowerManager pm =null;
    PowerManager.WakeLock wakeLock = null;

    @SuppressLint("InvalidWakeLockTag")
    public ReserveProcessor(Context context){
        super(context);
    }
    @Override
    public Map<String,String> process(NeulinkTopicParser.Topic topic, ReserveSyncCmd cmd) {
        try {

            //cmd.getCmdStr();  ADD  DEL  UPDATE  SYNC   PUSH
            ICmdListener listener = getListener();
            if(listener==null){
                throw new NeulinkException(404,"awaken Listener does not implemention");
            }
            Log.i("IMqttMessageListener", "messageArrived    cmd.toArrays() :" + new Gson().toJson(cmd)); //有效
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
    public ReserveSyncCmd parser(String payload) {
        return (ReserveSyncCmd) JSonUtils.toObject(payload, ReserveSyncCmd.class);
    }

    @Override
    protected CmdRes responseWrapper(ReserveSyncCmd cmd, Map<String, String> result) {
        CmdRes res = new CmdRes();
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(200);
        res.setMsg("success");
        return res;
    }

    @Override
    protected CmdRes fail(ReserveSyncCmd cmd, String error) {
        CmdRes res = new CmdRes();
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(500);
        res.setMsg(error);
        return res;
    }

    @Override
    protected CmdRes fail(ReserveSyncCmd cmd, int code, String error) {
        CmdRes res = new CmdRes();
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(error);
        return res;
    }

    @Override
    protected ICmdListener getListener() {
        return ListenerFactory.getInstance().getReserveListener();
    }
}
