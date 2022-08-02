package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.log.NeuLogUtils;

import org.eclipse.paho.mqttv5.client.IMqttToken;


/**
 * Mqtt事件回调实现
 */
public class MyMqttCallbackImpl implements IMqttCallBack {
    private String TAG = "MyMqttCallbackImpl";
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        /**
         * 可以用在APP交互提示等
         */
        NeuLogUtils.iTag(TAG,"connectComplete");
    }

    @Override
    public void messageArrived(String topic, String message, int qos) throws Exception {
        /**
         * 可以不用管
         */
        NeuLogUtils.iTag(TAG,"messageArrived");
    }

    @Override
    public void connectionLost(Throwable arg0) {
        /**
         * 可以用在APP交互提示等
         */
        NeuLogUtils.iTag(TAG,"connectionLost");
    }

    @Override
    public void deliveryComplete(IMqttToken arg0) {
        /**
         * 可以用在APP交互提示等
         */
        NeuLogUtils.iTag(TAG,"deliveryComplete");
    }

    @Override
    public void connectSuccess(IMqttToken arg0) {
        /**
         * 可以用在APP交互提示等
         */
        NeuLogUtils.iTag(TAG,"connectSuccess");
    }

    @Override
    public void connectFailed(IMqttToken arg0, Throwable arg1) {
        /**
         * 可以用在APP交互提示等
         */
        NeuLogUtils.iTag(TAG,"connectFailed");
    }
}
