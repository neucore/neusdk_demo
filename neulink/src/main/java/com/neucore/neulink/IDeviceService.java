package com.neucore.neulink;

import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.cmd.msg.DeviceInfo;
import com.neucore.neulink.impl.cmd.msg.HeatbeatInfo;
import com.neucore.neulink.impl.cmd.msg.RuntimeInfo;
import com.neucore.neulink.impl.service.LWTPayload;
import com.neucore.neulink.impl.service.LWTTopic;
import com.neucore.neulink.impl.service.device.LocalTimezone;
import com.neucore.neulink.util.AppUtils;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.SecuretSign;

import java.util.Locale;

import cn.hutool.core.util.ObjectUtil;

public interface IDeviceService {

    /**
     * 设备序列号，每台设备必须固定且唯一
     * @return
     */
    default String getExtSN() {
        /**
         * 默认实现
         * 每台设备固定不变【必须和设备出厂时的设备序列号一致，当不一致的时候设备将无法使用neucore云管理设备】
         * 这个主要时提供给中小企业不想建立云平台，想使用neucore云服务
         */
        if(ObjectUtil.isEmpty(getDeviceName())){
            return DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext());
        }
        else{
            return getDeviceName()+"@"+ getMacAddress();
        }
    }

    default String getMacAddress(){
        return DeviceUtils.getMacAddress().replace(":","").toUpperCase();
    }

    String getMqttServer();
    /**
     * 获取授权设备所属产品Id
     * @return
     */
    String getProductKey();

    /**
     * 设备id【设备授权id：椰壳Id】
     * @return
     */
    String getDeviceName();

    /**
     * 设备密钥
     * @return
     */
    String getDeviceSecret();

    /**
     * 获取操作系统名称
     * @return
     */
    default String getOsName(){
        //os.name
        return android.os.Build.MANUFACTURER+"@"+android.os.Build.PRODUCT;
    }

    /**
     * 操作系统版本
     * @return
     */
    default String getOsVersion() {
        //os.version
        return DeviceUtils.getSystemPropertiesCrop("ro.product.build.dim","V0.0.0");
    }

    /**
     * 固件名称
     * @return
     */
    default String getFirName(){
        //os.name
        return android.os.Build.MANUFACTURER+"@"+android.os.Build.PRODUCT;
    }

    /**
     * 固件版本
     * @return
     */
    default String getFirVersion() {
        //os.version
        return DeviceUtils.getSystemPropertiesCrop("ro.product.build.dim","V0.0.0");
    }

    /**
     * 获取主apk名称
     * @return
     */
    default String getApkName(){
        return AppUtils.getApkName(ContextHolder.getInstance().getContext());
    }

    /**
     * 获取主Apk版本
     * @return
     */
    default String getApkVersion(){
        return AppUtils.getVersionName(ContextHolder.getInstance().getContext());
    }

    /**
     * 是否是新版本
     * @return
     */
    default boolean newVersion(){
        return ObjectUtil.isNotEmpty(getProductKey()) &&ObjectUtil.isNotEmpty(getDeviceName()) && ObjectUtil.isNotEmpty(getDeviceSecret());
    }
    default String getSecuremode(){
        return "2";
    }

    default String getSignmethod(){
        return "hmacsha256";
    }
    default String getVersion(){
        return "paho-1.0.0";
    }
    /**
     * 获取签名
     * @return
     */
    default SecuretSign sign(){
        String timestamp = String.valueOf(System.currentTimeMillis());
        SecuretSign securetSign = new SecuretSign(getProductKey(),getDeviceName(),getDeviceSecret(), DeviceUtils.getMacAddress(),"salt",timestamp,"device",getSecuremode(),getSignmethod(),getVersion());
        return securetSign;
    }
    /**
     *
     * @return
     * @Deprecated
     */
    @Deprecated
    default String getDeviceId(){
        return getDeviceName();
    }

    /**
     *
     * @return
     */
    @Deprecated
    default String getDevId(){
        return getDeviceName();
    }

    DeviceInfo getInfo();

    /**
     * enable.heartbeat;默认关闭：即不上报
     * @return
     */
    HeatbeatInfo heatbeat();
    /**
     * enable.runtime；默认关闭：即不上报
     * @return
     */
    RuntimeInfo runtime();

    Locale getLocale();

    LocalTimezone getTimezone();

    boolean regist(DeviceInfo deviceInfo);

    void connect();

    void disconnect();

    LWTTopic lwtTopic();

    LWTPayload lwtPayload();

    /**
     * 订阅的topic
     * @return
     */
    default String[] subscribeTopics(){
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
         * 终端配置管理       rrpc/req/${dev_id}/cfg/v1d2/${req_no}[/${md5}],qos=0
         * 查看终端配置       rrpc/req/${dev_id}/qcfg/v1d2/${req_no}[/${md5}],qos=0
         * 预约信息展示       rrpc/req/${dev_id}/reserve/v1.0/${req_no}[/${md5}], qos=0
         */
        String rmsg_topic = "rmsg/req/" + getExtSN() + "/#";
        String rrpc_topic = "rrpc/req/" + getExtSN() + "/#";
        String productKey = getProductKey();
        if(ObjectUtil.isNotEmpty(productKey)){
            rmsg_topic = String.format("%s/%s",productKey,rmsg_topic);
            rrpc_topic = String.format("%s/%s",productKey,rrpc_topic);
        }
        boolean bcstEnable = ConfigContext.getInstance().getConfig(ConfigContext.BCST_ENABLE,false);
        if(bcstEnable){
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
             * 终端配置管理          bcst/req/${scopeId}/cfg/v1d2/${req_no}[/${md5}],qos=0
             * 查看终端配置          bcst/req/${scopeId}/qcfg/v1d2/${req_no}[/${md5}],qos=0
             * 预约信息展示          bcst/req/${scopeId}/reserve/v1.0/${req_no}[/${md5}], qos=0
             */
            String custId = NeulinkService.getInstance().getCustId();
            String bcst_topic = "bcst/req/" + custId + "/#";
            if(ObjectUtil.isNotEmpty(productKey)){
                bcst_topic = String.format("%s/%s",productKey,bcst_topic);
            }
            return new String[]{rmsg_topic,rrpc_topic,bcst_topic};
        }
        return new String[]{rmsg_topic,rrpc_topic};
    }

    /**
     * 订阅topic对应的qos
     * 按照 订阅topic顺序指定，默认为0
     * @return
     */
    default int[] subscribeQoss(){
        int qos = ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,0);
        String[] topics = subscribeTopics();
        int[] qoss = new int[topics.length];
        for(int i=0;i<topics.length;i++){
            qoss[i]=qos;
        }
        return qoss;
    }
}
