package com.neucore.neulink;

import com.neucore.neulink.impl.cmd.rrpc.PkgActionResult;
import com.neucore.neulink.impl.cmd.rrpc.PkgCmd;
import com.neucore.neulink.impl.cmd.rrpc.PkgRes;

public interface IBlib$ObjtypeProcessor<Req extends PkgCmd, Res extends PkgRes, ActionResult extends PkgActionResult> extends NeulinkConst {

    Res responseWrapper(Req req, ActionResult actionResult);

    Res fail(Req req, String error);

    Res fail(Req req,int code, String error);

    /**
     * 下载包数据并构建包请求
     * @param cmd
     * @return
     * @throws NeulinkException
     */
    Req buildPkg(Req cmd) throws NeulinkException;
}
