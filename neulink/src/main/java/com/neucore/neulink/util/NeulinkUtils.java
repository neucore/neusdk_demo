package com.neucore.neulink.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.Cmd;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.impl.registry.ServiceRegistry;

import cn.hutool.core.util.ObjectUtil;

public class NeulinkUtils implements NeulinkConst {

    public static void binding(Cmd req, NeulinkTopicParser.Topic topic, JsonObject headers){

        String biz = topic.getBiz();
        String version = topic.getVersion();
        String reqNo = topic.getReqId();
        String md5 = topic.getMd5();
        if(ObjectUtil.isNotEmpty(headers)){
            JsonPrimitive _biz = (JsonPrimitive) headers.get(NEULINK_HEADERS_BIZ);
            if(ObjectUtil.isNotEmpty(_biz)){
                String temp = _biz.getAsString();
                if(ObjectUtil.isNotEmpty(temp)){
                    biz = temp;
                }
            }

            JsonPrimitive _version = (JsonPrimitive) headers.get(NEULINK_HEADERS_VERSION);
            if(ObjectUtil.isNotEmpty(_version)){
                String temp = _version.getAsString();
                if(ObjectUtil.isNotEmpty(temp)){
                    version = temp;
                }
            }
            JsonPrimitive _reqNo = (JsonPrimitive)headers.get(NEULINK_HEADERS_REQNO);
            if(ObjectUtil.isNotEmpty(_reqNo)){
                String temp = _reqNo.getAsString();
                if(ObjectUtil.isNotEmpty(temp)){
                    reqNo = temp;
                }
            }
            JsonPrimitive _md5 = (JsonPrimitive)headers.get(NEULINK_HEADERS_MD5);
            if(ObjectUtil.isNotEmpty(_md5)){
                String temp = _md5.getAsString();
                if(ObjectUtil.isNotEmpty(temp)){
                    md5 = temp;
                }
            }
        }
        if(ObjectUtil.isEmpty(version)){
            version = "v1.0";
        }
        req.setHeader(NEULINK_HEADERS_VERSION,version);
        req.setHeader(NEULINK_HEADERS_REQNO,reqNo);
        req.setHeader(NEULINK_HEADERS_DEVID, ServiceRegistry.getInstance().getDeviceService().getExtSN());
        req.setHeader(NEULINK_HEADERS_BIZ,biz);
        req.setHeader(NEULINK_HEADERS_MD5,md5);
    }

    /**
     *
     * @param payload
     * @param topicStr
     */
    public static void binding(JsonObject payload,String topicStr,int qos){
        long resTime = DatesUtil.getNowTimeStamp();//msg.getReqtime();
        NeulinkTopicParser.Topic topic = NeulinkTopicParser.getInstance().end2cloudParser(topicStr,qos);
        JsonObject  headers = (JsonObject) payload.get(NEULINK_HEADERS);
        if(ObjectUtil.isNotEmpty(headers)){
            headers.add(NEULINK_HEADERS_BIZ, new JsonPrimitive(topic.getBiz()));
            headers.add(NEULINK_HEADERS_VERSION,new JsonPrimitive(topic.getVersion()));
            headers.add(NEULINK_HEADERS_REQNO,new JsonPrimitive(topic.getReqId()));
            headers.add(NEULINK_HEADERS_MD5,new JsonPrimitive(topic.getMd5()));

            headers.add(NEULINK_HEADERS_DEVID, new JsonPrimitive(ServiceRegistry.getInstance().getDeviceService().getExtSN()));
            headers.add(NEULINK_HEADERS_CUSTID, new JsonPrimitive(NeulinkService.getInstance().getCustId()));
            headers.add(NEULINK_HEADERS_STOREID ,new JsonPrimitive(NeulinkService.getInstance().getStoreId()));
            headers.add(NEULINK_HEADERS_ZONEID,new JsonPrimitive(NeulinkService.getInstance().getZoneId()));

            headers.add(NEULINK_HEADERS_TIME,new JsonPrimitive(String.valueOf(resTime)));
        }
        else{
            JsonObject tempHeaders = new JsonObject();

            tempHeaders.add(NEULINK_HEADERS_BIZ,new JsonPrimitive(topic.getBiz()));
            tempHeaders.add(NEULINK_HEADERS_VERSION,new JsonPrimitive(topic.getVersion()));
            tempHeaders.add(NEULINK_HEADERS_REQNO,new JsonPrimitive(topic.getReqId()));
            tempHeaders.add(NEULINK_HEADERS_MD5,new JsonPrimitive(topic.getMd5()));

            tempHeaders.add(NEULINK_HEADERS_DEVID, new JsonPrimitive(ServiceRegistry.getInstance().getDeviceService().getExtSN()));
            tempHeaders.add(NEULINK_HEADERS_CUSTID, new JsonPrimitive(NeulinkService.getInstance().getCustId()));
            tempHeaders.add(NEULINK_HEADERS_STOREID,new JsonPrimitive(NeulinkService.getInstance().getStoreId()));
            tempHeaders.add(NEULINK_HEADERS_ZONEID,new JsonPrimitive(NeulinkService.getInstance().getZoneId()));

            tempHeaders.add(NEULINK_HEADERS_TIME,new JsonPrimitive(String.valueOf(resTime)));

            payload.add(NEULINK_HEADERS,tempHeaders);
        }
    }
}
