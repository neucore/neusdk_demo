package com.neucore.neulink.proc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.impl.ArgCmd;
import com.neucore.neulink.impl.CmdRes;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.ShellExecutor;

import java.util.Map;

public class RebootProcessor extends GProcessor<ArgCmd, CmdRes,Map<String,String>> {

    private PowerManager pm =null;
    PowerManager.WakeLock wakeLock = null;

    @SuppressLint("InvalidWakeLockTag")
    public RebootProcessor(Context context){
        super(context);
    }
    @Override
    public Map<String,String> process(NeulinkTopicParser.Topic topic, ArgCmd cmd) {
        try {
            return ShellExecutor.run(this.getContext(), cmd.toArrays());
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
        res.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(200);
        res.setMsg("success");
        return res;
    }

    @Override
    protected CmdRes fail(ArgCmd cmd, String error) {
        CmdRes res = new CmdRes();
        res.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(500);
        res.setMsg(error);
        return res;
    }

    @Override
    protected CmdRes fail(ArgCmd cmd,int code, String error) {
        CmdRes res = new CmdRes();
        res.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(error);
        return res;
    }
    @Override
    protected String resTopic(){//不需要响应
        return null;
    }

    @Override
    protected ICmdListener getListener() {
        return null;
    }
}
