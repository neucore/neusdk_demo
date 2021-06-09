package com.neucore.neusdk_demo.db;

import android.content.Context;
import android.util.Log;

import com.neucore.greendao.gen.UserDao;
import com.neucore.neulink.cmd.rrpc.QCond;
import com.neucore.neusdk_demo.db.bean.User;

import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;
import java.util.concurrent.Callable;

public class UserDaoUtils {
    private static final String TAG = UserDaoUtils.class.getSimpleName();
    private DaoManager mManager;

    public UserDaoUtils(Context context){
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 完成user记录的插入，如果表未创建，先创建User表
     * @param user
     * @return
     */
    public Boolean insertUser(final User user){

        Boolean eft = mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {

             public Boolean call() {
                 mManager.getDaoSession().getUserDao().insertOrReplace(user);
                 return true;
             }
         });
        Log.i(TAG, "insert User :" + eft );//+ "-->" + user.toString());
        return eft;
    }

    /**
     * 插入多条数据，在子线程操作
     * @param userList
     * @return
     */
    public boolean insertMultUser(final List<User> userList) {
        boolean flag = false;

        flag = mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                for (User user : userList) {
                    mManager.getDaoSession().insertOrReplace(user);
                }
                return true;
            }
        });

        return flag;
    }

    /**
     * 修改一条数据
     * @param user
     * @return
     */
    public boolean updateUser(final User user){
        boolean flag = false;

        flag = mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {

            public Boolean call() {
                mManager.getDaoSession().update(user);
                return true;
            }
        });

        return flag;
    }

    public boolean updateUser(final  String userId,final User user){
        boolean flag = false;

        flag = mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                String sql = "update \"USER\" set \"NAME\" = ?,\"CARD_ID\"= ? ,\"FACE\" = ?,\"TIME\" = ? WHERE \"USER_ID\" = ?";


                DatabaseStatement statement = mManager.getDaoMaster().getDatabase().compileStatement(sql);

                statement.bindString(1, user.getName());
                statement.bindString(2, user.getCardId());
                statement.bindString(3, user.getFace());
                statement.bindLong(4, user.getTime());
                statement.bindString(5, userId);

                statement.execute();
                return true;
            }
        });

        return flag;
    }

    public boolean delete(final long reqTime){

        boolean flg =  mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {
                 @Override
                 public Boolean call() {
                     String sql = "delete from \"USER\" WHERE \"time\" < ?";

                     DatabaseStatement statement = mManager.getDaoMaster().getDatabase().compileStatement(sql);
                     statement.bindLong(1, reqTime);
                     statement.execute();
                     return true;
                 }
            });
        return flg;
    }

    /**
     * 删除单条记录
     * @param user
     * @return
     */
    public boolean deleteUser(final User user){
        boolean flag = false;

        flag = mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {
             @Override
             public Boolean call() {
                 //按照id删除
                 mManager.getDaoSession().delete(user);
                 return true;
             }
        });

        return flag;
    }

    /**
     * 删除单条记录
     * @param userId
     * @return
     */
    public boolean deleteUserByUserId(final String userId){
        boolean flag = false;

        flag = mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {
             @Override
             public Boolean call() {
                 DatabaseStatement statement = mManager.getDaoMaster().getDatabase().compileStatement("delete from \"USER\" where \"USER_ID\"=?");

                 statement.bindString(1, userId);

                 statement.execute();
                 return true;
             }
        });

        return flag;
    }

    /**
     * 删除所有记录
     * @return
     */
    public boolean deleteAll(){
        boolean flag = false;

        flag = mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {
             @Override
             public Boolean call() {
                 //按照id删除
                 mManager.getDaoSession().deleteAll(User.class);
                 return true;
             }
        });

        return flag;
    }

    /**
     * 查询所有记录
     * @return
     */
    public List<User> queryAllUser(){
        return mManager.getDaoSession().loadAll(User.class);
    }

    /**
     *
     * @param conds
     * @return
     */
    public long count(QCond[] conds){

        String sql = "select count(\"_ID\") from \"USER\" ";

        int size = conds==null?0:conds.length;
        StringBuffer condStr = new StringBuffer();
        for(int i=0;i<size;i++){
            QCond cond = conds[i];
            if(i==0) {
                condStr.append("WHERE ");
            }
            if("between".equalsIgnoreCase(cond.getName().trim())){
                throw new RuntimeException("不支持between");
            }
            condStr.append("\""+cond.getName()+"\" " + cond.getOpt() + " ? ");

            if(i!=size-1){
                condStr.append(" AND ");
            }
        }

        sql = sql + condStr.toString();

        DatabaseStatement count = mManager.getDaoMaster().getDatabase().compileStatement(sql);

        for(int i=0;i<size;i++){
            QCond cond = conds[i];
            count.bindString(i+1,cond.getVal());
        }

        return count.simpleQueryForLong();
    }

    public List<User> query(QCond[] conds, int offset){
        String sql = "select \"USER_ID\",\"CARD_ID\",\"NAME\",\"DEPARTMENT\" from \"USER\" ";
        int size = conds==null?0:conds.length;
        StringBuffer condStr = new StringBuffer();
        for(int i=0;i<size;i++){
            QCond cond = conds[i];
            if(i==0) {
                condStr.append("WHERE ");
            }
            if("between".equalsIgnoreCase(cond.getName().trim())){
                throw new RuntimeException("不支持between");
            }
            condStr.append("\""+cond.getName()+"\" " + cond.getOpt() + " ? ");

            if(i!=size-1){
                condStr.append(" AND ");
            }
        }

        condStr.append(" limit "+(offset*200) + " , "+200);

        String[] bindArgs = new String[size];

        for(int i=0;i<size;i++){
            QCond cond = conds[i];
            bindArgs[i] = cond.getVal();
        }

        sql = condStr.toString();

        return mManager.getDaoSession().queryRaw(User.class,sql,bindArgs);//.getDatabase().execSQL(sql + condStr,bindArgs);
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    public User queryUserById(long key){
        return mManager.getDaoSession().load(User.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<User> queryUserByNativeSql(String sql, String[] conditions){
        return mManager.getDaoSession().queryRaw(User.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     * @return
     */
    public List<User> queryUserByQueryBuilder(long id){
        QueryBuilder<User> queryBuilder = mManager.getDaoSession().queryBuilder(User.class);
        return queryBuilder.where(UserDao.Properties._id.eq(id)).list();
    }
    public List<User> queryUser(int offset){
        QueryBuilder<User> queryBuilder = mManager.getDaoSession().queryBuilder(User.class);
        return queryBuilder.offset(offset * 200).limit(200).list();
    }
    public List<User> queryUserId(String userId, int offset){
        QueryBuilder<User> queryBuilder = mManager.getDaoSession().queryBuilder(User.class);
        return queryBuilder.where(UserDao.Properties.UserId.eq(userId)).offset(offset * 200).limit(200).list();
    }
    public List<User> queryCardId(String cardId, int offset){
        QueryBuilder<User> queryBuilder = mManager.getDaoSession().queryBuilder(User.class);
        return queryBuilder.where(UserDao.Properties.CardId.eq(cardId)).offset(offset * 200).limit(200).list();
    }
    public List<User> queryName(String name, int offset){
        QueryBuilder<User> queryBuilder = mManager.getDaoSession().queryBuilder(User.class);
        return queryBuilder.where(UserDao.Properties.Name.eq(name)).offset(offset * 200).limit(200).list();
    }
}