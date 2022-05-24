package com.neucore.neulink.impl.registry;

import com.neucore.neulink.IResCallback;
import com.neucore.neulink.NeulinkConst;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CallbackRegistry implements NeulinkConst {

    private static CallbackRegistry instance = new CallbackRegistry();

    public static CallbackRegistry getInstance(){
        return instance;
    }

    private Map<String, IResCallback> callbackMap = new ConcurrentHashMap<>();
    public IResCallback getResCallback(String cmd){
        return callbackMap.get(cmd.toLowerCase());
    }

    public void setResCallback(String cmd, IResCallback callback){
        callbackMap.put(cmd.toLowerCase(),callback);
    }
}
