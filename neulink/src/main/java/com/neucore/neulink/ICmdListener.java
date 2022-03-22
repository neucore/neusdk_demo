package com.neucore.neulink;

import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.Result;
import com.neucore.neulink.util.IActionResult;

public interface ICmdListener<T extends IActionResult,ReqCmd> extends NeulinkConst {

    T doAction(NeulinkEvent<ReqCmd> event);

}
