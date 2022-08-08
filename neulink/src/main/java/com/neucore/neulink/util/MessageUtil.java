package com.neucore.neulink.util;

import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;

import org.eclipse.paho.mqttv5.common.MqttMessage;

import java.nio.charset.StandardCharsets;

public class MessageUtil implements NeulinkConst {
    private static String TAG = TAG_PREFIX+"SubscriberFacde";

    public static byte[] encode(boolean debug,String topic, String payload){
        byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
        Boolean isCompress = ConfigContext.getInstance().getConfig(ConfigContext.PRODUCT_COMPRESS,true);
        if(debug||!isCompress){
            return bytes;
        }
        byte[] compress = CompressUtil.gzipCompress(bytes);
        return compress;
    }

    public static String decode(boolean debug, String topic, MqttMessage message){
        int messageId = message.getId();
        int qos = message.getQos();
        boolean isRetained = message.isRetained();
        byte[] payload = message.getPayload();
        String msgContent = null;
        Boolean isCompress = ConfigContext.getInstance().getConfig(ConfigContext.CUSTMER_COMPRESS,true);
        if(!debug && isCompress){
            payload = CompressUtil.gzipUncompress(payload);
        }
        msgContent = new String(payload,StandardCharsets.UTF_8);
        return msgContent;
    }
}
