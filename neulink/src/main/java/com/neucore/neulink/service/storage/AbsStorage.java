package com.neucore.neulink.service.storage;

import com.neucore.neulink.cfg.ConfigContext;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;

import java.util.Calendar;

public abstract class AbsStorage {

    String bucketName = ConfigContext.getInstance().getConfig(ConfigContext.FTP_BUCKET_NAME);
    /**
     *
     * @param appName
     * @param deviceId
     * @return bucketName/appName/yyyy/mm/weekOfMonth/dayOfWeek/deviceId
     */
    public String getDateFolder(String appName,String deviceId) {

        String[] retVal = new String[7];

        Calendar calendar = Calendar.getInstance();
        retVal[0] = bucketName;

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

    /**
     *
     * @param requestId
     * @return
     */
    public String getObjectBakKey(String requestId){

        String path = getDateFolder("backups",getDeviceId());

        return String.format("%s/%s",path,requestId);
    }

    /**
     *
     * @param requestId
     * @return
     */
    public String getObjectQDataKey(String requestId){

        String path = getDateFolder("qdatas",getDeviceId());

        return String.format("%s/%s",path,requestId);
    }

    /**
     *
     * @param requestId
     * @return
     */
    public String getObjectLogKey(String requestId){

        String path = getDateFolder("logs",getDeviceId());

        return String.format("%s/%s",path,requestId);
    }

    /**
     *
     * @param requestId
     * @return backup/date/device_id/req_no/indexNum.json
     */
    public String getObjectImageKey(String requestId){

        String path = getDateFolder("images",getDeviceId());

        return String.format("%s/%s",path,requestId);
    }

    /**
     *
     * @param requestId
     * @return datas/date/device_id/req_no/indexNum.json
     */
    public String getObjectDataKey(String requestId) {

        String path = getDateFolder("datas",getDeviceId());

        return String.format("%s/%s",path, requestId);
    }

    private String getDeviceId(){
        return DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext());
    }

    /**
     * xdf/fdaf/bb/xx.zip
     * @param path
     * @return xx.zip
     */
    public String getName(String path){
        int idx = path.lastIndexOf("/");
        String name = path;
        if(idx!=-1){
            name = path.substring(idx+1);
        }
        return name;
    }

    public String uploadBak(String path, String requestId, int index) {

        String name = getName(path);

        String ftpSavePath = getObjectBakKey(requestId);

        return uploadFile(ftpSavePath,name,path);
    }

    public String uploadQData(String path, String requestId, int index) {

        String name = getName(path);

        String ftpSavePath = getObjectQDataKey(requestId);

        return uploadFile(ftpSavePath,name,path);
    }

    public String uploadLog(String path, String requestId, int index) {

        String name = getName(path);

        String ftpSavePath = getObjectLogKey(requestId);

        return uploadFile(ftpSavePath,name,path);
    }

    public String uploadImage(String path, String requestId, int index) {

        String name = getName(path);

        String ftpSavePath = getObjectImageKey(requestId);

        return uploadFile(ftpSavePath,name,path);

    }

    protected abstract String uploadFile(String ftpSavePath, String ftpSaveFileName, String originFileName);
}
