package com.neucore.neulink.impl;

import android.content.Context;

import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.registry.ServiceRegistry;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttReceivedMessage;

import java.util.List;

/**
 * 终端消费者
 */
public class NeulinkSubscriberFacde implements NeulinkConst{
    private String TAG = TAG_PREFIX+"SubscriberFacde";

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

        subScopeBcst();//订阅租户广播消息
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
    private void subRmsg(){

        String rmsg_topic = "rmsg/req/" + ServiceRegistry.getInstance().getDeviceService().getExtSN() + "/#";

        service.subscribeToTopic(rmsg_topic, 0,messageListener);
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
    private void subRrpc(){

        String rrpc_topic = "rrpc/req/" + ServiceRegistry.getInstance().getDeviceService().getExtSN() + "/#";
        service.subscribeToTopic(rrpc_topic, 0,messageListener);
    }

    /**
     * 车牌信息上报响应 upld/res/${dev_id}/carplateinfo/v1.0/${req_no}[/${md5}], qos=0
     * 温度信息上报响应 upld/res/${dev_id}/facetemprature/v1.0/${req_no}[/${md5}], qos=0
     */
    private void subUpld(){

        String upld_topic = "upld/res/" + ServiceRegistry.getInstance().getDeviceService().getExtSN() + "/#";

        service.subscribeToTopic(upld_topic, 0, messageListener);
    }

    /**
     * 广播
     */
    private void subScopeBcst(){
        String sys_ctrl_topic = "bcst/req/" + service.getCustId() + "/#";
        service.subscribeToTopic(sys_ctrl_topic, 0, messageListener);
    }

    /**
     * msg resppnse listener
     */
    private IMqttMessageListener messageListener = new IMqttMessageListener(){

        private String TAG = TAG_PREFIX+"IMqttMessageListener";
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {

            MqttReceivedMessage receivedMessage = (MqttReceivedMessage)message;

            String msgContent = new String(receivedMessage.getPayload());

            List<IMqttCallBack>  mqttCallBacks = service.getMqttCallBacks();
            if (mqttCallBacks != null) {
                if (mqttCallBacks != null) {
                    for (IMqttCallBack callback: mqttCallBacks) {
                        callback.messageArrived(topic, msgContent, receivedMessage.getQos());
                    }
                }
            }
        }
    };
}
