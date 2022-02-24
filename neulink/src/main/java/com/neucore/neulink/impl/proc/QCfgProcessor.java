package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.cmd.cfg.CfgCmd;
import com.neucore.neulink.cmd.cfg.CfgItem;
import com.neucore.neulink.cmd.cfg.CfgQueryCmdRes;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class QCfgProcessor extends GProcessor<CfgCmd, CfgQueryCmdRes,CfgItem[]> {


    public QCfgProcessor(Context context){
        super(context);
    }
    @Override
    public CfgItem[] process(NeulinkTopicParser.Topic topic, CfgCmd cmd) {
        try{
            CfgItem[] retItems = null;
            if("query".equalsIgnoreCase(cmd.getCmdStr())) {
                Properties configs = ConfigContext.getInstance().getConfigs();
                Enumeration keys = configs.keys();
                List<CfgItem> result = new ArrayList<CfgItem>();
                while (keys.hasMoreElements()) {
                    String key = (String) keys.nextElement();
                    CfgItem item = new CfgItem();
                    item.setKeyName(key);
                    item.setValue(configs.getProperty(key));
                    result.add(item);
                }
                retItems = new CfgItem[result.size()];
                result.toArray(retItems);
            }
            return retItems;
        }
        catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    public CfgCmd parser(String payload){
        return (CfgCmd) JSonUtils.toObject(payload, CfgCmd.class);
    }

    public CfgQueryCmdRes responseWrapper(CfgCmd cmd,CfgItem[] result) {
        CfgQueryCmdRes res = new CfgQueryCmdRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getSN());
        res.setCode(200);
        res.setMsg("success");
        res.setData(result);
        return res;
    }

    public CfgQueryCmdRes fail(CfgCmd cmd,String message) {
        CfgQueryCmdRes res = new CfgQueryCmdRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getSN());
        res.setCode(500);
        res.setMsg(message);
        return res;
    }
    public CfgQueryCmdRes fail(CfgCmd cmd,int code,String message) {
        CfgQueryCmdRes res = new CfgQueryCmdRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getSN());
        res.setCode(code);
        res.setMsg(message);
        return res;
    }

    @Override
    protected ICmdListener getListener() {
        return ListenerFactory.getInstance().getCfgListener();
    }
}
