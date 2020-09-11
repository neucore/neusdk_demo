package com.neucore.neulink.proc;

import android.content.Context;

import com.neucore.neulink.cfg.CfgCmd;
import com.neucore.neulink.cfg.CfgCmdRes;
import com.neucore.neulink.cfg.CfgItem;
import com.neucore.neulink.cfg.ConfigContext;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;

public class CfgProcessor extends GProcessor<CfgCmd, CfgCmdRes,String> {

    public CfgProcessor(Context context){
        super(context);
    }
    @Override
    public String process(NeulinkTopicParser.Topic topic, CfgCmd cmd) {

        CfgItem[] items = cmd.getData();

        int size = items==null?0:items.length;
        try {
            if ("add".equalsIgnoreCase(cmd.getCmdStr())) {
                ConfigContext.getInstance().add(items);
            }
            else if("update".equalsIgnoreCase(cmd.getCmdStr())){
                ConfigContext.getInstance().update(items);
            }
            else if("del".equalsIgnoreCase(cmd.getCmdStr())){
                ConfigContext.getInstance().delete(items);
            }
            else if("sync".equalsIgnoreCase(cmd.getCmdStr())){
                ConfigContext.getInstance().sync(items);
            }
            return "success";
        }
        catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    public CfgCmd parser(String payload){
        return (CfgCmd) JSonUtils.toObject(payload, CfgCmd.class);
    }

    public CfgCmdRes responseWrapper(CfgCmd cmd,String result) {
        CfgCmdRes res = new CfgCmdRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        res.setCode(200);
        res.setMsg(result);
        return res;
    }

    public CfgCmdRes fail(CfgCmd cmd,String message) {
        CfgCmdRes res = new CfgCmdRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        res.setCode(500);
        res.setMsg(message);
        return res;
    }

    public CfgCmdRes fail(CfgCmd cmd,int code,String message) {
        CfgCmdRes res = new CfgCmdRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        res.setCode(code);
        res.setMsg(message);
        return res;
    }
}
