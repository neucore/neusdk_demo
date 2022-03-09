package com.neucore.neulink;

import com.neucore.neulink.extend.NeulinkEvent;

public interface ICmdListener<T,CMD> {
    T doAction(NeulinkEvent<CMD> event);
}
