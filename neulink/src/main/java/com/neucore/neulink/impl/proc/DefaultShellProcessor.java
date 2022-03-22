package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.cmd.rmsg.ShellCmd;
import com.neucore.neulink.cmd.rmsg.ShellCmdRes;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.ServiceRegistrator;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.ListenerRegistrator;
import com.neucore.neulink.impl.listener.DefaultShellCmdListener;
import com.neucore.neulink.util.JSonUtils;

import java.util.Map;

public class DefaultShellProcessor extends GProcessor<ShellCmd, ShellCmdRes, ActionResult<Map<String, String>>> {

    public DefaultShellProcessor(Context context){
        super(context);
        ListenerRegistrator.getInstance().setExtendListener("shell",new DefaultShellCmdListener());
    }

    @Override
    public ShellCmd parser(String payload) {
        return JSonUtils.toObject(payload, ShellCmd.class);
    }

    @Override
    protected ShellCmdRes responseWrapper(ShellCmd cmd, ActionResult<Map<String, String>> actionResult) {
        ShellCmdRes res = new ShellCmdRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());

        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        res.setStdout(actionResult.getData().get("stdout"));
        res.setShellRet(Integer.valueOf(actionResult.getData().get("shellRet")));
        res.setData(actionResult.getData());
        return res;
    }

    @Override
    protected ShellCmdRes fail(ShellCmd cmd, String error) {
        ShellCmdRes res = new ShellCmdRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }

    @Override
    protected ShellCmdRes fail(ShellCmd cmd,int code, String error) {
        ShellCmdRes res = new ShellCmdRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }
}
