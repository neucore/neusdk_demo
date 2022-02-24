package com.neucore.neulink.impl.proc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.impl.ArgCmd;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.cmd.rmsg.RebootCmd;
import com.neucore.neulink.cmd.rmsg.RebootRes;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.ShellExecutor;

import java.util.Map;

public class RebootProcessor extends GProcessor<RebootCmd, RebootRes,Map<String,String>> {

    private PowerManager pm =null;
    PowerManager.WakeLock wakeLock = null;

    @SuppressLint("InvalidWakeLockTag")
    public RebootProcessor(Context context){
        super(context);
    }
    @Override
    public Map<String,String> process(NeulinkTopicParser.Topic topic, RebootCmd cmd) {
        try {
            return ShellExecutor.run(this.getContext(), cmd.toArrays());
        }
        catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public RebootCmd parser(String payload) {
        return (RebootCmd) JSonUtils.toObject(payload, ArgCmd.class);
    }

    @Override
    protected RebootRes responseWrapper(RebootCmd cmd, Map<String, String> result) {
        RebootRes res = new RebootRes();
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(200);
        res.setMsg("success");
        return res;
    }

    @Override
    protected RebootRes fail(RebootCmd cmd, String error) {
        RebootRes res = new RebootRes();
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(500);
        res.setMsg(error);
        return res;
    }

    @Override
    protected RebootRes fail(RebootCmd cmd,int code, String error) {
        RebootRes res = new RebootRes();
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(error);
        return res;
    }

    @Override
    protected ICmdListener getListener() {
        return null;
    }
}
