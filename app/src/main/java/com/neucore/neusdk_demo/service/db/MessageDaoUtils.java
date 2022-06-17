package com.neucore.neusdk_demo.service.db;

import android.content.Context;

import com.neucore.greendao.gen.MessageDao;
import com.neucore.neusdk_demo.service.db.bean.Message;

import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;
import java.util.concurrent.Callable;

public class MessageDaoUtils {
    private static final String TAG = MessageDaoUtils.class.getSimpleName();
    private DaoManager mManager;

    public MessageDaoUtils(Context context){
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 完成message记录的插入，如果表未创建，先创建User表
     * @param message
     * @return
     */
    public boolean insertMessage(final Message message){

        boolean flg = mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                mManager.getDaoSession().getMessageDao().insertOrReplace(message);
                return true;
            }
        });
        boolean flag = true;
        return flag;
    }


    public Boolean update(final long id, final String status,final String msg) {

        return mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    String sql = "update \"MESSAGE\" set \"STATUS\" = ?,\"RESULT\" = ? WHERE \"_ID\" = ?";
                    DatabaseStatement statement = mManager.getDaoMaster().getDatabase().compileStatement(sql);
                    statement.bindString(1, status);
                    statement.bindString(2, msg);
                    statement.bindLong(3, id);
                    statement.execute();
                    return true;
                }
            });

    }

    public Boolean updatePkg(final long id, final long offset,final String status, final String msg) {

        return mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    String sql = "update \"MESSAGE\" set \"PKG_STATUS\" = ?,\"RESULT\" = ?,\"OFFSET\" = ? WHERE \"_ID\" = ?";
                    DatabaseStatement statement = mManager.getDaoMaster().getDatabase().compileStatement(sql);
                    statement.bindString(1, status);
                    statement.bindString(2, msg);
                    statement.bindLong(3, offset);
                    statement.bindLong(4, id);
                    statement.execute();
                    return true;
                }
            });
    }

    public Boolean update(final long id, final long offset) {

        return mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {
                 @Override
                 public Boolean call() {
                     String sql = "update \"MESSAGE\" set \"OFFSET\" = ? WHERE \"_ID\" = ?";
                     DatabaseStatement statement = mManager.getDaoMaster().getDatabase().compileStatement(sql);
                     statement.bindLong(1, offset);
                     statement.bindLong(2, id);
                     statement.execute();
                     return true;
                 }
            });
    }


    /**
     * 修改一条数据
     * @param message
     * @return
     */
    public boolean updateMessage(final Message message){

        return mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {
                 @Override
                 public Boolean call() {
                     String sql = "update \"MESSAGE\" set \"STATUS\" = ?,\"RESULT\" = ?,\"OFFSET\" = ? WHERE \"_ID\" = ?";
                     DatabaseStatement statement = mManager.getDaoMaster().getDatabase().compileStatement(sql);

                     statement.bindString(1, message.getStatus());
                     statement.bindString(2, message.getResult());
                     statement.bindLong(3, message.getOffset());
                     statement.bindLong(4, message.getId());
                     statement.execute();
                     return true;
                 }
            });
    }

    /**
     * 删除所有记录
     * @return
     */
    public boolean deleteAll(){
        return mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {
                 @Override
                 public Boolean call() {
                     //按照id删除
                     mManager.getDaoSession().deleteAll(Message.class);
                     return true;
                 }
            });
    }

    /**
     * 查询所有记录
     * @return
     */
    public List<Message> queryAllMessage(){
        return mManager.getDaoSession().loadAll(Message.class);
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    public Message queryMessageById(long key){
        return mManager.getDaoSession().load(Message.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<Message> queryUserByNativeSql(String sql, String[] conditions){
        return mManager.getDaoSession().queryRaw(Message.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     * @return
     */
    public List<Message> queryMessageByQueryBuilder(long id){
        QueryBuilder<Message> queryBuilder = mManager.getDaoSession().queryBuilder(Message.class);
        return queryBuilder.where(MessageDao.Properties.Id.eq(id)).list();
    }
    public List<Message> queryMessage(int offset){
        QueryBuilder<Message> queryBuilder = mManager.getDaoSession().queryBuilder(Message.class);
        return queryBuilder.offset(offset * 200).limit(200).list();
    }
    public List<Message> queryMessageId(String id, int offset){
        QueryBuilder<Message> queryBuilder = mManager.getDaoSession().queryBuilder(Message.class);
        return queryBuilder.where(MessageDao.Properties.Id.eq(id)).offset(offset * 200).limit(200).list();
    }
    public List<Message> queryReqId(String reqId, int offset){
        QueryBuilder<Message> queryBuilder = mManager.getDaoSession().queryBuilder(Message.class);
        return queryBuilder.where(MessageDao.Properties.ReqId.eq(reqId)).offset(offset * 200).limit(200).list();
    }

    public long countByReqtime(long startReqtime,long endReqtime){

        String sql = "select count(\"_ID\") from \"MESSAGE\" WHERE \"REQTIME\" between ? AND ?";
        DatabaseStatement count = mManager.getDaoMaster().getDatabase().compileStatement(sql);

        count.bindLong(1,startReqtime);
        count.bindLong(2,endReqtime);
        return count.simpleQueryForLong();

    }

    public List<Message> queryReqtime(long startReqtime, long endReqtime, int offset){
        QueryBuilder<Message> queryBuilder = mManager.getDaoSession().queryBuilder(Message.class);
        return queryBuilder.where(MessageDao.Properties.Reqtime.between(startReqtime,endReqtime)).offset(offset * 200).limit(200).list();
    }
}