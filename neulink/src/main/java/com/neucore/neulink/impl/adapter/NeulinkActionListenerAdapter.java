package com.neucore.neulink.impl.adapter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.IProcessor;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.impl.registry.ProcessRegistry;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.MessageUtil;
import com.neucore.neulink.util.RequestContext;

import org.eclipse.paho.mqttv5.client.IMqttDeliveryToken;
import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttActionListener;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.client.internal.ConnectActionListener;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

public class NeulinkActionListenerAdapter implements MqttActionListener, MqttCallback, IMqttMessageListener, NeulinkConst {
    private String TAG = TAG_PREFIX+"NeulinkActionListenerAdapter";
    private NeulinkService service = null;
    private IDeviceService deviceService = null;
    private Context context = null;
    private ReentrantLock reentrantLock = new ReentrantLock();

    public NeulinkActionListenerAdapter(Context context, NeulinkService service){
        this.context = context;
        this.service = service;
        deviceService = ServiceRegistry.getInstance().getDeviceService();
    }

    /**
     * MqttActionListener
     * @param asyncActionToken
     */
    @Override
    public void onSuccess(IMqttToken asyncActionToken) {

        MqttActionListener actionListener = asyncActionToken.getActionCallback();
        if(actionListener instanceof ConnectActionListener){
            service.setFailException(null);
            List<IMqttCallBack> mqttCallBacks = service.getMqttCallBacks();
            NeuLogUtils.iTag(TAG, "connectSuccess ");
            if (mqttCallBacks != null) {
                for (IMqttCallBack callback: mqttCallBacks) {
                    try {
                        callback.connectSuccess(asyncActionToken);
                    }
                    catch (Exception ex){
                        NeuLogUtils.eTag(TAG,ex.getMessage());
                    }
                }
            }
        }
    }

