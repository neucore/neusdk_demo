package com.neucore.neulink.impl;

import com.neucore.neulink.impl.cmd.cfg.ConfigContext;

public class LWTInfo {

    private String topicPrefix;
    private String payload;
    private int qos = ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1);
    private boolean retained=ConfigContext.getInstance().getConfig(ConfigContext.MQTT_RETAINED,false);

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean getRetained() {
        return retained;
    }

    public void setRetained(boolean retained) {
        this.retained = retained;
    }

    public String getTopicPrefix() {
        return topicPrefix;
    }

    public void setTopicPrefix(String topicPrefix) {
        this.topicPrefix = topicPrefix;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
