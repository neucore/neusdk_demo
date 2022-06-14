package com.neucore.neusdk_demo.neulink.extend;

import android.util.Log;

import com.neucore.neulink.IMqttCallBack;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;

public class MyMqttCallbackImpl implements IMqttCallBack {
    private String TAG = "MyMqttCallbackImpl";
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        /**
         * 可以用在APP交互提示等
         */
        Log.i(TAG,"connectComplete");
    }

    @Override
    public void messageArrived(String topic, String message, int qos) throws Exception {
        /**
         * 可以不用管
         */
        Log.i(TAG,"messageArrived");
    }

    @Override
    public void connectionLost(Throwable arg0) {
        /**
         * 可以用在APP交互提示等
         */
        Log.i(TAG,"connectionLost");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {
        /**
         * 可以用在APP交互提示等
         */
        Log.i(TAG,"deliveryComplete");
    }

    @Override
    public void connectSuccess(IMqttToken arg0) {
        /**
         * 可以用在APP交互提示等
         */
        Log.i(TAG,"connectSuccess");
    }

    @Override
    public void connectFailed(IMqttToken arg0, Throwable arg1) {
        /**
         * 可以用在APP交互提示等
         */
        Log.i(TAG,"connectFailed");
    }
}