    /**
     * MqttActionListener
     * @param asyncActionToken
     * @param exception
     */
    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        NeuLogUtils.eTag(TAG, "onFailure ",exception);
        MqttActionListener actionListener = asyncActionToken.getActionCallback();
        if(actionListener instanceof ConnectActionListener){
            NeuLogUtils.eTag(TAG, "connectFailed ",exception);
            if (!service.isMqttConnSuccessed()) {
                NeuLogUtils.iTag(TAG, "历史没有连接连接成功");
                service.setFailException(exception);
            }
            List<IMqttCallBack> mqttCallBacks = service.getMqttCallBacks();
            if (mqttCallBacks != null) {
                for (IMqttCallBack callback : mqttCallBacks) {
                    try {
                        callback.connectFailed(asyncActionToken, exception);
                    } catch (Exception ex) {
                        NeuLogUtils.eTag(TAG, ex.getMessage());
                    }
                }
            }
        }
    }

    /**
     * MqttCallbackExtended
     * IMqttCallBack
     * @param reconnect
     * @param serverURI
     */
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        try {
            NeuLogUtils.iTag(TAG, "connectComplete ");
            reentrantLock.lock();
            service.getSubscriberFacde().subAll();
            deviceService.connect();
            NeuLogUtils.dTag(TAG, "Server:" + serverURI + " ,connectComplete reconnect:" + reconnect);
            List<IMqttCallBack> mqttCallBacks = service.getMqttCallBacks();
            if (mqttCallBacks != null) {
                for (IMqttCallBack callback: mqttCallBacks) {
                    try {
                        callback.connectComplete(reconnect, serverURI);
                    }
                    catch (Exception ex){
                        NeuLogUtils.eTag(TAG,ex.getMessage());
                    }
                }
            }
        }
        finally {
            reentrantLock.unlock();
        }
    }

    /**
     *
     * @param reasonCode
     * @param properties
     */
    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties) {
        NeuLogUtils.dTag(TAG,String.format("authPacketArrived reasonCode=%s,properties=%s",reasonCode,properties));
    }

    /**
     *
     * @param token
     */
    @Override
    public void deliveryComplete(IMqttToken token) {
        NeuLogUtils.dTag(TAG,"deliveryComplete");
        List<IMqttCallBack> mqttCallBacks = service.getMqttCallBacks();
        if (mqttCallBacks != null) {
            for (IMqttCallBack callback: mqttCallBacks) {
                try {
                    callback.deliveryComplete(token);
                }
                catch (Exception ex){
                    NeuLogUtils.eTag(TAG,ex.getMessage());
                }
            }
        }
    }

    /**
     *
     * @param disconnectResponse
     */
    @Override
    public void disconnected(MqttDisconnectResponse disconnectResponse) {
        NeuLogUtils.eTag(TAG,"disconnected",disconnectResponse.getException());
        deviceService.disconnect();
        List<IMqttCallBack> mqttCallBacks = service.getMqttCallBacks();
        if (mqttCallBacks != null) {
            for (IMqttCallBack callback: mqttCallBacks) {
                try {
                    callback.connectionLost(disconnectResponse.getException());
                }
                catch (Exception ex){
                    NeuLogUtils.eTag(TAG,ex.getMessage());
                }
            }
        }
    }

    /**
     *
     * @param exception
     */
    @Override
    public void mqttErrorOccurred(MqttException exception) {
        NeuLogUtils.eTag(TAG,"mqttErrorOccurred",exception);
    }

    /**
     *
     * MqttCallbackExtended
     * IMqttMessageListener
     * MqttCallback
     * @param topicStr
     * @param message
     * @throws Exception
     */
    @Override
    public void messageArrived(String topicStr, MqttMessage message) {
        boolean debug = topicStr.toLowerCase().endsWith("/debug");
        RequestContext.setDebug(debug);
        NeulinkTopicParser.Topic topic = NeulinkTopicParser.getInstance().cloud2EndParser(topicStr);
        String biz = topic.getBiz();
        String reqId = topic.getReqId();
        int qos = message.getQos();
        boolean isRetained = message.isRetained();
        int messageId = message.getId();
        String msgContent = MessageUtil.decode(debug,topicStr,message);
        JsonObject payload = JSonUtils.toObject(msgContent,JsonObject.class);
        JsonObject headers = (JsonObject) payload.get(NEULINK_HEADERS);
        if(ObjectUtil.isNotEmpty(headers)){
            JsonPrimitive _biz = (JsonPrimitive)headers.get(NEULINK_HEADERS_BIZ);
            if(ObjectUtil.isNotEmpty(_biz)){
                biz = _biz.getAsString();
            }
            JsonPrimitive _reqId = (JsonPrimitive)headers.get(NEULINK_HEADERS_REQNO);
            if(ObjectUtil.isNotEmpty(_reqId)){
                reqId = _reqId.getAsString();
            }
        }
        RequestContext.setId(reqId==null? UUID.randomUUID().toString():reqId);
        String detailLog = topic + ";qos:" + qos + ";retained:" + isRetained + "messageId:"+messageId;
        NeuLogUtils.iTag(TAG, detailLog);
        NeuLogUtils.iTag(TAG, "messageArrived:" + msgContent);
        biz = biz.toLowerCase();
        IProcessor processor = ProcessRegistry.build(context,biz);

        try {
            if(processor!=null){

                processor.execute(debug,qos,isRetained,topic,headers,payload);
            }
            else {
                NeuLogUtils.eTag(TAG,topicStr+String.format("没有找到相关的%sProcessor实现类...",StrUtil.upperFirst(biz)));
                //throw new Exception(topicStr+String.format("没有找到相关的%sProcessor实现类...", StrUtil.upperFirst(biz)));
            }
            List<IMqttCallBack> mqttCallBacks = service.getMqttCallBacks();
            if (mqttCallBacks != null) {
                for (IMqttCallBack callback: mqttCallBacks) {
                    try {
                        callback.messageArrived(topicStr, msgContent, message.getQos());
                    }
                    catch (Exception ex){
                        NeuLogUtils.eTag(TAG,ex.getMessage());
                    }
                }
            }
        }
        catch (Throwable ex){
            NeuLogUtils.eTag(TAG,"messageArrived",ex);
        }
        finally {
            NeuLogUtils.dTag(TAG,"finished topic:"+ topicStr+",message:"+msgContent);
        }
    }
}
