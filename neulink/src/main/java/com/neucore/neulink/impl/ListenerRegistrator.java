package com.neucore.neulink.impl;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.app.NeulinkConst;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ListenerRegistrator implements NeulinkConst {

    private static ListenerRegistrator instance = new ListenerRegistrator();

    public static ListenerRegistrator getInstance(){
        return instance;
    }

    private String TAG = TAG_PREFIX+"ListenerRegistrator";

    private Map<String,ICmdListener> listenerMap = new ConcurrentHashMap<>();
    public ICmdListener getExtendListener(String cmd){
        return listenerMap.get(cmd.toLowerCase());
    }

    public void setExtendListener(String cmd, ICmdListener listener){
        listenerMap.put(cmd.toLowerCase(),listener);
    }
}
