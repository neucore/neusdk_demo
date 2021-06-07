package com.neucore.neulink.service.storage;

import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;

import java.util.Calendar;

public abstract class AbsStorage {


    /**
     *
     * @param appName
     * @param deviceId
     * @return bucketName/appName/yyyy/mm/weekOfMonth/dayOfWeek/deviceId
     */
    protected String getDateFolder(String appName,String deviceId) {

        String[] retVal = new String[7];

        Calendar calendar = Calendar.getInstance();

        retVal[0] = getBucketName();

        retVal[1] = appName;

        int year = calendar.get(Calendar.YEAR);
        retVal[2] = String.valueOf(year);

        int month = calendar.get(Calendar.MONTH)+1;
        retVal[3] = String.valueOf(month);

        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        retVal[4] = String.valueOf(week);

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        retVal[5] = String.valueOf(day);

        return String.format("%s/%s/%s/%s/%s/%s/%s",retVal,deviceId);
    }

    protected String getDeviceId(){
        return DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext());
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

        String ftpSavePath = getObjectKey(appName,requestId);

        return uploadFile(ftpSavePath,name,path);

    }

    /**
     * @param appName
     * @param requestId
     * @return datas/date/device_id/req_no/indexNum.json
     */
    protected String getObjectKey(String appName,String requestId) {

        String path = getDateFolder(appName,getDeviceId());

        return String.format("%s/%s",path, requestId);
    }

    protected abstract String uploadFile(String ftpSavePath, String ftpSaveFileName, String originFileName);

    protected abstract String getBucketName();
}
