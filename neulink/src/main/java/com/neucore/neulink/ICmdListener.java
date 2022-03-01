package com.neucore.neulink;

import com.neucore.neulink.extend.NeulinkEvent;

public interface ICmdListener<T> {
    T doAction(NeulinkEvent event);
}
