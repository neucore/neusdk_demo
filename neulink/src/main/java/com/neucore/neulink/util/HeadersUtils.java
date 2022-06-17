package com.neucore.neulink.util;

import com.neucore.neulink.impl.Cmd;
import com.neucore.neulink.impl.CmdRes;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.impl.registry.ServiceRegistry;

import java.util.HashMap;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;

public class HeadersUtils {

    public static void binding(Cmd req, NeulinkTopicParser.Topic topic, JSONObject headers){

        String biz = topic.getBiz();
        String version = topic.getVersion();
        String reqNo = topic.getReqId();
        String md5 = topic.getMd5();
        if(ObjectUtil.isNotEmpty(headers)){
            String _biz = (String) headers.get("biz");
            if(ObjectUtil.isNotEmpty(_biz)){
                biz = _biz;
            }
            String _version = (String)headers.get("version");
            if(ObjectUtil.isNotEmpty(_version)){
                version = _version;
            }
            String _reqNo = (String)headers.get("reqNo");
            if(ObjectUtil.isNotEmpty(_reqNo)){
                reqNo = _reqNo;
            }
            String _md5 = (String)headers.get("md5");
            if(ObjectUtil.isNotEmpty(_md5)){
                md5 = _md5;
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
    public static void binding(JSONObject payload,String topicStr,int qos){
        long resTime = DatesUtil.getNowTimeStamp();//msg.getReqtime();
        NeulinkTopicParser.Topic topic = NeulinkTopicParser.getInstance().end2cloudParser(topicStr,qos);
        JSONObject  headers = (JSONObject) payload.get("headers");
        if(ObjectUtil.isNotEmpty(headers)){
            headers.putOpt("biz",topic.getBiz());
            headers.putOpt("version",topic.getVersion());
            headers.putOpt("reqNo",topic.getReqId());
            headers.putOpt("md5",topic.getMd5());
            headers.putOpt("devid", ServiceRegistry.getInstance().getDeviceService().getExtSN());
            headers.putOpt("custid", NeulinkService.getInstance().getCustId());
            headers.putOpt("storeid",NeulinkService.getInstance().getStoreId());
            headers.putOpt("zoneid",NeulinkService.getInstance().getZoneId());

            headers.putOpt("restime",String.valueOf(resTime));
        }
        else{
            HashMap<String,String> tempHeaders = new HashMap<>();
            tempHeaders.put("biz",topic.getBiz());
            tempHeaders.put("version",topic.getVersion());
            tempHeaders.put("reqNo",topic.getReqId());
            tempHeaders.put("md5",topic.getMd5());

            tempHeaders.put("devid", ServiceRegistry.getInstance().getDeviceService().getExtSN());
            tempHeaders.put("custid", NeulinkService.getInstance().getCustId());
            tempHeaders.put("storeid",NeulinkService.getInstance().getStoreId());
            tempHeaders.put("zoneid",NeulinkService.getInstance().getZoneId());

            tempHeaders.put("restime",String.valueOf(resTime));

            payload.putOnce("headers",tempHeaders);
        }
    }
}
