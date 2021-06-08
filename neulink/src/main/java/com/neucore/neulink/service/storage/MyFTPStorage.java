package com.neucore.neulink.service.storage;

import android.util.Log;

import com.neucore.neulink.IStorage;
import com.neucore.neulink.cfg.ConfigContext;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;

import java.io.File;
import java.io.FileInputStream;

import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpConfig;
import cn.hutool.extra.ftp.FtpMode;

public class MyFTPStorage extends AbsStorage implements IStorage {

    private String TAG = "MyFTPStorage";

    private String server = "";

    //ftp登录账号
    private String username = "";
    //ftp登录密码
    private String password = "";

    private String bucketName = "";
    Ftp ftp = null;
    public MyFTPStorage(){

        server = ConfigContext.getInstance().getConfig(ConfigContext.FTP_SERVER);
        username = ConfigContext.getInstance().getConfig(ConfigContext.FTP_USER_NAME);
        password = ConfigContext.getInstance().getConfig(ConfigContext.FTP_PASSWORD);

        int connectTimeOut = ConfigContext.getInstance().getConfig(ConfigContext.CONN_TIME_OUT,15*1000);

        int readTimeOut = ConfigContext.getInstance().getConfig(ConfigContext.READ_TIME_OUT,15*1000);

        FtpConfig config = new FtpConfig();
        config.setHost(server);
        config.setPort(21);
        config.setUser(username);
        config.setPassword(password);
        config.setConnectionTimeout(connectTimeOut);
        config.setSoTimeout(readTimeOut);
        ftp = new Ftp(config,FtpMode.Passive);
        ftp.setBackToPwd(true);
    }



    /**
     * 上传文件
     *
     * @param savePath     ftp服务保存地址  (不带文件名)
     * @param saveFileName 上传到ftp的文件名
     * @param originFileName  待上传文件的名称（绝对地址） *
     * @return
     */
    @Override
    protected String uploadFile(String savePath, String saveFileName, String originFileName){
        boolean successed = false;
        String url = null;
        try {
            FileInputStream inputStream = new FileInputStream(new File(originFileName));
            successed = ftp.upload(savePath, saveFileName, inputStream);
            url = savePath+"/"+saveFileName;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage() + "  " + e);
        }
        Log.i(TAG,"uploadFile: "+successed);
        if(successed){
            return url;
        }
        else{
            return null;
        }
    }

    @Override
    protected String getBucketName() {
        return ConfigContext.getInstance().getConfig(ConfigContext.FTP_BUCKET_NAME);
    }
}
