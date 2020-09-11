package com.neucore.neulink.proc;

import android.content.Context;

import com.neucore.neulink.cfg.CfgItem;
import com.neucore.neulink.cfg.ConfigContext;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.rmsg.app.Debug;
import com.neucore.neulink.rmsg.app.DebugRes;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;

import java.util.Map;

public class DebugProcessor extends GProcessor<Debug, DebugRes,String> {

    public DebugProcessor(Context context){
        super(context);
    }
    @Override
    public String process(NeulinkTopicParser.Topic topic, Debug cmd) {
        String[] cmds = null;
        Map<String, String> result = null;
        try {
            String[] args = cmd.getArgs();
            if(args==null||args.length==0||(!"on".equalsIgnoreCase(args[0]) && !"off".equalsIgnoreCase(args[0]))){
                throw new RuntimeException("参数只接受[on或者off]");
            }
            else {
                CfgItem item = new CfgItem();
                item.setKeyName("Debug");
                item.setValue(args[0]);
                CfgItem[] items = new CfgItem[]{item};
                ConfigContext.getInstance().update(items);
                return "success";
            }
        }
        catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    public Debug parser(String payload){
        return (Debug) JSonUtils.toObject(payload, Debug.class);
    }

    public DebugRes responseWrapper(Debug cmd, String result) {
        DebugRes res = new DebugRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        res.setCode(200);
        res.setMsg("success");
        return res;
    }

    public DebugRes fail(Debug cmd,String message) {
        DebugRes res = new DebugRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        res.setCode(500);
        res.setMsg(message);
        return res;
    }
    public DebugRes fail(Debug cmd,int code,String message) {
        DebugRes res = new DebugRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        res.setCode(code);
        res.setMsg(message);
        return res;
    }
}
