package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.cmd.rmsg.ShellCmd;
import com.neucore.neulink.cmd.rmsg.ShellCmdRes;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.ShellExecutor;

import java.util.Map;

public class ShellProcessor extends GProcessor<ShellCmd, ShellCmdRes,Map<String, String>> {

    public ShellProcessor(Context context){
        super(context);
    }
    @Override
    public Map<String, String> process(NeulinkTopicParser.Topic topic, ShellCmd cmd) {
        String[] cmds = null;
        Map<String, String> result = null;
        try {
            String[] cmdA = cmd.toArrays();
            return ShellExecutor.run(this.getContext(), cmdA);
        }
        catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

//    protected String getStatus(){
//        return Message.STATUS_SUCCESS;
//    }

    @Override
    public ShellCmd parser(String payload) {
        return (ShellCmd) JSonUtils.toObject(payload, ShellCmd.class);
    }

    @Override
    protected ShellCmdRes responseWrapper(ShellCmd cmd, Map<String, String> result) {
        ShellCmdRes res = new ShellCmdRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCode(200);
        res.setMsg("success");
        res.setStdout(result.get("stdout"));
        res.setShellRet(Integer.valueOf(result.get("shellRet")));
        return res;
    }

    @Override
    protected ShellCmdRes fail(ShellCmd cmd, String error) {
        ShellCmdRes res = new ShellCmdRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCode(500);
        res.setMsg(error);
        return res;
    }

    @Override
    protected ShellCmdRes fail(ShellCmd cmd,int code, String error) {
        ShellCmdRes res = new ShellCmdRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCode(code);
        res.setMsg(error);
        return res;
    }

    @Override
    protected ICmdListener getListener() {
        return null;
    }
}
