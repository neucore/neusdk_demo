package com.neucore.neulink.service.storage;

import android.util.Log;

import com.neucore.neulink.IStorage;
import com.neucore.neulink.cfg.ConfigContext;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DatesUtil;
import com.neucore.neulink.util.DeviceUtils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;

@Deprecated
public class FTPStorage implements IStorage {

    private String server = "";

    //ftp登录账号
    private String username = "";
    //ftp登录密码
    private String password = "";

    private String bucketName = "";

    //超时时间
    public int timeOut = 2;
    //被动模式开关 如果不开被动模式 有防火墙 可能会上传失败， 但被动模式需要ftp支持
    public boolean enterLocalPassiveMode = true;

    private FTPClient ftpClient = null;

    public FTPStorage(){
        server = ConfigContext.getInstance().getConfig("FTP.Server");
        username = ConfigContext.getInstance().getConfig("FTP.UserName");
        password = ConfigContext.getInstance().getConfig("FTP.Password");
        bucketName = ConfigContext.getInstance().getConfig("FTP.BucketName");

        int connectTimeOut = ConfigContext.getInstance().getConfig("connectTimeOut",15*1000);
        int readTimeOut = ConfigContext.getInstance().getConfig("readTimeOut",15*1000);

        ftpClient = new FTPClient();
        //设置超时时间以毫秒为单位使用时，从数据连接读。
        //ftpClient.enterLocalPassiveMode();
        ftpClient.setConnectTimeout(connectTimeOut);
        ftpClient.setDataTimeout(readTimeOut);
        ftpClient.setControlEncoding("utf-8");
    }

    private boolean connect(){
        boolean flag = false;
        try {
            Log.e("FTP", "连接...FTP服务器...");
            int index = server.lastIndexOf("/");

            String tmp = server.substring(index+1);
            int hostnameIdx = tmp.indexOf(":");
            String hostname = tmp.substring(0,hostnameIdx);
            int port = Integer.valueOf(tmp.substring(hostnameIdx+1));
            ftpClient.connect(hostname, port); //连接ftp服务器
            //是否开启被动模式
            if (enterLocalPassiveMode) {
                ftpClient.setRemoteVerificationEnabled(false);
                ftpClient.enterLocalPassiveMode();
            }
            ftpClient.login(username, password); //登录ftp服务器
            int replyCode = ftpClient.getReplyCode(); //是否成功登录服务器
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                Log.e("--------->","连接...FTP服务器...失败: " + hostname + ":" + port+ "");
            }
            Log.e("FTP","连接...FTP服务器...成功:" + hostname + ":" + port);
        } catch (MalformedURLException e) {
            Log.e(e.getMessage(), e+"");
        } catch (IOException e) {
            Log.e(e.getMessage(), e+"");
        }
        return flag;
    }

    @Override
    public String uploadBak(String path, String requestId, int index) {

        int idx = path.lastIndexOf("/");
        String name = path;
        if(idx!=-1){
            name = path.substring(idx+1);
        }

        String ftpSavePath = getObjectBakKey(DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext()),requestId,index,"");

        uploadFile(bucketName+"/"+ftpSavePath,name,path);

        return ftpSavePath+"/"+name;
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

        uploadFile(bucketName+"/"+ftpSavePath,name,path);

        return ftpSavePath+"/"+name;
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

        uploadFile(bucketName+"/"+ftpSavePath,name,path);

        return ftpSavePath+"/"+name;
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

        uploadFile(bucketName+"/"+ftpSavePath,name,path);

        return ftpSavePath+"/"+name;
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
     * @param originFile      待上传文件
     * @return
     */
    private boolean uploadFile(String ftpSavePath, String ftpSaveFileName, File originFile) {
        boolean flag = false;
        try {
            FileInputStream inputStream = new FileInputStream(originFile);
            flag = uploadFile(ftpSavePath, ftpSaveFileName, inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("FTP", e.getMessage() + "  " + e);
        }
        return flag;
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
            flag = uploadFile(ftpSavePath, ftpSaveFileName, inputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("------------>", e.getMessage() + "  " + e);
        }
        return flag;
    }

    /**
     * 上传文件(直接读取输入流形式)
     *
     * @param ftpSavePath    ftp服务保存地址
     * @param ftpSaveFileName    上传到ftp的文件名
     * @param inputStream 输入文件流
     * @return
     */
    private boolean uploadFile(String ftpSavePath, String ftpSaveFileName, InputStream inputStream) {
        boolean flag = false;
        try {
            //第一次进来,将上传路径设置成相对路径
            if (ftpSavePath.startsWith("/")) {
                ftpSavePath = ftpSavePath.substring(1);
            }
            Log.e("FTP","上传文件的路径 :" + ftpSavePath);
            Log.e("FTP", "上传文件名 :" + ftpSaveFileName);
            //初始化FTP服务器
            connect();
            Log.e("FTP", "开始上传文件...");
            //设置文件类型,图片为二进制
            ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
            //创建文件路径
            if (!CreateDirecroty(ftpSavePath)) {
                return flag;
            }
            flag = ftpClient.storeFile(new String(ftpSaveFileName.getBytes("UTF-8"), "iso-8859-1"), inputStream);
            inputStream.close();
            ftpClient.logout();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(e.getMessage(), e+"");
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    Log.e(e.getMessage(), e+"");
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(e.getMessage(), e+"");
                }
            }
            Log.e("FTP","上传文件结束...结果 :" + (flag ? "成功" : "失败 "));
        }
        return flag;
    }

    private boolean CreateDirecroty(String remote) throws IOException {
        boolean success = true;
        String directory = remote + "/";
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(new String(directory))) {
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            //从第一个"/"索引之后开始得到下一个"/"的索引
            end = directory.indexOf("/", start);
            while (true) {
                Log.e("FTP","所在的目录 :" + ftpClient.printWorkingDirectory());
                String subDirectory = new String(remote.substring(start, end).getBytes("UTF-8"), "iso-8859-1");
                if (!existFile(subDirectory)) {
                    if (makeDirectory(subDirectory)) {
                        if (!changeWorkingDirectory(subDirectory)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    if (!changeWorkingDirectory(subDirectory)) {
                        return false;
                    }
                }

                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return success;
    }

    //判断ftp服务器文件是否存在
    private boolean existFile(String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }

    //改变目录路径
    private boolean changeWorkingDirectory(String directory) {
        boolean flag = true;
        try {
            flag = ftpClient.changeWorkingDirectory(directory);
            if (!flag) {
                Log.e( "FTP","所在的目录 : " + ftpClient.printWorkingDirectory() + " 进入下一级 " + directory + " 目录失败");
            } else {
                Log.e("FTP","进入目录成功，当前所在目录 :" + ftpClient.printWorkingDirectory());
            }
        } catch (IOException ioe) {
            Log.e(ioe.getMessage(), ioe+"");
        }
        return flag;
    }

    //创建目录
    private boolean makeDirectory(String dir) {
        boolean flag = true;
        try {
            flag = ftpClient.makeDirectory(dir);
            if (!flag) {
                Log.e("FTP","所在的目录 : " + ftpClient.printWorkingDirectory() + " 创建下一级 " + dir + " 目录失败 ");
            } else {
                Log.e("FTP","所在的目录 : " + ftpClient.printWorkingDirectory() + " 创建下一级 " + dir + " 目录成功 ");
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), e+"");
        }
        return flag;
    }
}
