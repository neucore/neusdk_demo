package com.neucore.neulink.impl.adapter;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.log.LogUtils;
import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.IProcessor;
import com.neucore.neulink.impl.NeulinkProcessorFactory;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.RequestContext;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import java.util.UUID;

import cn.hutool.core.util.StrUtil;

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

        NeulinkTopicParser.Topic topic = NeulinkTopicParser.getInstance().parser(topicStr,qos);
        String biz = topic.getBiz();
        String reqId = topic.getReqId();
        RequestContext.setId(reqId==null? UUID.randomUUID().toString():reqId);

        LogUtils.dTag(TAG,"start topic:"+ topicStr+",message:"+message);

        IProcessor processor = NeulinkProcessorFactory.build(context,topic);

        try {
            if(processor!=null){
                processor.execute(topic,message);
            }
            else {
                throw new Exception(topicStr+String.format("没有找到相关的%sProcessor实现类...", StrUtil.upperFirst(biz)));
            }
        }
        catch (Throwable ex){
            LogUtils.eTag(TAG,"messageArrived",ex);
        }
        finally {
            LogUtils.dTag(TAG,"finished topic:"+ topicStr+",message:"+message);
            RequestContext.remove();
        }
    }

    public void connectComplete(boolean reconnect, String serverURI){

    }

    @Override
    public void connectionLost(Throwable arg0) {
        //连接断开
        LogUtils.dTag(TAG,"connectionLost");
        LogUtils.eTag(TAG,"connectFailed",arg0);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {
        LogUtils.dTag(TAG,"deliveryComplete");
    }

    @Override
    public void connectSuccess(IMqttToken arg0) {
        LogUtils.dTag(TAG,"connectSuccess");
    }

    @Override
    public void connectFailed(IMqttToken arg0, Throwable arg1) {
        //连接失败
        LogUtils.eTag(TAG,"connectFailed: "+arg1.getMessage());
    }

}
