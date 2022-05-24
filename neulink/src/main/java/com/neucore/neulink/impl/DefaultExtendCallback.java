package com.neucore.neulink.impl;

import com.neucore.neulink.IExtendCallback;
import com.neucore.neulink.impl.listener.DefaultAlogCmdListener;
import com.neucore.neulink.impl.listener.DefaultAwakenCmdListener;
import com.neucore.neulink.impl.listener.DefaultBackupCmdListener;
import com.neucore.neulink.impl.listener.DefaultCfgCmdListener;
import com.neucore.neulink.impl.listener.DefaultFaceCheckListener;
import com.neucore.neulink.impl.listener.DefaultFaceQueryListener;
import com.neucore.neulink.impl.listener.DefaultFaceSyncListener;
import com.neucore.neulink.impl.listener.DefaultFirewareCmdListener;
import com.neucore.neulink.impl.listener.DefaultHibrateCmdListener;
import com.neucore.neulink.impl.listener.DefaultResetCmdListener;
import com.neucore.neulink.impl.registry.ListenerRegistry;

public class DefaultExtendCallback implements IExtendCallback {
    @Override
    public void onCallBack() {
        /**
         * SDK默认实现扩展
         */
        //######################################################################################
        /**
         * 配置扩展
         */
        ListenerRegistry.getInstance().setExtendListener("cfg",new DefaultCfgCmdListener());
        /**
         * 人脸下发 扩展
         */
        ListenerRegistry.getInstance().setExtendListener("blib",new DefaultFaceSyncListener());
        /**
         * 人脸比对 扩展
         */
        ListenerRegistry.getInstance().setExtendListener("check",new DefaultFaceCheckListener());
        /**
         * 人脸查询 扩展
         */
        ListenerRegistry.getInstance().setExtendListener("qlib",new DefaultFaceQueryListener());

        /**
         * 唤醒 扩展
         */
        ListenerRegistry.getInstance().setExtendListener("awaken",new DefaultAwakenCmdListener());
        /**
         * 休眠 扩展
         */
        ListenerRegistry.getInstance().setExtendListener("hibrate",new DefaultHibrateCmdListener());
        /**
         * 算法升级 扩展
         */
        ListenerRegistry.getInstance().setExtendListener("alog",new DefaultAlogCmdListener());

        /**
         * 固件$APK 升级扩展
         */
        ListenerRegistry.getInstance().setExtendListener("firmware",new DefaultFirewareCmdListener());

        /**
         * 备份实现
         */
        ListenerRegistry.getInstance().setExtendListener("backup",new DefaultBackupCmdListener());

        /**
         * 重置系统扩展
         */
        ListenerRegistry.getInstance().setExtendListener("reset",new DefaultResetCmdListener());
    }
}
