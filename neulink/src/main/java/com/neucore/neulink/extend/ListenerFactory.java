package com.neucore.neulink.extend;

import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.impl.ListenerRegistrator;

/**
 * @deprecated
 * 请使用ListenerRegistrator实现
 */
public class ListenerFactory extends ListenerRegistrator implements NeulinkConst{
    private static ListenerFactory instance = new ListenerFactory();

    public static ListenerFactory getInstance(){
        return instance;
    }
}
