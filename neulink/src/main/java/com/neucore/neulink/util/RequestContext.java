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

    private static ThreadLocal<Boolean> debugs = new InheritableThreadLocal<Boolean>();

    public static boolean isDebug(){
        Boolean debug = debugs.get();
        if(ObjectUtil.isNotEmpty(debug)){
            return debug;
        }
        return false;
    }

    public static void setDebug(boolean debug){
        debugs.set(debug);
    }
}
