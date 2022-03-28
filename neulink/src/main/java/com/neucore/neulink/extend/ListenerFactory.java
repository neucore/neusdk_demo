package com.neucore.neulink.extend;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.cmd.bak.BackupCmd;
import com.neucore.neulink.cmd.cfg.CfgCmd;
import com.neucore.neulink.cmd.check.CheckCmd;
import com.neucore.neulink.cmd.recv.RecoverCmd;
import com.neucore.neulink.cmd.rmsg.AwakenCmd;
import com.neucore.neulink.cmd.rmsg.app.AlogUpgrCmd;
import com.neucore.neulink.cmd.rrpc.FaceCmd;
import com.neucore.neulink.cmd.rrpc.TLibQueryCmd;
import com.neucore.neulink.impl.Cmd;
import com.neucore.neulink.impl.ListenerRegistrator;

import java.util.Map;

/**
 * 
 * 请使用ListenerRegistrator实现
 */
public class ListenerFactory implements NeulinkConst{

    private ListenerRegistrator listenerRegistrator = ListenerRegistrator.getInstance();
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
        listenerRegistrator.setExtendListener(biz.toLowerCase(),resetListener);
    }
}
