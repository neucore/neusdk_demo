package com.neucore.neusdk_demo.service.db;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.neucore.greendao.gen.RecordDao;
import com.neucore.neusdk_demo.service.db.bean.Record;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class RecordDaoUtils {
    private static final String TAG = RecordDaoUtils.class.getSimpleName();
    private DaoManager mManager;

    public RecordDaoUtils(Context context){
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 完成user记录的插入，如果表未创建，先创建Record表
     * @param user
     * @return
     */
    public boolean insertRecord(Record user){
        boolean flag = false;
        flag = mManager.getDaoSession().getRecordDao().insert(user) == -1 ? false : true;
        Log.i(TAG, "insert Record :" + flag + "-->" + user.toString());
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     * @param userList
     * @return
     */
    public boolean insertMultRecord(final List<Record> userList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (Record user : userList) {
                        mManager.getDaoSession().insertOrReplace(user);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            Log.e(TAG,"insertMultRecord",e);
        }
        return flag;
    }

    /**
     * 修改一条数据
     * @param user
     * @return
     */
    public boolean updateRecord(Record user){
        boolean flag = false;
        try {
            mManager.getDaoSession().update(user);
            flag = true;
        }catch (Exception e){
            Log.e(TAG,"updateRecord",e);
        }
        return flag;
    }

    /**
     * 删除单条记录
     * @param user
     * @return
     */
    public boolean deleteRecord(Record user){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(user);
            flag = true;
        }catch (Exception e){
            Log.e(TAG,"deleteRecord",e);
        }
        return flag;
    }

    /**
     * 删除所有记录
     * @return
     */
    public boolean deleteAll(){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().deleteAll(Record.class);
            flag = true;
        }catch (Exception e){
            Log.e(TAG,"deleteAll",e);
        }
        return flag;
    }

    /**
     * 查询所有记录
     * @return
     */
    public List<Record> queryAllRecord(){
        return mManager.getDaoSession().loadAll(Record.class);
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    public Record queryRecordById(long key){
        return mManager.getDaoSession().load(Record.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<Record> queryRecordByNativeSql(String sql, String[] conditions){
        return mManager.getDaoSession().queryRaw(Record.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     * @return
     */
    public List<Record> queryRecordByQueryBuilder(long id){
        QueryBuilder<Record> queryBuilder = mManager.getDaoSession().queryBuilder(Record.class);
        return queryBuilder.where(RecordDao.Properties._id.eq(id)).list();
    }
    public List<Record> queryRecordId(String userId){
        QueryBuilder<Record> queryBuilder = mManager.getDaoSession().queryBuilder(Record.class);
        if(TextUtils.isEmpty(userId)) {
            return queryBuilder.list();
        }else{
            return queryBuilder.where(RecordDao.Properties.UserId.eq(userId)).list();
        }
    }
    public List<Record> queryRecord(int offset){
        QueryBuilder<Record> queryBuilder = mManager.getDaoSession().queryBuilder(Record.class);
        return queryBuilder.offset(offset * 20).limit(20).list();
    }
    public List<Record> queryRecordId(String userId, int offset){
        QueryBuilder<Record> queryBuilder = mManager.getDaoSession().queryBuilder(Record.class);
        if(TextUtils.isEmpty(userId)) {
            return queryBuilder.offset(offset * 20).limit(20).list();
        }else{
            return queryBuilder.where(RecordDao.Properties.UserId.eq(userId)).offset(offset * 20).limit(20).list();
        }
    }
    public List<Record> queryCardId(String cardId, int offset){
        QueryBuilder<Record> queryBuilder = mManager.getDaoSession().queryBuilder(Record.class);
        if(TextUtils.isEmpty(cardId)) {
            return queryBuilder.offset(offset * 20).limit(20).list();
        }else{
            return queryBuilder.where(RecordDao.Properties.CardId.eq(cardId)).offset(offset * 20).limit(20).list();
        }
    }
    public List<Record> queryName(String name, int offset){
        QueryBuilder<Record> queryBuilder = mManager.getDaoSession().queryBuilder(Record.class);
        if(TextUtils.isEmpty(name)) {
            return queryBuilder.offset(offset * 20).limit(20).list();
        }else{
            return queryBuilder.where(RecordDao.Properties.Name.eq(name)).offset(offset * 20).limit(20).list();
        }
    }
    public List<Record> queryRecordId(String userId, int offset, long start, long end){
        QueryBuilder<Record> queryBuilder = mManager.getDaoSession().queryBuilder(Record.class);
        if(TextUtils.isEmpty(userId)) {
            return queryBuilder.where(RecordDao.Properties.Time.ge(start),RecordDao.Properties.Time.le(end)).offset(offset * 20).limit(20).list();
        }else{
            return queryBuilder.where(RecordDao.Properties.UserId.eq(userId),RecordDao.Properties.Time.ge(start),RecordDao.Properties.Time.le(end)).offset(offset * 20).limit(20).list();
        }
    }
    public List<Record> queryCardId(String cardId, int offset, long start, long end){
        QueryBuilder<Record> queryBuilder = mManager.getDaoSession().queryBuilder(Record.class);
        if(TextUtils.isEmpty(cardId)) {
            return queryBuilder.where(RecordDao.Properties.Time.ge(start),RecordDao.Properties.Time.le(end)).offset(offset * 20).limit(20).list();
        }else{
            return queryBuilder.where(RecordDao.Properties.CardId.eq(cardId),RecordDao.Properties.Time.ge(start),RecordDao.Properties.Time.le(end)).offset(offset * 20).limit(20).list();
        }
    }
    public List<Record> queryName(String name, int offset, long start, long end){
        QueryBuilder<Record> queryBuilder = mManager.getDaoSession().queryBuilder(Record.class);
        if(TextUtils.isEmpty(name)) {
            return queryBuilder.where(RecordDao.Properties.Time.ge(start),RecordDao.Properties.Time.le(end)).offset(offset * 20).limit(20).list();
        }else{
            return queryBuilder.where(RecordDao.Properties.Name.eq(name),RecordDao.Properties.Time.ge(start),RecordDao.Properties.Time.le(end)).offset(offset * 20).limit(20).list();
        }
    }
}