package com.neucore.neulink.util;

import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.log.NeuLogUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;

public class MessageUtil implements NeulinkConst {
    private static String TAG = TAG_PREFIX+"SubscriberFacde";

    public static byte[] encode(String topic, String payload){
        byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
        Boolean isCompress = ConfigContext.getInstance().getConfig(ConfigContext.PRODUCT_COMPRESS,false);
        if(!isCompress){
            return bytes;
        }
        byte[] compress = CompressUtil.gzipCompress(bytes);
        return compress;
    }

    public static String decode(String topic,MqttMessage message){
        int messageId = message.getId();
        int qos = message.getQos();
        boolean isRetained = message.isRetained();
        byte[] payload = message.getPayload();
        String msgContent = null;
        Boolean isCompress = ConfigContext.getInstance().getConfig(ConfigContext.CUSTMER_COMPRESS,false);
        if(isCompress){
            payload = CompressUtil.gzipUncompress(payload);
        }
        msgContent = new String(payload,StandardCharsets.UTF_8);
        String detailLog = topic + ";qos:" + qos + ";retained:" + isRetained + "messageId:"+messageId;
        NeuLogUtils.iTag(TAG, detailLog);
        NeuLogUtils.iTag(TAG, "messageArrived:" + msgContent);
        return msgContent;
    }
}
