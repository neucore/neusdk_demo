package com.neucore.neulink.extend;

import android.util.Log;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.cmd.cfg.CfgCmd;
import com.neucore.neulink.cmd.cfg.CfgItem;
import com.neucore.neulink.cmd.cfg.ConfigContext;

import java.util.HashMap;
import java.util.Map;

public class ListenerFactory {
    private String TAG = NeulinkConst.TAG_PREFIX+"ListenerFactory";

    private ICmdListener<Result> alogListener = new ICmdListener<Result>() {
        @Override
        public Result doAction(NeulinkEvent event) {
            Log.i(TAG,"Algorithm upgrade need to by replace ");
            return new Result();
        }
    };
    private ICmdListener<Result> fireware$ApkListener = new ICmdListener<Result>() {
        @Override
        public Result doAction(NeulinkEvent event) {
            Log.i(TAG,"Application upgrade need to by replace ");
            return new Result();
        }
    };
    private ICmdListener<Result> awakenListener = new ICmdListener<Result>() {
        @Override
        public Result doAction(NeulinkEvent event) {
            Log.i(TAG,"device awaken implements need to by replace ");
            return new Result();
        }
    };
    private ICmdListener<Result> hibrateListener = new ICmdListener<Result>() {
        @Override
        public Result doAction(NeulinkEvent event) {
            Log.i(TAG,"device hibrate implements need to by replace ");
            return new Result();
        }
    };
    private ICmdListener<Result> reserveListener = new ICmdListener<Result>() {
        @Override
        public Result doAction(NeulinkEvent event) {
            Log.i(TAG,"device reserve implements need to by replace ");
            return new Result();
        }
    };

    private ICmdListener<Result> cfgListener = new ICmdListener<Result>() {
        public Result doAction(NeulinkEvent event) {
            CfgCmd cmd = (CfgCmd) event.getSource();
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

    private ICmdListener<UpdateResult> faceListener = new ICmdListener<UpdateResult>() {
        @Override
        public UpdateResult doAction(NeulinkEvent event) {
            Log.i(TAG,"face process implements need to by replace ");
            return new UpdateResult();
        }
    };

    private ICmdListener<QueryResult> faceQueryListener = new ICmdListener<QueryResult>() {
        @Override
        public QueryResult doAction(NeulinkEvent event) {
            Log.i(TAG,"face process implements need to by replace ");
            return new QueryResult();
        }
    };

    private ICmdListener<QueryResult> faceCheckListener = new ICmdListener<QueryResult>() {
        @Override
        public QueryResult doAction(NeulinkEvent event) {
            Log.i(TAG,"face process implements need to by replace ");
            return new QueryResult();
        }
    };

    private ICmdListener<QueryResult> backupListener = new ICmdListener<QueryResult>() {
        @Override
        public QueryResult<Map<String,String>> doAction(NeulinkEvent event) {
            Log.i(TAG,"face process implements need to by replace ");
            return new QueryResult<Map<String, String>>();
        }
    };

    private ICmdListener<QueryResult> recoverListener = new ICmdListener<QueryResult>() {
        @Override
        public QueryResult doAction(NeulinkEvent event) {
            Log.i(TAG,"face process implements need to by replace ");
            return new QueryResult();
        }
    };

    private static ListenerFactory instance = new ListenerFactory();

    public static ListenerFactory getInstance(){
        return instance;
    }

    public ICmdListener<Result> getAlogListener(){
        return alogListener;
    }

    public ICmdListener<Result> getAwakenListener(){
        return awakenListener;
    }

    public ICmdListener<Result> getCfgListener() {
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

    public ICmdListener<UpdateResult> getFaceListener() {
        return faceListener;
    }

    public void setFaceListener(ICmdListener<UpdateResult> faceListener) {
        this.faceListener = faceListener;
    }

    public ICmdListener<QueryResult> getFaceQueryListener() {
        return faceQueryListener;
    }

    public void setFaceQueryListener(ICmdListener<QueryResult> faceQueryListener) {
        this.faceQueryListener = faceQueryListener;
    }

    public ICmdListener<QueryResult> getFaceCheckListener() {
        return faceCheckListener;
    }

    public void setFaceCheckListener(ICmdListener<QueryResult> faceCheckListener) {
        this.faceCheckListener = faceCheckListener;
    }

    public ICmdListener<QueryResult> getBackupListener() {
        return backupListener;
    }

    public void setBackupListener(ICmdListener<QueryResult> backupListener) {
        this.backupListener = backupListener;
    }

    public ICmdListener<QueryResult> getRecoverListener() {
        return recoverListener;
    }

    public void setRecoverListener(ICmdListener<QueryResult> recoverListener) {
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
