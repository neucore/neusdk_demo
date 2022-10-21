package com.neucore.neulink.impl.registry;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.IPropChgListener;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.impl.SysPropAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.hutool.core.util.ObjectUtil;

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

    private List<IPropChgListener> propChgListenerList = new ArrayList<>();
    public void addPropChgListener(IPropChgListener listener){
        if(ObjectUtil.isNotEmpty(listener)){
            this.propChgListenerList.add(listener);
        }
    }

    /**
     * 触发
     * @param actions
     */
    public void fireProChgListener(List<SysPropAction> actions){
        for (IPropChgListener propChgListener:propChgListenerList){
            NeulinkEvent<List<SysPropAction>> event = new NeulinkEvent<>(actions);
            propChgListener.doAction(event);
        }
    }
}
