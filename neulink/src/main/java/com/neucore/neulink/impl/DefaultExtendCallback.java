package com.neucore.neulink.impl;

import com.neucore.neulink.IExtendCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.listener.DefaultAlogCmdListener;
import com.neucore.neulink.impl.listener.DefaultAwakenCmdListener;
import com.neucore.neulink.impl.listener.DefaultBackupCmdListener;
import com.neucore.neulink.impl.listener.DefaultCarCheckListener;
import com.neucore.neulink.impl.listener.DefaultCarQueryListener;
import com.neucore.neulink.impl.listener.DefaultCarSyncListener;
import com.neucore.neulink.impl.listener.DefaultCfgCmdListener;
import com.neucore.neulink.impl.listener.DefaultDebugCmdListener;
import com.neucore.neulink.impl.listener.DefaultFaceCheckListener;
import com.neucore.neulink.impl.listener.DefaultFaceQueryListener;
import com.neucore.neulink.impl.listener.DefaultFaceSyncListener;
import com.neucore.neulink.impl.listener.DefaultFirewareCmdListener;
import com.neucore.neulink.impl.listener.DefaultFirewareResumeCmdListener;
import com.neucore.neulink.impl.listener.DefaultHibrateCmdListener;
import com.neucore.neulink.impl.listener.DefaultLicCheckListener;
import com.neucore.neulink.impl.listener.DefaultLicQueryListener;
import com.neucore.neulink.impl.listener.DefaultLicSyncListener;
import com.neucore.neulink.impl.listener.DefaultQCfgCmdListener;
import com.neucore.neulink.impl.listener.DefaultQLogCmdListener;
import com.neucore.neulink.impl.listener.DefaultRebootCmdListener;
import com.neucore.neulink.impl.listener.DefaultRecoverCmdListener;
import com.neucore.neulink.impl.listener.DefaultResetCmdListener;
import com.neucore.neulink.impl.listener.DefaultShellCmdListener;
import com.neucore.neulink.impl.proc.DefaultCarCheckProcessor;
import com.neucore.neulink.impl.proc.DefaultCarQueryProcessor;
import com.neucore.neulink.impl.proc.DefaultCarSyncProcessor;
import com.neucore.neulink.impl.proc.DefaultFaceCheckProcessor;
import com.neucore.neulink.impl.proc.DefaultFaceQueryProcessor;
import com.neucore.neulink.impl.proc.DefaultFaceSyncProcessor;
import com.neucore.neulink.impl.proc.DefaultLicCheckProcessor;
import com.neucore.neulink.impl.proc.DefaultLicQueryProcessor;
import com.neucore.neulink.impl.proc.DefaultLicSyncProcessor;
import com.neucore.neulink.impl.proc.DefaultQLogProcessor;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.impl.registry.ProcessRegistry;

public final class DefaultExtendCallback implements IExtendCallback {
    @Override
    public void onCallBack() {
        /**
         * SDK默认实现扩展
         */
        //######################################################################################

        /**
         * 重启 扩展【默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_REBOOT,new DefaultRebootCmdListener());

        /**
         * Shell 扩展【默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_SHELL,new DefaultShellCmdListener());

        /**
         * 唤醒 扩展【默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_AWAKEN,new DefaultAwakenCmdListener());
        /**
         * 休眠 扩展【默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_HIBRATE,new DefaultHibrateCmdListener());
        /**
         * 算法升级 扩展【默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_ALOG,new DefaultAlogCmdListener());

        /**
         * 固件$APK 升级扩展【默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_FIRMWARE,new DefaultFirewareCmdListener());

        /**
         * 固件$APK 断点续传升级扩展【默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_FIRMWARE_RESUME,new DefaultFirewareResumeCmdListener());

        /**
         * 备份 扩展【默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_BACKUP,new DefaultBackupCmdListener());

        /**
         * 恢复 扩展【默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_RECOVER,new DefaultRecoverCmdListener());

        /**
         * 重置 扩展【默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_RESET,new DefaultResetCmdListener());

        /**
         * Debug 扩展【默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_DEBUG,new DefaultDebugCmdListener());

        /**
         * 日志查询扩展【默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_QLOG,new DefaultQLogCmdListener());

        /**
         * 配置下发 扩展【默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_CFG,new DefaultCfgCmdListener());

        /**
         * 配置查询扩展【默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_QCFG,new DefaultQCfgCmdListener());

        /**
         * 目标库同步【默认实现】
         */
        ProcessRegistry.registBlibProcessor(new DefaultBLibSyncProcessor());
        /**
         * 人脸下发 扩展【默认实现】
         */
        ProcessRegistry.registBlib$ObjtypeProcessor(NeulinkConst.NEULINK_BIZ_OBJTYPE_FACE,new DefaultFaceSyncProcessor(),new DefaultFaceSyncListener());
        /**
         * 车辆下发 扩展【默认实现】
         */
        ProcessRegistry.registBlib$ObjtypeProcessor(NeulinkConst.NEULINK_BIZ_OBJTYPE_CAR,new DefaultCarSyncProcessor(),new DefaultCarSyncListener());
        /**
         * 车辆下发 扩展【默认实现】
         */
        ProcessRegistry.registBlib$ObjtypeProcessor(NeulinkConst.NEULINK_BIZ_OBJTYPE_LIC,new DefaultLicSyncProcessor(),new DefaultLicSyncListener());

        /**
         * 目标库查询【默认实现】
         */
        ProcessRegistry.registQlibProcessor(new DefaultQLibProcessor());
        /**
         * 人脸查询 扩展【默认实现】
         */
        ProcessRegistry.registQlib$ObjtypeProcessor(NeulinkConst.NEULINK_BIZ_OBJTYPE_FACE,new DefaultFaceQueryProcessor(),new DefaultFaceQueryListener());
        /**
         * 车辆查询 扩展【默认实现】
         */
        ProcessRegistry.registQlib$ObjtypeProcessor(NeulinkConst.NEULINK_BIZ_OBJTYPE_CAR,new DefaultCarQueryProcessor(),new DefaultCarQueryListener());
        /**
         * 车辆查询 扩展【默认实现】
         */
        ProcessRegistry.registQlib$ObjtypeProcessor(NeulinkConst.NEULINK_BIZ_OBJTYPE_LIC,new DefaultLicQueryProcessor(),new DefaultLicQueryListener());

        /**
         * 目标库比对【默认实现】
         */
        ProcessRegistry.registClibProcessor(new DefaultCLibProcessor());
        /**
         * 人脸比对 扩展【默认实现】
         */
        ProcessRegistry.registClib$ObjtypeProcessor(NeulinkConst.NEULINK_BIZ_OBJTYPE_FACE,new DefaultFaceCheckProcessor(),new DefaultFaceCheckListener());
        /**
         * 车辆比对 扩展【默认实现】
         */
        ProcessRegistry.registClib$ObjtypeProcessor(NeulinkConst.NEULINK_BIZ_OBJTYPE_CAR,new DefaultCarCheckProcessor(),new DefaultCarCheckListener());
        /**
         * 车辆比对 扩展【默认实现】
         */
        ProcessRegistry.registClib$ObjtypeProcessor(NeulinkConst.NEULINK_BIZ_OBJTYPE_LIC,new DefaultLicCheckProcessor(),new DefaultLicCheckListener());
    }
}
