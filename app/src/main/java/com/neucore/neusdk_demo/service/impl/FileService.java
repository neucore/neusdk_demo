package com.neucore.neusdk_demo.service.impl;

import android.database.sqlite.SQLiteDatabase;

import com.neucore.greendao.gen.DaoMaster;
import com.neucore.greendao.gen.DaoSession;
import com.neucore.greendao.gen.FileInfoDao;
import com.neucore.neulink.impl.service.resume.IFileService;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neusdk_demo.service.db.bean.FileInfo;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;

public class FileService implements IFileService {
    private static FileService instance = new FileService();
    private DaoSession session;
    private FileService(){
        // regular SQLite database
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(ContextHolder.getInstance().getContext(),"resume.db");
        SQLiteDatabase database = helper.getWritableDatabase();
        // encrypted SQLCipher database
        // note: you need to add SQLCipher to your dependencies, check the build.gradle file
        // ExampleOpenHelper helper = new ExampleOpenHelper(this, "notes-db-encrypted");
        // Database db = helper.getEncryptedWritableDb("encryption-key");
        session = new DaoMaster(database).newSession();
    }
    public static FileService getInstance() {
        return instance;
    }

    public void update(String url, int thid,long pos){
        FileInfo fileInfo = new FileInfo();
        fileInfo.setUrl(url);
        fileInfo.setThid(thid);
        fileInfo.setProcessed(pos);
        session.getFileInfoDao().insertOrReplace(fileInfo);
    }

    /**
     * 历史下载记录
     * @param path
     * @return
     */
    public Map<Integer, Long> getData(String path){
        QueryBuilder queryBuilder = session.getFileInfoDao().queryBuilder();
        List<FileInfo> fileInfoList = queryBuilder.where(FileInfoDao.Properties.Url.eq(path)).list();
        /**
         * key:thread id
         * value:pos 位置
         */
        Map<Integer,Long> process = new HashMap<>();
        if(!ObjectUtil.isEmpty(fileInfoList)){
            for (FileInfo file:fileInfoList) {
                process.put(file.getThid(),file.getProcessed());
            }
        }
        return process;
    }

    public void save(String path,  Map<Integer, Long> thids){

        Integer[] thid = new Integer[thids.size()];
        thids.keySet().toArray(thid);
        for (int th:thid) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setUrl(path);
            Long pos = thids.get(th);
            fileInfo.setThid(th);
            fileInfo.setProcessed(pos);
            session.getFileInfoDao().insertOrReplace(fileInfo);
        }
    }

    public void delete(String path){

        QueryBuilder queryBuilder = session.getFileInfoDao().queryBuilder();
        List<FileInfo> fileInfoList = queryBuilder.where(FileInfoDao.Properties.Url.eq(path)).list();
        session.getFileInfoDao().deleteInTx(fileInfoList);
    }
}