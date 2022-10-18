package com.neucore.neulink.impl;

import com.neucore.neulink.IDebugger;

public class MyDebugger implements IDebugger {
    private Boolean debug;
    public MyDebugger(Boolean debug){
        this.debug = debug;
    }
    @Override
    public Boolean isDebug() {
        return debug;
    }
}
