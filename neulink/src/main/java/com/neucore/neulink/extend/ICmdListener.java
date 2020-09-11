package com.neucore.neulink.extend;

public interface ICmdListener<T extends Result> {
    T doAction(NeulinkEvent event);
}
