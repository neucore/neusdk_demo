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

    public ICmdListener getBlibExtendListener(String objType){
        String batchBiz = NEULINK_BIZ_BLIB+"."+objType.toLowerCase();
        return listenerMap.get(batchBiz.toLowerCase());
    }

    public void setBlibExtendListener(String objType, ICmdListener listener){
        String batchBiz = NEULINK_BIZ_BLIB+"."+objType.toLowerCase();
        listenerMap.put(batchBiz.toLowerCase(),listener);
    }

    public ICmdListener getClibExtendListener(String objType){
        String batchBiz = NEULINK_BIZ_CLIB+"."+objType.toLowerCase();
        return listenerMap.get(batchBiz.toLowerCase());
    }

    public void setClibExtendListener(String objType, ICmdListener listener){
        String batchBiz = NEULINK_BIZ_CLIB+"."+objType.toLowerCase();
        listenerMap.put(batchBiz.toLowerCase(),listener);
    }

    public ICmdListener getQlibExtendListener(String objType){
        String batchBiz = NEULINK_BIZ_QLIB+"."+objType.toLowerCase();
        return listenerMap.get(batchBiz.toLowerCase());
    }

    public void setQlibExtendListener(String objType, ICmdListener listener){
        String batchBiz = NEULINK_BIZ_QLIB+"."+objType.toLowerCase();
        listenerMap.put(batchBiz,listener);
    }
}
