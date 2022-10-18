package com.neucore.neulink.util;

import com.neucore.neulink.IDebugger;
import com.neucore.neulink.impl.MyDebugger;

import java.util.UUID;

import cn.hutool.core.util.ObjectUtil;

public class RequestContext {
    private static ThreadLocal<String> ids = new InheritableThreadLocal<String>();
    public static String getId(){
        String id = ids.get();
        if(id==null){
            id = UUID.randomUUID().toString();
            ids.set(id);
        }
        return id;
    }
    public static void setId(String id){
        ids.set(id);
    }
    public static void removeId(){
        ids.remove();
    }

    private static ThreadLocal<IDebugger> debugs = new InheritableThreadLocal<IDebugger>();

    public static boolean isDebug(){
        IDebugger debug = debugs.get();
        if(ObjectUtil.isNotEmpty(debug)){
            return debug.isDebug();
        }
        return false;
    }

    public static void setDebug(boolean debug){
        debugs.set(new MyDebugger(debug));
    }

    public static void removeDebug(){
        debugs.remove();
    }
}
