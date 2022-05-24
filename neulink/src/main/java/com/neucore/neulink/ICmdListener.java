package com.neucore.neulink;

import com.neucore.neulink.impl.NeulinkEvent;

public interface ICmdListener<T extends IActionResult,ReqCmd> extends NeulinkConst {

    T doAction(NeulinkEvent<ReqCmd> event);

}
