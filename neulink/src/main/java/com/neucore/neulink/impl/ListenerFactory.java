package com.neucore.neulink.impl;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.registry.ListenerRegistry;

/**
 * 
 * 请使用ListenerRegistry实现
 * @deprecated
 */
public class ListenerFactory implements NeulinkConst{

    private ListenerRegistry listenerRegistry = ListenerRegistry.getInstance();
    private static ListenerFactory instance = new ListenerFactory();

    /**
     * @deprecated
     * @return
     */
    public static ListenerFactory getInstance(){
        return instance;
    }

    /**
     * @deprecated
     * @param alogListener
     */
    public void setAlogListener(ICmdListener alogListener) {
        setListener("alog",alogListener);
    }

    /**
     * @deprecated
     * @param awakenListener
     */
    public void setAwakenListener(ICmdListener awakenListener) {
        setListener("awaken",awakenListener);
    }

    /**
     *
     * @deprecated
     * @param cfgListener
     */
    public void setCfgListener(ICmdListener cfgListener) {
        setListener("cfg",cfgListener);
    }

    /**
     * @deprecated
     * @param fireware$ApkListener
     */
    public void setFireware$ApkListener(ICmdListener fireware$ApkListener) {
        setListener("firmware",fireware$ApkListener);
    }

    /**
     * @deprecated
     * @param hibrateListener
     */
    public void setHibrateListener(ICmdListener hibrateListener) {
        setListener("hibrate",hibrateListener);
    }
    /**
     * @deprecated
     * @param faceListener
     */
    public void setFaceListener(ICmdListener faceListener) {
        setListener("blib",faceListener);
    }

    /**
     * @deprecated
     * @param faceQueryListener
     */
    public void setFaceQueryListener(ICmdListener faceQueryListener) {
        setListener("qlib",faceQueryListener);
    }

    /**
     * @deprecated
     * @param faceCheckListener
     * 
     */
    public void setFaceCheckListener(ICmdListener faceCheckListener) {
        setListener("check",faceCheckListener);
    }

    /**
     * @deprecated
     * @param backupListener
     */
    public void setBackupListener(ICmdListener backupListener) {
        setListener("backup",backupListener);
    }

    /**
     * @deprecated
     * @param recoverListener
     */
    public void setRecoverListener(ICmdListener recoverListener) {
        setListener("recover",recoverListener);
    }

    /**
     * @deprecated
     * @param resetListener
     */
    public void setResetListener(ICmdListener resetListener) {
        setListener("reset",resetListener);
    }

    public void setListener(String biz,ICmdListener resetListener) {
        listenerRegistry.setExtendListener(biz.toLowerCase(),resetListener);
    }
}
