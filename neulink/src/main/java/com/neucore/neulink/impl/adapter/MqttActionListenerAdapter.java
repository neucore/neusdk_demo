package com.neucore.neulink.impl.adapter;

import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.IResCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.Result;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

public class MqttActionListenerAdapter implements IMqttActionListener, NeulinkConst {

    private String TAG = TAG_PREFIX+"MqttActionListenerAdapter";
    private String reqId;
    private IResCallback iResCallback;
    public MqttActionListenerAdapter(String reqId, IResCallback iResCallback){
        this.reqId = reqId;
        this.iResCallback = iResCallback;
    }
    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        NeuLogUtils.iTag(TAG,"onSuccess");
        Result result = Result.ok();
        result.setReqId(reqId);
        iResCallback.onFinished(result);
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        NeuLogUtils.iTag(TAG,"onFailure");
        Result result = Result.fail(exception.getMessage());
        result.setReqId(reqId);
        iResCallback.onFinished(result);
    }
}
