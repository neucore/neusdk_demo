package com.neucore.neulink.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.neucore.neulink.impl.Cmd;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.impl.registry.ServiceRegistry;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.HashMap;

import cn.hutool.core.util.ObjectUtil;

public class HeadersUtils {

    public static void binding(Cmd req, NeulinkTopicParser.Topic topic, JsonObject headers){

        String biz = topic.getBiz();
        String version = topic.getVersion();
        String reqNo = topic.getReqId();
        String md5 = topic.getMd5();
        if(ObjectUtil.isNotEmpty(headers)){
            JsonPrimitive _biz = (JsonPrimitive) headers.get("biz");
            if(ObjectUtil.isNotEmpty(_biz)){
                biz = _biz.getAsString();
            }
            JsonPrimitive _version = (JsonPrimitive) headers.get("version");
            if(ObjectUtil.isNotEmpty(_version)){
                version = _version.getAsString();
            }
            JsonPrimitive _reqNo = (JsonPrimitive)headers.get("reqNo");
            if(ObjectUtil.isNotEmpty(_reqNo)){
                reqNo = _reqNo.getAsString();
            }
            JsonPrimitive _md5 = (JsonPrimitive)headers.get("md5");
            if(ObjectUtil.isNotEmpty(_md5)){
                md5 = _md5.getAsString();
            }
        }
        req.setHeader("biz",biz);
        req.setHeader("version",version);
        req.setHeader("reqNo",reqNo);
        req.setHeader("devId", ServiceRegistry.getInstance().getDeviceService().getExtSN());
    }
    
    /**
     *
     * @param payload
     * @param topicStr
     */
    public static void binding(JsonObject payload,String topicStr,int qos){
        long resTime = DatesUtil.getNowTimeStamp();//msg.getReqtime();
        NeulinkTopicParser.Topic topic = NeulinkTopicParser.getInstance().end2cloudParser(topicStr,qos);
        JsonObject  headers = (JsonObject) payload.get("headers");
        if(ObjectUtil.isNotEmpty(headers)){
            headers.add("biz", new JsonPrimitive(topic.getBiz()));
            headers.add("version",new JsonPrimitive(topic.getVersion()));
            headers.add("reqNo",new JsonPrimitive(topic.getReqId()));
            headers.add("md5",new JsonPrimitive(topic.getMd5()));

            headers.add("devid", new JsonPrimitive(ServiceRegistry.getInstance().getDeviceService().getExtSN()));
            headers.add("custid", new JsonPrimitive(NeulinkService.getInstance().getCustId()));
            headers.add("storeid",new JsonPrimitive(NeulinkService.getInstance().getStoreId()));
            headers.add("zoneid",new JsonPrimitive(NeulinkService.getInstance().getZoneId()));

            headers.add("time",new JsonPrimitive(String.valueOf(resTime)));
        }
        else{
            JsonObject tempHeaders = new JsonObject();

            tempHeaders.add("biz",new JsonPrimitive(topic.getBiz()));
            tempHeaders.add("version",new JsonPrimitive(topic.getVersion()));
            tempHeaders.add("reqNo",new JsonPrimitive(topic.getReqId()));
            tempHeaders.add("md5",new JsonPrimitive(topic.getMd5()));

            tempHeaders.add("devid", new JsonPrimitive(ServiceRegistry.getInstance().getDeviceService().getExtSN()));
            tempHeaders.add("custid", new JsonPrimitive(NeulinkService.getInstance().getCustId()));
            tempHeaders.add("storeid",new JsonPrimitive(NeulinkService.getInstance().getStoreId()));
            tempHeaders.add("zoneid",new JsonPrimitive(NeulinkService.getInstance().getZoneId()));

            tempHeaders.add("time",new JsonPrimitive(String.valueOf(resTime)));

            payload.add("headers",tempHeaders);
        }
    }
}
