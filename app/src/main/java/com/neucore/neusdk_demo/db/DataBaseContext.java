package com.neucore.neusdk_demo.db;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.neucore.neulink.util.DeviceUtils;

import java.io.File;
import java.io.IOException;

public class DataBaseContext extends ContextWrapper {
    private static String TAG = "DataBaseContext";
    public DataBaseContext(Context base) {
        super(base);
    }


    @Override
    public File getDatabasePath(String name) {

        String dirPath=DeviceUtils.getDBPath(getBaseContext());

        String path=null;
        File parentFile=new File(dirPath);
        if(!parentFile.exists()){
            parentFile.mkdirs();
        }
        String parentPath=parentFile.getAbsolutePath();
        if(parentPath.lastIndexOf("\\/")!=-1){
            path=dirPath + name;
        }else{
            path=dirPath+File.separator+name;
        }
        File dbFile = new File(path);
        try {
            dbFile.createNewFile();
        } catch (IOException e) {
            Log.e(TAG,"getDatabasePath",e);
        }

        return dbFile;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name).getAbsolutePath(),factory,errorHandler);
    }
}
