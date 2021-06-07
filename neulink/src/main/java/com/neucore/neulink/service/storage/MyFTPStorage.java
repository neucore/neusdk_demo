package com.neucore.neulink.service.storage;

import android.util.Log;

import com.neucore.neulink.IStorage;
import com.neucore.neulink.cfg.ConfigContext;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DatesUtil;
import com.neucore.neulink.util.DeviceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpConfig;
import cn.hutool.extra.ftp.FtpMode;

public class MyFTPStorage implements IStorage {

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
        bucketName = ConfigContext.getInstance().getConfig(ConfigContext.FTP_BUCKET_NAME);

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

    @Override
    public String uploadBak(String path, String requestId, int index) {

        int idx = path.lastIndexOf("/");
        String name = path;
        if(idx!=-1){
            name = path.substring(idx+1);
        }

        String ftpSavePath = getObjectBakKey(DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext()),requestId,index,"");

        boolean successed = uploadFile(bucketName+"/"+ftpSavePath,name,path);

        if(successed){
            return ftpSavePath+"/"+name;
        }
        else{
            return null;
        }
    }

    private String getObjectBakKey(String deviceId,String requestId,int index,String sufix){
        String dateString = DatesUtil.getDateString();
        return String.format("backups/%s/%s/%s",deviceId,dateString,requestId);
    }

    @Override
    public String uploadQData(String path, String requestId, int index) {
        int idx = path.lastIndexOf("/");
        String name = path;
        if(idx!=-1){
            name = path.substring(idx+1);
        }

        String ftpSavePath = getObjectQDataKey(DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext()),requestId,index,"");

        boolean successed = uploadFile(bucketName+"/"+ftpSavePath,name,path);

        if(successed){
            return ftpSavePath+"/"+name;
        }
        else{
            return null;
        }
    }

    private String getObjectQDataKey(String deviceId,String requestId,int index,String sufix){
        String dateString = DatesUtil.getDateString();
        return String.format("qdatas/%s/%s/%s",deviceId,dateString,requestId);
    }

    @Override
    public String uploadLog(String path, String requestId, int index) {
        int idx = path.lastIndexOf("/");
        String name = path;
        if(idx!=-1){
            name = path.substring(idx+1);
        }

        String ftpSavePath = getObjectLogKey(DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext()),requestId,index,"");


        boolean successed = uploadFile(bucketName+"/"+ftpSavePath,name,path);

        if(successed){
            return ftpSavePath+"/"+name;
        }
        else{
            return null;
        }
    }
    private String getObjectLogKey(String deviceId,String requestId,int index,String sufix){
        String dateString = DatesUtil.getDateString();
        return String.format("logs/%s/%s/%s",deviceId,dateString,requestId);
    }

    @Override
    public String uploadImage(String path, String requestId, int index) {
        int idx = path.lastIndexOf("/");
        String name = path;
        if(idx!=-1){
            name = path.substring(idx+1);
        }

        String ftpSavePath = getObjectImageKey(DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext()),requestId,index,"");

        boolean successed = uploadFile(bucketName+"/"+ftpSavePath,name,path);

        if(successed){
            return ftpSavePath+"/"+name;
        }
        else{
            return null;
        }
    }

    /**
     *
     * @param requestId
     * @param index
     * @return backup/date/device_id/req_no/indexNum.json
     */
    private String getObjectImageKey(String deviceId,String requestId,int index,String sufix){
        String dateString = DatesUtil.getDateString();
        return String.format("images/%s/%s/%s",deviceId,dateString,requestId);
    }

    /**
     * 上传文件
     *
     * @param ftpSavePath     ftp服务保存地址  (不带文件名)
     * @param ftpSaveFileName 上传到ftp的文件名
     * @param originFileName  待上传文件的名称（绝对地址） *
     * @return
     */
    private boolean uploadFile(String ftpSavePath, String ftpSaveFileName, String originFileName) {
        boolean flag = false;

        try {
            FileInputStream inputStream = new FileInputStream(new File(originFileName));
            flag = ftp.upload(ftpSavePath, ftpSaveFileName, inputStream);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage() + "  " + e);
        }
        Log.i(TAG,"uploadFile: "+flag);
        return flag;
    }
}
