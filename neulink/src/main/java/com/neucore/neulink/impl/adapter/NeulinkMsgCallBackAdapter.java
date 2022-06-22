package com.neucore.neulink.impl.adapter;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.neucore.neulink.impl.registry.ProcessRegistry;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.IProcessor;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.RequestContext;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import java.util.UUID;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;

public class NeulinkMsgCallBackAdapter implements IMqttCallBack {

    private String TAG = TAG_PREFIX+"MsgCallBack";

    private Context context;
    private NeulinkService service;
    //private MessageDaoUtils messageDaoUtils;

    public NeulinkMsgCallBackAdapter(Context context, NeulinkService service){
        this.context = context;
        this.service = service;
    }
    @Override
    public void messageArrived(String topicStr, String message, int qos) {

        NeulinkTopicParser.Topic topic = NeulinkTopicParser.getInstance().cloud2EndParser(topicStr,qos);
        String biz = topic.getBiz();
        String reqId = topic.getReqId();
        RequestContext.setId(reqId==null? UUID.randomUUID().toString():reqId);
        JsonObject payload = JSonUtils.toObject(message,JsonObject.class);
        JsonObject headers = (JsonObject) payload.get("headers");
        if(ObjectUtil.isNotEmpty(headers)){
            JsonPrimitive _biz = (JsonPrimitive)headers.get("biz");
            if(ObjectUtil.isNotEmpty(_biz)){
                biz = _biz.getAsString();
            }
        }
        NeuLogUtils.dTag(TAG,"start topic:"+ topicStr+",headers:"+headers);
        NeuLogUtils.dTag(TAG,"message:"+payload);
        biz = biz.toLowerCase();
        IProcessor processor = ProcessRegistry.build(context,biz);

        try {
            if(processor!=null){
                processor.execute(topic,headers,payload);
            }
            else {
                throw new Exception(topicStr+String.format("没有找到相关的%sProcessor实现类...", StrUtil.upperFirst(biz)));
            }
        }
        catch (Throwable ex){
            NeuLogUtils.eTag(TAG,"messageArrived",ex);
        }
        finally {
            NeuLogUtils.dTag(TAG,"finished topic:"+ topicStr+",message:"+message);
            RequestContext.remove();
        }
    }

    public void connectComplete(boolean reconnect, String serverURI){

    }

    @Override
    public void connectionLost(Throwable arg0) {
        //连接断开
        NeuLogUtils.dTag(TAG,"connectionLost");
        NeuLogUtils.eTag(TAG,"connectFailed",arg0);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {
        NeuLogUtils.dTag(TAG,"deliveryComplete");
    }

    @Override
    public void connectSuccess(IMqttToken arg0) {
        NeuLogUtils.dTag(TAG,"connectSuccess");
    }

    @Override
    public void connectFailed(IMqttToken arg0, Throwable arg1) {
        //连接失败
        NeuLogUtils.eTag(TAG,"connectFailed: "+arg1.getMessage());
    }

}
