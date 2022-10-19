package com.neucore.neulink;

import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.impl.cmd.rrpc.QResult;
import com.neucore.neulink.impl.cmd.rrpc.TLQueryRes;
import com.neucore.neulink.impl.cmd.rrpc.TLibQueryCmd;

public interface IQLibProcessor <Req extends TLibQueryCmd, Res extends TLQueryRes, ActionResult extends QResult> extends IProcessor{
    ActionResult process(NeulinkTopicParser.Topic topic, Req cmd);
    ICmdListener<ActionResult,Req> getListener(String objType);
    Req parser(String payload);
    Res responseWrapper(Req cmd, ActionResult actionResult);
    Res fail(Req cmd, String error);
    Res fail(Req cmd,int code, String error);
    Req buildPkg(Req cmd) throws NeulinkException;
}
