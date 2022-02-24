package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.extend.ServiceFactory;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttReceivedMessage;

/**
 * 终端消费者
 */
public class NeulinkSubscriberFacde {
    private String TAG = NeulinkConst.TAG_PREFIX+"SubscriberFacde";

    private Context context;
    private NeulinkService service;

    public NeulinkSubscriberFacde(Context context, NeulinkService service){
        this.context = context;
        this.service = service;
    }

    /**
     *
     */
    protected void subAll(){

        subRmsg();//订阅设备管理消息

        subRrpc();//订阅应用管理消息

        subUpld();//信息上报
    }

    /**
     *
     * 设备重启 rmsg/req/${dev_id}/sys_ctrl/v1.0/${req_no}[/${md5}], qos=0
     * 设备休眠 rmsg/req/${dev_id}/sys_ctrl/v1.0/${req_no}[/${md5}], qos=0
     * 设备唤醒 rmsg/req/${dev_id}/sys_ctrl/v1.0/${req_no}[/${md5}], qos=0
     * 固件升级 rmsg/req/${dev_id}/fireware/v1.0/${req_no}[/${md5}], qos=0
     * Debug设置rmsg/req/${dev_id}/sys_ctrl/v1.0/${req_no}[/${md5}], qos=0
     *
     */
    public void subRmsg(){

        String sys_ctrl_topic = "rmsg/req/" + ServiceFactory.getInstance().getDeviceService().getExtSN() + "/#";

        service.subscribeToTopic(sys_ctrl_topic, 0,messageListener);
    }

    /**
     *
     *
     * 算法升级            rrpc/req/${dev_id}/alog/v1.0/${req_no}[/${md5}], qos=0
     * 执行shell命令      rrpc/req/${dev_id}/shell/v1.0/${req_no}[/${md5}], qos=0
     * 日志导出           rrpc/req/${dev_id}/rlog/v1.0/${req_no}[/${md5}],qos=0
     * 目标库批量同步      rrpc/req/${dev_id}/blib/v1.0/${req_no}[/${md5}],qos=0
     * 目标库单条写操作    rrpc/req/${dev_id}/lib/v1.0/${req_no}[/${md5}],qos=0
     * 目标库批量查询操作  rrpc/req/ ${dev_id}/qlib/${req_no}[/${md5},qos=0
     * 终端配置管理       rrpc/req/${dev_id}/cfg/v1.1/${req_no}[/${md5}],qos=0
     * 查看终端配置       rrpc/req/${dev_id}/qcfg/v1.1/${req_no}[/${md5}],qos=0
     * 预约信息展示       rrpc/req/${dev_id}/reserve/v1.0/${req_no}[/${md5}], qos=0
     *
     */
    public void subRrpc(){

        String sys_ctrl_topic = "rrpc/req/" + ServiceFactory.getInstance().getDeviceService().getExtSN() + "/#";

        service.subscribeToTopic(sys_ctrl_topic, 0,messageListener);
    }

    /**
     * 车牌信息上报响应 upld/res/${dev_id}/carplateinfo/v1.0/${req_no}[/${md5}], qos=0
     * 温度信息上报响应 upld/res/${dev_id}/facetemprature/v1.0/${req_no}[/${md5}], qos=0
     */
    public void subUpld(){

        String sys_ctrl_topic = "upld/res/" + ServiceFactory.getInstance().getDeviceService().getExtSN() + "/#";

        service.subscribeToTopic(sys_ctrl_topic, 0, messageListener);
    }

    /**
     * msg resppnse listener
     */
    private IMqttMessageListener messageListener = new IMqttMessageListener(){

        private String TAG = "IMqttMessageListener";
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {

            MqttReceivedMessage receivedMessage = (MqttReceivedMessage)message;

            int messageId = receivedMessage.getMessageId();
            String detailLog = topic + ";qos:" + receivedMessage.getQos() + ";retained:" + receivedMessage.isRetained() + ",messageId:"+messageId;
            String msgContent = new String(receivedMessage.getPayload());

            Log.i(TAG, detailLog);

            Log.i(TAG, "messageArrived:" + msgContent);

            if (service.getStarMQTTCallBack() != null) {
                service.getStarMQTTCallBack().messageArrived(topic, msgContent, receivedMessage.getQos());
            }
        }
    };
}
