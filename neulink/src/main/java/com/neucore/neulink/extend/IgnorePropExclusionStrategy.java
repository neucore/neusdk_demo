package com.neucore.neulink.extend;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.neucore.neulink.extend.annotation.IgnoreProp;

public class IgnorePropExclusionStrategy implements ExclusionStrategy {
    public boolean shouldSkipClass(Class<?> clazz) {
        return clazz.getAnnotation(IgnoreProp.class) != null;
    }

    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(IgnoreProp.class) != null;
    }
}
