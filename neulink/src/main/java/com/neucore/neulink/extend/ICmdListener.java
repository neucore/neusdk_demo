package com.neucore.neulink.extend;

public interface ICmdListener<T> {
    T doAction(NeulinkEvent event);
}
