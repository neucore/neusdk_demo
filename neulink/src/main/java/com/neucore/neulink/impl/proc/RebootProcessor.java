package com.neucore.neulink.impl.proc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.impl.ArgCmd;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.cmd.rmsg.RebootCmd;
import com.neucore.neulink.cmd.rmsg.RebootRes;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.ShellExecutor;

import java.util.Map;

public class RebootProcessor extends GProcessor<RebootCmd, RebootRes, ActionResult<Map<String,String>>> {

    private PowerManager pm =null;
    PowerManager.WakeLock wakeLock = null;

    @SuppressLint("InvalidWakeLockTag")
    public RebootProcessor(Context context){
        super(context);
    }
    @Override
    public ActionResult<Map<String,String>> process(NeulinkTopicParser.Topic topic, RebootCmd cmd) {
        try {
            Map<String,String> stringStringMap = ShellExecutor.run(this.getContext(), cmd.toArrays());
            ActionResult<Map<String,String>> actionResult = new ActionResult<>();
            actionResult.setData(stringStringMap);
            return actionResult;
        }
        catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public RebootCmd parser(String payload) {
        return (RebootCmd) JSonUtils.toObject(payload, RebootCmd.class);
    }

    @Override
    protected RebootRes responseWrapper(RebootCmd cmd, ActionResult<Map<String,String>>  result) {
        RebootRes res = new RebootRes();
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        return res;
    }

    @Override
    protected RebootRes fail(RebootCmd cmd, String error) {
        RebootRes res = new RebootRes();
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(error);
        return res;
    }

    @Override
    protected RebootRes fail(RebootCmd cmd,int code, String error) {
        RebootRes res = new RebootRes();
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
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
