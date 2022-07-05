package com.neucore.neulink.impl;

import android.content.Context;

import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.util.MessageUtil;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

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
    public void subAll(){
        /**
         * 单播
         *
         * 设备重启             rmsg/req/${dev_id}/sys_ctrl/v1.0/${req_no}[/${md5}], qos=0
         * 设备休眠             rmsg/req/${dev_id}/sys_ctrl/v1.0/${req_no}[/${md5}], qos=0
         * 设备唤醒             rmsg/req/${dev_id}/sys_ctrl/v1.0/${req_no}[/${md5}], qos=0
         * 固件升级             rmsg/req/${dev_id}/fireware/v1.0/${req_no}[/${md5}], qos=0
         * Debug设置          rmsg/req/${dev_id}/sys_ctrl/v1.0/${req_no}[/${md5}], qos=0
         *
         * 算法升级           rrpc/req/${dev_id}/alog/v1.0/${req_no}[/${md5}], qos=0
         * 执行shell命令      rrpc/req/${dev_id}/shell/v1.0/${req_no}[/${md5}], qos=0
         * 日志导出           rrpc/req/${dev_id}/rlog/v1.0/${req_no}[/${md5}],qos=0
         * 目标库批量同步      rrpc/req/${dev_id}/blib/v1.0/${req_no}[/${md5}],qos=0
         * 目标库单条写操作    rrpc/req/${dev_id}/lib/v1.0/${req_no}[/${md5}],qos=0
         * 目标库批量查询操作  rrpc/req/ ${dev_id}/qlib/${req_no}[/${md5},qos=0
         * 终端配置管理       rrpc/req/${dev_id}/cfg/v1.1/${req_no}[/${md5}],qos=0
         * 查看终端配置       rrpc/req/${dev_id}/qcfg/v1.1/${req_no}[/${md5}],qos=0
         * 预约信息展示       rrpc/req/${dev_id}/reserve/v1.0/${req_no}[/${md5}], qos=0
         *
         * 抓拍库上报响应      upld/res/{$dev_id}/biz/v1.0/${req_no}[/${md5}], qos=0
         */
        String ucst_topic = "+/req/" + ServiceRegistry.getInstance().getDeviceService().getExtSN() + "/#";
        /**
         * 广播
         *
         * 设备重启             bcst/req/${scopeId}/sys_ctrl/v1.0/${req_no}[/${md5}], qos=0
         * 设备休眠             bcst/req/${scopeId}/sys_ctrl/v1.0/${req_no}[/${md5}], qos=0
         * 设备唤醒             bcst/req/${scopeId}/sys_ctrl/v1.0/${req_no}[/${md5}], qos=0
         * 固件升级             bcst/req/${scopeId}/fireware/v1.0/${req_no}[/${md5}], qos=0
         * Debug设置           bcst/req/${scopeId}/sys_ctrl/v1.0/${req_no}[/${md5}], qos=0
         *
         * 算法升级             bcst/req/${scopeId}/alog/v1.0/${req_no}[/${md5}], qos=0
         * 执行shell命令        bcst/req/${scopeId}/shell/v1.0/${req_no}[/${md5}], qos=0
         * 日志导出             bcst/req/${scopeId}/rlog/v1.0/${req_no}[/${md5}],qos=0
         * 目标库批量同步        bcst/req/${scopeId}/blib/v1.0/${req_no}[/${md5}],qos=0
         * 目标库单条写操作       bcst/req/${scopeId}/lib/v1.0/${req_no}[/${md5}],qos=0
         * 目标库批量查询操作     bcst/req/ ${scopeId}/qlib/${req_no}[/${md5},qos=0
         * 终端配置管理          bcst/req/${scopeId}/cfg/v1.1/${req_no}[/${md5}],qos=0
         * 查看终端配置          bcst/req/${scopeId}/qcfg/v1.1/${req_no}[/${md5}],qos=0
         * 预约信息展示          bcst/req/${scopeId}/reserve/v1.0/${req_no}[/${md5}], qos=0
         */
        String bcst_topic = "+/req/" + service.getCustId() + "/#";
        int qos = ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,0);
        int[] qoss = new int[]{qos,qos};
        IMqttMessageListener[] listeners = new IMqttMessageListener[]{service.getDefaultNeulinkMqttCallbackAdapter(),service.getDefaultNeulinkMqttCallbackAdapter()};
        service.subscribeToTopic(new String[]{ucst_topic,bcst_topic}, qoss, listeners);
    }
}
