package com.neucore.neulink.impl;

import java.util.EventObject;

public class NeulinkEvent<T> extends EventObject {
    private T source;
    public NeulinkEvent(T source){
        super(source);
        this.source = source;
    }

    @Override
    public T getSource(){
      return source;
    }
}
