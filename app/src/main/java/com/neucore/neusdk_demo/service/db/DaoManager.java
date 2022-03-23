package com.neucore.neusdk_demo.service.db;


import android.content.Context;

import com.neucore.greendao.gen.DaoMaster;
import com.neucore.greendao.gen.DaoSession;
import com.neucore.greendao.gen.KeywordHistoryEntityDao;
import com.neucore.neulink.BuildConfig;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.FileUtils;
import com.neucore.neulink.util.NeuHttpHelper;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.io.IOException;

/**
 * 创建数据库、创建数据库表、包含增删改查的操作以及数据库的升级
 * Created by Mr.sorrow on 2017/5/5.
 */

public class DaoManager
{
    private static final String TAG = DaoManager.class.getSimpleName();
    public static final String DB_NAME = "tlib.db";

    private Context context;

    //多线程中要被共享的使用volatile关键字修饰
    private volatile static DaoManager manager = new DaoManager();
    private static DaoMaster sDaoMaster;
    private static DaoMaster.DevOpenHelper sHelper;
    private static DaoSession sDaoSession;

    /**
     * 单例模式获得操作数据库对象
     *
     * @return
     */
    public static DaoManager getInstance()
    {
        return manager;
    }

    public void store(Context context,String requestId,String url){
        try {
            String dirPath= DeviceUtils.getDBPath(context);
            String path=null;
            File parentFile=new File(dirPath);
            if(!parentFile.exists()){
                parentFile.mkdirs();
            }
            String parentPath=parentFile.getAbsolutePath();
            if(parentPath.lastIndexOf("\\/")!=-1){
                path=dirPath + DaoManager.DB_NAME;
            }else{
                path=dirPath+File.separator+ DaoManager.DB_NAME;
            }
            File tmpFile = NeuHttpHelper.dld2File(context,requestId,url);
            FileUtils.move(tmpFile,new File(path));
            tmpFile.delete();
        } catch (IOException e) {
            throw new NeulinkException(500,e.getMessage());
        }
    }

    private DaoManager()
    {
        setDebug();
    }

    public void init(Context context)
    {
        this.context = context;
        /**
         * 关闭查询sql语句日志输出
         */
        QueryBuilder.LOG_SQL=false;
        QueryBuilder.LOG_VALUES = false;
    }

    /**
     * 判断是否有存在数据库，如果没有则创建
     *
     * @return
     */
    public DaoMaster getDaoMaster()
    {
        if (sDaoMaster == null)
        {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(new DataBaseContext(context), DB_NAME, null){

                @Override
                public void onCreate(Database db) {
                    super.onCreate(db);
                    startMigrate(db);
                }

                @Override
                public void onUpgrade(Database db, int oldVersion, int newVersion) {
                    super.onUpgrade(db, oldVersion, newVersion);
                    startMigrate(db);
                }

                private void startMigrate(Database db) {
                    MigrationHelper.migrate(db, KeywordHistoryEntityDao.class);
                }
            };
            sDaoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return sDaoMaster;
    }

    /**
     * 完成对数据库的添加、删除、修改、查询操作，仅仅是一个接口
     *
     * @return
     */
    public DaoSession getDaoSession()
    {
        if (sDaoSession == null)
        {
            if (sDaoMaster == null)
            {
                sDaoMaster = getDaoMaster();
            }
            /**
             * 关闭缓存
             */
            sDaoSession = sDaoMaster.newSession(IdentityScopeType.None);
        }
        return sDaoSession;
    }

    /**
     * 打开输出日志，默认关闭
     */
    public void setDebug()
    {
        if (BuildConfig.DEBUG)
        {
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
        }
    }

    /**
     * 关闭所有的操作，数据库开启后，使用完毕要关闭
     */
    public void closeConnection()
    {
        closeHelper();
        closeDaoSession();
    }

    public void closeHelper()
    {
        if (sHelper != null)
        {
            sHelper.close();
            sHelper = null;
        }
    }

    public void closeDaoSession()
    {
        if (sDaoSession != null)
        {
            sDaoSession.clear();
            sDaoSession = null;
        }
    }
}
