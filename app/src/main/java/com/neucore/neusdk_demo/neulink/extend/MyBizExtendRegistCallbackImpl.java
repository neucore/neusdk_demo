package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.IExtendCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.DefaultBLibSyncProcessor;
import com.neucore.neulink.impl.DefaultCLibProcessor;
import com.neucore.neulink.impl.DefaultQLibProcessor;
import com.neucore.neulink.impl.listener.DefaultCarCheckListener;
import com.neucore.neulink.impl.listener.DefaultCarQueryListener;
import com.neucore.neulink.impl.listener.DefaultCarSyncListener;
import com.neucore.neulink.impl.listener.DefaultFaceCheckListener;
import com.neucore.neulink.impl.listener.DefaultFaceQueryListener;
import com.neucore.neulink.impl.listener.DefaultFaceSyncListener;
import com.neucore.neulink.impl.listener.DefaultLicCheckListener;
import com.neucore.neulink.impl.listener.DefaultLicQueryListener;
import com.neucore.neulink.impl.listener.DefaultLicSyncListener;
import com.neucore.neulink.impl.proc.DefaultCarCheckProcessor;
import com.neucore.neulink.impl.proc.DefaultCarQueryProcessor;
import com.neucore.neulink.impl.proc.DefaultCarSyncProcessor;
import com.neucore.neulink.impl.proc.DefaultFaceCheckProcessor;
import com.neucore.neulink.impl.proc.DefaultFaceQueryProcessor;
import com.neucore.neulink.impl.proc.DefaultFaceSyncProcessor;
import com.neucore.neulink.impl.proc.DefaultLicCheckProcessor;
import com.neucore.neulink.impl.proc.DefaultLicQueryProcessor;
import com.neucore.neulink.impl.proc.DefaultLicSyncProcessor;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.impl.registry.ProcessRegistry;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neusdk_demo.neulink.extend.auth.AuthProcessor;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.AuthCmdListener;
import com.neucore.neusdk_demo.neulink.extend.bind.BindProcessor;
import com.neucore.neusdk_demo.neulink.extend.bind.listener.BindCmdListener;
import com.neucore.neusdk_demo.neulink.extend.hello.HelloProcessor;
import com.neucore.neusdk_demo.neulink.extend.hello.listener.HelloCmdListener;
import com.neucore.neusdk_demo.neulink.extend.hello.response.HellResCallback;

/**
 * 扩展业务注册回调实现
 */
