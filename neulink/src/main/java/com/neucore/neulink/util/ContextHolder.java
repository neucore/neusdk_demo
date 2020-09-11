package com.neucore.neulink.util;

import android.content.Context;

public class ContextHolder {

    private Context context;
    private static ContextHolder instance = new ContextHolder();
    public static ContextHolder getInstance(){
        return instance;
    }
    public void setContext(Context context){
        this.context = context;
    }
    public Context getContext(){
        return context;
    }
}
