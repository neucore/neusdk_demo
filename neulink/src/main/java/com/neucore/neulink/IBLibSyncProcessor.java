package com.neucore.neulink;

import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.impl.cmd.rrpc.PkgActionResult;
import com.neucore.neulink.impl.cmd.rrpc.PkgCmd;
import com.neucore.neulink.impl.cmd.rrpc.PkgRes;

public interface IBLibSyncProcessor<Req extends PkgCmd,Res extends PkgRes, ActionResult extends PkgActionResult> extends IProcessor{
    ActionResult process(NeulinkTopicParser.Topic topic, Req cmd);
    ICmdListener<ActionResult,Req> getListener(String objType);
    Req parser(String payload);
    Res responseWrapper(Req cmd, ActionResult actionResult);
    Res fail(Req cmd, String error);
    Res fail(Req cmd,int code, String error);
    Req buildPkg(Req cmd, String dataUrl, long offset) throws NeulinkException;
}
