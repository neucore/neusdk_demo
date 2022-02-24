package com.neucore.neulink.impl.service.storage;

import com.neucore.neulink.extend.ServiceFactory;

import java.util.Calendar;

public abstract class AbsStorage {


    /**
     *
     * @param appName
     * @param deviceId
     * @return bucketName/appName/yyyy/mm/weekOfMonth/dayOfWeek/deviceId
     */
    protected String getDateFolder(String appName,String deviceId,String requestId) {

        //String[] retVal = new String[7];

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);

        int month = calendar.get(Calendar.MONTH)+1;

        int week = calendar.get(Calendar.WEEK_OF_MONTH);

        int day = calendar.get(Calendar.DAY_OF_WEEK);

        return String.format("%s/%s/%s/%s/%s/%s/%s/%s",getBucketName(),appName,year,month,week,day,deviceId,requestId);
    }

    protected String getDeviceId(){
        return ServiceFactory.getInstance().getDeviceService().getExtSN();
    }

    /**
     * xdf/fdaf/bb/xx.zip
     * @param path
     * @return xx.zip
     */
    protected String getName(String path){
        int idx = path.lastIndexOf("/");
        String name = path;
        if(idx!=-1){
            name = path.substring(idx+1);
        }
        return name;
    }

    public String uploadBak(String path, String requestId, int index) {

        return upload("bkups",path,requestId,index);
    }

    public String uploadQData(String path, String requestId, int index) {

        return upload("qdatas",path,requestId,index);
    }

    public String uploadLog(String path, String requestId, int index) {

        return upload("rlogs",path,requestId,index);
    }

    public String uploadImage(String path, String requestId, int index) {

        return upload("images",path,requestId,index);
    }

    public String upload(String appName,String path, String requestId, int index) {

        String name = getName(path);

        String ftpSavePath = getDateFolder(appName,getDeviceId(),requestId);

        return uploadFile(ftpSavePath,name,path);

    }

    protected abstract String uploadFile(String ftpSavePath, String ftpSaveFileName, String originFileName);

    protected abstract String getBucketName();
}
