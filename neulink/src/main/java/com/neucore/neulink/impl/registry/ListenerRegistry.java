package com.neucore.neulink.impl.registry;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.NeulinkConst;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ListenerRegistry implements NeulinkConst {

    private static ListenerRegistry instance = new ListenerRegistry();

    public static ListenerRegistry getInstance(){
        return instance;
    }

    private String TAG = TAG_PREFIX+"ListenerRegistry";

    private Map<String,ICmdListener> listenerMap = new ConcurrentHashMap<>();
    public ICmdListener getExtendListener(String cmd){
        return listenerMap.get(cmd.toLowerCase());
    }

    public void setExtendListener(String cmd, ICmdListener listener){
        listenerMap.put(cmd.toLowerCase(),listener);
    }
}
