package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.cmd.cfg.CfgItem;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.cmd.rmsg.app.DebugCmd;
import com.neucore.neulink.cmd.rmsg.app.DebugRes;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;

import java.util.Map;

public class DebugProcessor extends GProcessor<DebugCmd, DebugRes,String> {

    public DebugProcessor(Context context){
        super(context);
    }
    @Override
    public String process(NeulinkTopicParser.Topic topic, DebugCmd cmd) {
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

    public DebugCmd parser(String payload){
        return (DebugCmd) JSonUtils.toObject(payload, DebugCmd.class);
    }

    public DebugRes responseWrapper(DebugCmd cmd, String result) {
        DebugRes res = new DebugRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getSN());
        res.setCode(200);
        res.setMsg("success");
        return res;
    }

    public DebugRes fail(DebugCmd cmd, String message) {
        DebugRes res = new DebugRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getSN());
        res.setCode(500);
        res.setMsg(message);
        return res;
    }
    public DebugRes fail(DebugCmd cmd, int code, String message) {
        DebugRes res = new DebugRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getSN());
        res.setCode(code);
        res.setMsg(message);
        return res;
    }

    @Override
    protected ICmdListener getListener() {
        return null;
    }
}
