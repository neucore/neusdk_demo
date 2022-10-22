package com.neucore.neulink;

import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.impl.SysPropAction;

import java.util.List;

/**
 * 系统属性设置侦听器
 * add、del、upd
 */
public interface IPropChgListener extends NeulinkConst{
    void doAction(NeulinkEvent<List<SysPropAction>> event);
}
