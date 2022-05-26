package com.neucore.neulink;

import com.neucore.neulink.impl.cmd.rrpc.TLQueryRes;
import com.neucore.neulink.impl.cmd.rrpc.TLibQueryCmd;
import com.neucore.neulink.impl.cmd.rrpc.QResult;

public interface IQlib$ObjtypeProcessor<Req extends TLibQueryCmd, Res extends TLQueryRes, ActionResult extends QResult> extends NeulinkConst {

    Res responseWrapper(Req req, ActionResult actionResult);

    Res fail(Req req, String error);

    Res fail(Req req, int code, String error);

    /**
     * 下载包数据并构建包请求
     * @param cmd
     * @return
     * @throws NeulinkException
     */
    Req buildPkg(Req cmd) throws NeulinkException;
}
