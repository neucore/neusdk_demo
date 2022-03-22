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
     * 
     * @return
     */
    public static ListenerFactory getInstance(){
        return instance;
    }

    /**
     * 
     * @param alogListener
     */
    public void setAlogListener(ICmdListener alogListener) {
        listenerRegistrator.setExtendListener("alog",alogListener);
    }

    /**
     * 
     * @return
     */
    public ICmdListener<ActionResult, AlogUpgrCmd> getAlogListener(){
        return listenerRegistrator.getExtendListener("alog");
    }

    /**
     * 
     * @param awakenListener
     */
    public void setAwakenListener(ICmdListener awakenListener) {
        listenerRegistrator.setExtendListener("awaken",awakenListener);
    }

    /**
     * 
     * @return
     */
    public ICmdListener<ActionResult, AwakenCmd> getAwakenListener(){
        return listenerRegistrator.getExtendListener("awaken");
    }

    /**
     *
     * 
     * @param cfgListener
     */
    public void setCfgListener(ICmdListener cfgListener) {
        listenerRegistrator.setExtendListener("cfg",cfgListener);
    }

    /**
     * 
     * @return
     */
    public ICmdListener<ActionResult, CfgCmd> getCfgListener() {
        return listenerRegistrator.getExtendListener("cfg");
    }

    /**
     * 
     * @return
     */
    public ICmdListener getFireware$ApkListener() {
        return listenerRegistrator.getExtendListener("firmware");
    }

    /**
     * 
     * @param fireware$ApkListener
     */
    public void setFireware$ApkListener(ICmdListener fireware$ApkListener) {
        listenerRegistrator.setExtendListener("firmware",fireware$ApkListener);
    }

    /**
     * 
     * @return
     */
    public ICmdListener getHibrateListener(){
        return listenerRegistrator.getExtendListener("hibrate");
    }

    /**
     * 
     * @param hibrateListener
     */
    public void setHibrateListener(ICmdListener hibrateListener) {
        listenerRegistrator.setExtendListener("hibrate",hibrateListener);
    }
    /**
     * 
     * @param faceListener
     */
    public void setFaceListener(ICmdListener faceListener) {
        listenerRegistrator.setExtendListener("blib",faceListener);
    }

    /**
     * 
     * @return
     */
    public ICmdListener<UpdateActionResult<Map<String,Object>>, FaceCmd> getFaceListener() {
        return listenerRegistrator.getExtendListener("blib");
    }

    /**
     * 
     * @return
     */
    public ICmdListener<QueryActionResult, TLibQueryCmd> getFaceQueryListener() {
        return listenerRegistrator.getExtendListener("qlib");
    }

    /**
     *  
     * @param faceQueryListener
     */
    public void setFaceQueryListener(ICmdListener faceQueryListener) {
        listenerRegistrator.setExtendListener("qlib",faceQueryListener);
    }

    /**
     * 
     * @return
     */
    public ICmdListener<QueryActionResult, CheckCmd> getFaceCheckListener() {
        return listenerRegistrator.getExtendListener("check");
    }

    /**
     *
     * @param faceCheckListener
     * 
     */
    public void setFaceCheckListener(ICmdListener faceCheckListener) {
        listenerRegistrator.setExtendListener("check",faceCheckListener);
    }

    /**
     * 
     * @return
     */
    public ICmdListener<QueryActionResult, BackupCmd> getBackupListener() {
        return listenerRegistrator.getExtendListener("backup");
    }

    /**
     * 
     * @param backupListener
     */
    public void setBackupListener(ICmdListener backupListener) {
        listenerRegistrator.setExtendListener("backup",backupListener);
    }

    /**
     * 
     * @return
     */
    public ICmdListener<QueryActionResult, RecoverCmd> getRecoverListener() {
        return listenerRegistrator.getExtendListener("recover");
    }

    /**
     * 
     * @param recoverListener
     */
    public void setRecoverListener(ICmdListener recoverListener) {
        listenerRegistrator.setExtendListener("recover",recoverListener);
    }
}
