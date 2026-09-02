package com.neucore.neulink.util;

import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.log.NeuLogUtils;

import org.eclipse.paho.mqttv5.common.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class MessageUtil implements NeulinkConst {
    private static String TAG = TAG_PREFIX+"SubscriberFacde";

    public static byte[] encode(boolean debug,String topic, String payload){
        // 替换 StandardCharsets.UTF_8 → 使用字符串"UTF‑8"捕获异常
        byte[] bytes;
        try {
            bytes = payload.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // UTF‑8一定支持，理论不会走到这里
            bytes = payload.getBytes();
        }

        Boolean isCompress = ConfigContext.getInstance().getConfig(ConfigContext.PRODUCT_COMPRESS,true);
        NeuLogUtils.iTag(TAG,String.format("encode topic=%s, debug=%s,isCompress=%s",topic,debug,isCompress));
        if(debug||!isCompress){
            return bytes;
        }
        byte[] compress = CompressUtil.gzipCompress(bytes);
        NeuLogUtils.iTag(TAG,String.format("success gzipCompress topic=%s",topic));
        return compress;
    }

    public static String decode(boolean debug, String topic, MqttMessage message){
        int messageId = message.getId();
        int qos = message.getQos();
        boolean isRetained = message.isRetained();
        byte[] payload = message.getPayload();
        String msgContent = null;
        Boolean isCompress = ConfigContext.getInstance().getConfig(ConfigContext.CUSTMER_COMPRESS,true);
        NeuLogUtils.iTag(TAG,String.format("decode topic=%s, debug=%s,isCompress=%s",topic,debug,isCompress));
        if(!debug && isCompress){
            payload = CompressUtil.gzipUncompress(payload);
            NeuLogUtils.iTag(TAG,String.format("success gzipUncompress topic=%s",topic));
        }
        try {
            msgContent = new String(payload, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // UTF‑8 安卓全部版本内置，理论不会触发异常
            msgContent = new String(payload);
        }
        return msgContent;
    }

    public static String decode(boolean debug, String topic, byte[] payload){
        String msgContent = null;
        Boolean isCompress = ConfigContext.getInstance().getConfig(ConfigContext.CUSTMER_COMPRESS,true);
        NeuLogUtils.iTag(TAG,String.format("decode topic=%s, debug=%s,isCompress=%s",topic,debug,isCompress));
        if(!debug && isCompress) {
            payload = CompressUtil.gzipUncompress(payload);
            NeuLogUtils.iTag(TAG,String.format("success gzipUncompress topic=%s",topic));
        }
        try {
            msgContent = new String(payload, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // UTF‑8 安卓全部版本内置，理论不会触发异常
            msgContent = new String(payload);
        }
        return msgContent;
    }

}