public class MyBizExtendRegistCallbackImpl implements IExtendCallback {
    private String TAG = "MyBizExtendRegistCallbackImpl";
    @Override
    public void onCallBack() {
        NeuLogUtils.iTag(TAG,"onCallBack...");
        /**
         * SDK默认实现扩展
         */
        //######################################################################################
        /**
         * 配置下发 扩展【取消注释，覆盖默认实现】
         */
        //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_CFG,new SampleCfgActionListener());
        /**
         * 目标库同步【默认实现】
         */
//        ProcessRegistry.registBlibProcessor(new DefaultBLibSyncProcessor());
        /**
         * 人脸下发 扩展【默认实现】
         */
//        ProcessRegistry.registBlib$ObjtypeProcessor(NeulinkConst.NEULINK_BIZ_OBJTYPE_FACE,new DefaultFaceSyncProcessor(),new DefaultFaceSyncListener());
        /**
         * 或者
         */
        ListenerRegistry.getInstance().setBlibExtendListener(NeulinkConst.NEULINK_BIZ_OBJTYPE_FACE,new DefaultFaceSyncListener());

        /**
         * 车辆下发 扩展【默认实现】
         */
//        ProcessRegistry.registBlib$ObjtypeProcessor(NeulinkConst.NEULINK_BIZ_OBJTYPE_CAR,new DefaultCarSyncProcessor(),new DefaultCarSyncListener());
        /**
         * 或者
         */
//        ListenerRegistry.getInstance().setBlibExtendListener(NeulinkConst.NEULINK_BIZ_OBJTYPE_CAR,new DefaultCarSyncListener());

        /**
         * 车辆下发 扩展【默认实现】
         */
//        ProcessRegistry.registBlib$ObjtypeProcessor(NeulinkConst.NEULINK_BIZ_OBJTYPE_LIC,new DefaultLicSyncProcessor(),new DefaultLicSyncListener());
        /**
         * 或者
         */
//        ListenerRegistry.getInstance().setBlibExtendListener(NeulinkConst.NEULINK_BIZ_OBJTYPE_LIC,new DefaultLicSyncListener());

        /**
         * 目标库查询【默认实现】
         */
//        ProcessRegistry.registQlibProcessor(new DefaultQLibProcessor());
        /**
         * 人脸查询 扩展【默认实现】
         */
//        ProcessRegistry.registQlib$ObjtypeProcessor(NeulinkConst.NEULINK_BIZ_OBJTYPE_FACE,new DefaultFaceQueryProcessor(),new DefaultFaceQueryListener());
        /**
         * 或者
         */
//        ListenerRegistry.getInstance().setQlibExtendListener(NeulinkConst.NEULINK_BIZ_OBJTYPE_FACE,new DefaultFaceQueryListener());
        /**
         * 车辆查询 扩展【默认实现】
         */
//        ProcessRegistry.registQlib$ObjtypeProcessor(NeulinkConst.NEULINK_BIZ_OBJTYPE_CAR,new DefaultCarQueryProcessor(),new DefaultCarQueryListener());
        /**
         * 或者
         */
//        ListenerRegistry.getInstance().setQlibExtendListener(NeulinkConst.NEULINK_BIZ_OBJTYPE_CAR,new DefaultCarQueryListener());
        /**
         * 车辆查询 扩展【默认实现】
         */
//        ProcessRegistry.registQlib$ObjtypeProcessor(NeulinkConst.NEULINK_BIZ_OBJTYPE_LIC,new DefaultLicQueryProcessor(),new DefaultLicQueryListener());
        /**
         * 或者
         */
//        ListenerRegistry.getInstance().setQlibExtendListener(NeulinkConst.NEULINK_BIZ_OBJTYPE_LIC,new DefaultLicQueryListener());
        /**
         * 目标库比对【默认实现】
         */
//        ProcessRegistry.registClibProcessor(new DefaultCLibProcessor());
        /**
         * 人脸比对 扩展【默认实现】
         */
//        ProcessRegistry.registClib$ObjtypeProcessor(NeulinkConst.NEULINK_BIZ_OBJTYPE_FACE,new DefaultFaceCheckProcessor(),new DefaultFaceCheckListener());
        /**
         * 或者
         */
//        ListenerRegistry.getInstance().setClibExtendListener(NeulinkConst.NEULINK_BIZ_OBJTYPE_FACE,new DefaultFaceCheckListener());
        /**
         * 车辆比对 扩展【默认实现】
         */
//        ProcessRegistry.registClib$ObjtypeProcessor(NeulinkConst.NEULINK_BIZ_OBJTYPE_CAR,new DefaultCarCheckProcessor(),new DefaultCarCheckListener());
        /**
         * 或者
         */
//        ListenerRegistry.getInstance().setClibExtendListener(NeulinkConst.NEULINK_BIZ_OBJTYPE_CAR,new DefaultCarCheckListener());
        /**
         * 车辆比对 扩展【默认实现】
         */
//        ProcessRegistry.registClib$ObjtypeProcessor(NeulinkConst.NEULINK_BIZ_OBJTYPE_LIC,new DefaultLicCheckProcessor(),new DefaultLicCheckListener());
        /**
         * 或者
         */
//        ListenerRegistry.getInstance().setClibExtendListener(NeulinkConst.NEULINK_BIZ_OBJTYPE_LIC,new DefaultLicCheckListener());

        /**
         * 重启 扩展【取消注释，覆盖默认实现】
         */
        //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_REBOOT,new SampleRebootCmdListener());

        /**
         * Shell 扩展【取消注释，覆盖默认实现】
         */
        //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_SHELL,new SampleShellCmdListener());

        /**
         * 唤醒 扩展【取消注释，覆盖默认实现】
         */
        //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_AWAKEN,new SampleAwakenActionListener());
        /**
         * 休眠 扩展【取消注释，覆盖默认实现】
         */
        //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_HIBRATE,new SampleHibrateActionListener());
        /**
         * 算法升级 扩展【取消注释，覆盖默认实现】
         */
        //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_ALOG,new SampleAlogUpgrdActionListener());

        /**
         * 固件$APK 升级扩展【取消注释，覆盖默认实现】
         */
        //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_FIRMWARE,new SampleFirewareUpgrdActionListener());

        /**
         * 固件$APK 断点续传升级扩展【取消注释，覆盖默认实现】
         */
        //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_FIRMWARE_RESUME,new SampleFirewareResumeCmdListener());

        /**
         * 备份 扩展【取消注释，覆盖默认实现】
         */
        //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_BACKUP,new SampleBackupActionListener());

        /**
         * 恢复 扩展【取消注释，覆盖默认实现】
         */
        //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_RECOVER,new SampleRecoverActionListener());

        /**
         * 重置 扩展【取消注释，覆盖默认实现】
         */
        //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_RESET,new SampleResetActionListener());

        /**
         * Debug 扩展【取消注释，覆盖默认实现】
         */
        //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_DEBUG,new SampleDebugCmdListener());

        //######################################################################################
        /**
         * SDK 自定义业务扩展实现
         * 框架已经实现消息的接收及响应处理机制
         * 新业务可以参考Hello业务的实现业务就行
         */
        ProcessRegistry.regist(NeulinkConst.NEULINK_BIZ_AUTH,new AuthProcessor(),new AuthCmdListener());

        ProcessRegistry.regist(NeulinkConst.NEULINK_BIZ_BINDING,new BindProcessor(),new BindCmdListener());
        /**
         * doAction返回结果后框架会把处理结果返回给云端；同时把云端处理状态返回给HellResCallback
         */
        ProcessRegistry.regist("hello",new HelloProcessor(),new HelloCmdListener(),new HellResCallback());
        //######################################################################################
        /**
         * 上传结果给到云端
         * 这个业务一般用于端侧自动抓拍、日志自动上报
         * 端侧审核操作【同意、拒绝】结果给到云端
         * NeulinkPublisherFacde publisher = NeulinkService.getInstance().getPublisherFacde()
         * 具体参考Neulink 使用手册《上报消息到云端》部分
         */
    }
}
