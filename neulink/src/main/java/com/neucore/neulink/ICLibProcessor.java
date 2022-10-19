package com.neucore.neulink;

import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.impl.QueryActionResult;
import com.neucore.neulink.impl.cmd.check.CheckCmd;
import com.neucore.neulink.impl.cmd.check.CheckCmdRes;

import java.util.Map;

public interface ICLibProcessor <Req extends CheckCmd, Res extends CheckCmdRes, ActionResult extends QueryActionResult<Map<String,Object>>> extends IProcessor{
    ActionResult process(NeulinkTopicParser.Topic topic, Req cmd);
    ICmdListener<ActionResult,Req> getListener(String objType);
    Req parser(String payload);
    Res responseWrapper(Req cmd, ActionResult actionResult);
    Res fail(Req cmd, String error);
    Res fail(Req cmd,int code, String error);
    Req buildPkg(Req cmd) throws NeulinkException;
}
