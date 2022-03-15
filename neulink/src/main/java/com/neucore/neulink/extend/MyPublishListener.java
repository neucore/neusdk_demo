package com.neucore.neulink.extend;

import android.util.Log;

import com.neucore.neulink.IPublishCallback;
import com.neucore.neulink.app.NeulinkConst;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

public class MyPublishListener implements IMqttActionListener, NeulinkConst {

    private String TAG = TAG_PREFIX+"MyPublishListener";
    private String reqId;
    private IPublishCallback iPublishCallback;
    public MyPublishListener(String reqId, IPublishCallback iPublishCallback){
        this.reqId = reqId;
        this.iPublishCallback = iPublishCallback;
    }
    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        Log.i(TAG,"onSuccess");
        Result result = Result.ok();
        result.setReqId(reqId);
        iPublishCallback.onFinished(result);
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Log.i(TAG,"onFailure");
        Result result = Result.fail(exception.getMessage());
        result.setReqId(reqId);
        iPublishCallback.onFinished(result);
    }
}
