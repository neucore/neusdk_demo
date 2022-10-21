package com.neucore.neulink.impl;

public class SysPropAction {
    private String name;
    private String propKey;
    private String propValue;
    public SysPropAction(String name, String propKey, String propValue){
        this.name = name;
        this.propKey = propKey;
        this.propValue = propValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPropKey() {
        return propKey;
    }

    public void setPropKey(String propKey) {
        this.propKey = propKey;
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }
}
