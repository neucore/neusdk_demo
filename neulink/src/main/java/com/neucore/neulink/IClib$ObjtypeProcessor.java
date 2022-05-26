package com.neucore.neulink;

import com.neucore.neulink.impl.QueryActionResult;
import com.neucore.neulink.impl.cmd.check.CheckCmd;
import com.neucore.neulink.impl.cmd.check.CheckCmdRes;

import java.util.Map;

public interface IClib$ObjtypeProcessor<Req extends CheckCmd, Res extends CheckCmdRes, ActionResult extends QueryActionResult<Map<String,Object>>> extends NeulinkConst {

    String getBiz();

    String getObjType();

    Res responseWrapper(Req req, ActionResult actionResult);

    Res fail(Req req, String error);

    Res fail(Req req, int code, String error);

    /**
     * 下载包数据并构建包请求
     * @param cmdStr
     * @return
     * @throws NeulinkException
     */
    Req buildPkg(String cmdStr) throws NeulinkException;
}
