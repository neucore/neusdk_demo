package com.neucore.neulink.extend;

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
