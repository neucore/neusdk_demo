package com.neucore.neulink.impl;

import com.neucore.neulink.IResCallback;
import com.neucore.neulink.app.NeulinkConst;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CallbackRegistrator implements NeulinkConst {

    private static CallbackRegistrator instance = new CallbackRegistrator();

    public static CallbackRegistrator getInstance(){
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
