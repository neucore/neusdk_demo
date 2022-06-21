package com.neucore.neulink.impl.service;

import com.neucore.neulink.impl.cmd.cfg.ConfigContext;

public class LWTTopic {

    private String topic;
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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
