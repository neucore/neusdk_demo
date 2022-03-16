package com.neucore.neulink.impl;

import android.util.Log;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.cmd.bak.BackupCmd;
import com.neucore.neulink.cmd.cfg.CfgCmd;
import com.neucore.neulink.cmd.cfg.CfgItem;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.cmd.check.CheckCmd;
import com.neucore.neulink.cmd.recv.RecoverCmd;
import com.neucore.neulink.cmd.rmsg.AwakenCmd;
import com.neucore.neulink.cmd.rmsg.HibrateCmd;
import com.neucore.neulink.cmd.rmsg.UpgrCmd;
import com.neucore.neulink.cmd.rmsg.app.AlogUpgrCmd;
import com.neucore.neulink.cmd.rrpc.BTLibSyncCmd;
import com.neucore.neulink.cmd.rrpc.FaceCmd;
import com.neucore.neulink.cmd.rrpc.TLibQueryCmd;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.QueryActionResult;
import com.neucore.neulink.extend.UpdateActionResult;
import com.neucore.neulink.impl.listener.BLibSyncCmdListener;
import com.neucore.neulink.rmsg.ReserveCmd;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ListenerRegistrator implements NeulinkConst {

    private static ListenerRegistrator instance = new ListenerRegistrator();

    public static ListenerRegistrator getInstance(){
        return instance;
    }

    private String TAG = TAG_PREFIX+"ListenerRegistrator";

    private ICmdListener<ActionResult, AlogUpgrCmd> alogListener = new ICmdListener<ActionResult, AlogUpgrCmd>() {
        @Override
        public ActionResult doAction(NeulinkEvent<AlogUpgrCmd> event) {
            Log.i(TAG,"Algorithm upgrade need to by replace ");
            return null;
        }
    };
    private ICmdListener<ActionResult, UpgrCmd> fireware$ApkListener = new ICmdListener<ActionResult, UpgrCmd>() {
        @Override
        public ActionResult doAction(NeulinkEvent<UpgrCmd> event) {
            Log.i(TAG,"Application upgrade need to by replace ");
            return new ActionResult();
        }
    };
    private ICmdListener<ActionResult, AwakenCmd> awakenListener = new ICmdListener<ActionResult, AwakenCmd>() {
        @Override
        public ActionResult doAction(NeulinkEvent<AwakenCmd> event) {
            Log.i(TAG,"device awaken implements need to by replace ");
            return new ActionResult();
        }
    };
    private ICmdListener<ActionResult, HibrateCmd> hibrateListener = new ICmdListener<ActionResult, HibrateCmd>() {
        @Override
        public ActionResult doAction(NeulinkEvent<HibrateCmd> event) {
            Log.i(TAG,"device hibrate implements need to by replace ");
            return new ActionResult();
        }
    };
    private ICmdListener<ActionResult, ReserveCmd> reserveListener = new ICmdListener<ActionResult, ReserveCmd>() {
        @Override
        public ActionResult doAction(NeulinkEvent<ReserveCmd> event) {
            Log.i(TAG,"device reserve implements need to by replace ");
            return new ActionResult();
        }
    };

    private ICmdListener<ActionResult, CfgCmd> cfgListener = new ICmdListener<ActionResult,CfgCmd>() {
        public ActionResult doAction(NeulinkEvent<CfgCmd> event) {
            CfgCmd cmd = event.getSource();
            CfgItem[] items = cmd.getData();
            int size = items==null?0:items.length;

            if ("add".equalsIgnoreCase(cmd.getCmdStr())) {
                ConfigContext.getInstance().add(items);
            }
            else if("update".equalsIgnoreCase(cmd.getCmdStr())){
                ConfigContext.getInstance().update(items);
            }
            else if("del".equalsIgnoreCase(cmd.getCmdStr())){
                ConfigContext.getInstance().delete(items);
            }
            else if("sync".equalsIgnoreCase(cmd.getCmdStr())){
                ConfigContext.getInstance().sync(items);
            }
            return new ActionResult();
        }
    };
    private ICmdListener<UpdateActionResult, BTLibSyncCmd> internalFaceListener = new BLibSyncCmdListener() {
        @Override
        public UpdateActionResult doAction(NeulinkEvent<BTLibSyncCmd> event) {
            Log.i(TAG,"face process implements need to by replace ");
            return new UpdateActionResult();
        }
    };
    private ICmdListener<UpdateActionResult<Map<String,Object>>, FaceCmd> faceListener = new ICmdListener<UpdateActionResult<Map<String,Object>>, FaceCmd>() {
        @Override
        public UpdateActionResult<Map<String,Object>> doAction(NeulinkEvent<FaceCmd> event) {
            Log.i(TAG,"face process implements need to by replace ");
            return new UpdateActionResult<Map<String,Object>>();
        }
    };

    private ICmdListener<QueryActionResult, TLibQueryCmd> faceQueryListener = new ICmdListener<QueryActionResult, TLibQueryCmd>() {
        @Override
        public QueryActionResult doAction(NeulinkEvent<TLibQueryCmd> event) {
            Log.i(TAG,"face process implements need to by replace ");
            return new QueryActionResult();
        }
    };

    private ICmdListener<QueryActionResult, CheckCmd> faceCheckListener = new ICmdListener<QueryActionResult, CheckCmd>() {
        @Override
        public QueryActionResult doAction(NeulinkEvent<CheckCmd> event) {
            Log.i(TAG,"face process implements need to by replace ");
            return new QueryActionResult();
        }
    };

    private ICmdListener<QueryActionResult, BackupCmd> backupListener = new ICmdListener<QueryActionResult, BackupCmd>() {
        @Override
        public QueryActionResult<Map<String,String>> doAction(NeulinkEvent<BackupCmd> event) {
            Log.i(TAG,"face process implements need to by replace ");
            return new QueryActionResult<Map<String, String>>();
        }
    };

    private ICmdListener<QueryActionResult, RecoverCmd> recoverListener = new ICmdListener<QueryActionResult, RecoverCmd>() {
        @Override
        public QueryActionResult doAction(NeulinkEvent<RecoverCmd> event) {
            Log.i(TAG,"face process implements need to by replace ");
            return new QueryActionResult();
        }
    };

    public ICmdListener<ActionResult,AlogUpgrCmd> getAlogListener(){
        return alogListener;
    }

    public ICmdListener<ActionResult,AwakenCmd> getAwakenListener(){
        return awakenListener;
    }

    public ICmdListener<ActionResult,CfgCmd> getCfgListener() {
        return cfgListener;
    }

    public void setCfgListener(ICmdListener cfgListener) {
        this.cfgListener = cfgListener;
    }

    public void setAlogListener(String auth, ICmdListener alogListener) {
        this.alogListener = alogListener;
    }

    public ICmdListener getFireware$ApkListener() {
        return fireware$ApkListener;
    }

    public void setFireware$ApkListener(ICmdListener fireware$ApkListener) {
        this.fireware$ApkListener = fireware$ApkListener;
    }

    public void setAwakenListener(ICmdListener awakenListener) {
        this.awakenListener = awakenListener;
    }

    public ICmdListener getHibrateListener(){
        return hibrateListener;
    }

    public void setHibrateListener(ICmdListener hibrateListener) {
        this.hibrateListener = hibrateListener;
    }

    public ICmdListener getReserveListener(){
        return reserveListener;
    }

    public void setReserveListener(ICmdListener reserveListener) {
        this.reserveListener = reserveListener;
    }

    public ICmdListener<UpdateActionResult<Map<String,Object>>, FaceCmd> getFaceListener() {
        return faceListener;
    }

    public void setFaceListener(ICmdListener faceListener) {
        this.faceListener = faceListener;
    }

    public ICmdListener<QueryActionResult,TLibQueryCmd> getFaceQueryListener() {
        return faceQueryListener;
    }

    public void setFaceQueryListener(ICmdListener faceQueryListener) {
        this.faceQueryListener = faceQueryListener;
    }

    public ICmdListener<QueryActionResult,CheckCmd> getFaceCheckListener() {
        return faceCheckListener;
    }

    public void setFaceCheckListener(ICmdListener faceCheckListener) {
        this.faceCheckListener = faceCheckListener;
    }

    public ICmdListener<QueryActionResult,BackupCmd> getBackupListener() {
        return backupListener;
    }

    public void setBackupListener(ICmdListener backupListener) {
        this.backupListener = backupListener;
    }

    public ICmdListener<QueryActionResult,RecoverCmd> getRecoverListener() {
        return recoverListener;
    }

    public void setRecoverListener(ICmdListener recoverListener) {
        this.recoverListener = recoverListener;
    }

    private Map<String,ICmdListener> listenerMap = new ConcurrentHashMap<>();
    public ICmdListener getExtendListener(String cmd){
        return listenerMap.get(cmd.toLowerCase());
    }

    public void setExtendListener(String cmd, ICmdListener listener){
        listenerMap.put(cmd.toLowerCase(),listener);
    }
}
