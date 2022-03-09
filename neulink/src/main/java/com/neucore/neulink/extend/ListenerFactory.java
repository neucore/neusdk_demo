package com.neucore.neulink.extend;

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
import com.neucore.neulink.cmd.rrpc.BTLibSyncCmd;
import com.neucore.neulink.cmd.rrpc.FaceCmd;
import com.neucore.neulink.cmd.rrpc.TLibQueryCmd;
import com.neucore.neulink.impl.listener.BLibSyncCmdListener;
import com.neucore.neulink.rmsg.ReserveCmd;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ListenerFactory {
    private String TAG = NeulinkConst.TAG_PREFIX+"ListenerFactory";

    private ICmdListener<Result,AlogUpgrCmd> alogListener = new ICmdListener<Result, AlogUpgrCmd>() {
        @Override
        public Result doAction(NeulinkEvent<AlogUpgrCmd> event) {
            Log.i(TAG,"Algorithm upgrade need to by replace ");
            return new Result();
        }
    };
    private ICmdListener<Result,UpgrCmd> fireware$ApkListener = new ICmdListener<Result, UpgrCmd>() {
        @Override
        public Result doAction(NeulinkEvent<UpgrCmd> event) {
            Log.i(TAG,"Application upgrade need to by replace ");
            return new Result();
        }
    };
    private ICmdListener<Result,AwakenCmd> awakenListener = new ICmdListener<Result, AwakenCmd>() {
        @Override
        public Result doAction(NeulinkEvent<AwakenCmd> event) {
            Log.i(TAG,"device awaken implements need to by replace ");
            return new Result();
        }
    };
    private ICmdListener<Result,HibrateCmd> hibrateListener = new ICmdListener<Result, HibrateCmd>() {
        @Override
        public Result doAction(NeulinkEvent<HibrateCmd> event) {
            Log.i(TAG,"device hibrate implements need to by replace ");
            return new Result();
        }
    };
    private ICmdListener<Result,ReserveCmd> reserveListener = new ICmdListener<Result, ReserveCmd>() {
        @Override
        public Result doAction(NeulinkEvent<ReserveCmd> event) {
            Log.i(TAG,"device reserve implements need to by replace ");
            return new Result();
        }
    };

    private ICmdListener<Result,CfgCmd> cfgListener = new ICmdListener<Result,CfgCmd>() {
        public Result doAction(NeulinkEvent<CfgCmd> event) {
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
            return new Result();
        }
    };
    private ICmdListener<UpdateResult, BTLibSyncCmd> internalFaceListener = new BLibSyncCmdListener() {
        @Override
        public UpdateResult doAction(NeulinkEvent<BTLibSyncCmd> event) {
            Log.i(TAG,"face process implements need to by replace ");
            return new UpdateResult();
        }
    };
    private ICmdListener<UpdateResult<Map<String,Object>>, FaceCmd> faceListener = new ICmdListener<UpdateResult<Map<String,Object>>, FaceCmd>() {
        @Override
        public UpdateResult<Map<String,Object>> doAction(NeulinkEvent<FaceCmd> event) {
            Log.i(TAG,"face process implements need to by replace ");
            return new UpdateResult<Map<String,Object>>();
        }
    };

    private ICmdListener<QueryResult,TLibQueryCmd> faceQueryListener = new ICmdListener<QueryResult, TLibQueryCmd>() {
        @Override
        public QueryResult doAction(NeulinkEvent<TLibQueryCmd> event) {
            Log.i(TAG,"face process implements need to by replace ");
            return new QueryResult();
        }
    };

    private ICmdListener<QueryResult,CheckCmd> faceCheckListener = new ICmdListener<QueryResult, CheckCmd>() {
        @Override
        public QueryResult doAction(NeulinkEvent<CheckCmd> event) {
            Log.i(TAG,"face process implements need to by replace ");
            return new QueryResult();
        }
    };

    private ICmdListener<QueryResult,BackupCmd> backupListener = new ICmdListener<QueryResult, BackupCmd>() {
        @Override
        public QueryResult<Map<String,String>> doAction(NeulinkEvent<BackupCmd> event) {
            Log.i(TAG,"face process implements need to by replace ");
            return new QueryResult<Map<String, String>>();
        }
    };

    private ICmdListener<QueryResult,RecoverCmd> recoverListener = new ICmdListener<QueryResult, RecoverCmd>() {
        @Override
        public QueryResult doAction(NeulinkEvent<RecoverCmd> event) {
            Log.i(TAG,"face process implements need to by replace ");
            return new QueryResult();
        }
    };

    private static ListenerFactory instance = new ListenerFactory();

    public static ListenerFactory getInstance(){
        return instance;
    }

    public ICmdListener<Result,AlogUpgrCmd> getAlogListener(){
        return alogListener;
    }

    public ICmdListener<Result,AwakenCmd> getAwakenListener(){
        return awakenListener;
    }

    public ICmdListener<Result,CfgCmd> getCfgListener() {
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

    public ICmdListener<UpdateResult<Map<String,Object>>, FaceCmd> getFaceListener() {
        return faceListener;
    }

    public void setFaceListener(ICmdListener faceListener) {
        this.faceListener = faceListener;
    }

    public ICmdListener<QueryResult,TLibQueryCmd> getFaceQueryListener() {
        return faceQueryListener;
    }

    public void setFaceQueryListener(ICmdListener faceQueryListener) {
        this.faceQueryListener = faceQueryListener;
    }

    public ICmdListener<QueryResult,CheckCmd> getFaceCheckListener() {
        return faceCheckListener;
    }

    public void setFaceCheckListener(ICmdListener faceCheckListener) {
        this.faceCheckListener = faceCheckListener;
    }

    public ICmdListener<QueryResult,BackupCmd> getBackupListener() {
        return backupListener;
    }

    public void setBackupListener(ICmdListener backupListener) {
        this.backupListener = backupListener;
    }

    public ICmdListener<QueryResult,RecoverCmd> getRecoverListener() {
        return recoverListener;
    }

    public void setRecoverListener(ICmdListener recoverListener) {
        this.recoverListener = recoverListener;
    }

    private Map<String,ICmdListener> listenerMap = new HashMap<>();
    public ICmdListener getExtendListener(String cmd){
        return listenerMap.get(cmd.toLowerCase());
    }

    public void setExtendListener(String cmd, ICmdListener listener){
        listenerMap.put(cmd.toLowerCase(),listener);
    }

}
