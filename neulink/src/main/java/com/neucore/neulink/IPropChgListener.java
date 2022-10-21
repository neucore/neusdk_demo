package com.neucore.neulink;

import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.impl.SysPropAction;

import java.util.List;

public interface IPropChgListener extends NeulinkConst{
    void doAction(NeulinkEvent<List<SysPropAction>> event);
}
