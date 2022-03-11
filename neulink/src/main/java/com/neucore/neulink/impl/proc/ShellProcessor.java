package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.cmd.rmsg.ShellCmd;
import com.neucore.neulink.cmd.rmsg.ShellCmdRes;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.ShellExecutor;

import java.util.Map;

public class ShellProcessor extends GProcessor<ShellCmd, ShellCmdRes, ActionResult<Map<String, String>>> {

    public ShellProcessor(Context context){
        super(context);
    }
    @Override
    public ActionResult<Map<String, String>> process(NeulinkTopicParser.Topic topic, ShellCmd cmd) {
        String[] cmds = null;
        Map<String, String> resultMap = null;
        try {
            String cmdA = cmd.toString();
            ActionResult<Map<String, String>> actionResult = new ActionResult<>();
            resultMap = ShellExecutor.execute(this.getContext(), cmdA);
            actionResult.setData(resultMap);
            return actionResult;
        }
        catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ShellCmd parser(String payload) {
        return (ShellCmd) JSonUtils.toObject(payload, ShellCmd.class);
    }

    @Override
    protected ShellCmdRes responseWrapper(ShellCmd cmd, ActionResult<Map<String, String>> actionResult) {
        ShellCmdRes res = new ShellCmdRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        res.setStdout(actionResult.getData().get("stdout"));
        res.setShellRet(Integer.valueOf(actionResult.getData().get("shellRet")));
        return res;
    }

    @Override
    protected ShellCmdRes fail(ShellCmd cmd, String error) {
        ShellCmdRes res = new ShellCmdRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCode(STATUS_500);
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
