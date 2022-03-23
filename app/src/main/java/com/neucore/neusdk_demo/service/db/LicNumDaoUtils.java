package com.neucore.neusdk_demo.service.db;

import android.content.Context;
import android.util.Log;

import com.neucore.greendao.gen.LicNumberDao;
import com.neucore.greendao.gen.UserDao;
import com.neucore.neulink.cmd.rrpc.QCond;
import com.neucore.neusdk_demo.service.db.bean.LicNumber;

import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;
import java.util.concurrent.Callable;

public class LicNumDaoUtils {
    private static final String TAG = LicNumDaoUtils.class.getSimpleName();
    private DaoManager mManager;

    public LicNumDaoUtils(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 完成number记录的插入，如果表未创建，先创建User表
     *
     * @param number
     * @return
     */
    public Boolean insertLicNumber(final LicNumber number) {

        Boolean eft = mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {

            public Boolean call() {
                mManager.getDaoSession().getLicNumberDao().insertOrReplace(number);
                return true;
            }
        });
        Log.i(TAG, "insert LicNumber :" + eft + "-->" + number.toString());
        return eft;
    }

    /**
     * 插入多条数据，在子线程操作
     *
     * @param licNumberList
     * @return
     */
    public boolean insertMultNumber(final List<LicNumber> licNumberList) {
        boolean flag = false;

        flag = mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                for (LicNumber licNumber : licNumberList) {
                    mManager.getDaoSession().insertOrReplace(licNumber);
                }
                return true;
            }
        });

        return flag;
    }

    /**
     * 修改一条数据
     *
     * @param licNumber
     * @return
     */
    public boolean updateLicNumber(final LicNumber licNumber) {
        boolean flag = false;

        flag = mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {

            public Boolean call() {
                mManager.getDaoSession().update(licNumber);
                return true;
            }
        });

        return flag;
    }

    public boolean updateLicNum(final long reqTime, final String licNum) {
        boolean flag = false;

        flag = mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                String sql = "update \"LIC_NUMBER\" set \"TIME\" = ? WHERE \"LIC_NUM\" = ?";


                DatabaseStatement statement = mManager.getDaoMaster().getDatabase().compileStatement(sql);

                statement.bindLong(1, reqTime);
                statement.bindString(2, licNum);

                statement.execute();
                return true;
            }
        });

        return flag;
    }

    public boolean delete(final long reqTime) {

        boolean flg = mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                String sql = "delete from \"LIC_NUMBER\" WHERE \"time\" < ?";

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
     *
     * @param licNumber
     * @return
     */
    public boolean deleteLicNum(final LicNumber licNumber) {
        boolean flag = false;

        flag = mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                //按照id删除
                mManager.getDaoSession().delete(licNumber);
                return true;
            }
        });

        return flag;
    }

    /**
     * 删除单条记录
     *
     * @param licNumber
     * @return
     */
    public boolean deleteLicByLicNum(final String licNumber) {
        boolean flag = false;

        flag = mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                DatabaseStatement statement = mManager.getDaoMaster().getDatabase().compileStatement("delete from \"LIC_NUMBER\" where \"LIC_NUM\"=?");

                statement.bindString(1, licNumber);

                statement.execute();
                return true;
            }
        });

        return flag;
    }

    /**
     * 删除所有记录
     *
     * @return
     */
    public boolean deleteAll() {
        boolean flag = false;

        flag = mManager.getDaoSession().callInTxNoException(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                //按照id删除
                mManager.getDaoSession().deleteAll(LicNumber.class);
                return true;
            }
        });

        return flag;
    }

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<LicNumber> queryAllLicNumber() {
        return mManager.getDaoSession().loadAll(LicNumber.class);
    }

    /**
     * @param conds
     * @return
     */
    public long count(QCond[] conds) {

        String sql = "select count(\"_ID\") from \"LIC_NUMBER\" ";

        int size = conds == null ? 0 : conds.length;
        StringBuffer condStr = new StringBuffer();
        for (int i = 0; i < size; i++) {
            QCond cond = conds[i];
            if (i == 0) {
                condStr.append("WHERE ");
            }
            if ("between".equalsIgnoreCase(cond.getName().trim())) {
                throw new RuntimeException("不支持between");
            }
            condStr.append("\"" + cond.getName() + "\" " + cond.getOpt() + " ? ");

            if (i != size - 1) {
                condStr.append(" AND ");
            }
        }

        sql = sql + condStr.toString();

        DatabaseStatement count = mManager.getDaoMaster().getDatabase().compileStatement(sql);

        for (int i = 0; i < size; i++) {
            QCond cond = conds[i];
            count.bindString(i + 1, cond.getVal());
        }

        return count.simpleQueryForLong();
    }

    public List<LicNumber> query(QCond[] conds, int offset) {
        String sql = "select \"_ID\",\"LIC_NUM\" from \"LIC_NUMBER\" ";
        int size = conds == null ? 0 : conds.length;
        StringBuffer condStr = new StringBuffer();
        for (int i = 0; i < size; i++) {
            QCond cond = conds[i];
            if (i == 0) {
                condStr.append("WHERE ");
            }
            if ("between".equalsIgnoreCase(cond.getName().trim())) {
                throw new RuntimeException("不支持between");
            }
            condStr.append("\"" + cond.getName() + "\" " + cond.getOpt() + " ? ");

            if (i != size - 1) {
                condStr.append(" AND ");
            }
        }

        condStr.append(" limit " + (offset * 200) + " , " + 200);

        String[] bindArgs = new String[size];

        for (int i = 0; i < size; i++) {
            QCond cond = conds[i];
            bindArgs[i] = cond.getVal();
        }

        sql = condStr.toString();

        return mManager.getDaoSession().queryRaw(LicNumber.class, sql, bindArgs);//.getDatabase().execSQL(sql + condStr,bindArgs);
    }

    /**
     * 根据主键id查询记录
     *
     * @param key
     * @return
     */
    public LicNumber queryLicNumberById(long key) {
        return mManager.getDaoSession().load(LicNumber.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<LicNumber> queryLicNumberByNativeSql(String sql, String[] conditions) {
        return mManager.getDaoSession().queryRaw(LicNumber.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     *
     * @return
     */
    public List<LicNumber> queryLicNumberByQueryBuilder(long id) {
        QueryBuilder<LicNumber> queryBuilder = mManager.getDaoSession().queryBuilder(LicNumber.class);
        return queryBuilder.where(UserDao.Properties._id.eq(id)).list();
    }

    public List<LicNumber> queryLicNumber(int offset) {
        QueryBuilder<LicNumber> queryBuilder = mManager.getDaoSession().queryBuilder(LicNumber.class);
        return queryBuilder.offset(offset * 200).limit(200).list();
    }
    public List<LicNumber> queryLicNumber(String licNumber, int offset){
        QueryBuilder<LicNumber> queryBuilder = mManager.getDaoSession().queryBuilder(LicNumber.class);
        return queryBuilder.where(LicNumberDao.Properties.LicNum.eq(licNumber)).offset(offset * 200).limit(200).list();
    }
}