package com.neucore.neulink.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.Cmd;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.impl.NewCmd;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.registry.ServiceRegistry;

import cn.hutool.core.util.ObjectUtil;

public class HeadersUtil implements NeulinkConst {

    /**
     * cloud2End
     * @param req
     * @param topic
     * @param headers
     */
    public static void binding(NewCmd req, NeulinkTopicParser.Topic topic, JsonObject headers){
        String group = topic.getGroup();
        String req$res = topic.getReq$res();
        String biz = topic.getBiz();
        String version = topic.getVersion();
        String reqNo = topic.getReqId();
        String md5 = topic.getMd5();
        String clientId = null;
        if(ObjectUtil.isNotEmpty(headers)){
            JsonPrimitive _group = (JsonPrimitive) headers.get(NEULINK_HEADERS_GROUP);
            if(ObjectUtil.isNotEmpty(_group)){
                String temp = _group.getAsString();
                if(ObjectUtil.isNotEmpty(temp)){
                    group = temp;
                }
            }

            JsonPrimitive _cmdType = (JsonPrimitive) headers.get(NEULINK_HEADERS_REQ$RES);
            if(ObjectUtil.isNotEmpty(_cmdType)){
                String temp = _cmdType.getAsString();
                if(ObjectUtil.isNotEmpty(temp)){
                    req$res = temp;
                }
            }

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
            JsonPrimitive _clientId = (JsonPrimitive)headers.get(NEULINK_HEADERS_CLIENT_ID);
            if(ObjectUtil.isNotEmpty(_clientId)){
                String temp = _clientId.getAsString();
                if(ObjectUtil.isNotEmpty(temp)){
                    clientId = temp;
                }
            }
        }
        if(ObjectUtil.isEmpty(version)){
            version = "v1.0";
        }
        req.setGroup(group);
        req.setCmdType(req$res);
        req.setBiz(biz);
        req.setVersion(version);
        req.setReqNo(reqNo);
        req.setMd5(md5);
        req.setClientId(clientId);
    }

    /**
     * 设备注册
     * @param payload
     * @param topicStr
     * @param qos
     */
    public static void registBinding(JsonObject payload,String topicStr,int qos){
        binding(payload,topicStr,qos);
        String lzr = ConfigContext.getInstance().getConfig(NeulinkConst.NEULINK_HEADERS_LZR,TimeZoneId_Asia$ShangHai);
        payload.add(NEULINK_HEADERS_LZR,new JsonPrimitive(String.valueOf(lzr)));
    }
    /**
     * end2Clould
     * @param payload
     * @param topicStr
     */
    public static void binding(JsonObject payload,String topicStr,int qos){
        long resTime = DatesUtil.getNowTimeStamp();//msg.getReqtime();
        NeulinkTopicParser.Topic topic = NeulinkTopicParser.getInstance().end2cloudParser(topicStr,qos);
        JsonObject  headers = (JsonObject) payload.get(NEULINK_HEADERS);
        if(ObjectUtil.isNotEmpty(headers)){
            if(ObjectUtil.isNotEmpty(topic.getBiz())) {
                headers.add(NEULINK_HEADERS_BIZ, new JsonPrimitive(topic.getBiz()));
            }
            if(ObjectUtil.isNotEmpty(topic.getVersion())) {
                headers.add(NEULINK_HEADERS_VERSION, new JsonPrimitive(topic.getVersion()));
            }
            if(ObjectUtil.isNotEmpty(topic.getReqId())) {
                headers.add(NEULINK_HEADERS_REQNO, new JsonPrimitive(topic.getReqId()));
            }
            if(ObjectUtil.isNotEmpty(topic.getMd5())){
                headers.add(NEULINK_HEADERS_MD5,new JsonPrimitive(topic.getMd5()));
            }
            if(ObjectUtil.isNotEmpty(ServiceRegistry.getInstance().getDeviceService().getExtSN())) {
                headers.add(NEULINK_HEADERS_DEVID, new JsonPrimitive(ServiceRegistry.getInstance().getDeviceService().getExtSN()));
            }
            if(ObjectUtil.isNotEmpty(NeulinkService.getInstance().getCustId())) {
                headers.add(NEULINK_HEADERS_CUSTID, new JsonPrimitive(NeulinkService.getInstance().getCustId()));
            }
            if(ObjectUtil.isNotEmpty(NeulinkService.getInstance().getStoreId())) {
                headers.add(NEULINK_HEADERS_STOREID, new JsonPrimitive(NeulinkService.getInstance().getStoreId()));
            }
            if(ObjectUtil.isNotEmpty(NeulinkService.getInstance().getZoneId())) {
                headers.add(NEULINK_HEADERS_ZONEID, new JsonPrimitive(NeulinkService.getInstance().getZoneId()));
            }
            if(ObjectUtil.isNotEmpty(resTime)) {
                headers.add(NEULINK_HEADERS_TIME, new JsonPrimitive(String.valueOf(resTime)));
            }
        }
        else{
            JsonObject tempHeaders = new JsonObject();

            if(ObjectUtil.isNotEmpty(topic.getBiz())) {
                headers.add(NEULINK_HEADERS_BIZ, new JsonPrimitive(topic.getBiz()));
            }
            if(ObjectUtil.isNotEmpty(topic.getVersion())) {
                headers.add(NEULINK_HEADERS_VERSION, new JsonPrimitive(topic.getVersion()));
            }
            if(ObjectUtil.isNotEmpty(topic.getReqId())) {
                headers.add(NEULINK_HEADERS_REQNO, new JsonPrimitive(topic.getReqId()));
            }
            if(ObjectUtil.isNotEmpty(topic.getMd5())){
                headers.add(NEULINK_HEADERS_MD5,new JsonPrimitive(topic.getMd5()));
            }

            if(ObjectUtil.isNotEmpty(ServiceRegistry.getInstance().getDeviceService().getExtSN())) {
                headers.add(NEULINK_HEADERS_DEVID, new JsonPrimitive(ServiceRegistry.getInstance().getDeviceService().getExtSN()));
            }
            if(ObjectUtil.isNotEmpty(NeulinkService.getInstance().getCustId())) {
                headers.add(NEULINK_HEADERS_CUSTID, new JsonPrimitive(NeulinkService.getInstance().getCustId()));
            }
            if(ObjectUtil.isNotEmpty(NeulinkService.getInstance().getStoreId())) {
                headers.add(NEULINK_HEADERS_STOREID, new JsonPrimitive(NeulinkService.getInstance().getStoreId()));
            }
            if(ObjectUtil.isNotEmpty(NeulinkService.getInstance().getZoneId())) {
                headers.add(NEULINK_HEADERS_ZONEID, new JsonPrimitive(NeulinkService.getInstance().getZoneId()));
            }
            if(ObjectUtil.isNotEmpty(resTime)) {
                headers.add(NEULINK_HEADERS_TIME, new JsonPrimitive(String.valueOf(resTime)));
            }

            payload.add(NEULINK_HEADERS,tempHeaders);
        }
    }
}
