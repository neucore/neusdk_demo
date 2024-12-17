package com.neucore.neulink.impl;

import android.content.Context;

import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.registry.ServiceRegistry;

import org.eclipse.paho.mqttv5.client.MqttActionListener;

import cn.hutool.core.util.ObjectUtil;

/**
 * 终端消费者
 */
public class NeulinkSubscriberFacde implements NeulinkConst{

    private String TAG = TAG_PREFIX+"SubscriberFacde";

    private Context context;
    private NeulinkService service;
    private MqttActionListener listeners = null;
    private String[] topics = null;
    private int[] qoss = null;
    public NeulinkSubscriberFacde(Context context, NeulinkService service){
        this.context = context;
        this.service = service;
        listeners = service.getNeulinkActionListenerAdapter();
        IDeviceService deviceService = ServiceRegistry.getInstance().getDeviceService();
        String productId = deviceService.getProductKey();
        String extSN = deviceService.getExtSN();
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
         */
        String rmsg_topic = "rmsg/req/" + extSN + "/#";
        if(ObjectUtil.isNotEmpty(productId)){
            rmsg_topic = String.format("%s/%s",productId,rmsg_topic);
        }

        String rrpc_topic = "rrpc/req/" + extSN + "/#";
        if(ObjectUtil.isNotEmpty(productId)){
            rrpc_topic = String.format("%s/%s",productId,rrpc_topic);
        }
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
        int qos = ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,0);
        qoss = new int[]{qos,qos};
        topics = new String[]{rmsg_topic,rrpc_topic};

        boolean bcstEnable = ConfigContext.getInstance().getConfig(ConfigContext.BCST_ENABLE,false);
        if(bcstEnable){
            String bcst_topic = "bcst/req/" + service.getCustId() + "/#";
            if(ObjectUtil.isNotEmpty(productId)){
                bcst_topic = String.format("%s/%s",productId,bcst_topic);
            }
            qoss = new int[]{qos,qos,qos};
            topics = new String[]{rmsg_topic,rrpc_topic,bcst_topic};
        }
    }

    /**
     *
     */
    public void subAll(){
        service.subscribeToTopic(topics, qoss, listeners);
    }

    public void unsubAll(){
        service.unsubscribeToTopic(topics, qoss,listeners);
    }
}
