package com.neucore.neulink.util;

import java.util.UUID;

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
    public static void remove(){
        ids.remove();
    }
}
