package com.neucore.neulink;

import org.eclipse.paho.mqttv5.client.IMqttToken;

public interface IMqttCallBack extends NeulinkConst {

    /**
     * 收到消息
     *
     * @param topic   主题
     * @param message 消息内容
     * @param qos     消息策略
     */
    default void messageArrived(String topic, String message, int qos) throws Exception{}

    /**
     * 连接断开
     *
     * @param arg0 抛出的异常信息
     */
    default void connectionLost(Throwable arg0){}

    /**
     * 传送完成
     *
     * @param arg0
     */
    default void deliveryComplete(IMqttToken arg0){}

    /**
     * 连接成功
     *
     * @param arg0
     */
    default void connectSuccess(IMqttToken arg0){}

    /**
     * 连接完成
     * @param reconnect
     * @param serverURI
     */
    default void connectComplete(boolean reconnect, String serverURI){}

    /**
     * 连接失败
     *
     * @param arg0
     */
    default void connectFailed(IMqttToken arg0, Throwable arg1){}

}