package com.neucore.neulink.extend;

import android.util.Log;

public class ListenerFactory {
    private String TAG = "ListenerFactory";
    private ICmdListener alogListener = new ICmdListener<Result>() {
        @Override
        public Result doAction(NeulinkEvent event) {
            Log.i(TAG,"Algorithm upgrade need to by replace ");
            return new Result();
        }
    };
    private ICmdListener apkListener = new ICmdListener() {
        @Override
        public Result doAction(NeulinkEvent event) {
            Log.i(TAG,"Application upgrade need to by replace ");
            return new Result();
        }
    };
    private ICmdListener awakenListener = new ICmdListener() {
        @Override
        public Result doAction(NeulinkEvent event) {
            Log.i(TAG,"device awaken implements need to by replace ");
            return new Result();
        }
    };
    private ICmdListener hibrateListener = new ICmdListener() {
        @Override
        public Result doAction(NeulinkEvent event) {
            Log.i(TAG,"device hibrate implements need to by replace ");
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
        public QueryResult doAction(NeulinkEvent event) {
            Log.i(TAG,"face process implements need to by replace ");
            return new QueryResult();
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

    public ICmdListener getAlogListener(){
        return alogListener;
    }

    public ICmdListener getAPkListener(){
        return apkListener;
    }

    public ICmdListener getAwakenListener(){
        return awakenListener;
    }
    public ICmdListener getHibrateListener(){
        return hibrateListener;
    }

    public void setAlogListener(ICmdListener alogListener) {
        this.alogListener = alogListener;
    }

    public ICmdListener getApkListener() {
        return apkListener;
    }

    public void setApkListener(ICmdListener apkListener) {
        this.apkListener = apkListener;
    }

    public void setAwakenListener(ICmdListener awakenListener) {
        this.awakenListener = awakenListener;
    }

    public void setHibrateListener(ICmdListener hibrateListener) {
        this.hibrateListener = hibrateListener;
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
}
