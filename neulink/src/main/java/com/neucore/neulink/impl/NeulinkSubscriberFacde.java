package com.neucore.neulink.impl;

import android.content.Context;

import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.registry.ServiceRegistry;

import org.eclipse.paho.mqttv5.client.MqttActionListener;

import cn.hutool.core.util.ObjectUtil;

/**
 * 终端消费者
 */
public class NeulinkSubscriberFacde implements NeulinkConst{

    private String TAG = TAG_PREFIX+"SubscriberFacde";

    private Context context;
    private NeulinkService service;
    private MqttActionListener listeners = null;
    private String[] topics = null;
    private int[] qoss = null;
    public NeulinkSubscriberFacde(Context context, NeulinkService service){
        this.context = context;
        this.service = service;
        IDeviceService deviceService = ServiceRegistry.getInstance().getDeviceService();
        topics = deviceService.subscribeTopics();
        qoss = deviceService.subscribeQoss();
    }

    /**
     *
     */
    public void subAll(){
        listeners = service.getNeulinkActionListenerAdapter();
        service.subscribeToTopic(topics, qoss, listeners);
    }

    public void unsubAll(){
        listeners = service.getNeulinkActionListenerAdapter();
        service.unsubscribeToTopic(topics, qoss,listeners);
    }
}
