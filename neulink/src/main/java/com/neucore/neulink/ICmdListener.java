package com.neucore.neulink;

import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.extend.NeulinkEvent;

public interface ICmdListener<T,CMD> extends NeulinkConst {
    T doAction(NeulinkEvent<CMD> event);
}
