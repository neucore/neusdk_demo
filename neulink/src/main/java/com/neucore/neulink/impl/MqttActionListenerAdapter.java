package com.neucore.neulink.impl;

import android.util.Log;

import com.neucore.neulink.IResCallback;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.extend.Result;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

class MqttActionListenerAdapter implements IMqttActionListener, NeulinkConst {

    private String TAG = TAG_PREFIX+"MqttActionListenerAdapter";
    private String reqId;
    private IResCallback iResCallback;
    public MqttActionListenerAdapter(String reqId, IResCallback iResCallback){
        this.reqId = reqId;
        this.iResCallback = iResCallback;
    }
    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        Log.i(TAG,"onSuccess");
        Result result = Result.ok();
        result.setReqId(reqId);
        iResCallback.onFinished(result);
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Log.i(TAG,"onFailure");
        Result result = Result.fail(exception.getMessage());
        result.setReqId(reqId);
        iResCallback.onFinished(result);
    }
}
