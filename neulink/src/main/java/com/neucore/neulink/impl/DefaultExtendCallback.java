package com.neucore.neulink.impl;

import com.neucore.neulink.IExtendCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.listener.DefaultAlogCmdListener;
import com.neucore.neulink.impl.listener.DefaultAwakenCmdListener;
import com.neucore.neulink.impl.listener.DefaultBackupCmdListener;
import com.neucore.neulink.impl.listener.DefaultCfgCmdListener;
import com.neucore.neulink.impl.listener.DefaultDebugCmdListener;
import com.neucore.neulink.impl.listener.DefaultFaceCheckListener;
import com.neucore.neulink.impl.listener.DefaultFaceQueryListener;
import com.neucore.neulink.impl.listener.DefaultFaceSyncListener;
import com.neucore.neulink.impl.listener.DefaultFirewareCmdListener;
import com.neucore.neulink.impl.listener.DefaultFirewareResumeCmdListener;
import com.neucore.neulink.impl.listener.DefaultHibrateCmdListener;
import com.neucore.neulink.impl.listener.DefaultRebootCmdListener;
import com.neucore.neulink.impl.listener.DefaultRecoverCmdListener;
import com.neucore.neulink.impl.listener.DefaultResetCmdListener;
import com.neucore.neulink.impl.listener.DefaultShellCmdListener;
import com.neucore.neulink.impl.registry.ListenerRegistry;

public class DefaultExtendCallback implements IExtendCallback {
    @Override
    public void onCallBack() {
        /**
         * SDK默认实现扩展
         */
        //######################################################################################
        /**
         * SDK默认实现扩展
         */
        //######################################################################################
        /**
         * 配置下发 扩展【取消注释，覆盖默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_CFG,new DefaultCfgCmdListener());
        /**
         * 人脸下发 扩展【取消注释，覆盖默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_BLIB,new DefaultFaceSyncListener());
        /**
         * 人脸比对 扩展【取消注释，覆盖默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_CHECK,new DefaultFaceCheckListener());
        /**
         * 人脸查询 扩展【取消注释，覆盖默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_QLIB,new DefaultFaceQueryListener());

        /**
         * 重启 扩展【取消注释，覆盖默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_REBOOT,new DefaultRebootCmdListener());

        /**
         * Shell 扩展【取消注释，覆盖默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_SHELL,new DefaultShellCmdListener());

        /**
         * 唤醒 扩展【取消注释，覆盖默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_AWAKEN,new DefaultAwakenCmdListener());
        /**
         * 休眠 扩展【取消注释，覆盖默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_HIBRATE,new DefaultHibrateCmdListener());
        /**
         * 算法升级 扩展【取消注释，覆盖默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_ALOG,new DefaultAlogCmdListener());

        /**
         * 固件$APK 升级扩展【取消注释，覆盖默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_FIRMWARE,new DefaultFirewareCmdListener());

        /**
         * 固件$APK 断点续传升级扩展【取消注释，覆盖默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_FIRMWARE_RESUME,new DefaultFirewareResumeCmdListener());

        /**
         * 备份 扩展【取消注释，覆盖默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_BACKUP,new DefaultBackupCmdListener());

        /**
         * 恢复 扩展【取消注释，覆盖默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_RECOVER,new DefaultRecoverCmdListener());

        /**
         * 重置 扩展【取消注释，覆盖默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_RESET,new DefaultResetCmdListener());

        /**
         * Debug 扩展【取消注释，覆盖默认实现】
         */
        ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_DEBUG,new DefaultDebugCmdListener());
    }
}
