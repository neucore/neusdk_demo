package com.neucore.neulink.storage;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.neucore.neulink.IStorage;
import com.neucore.neulink.cfg.ConfigContext;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DatesUtil;
import com.neucore.neulink.util.DeviceUtils;

public class OSSStorage implements IStorage {

    private static OSS getOSSClient() {
        OSSCredentialProvider credentialProvider =
                new OSSPlainTextAKSKCredentialProvider(ConfigContext.getInstance().getConfig("OSS.AccessKeyID") ,
                        ConfigContext.getInstance().getConfig("OSS.AccessKeySecret"));
        return new OSSClient(ContextHolder.getInstance().getContext(), ConfigContext.getInstance().getConfig("OSS.EndPoint"), credentialProvider);
    }

    /**
     * 上传方法
     *
     * @param objectKey 标识
     * @param path      需上传文件的路径
     * @return 外网访问的路径
     */
    private String upload(String objectKey, String path) throws ClientException, ServiceException {
        // 构造上传请求
        PutObjectRequest request =
                new PutObjectRequest(ConfigContext.getInstance().getConfig("OSS.BucketName"),
                        objectKey, path);
        //得到client
        OSS client = getOSSClient();
        //上传获取结果
        PutObjectResult result = client.putObject(request);
        //获取可访问的url
        String url = client.presignPublicObjectURL(ConfigContext.getInstance().getConfig("OSS.BucketName"), objectKey);
        //格式打印输出
        //MyLog.e(String.format("PublicObjectURL:%s", url));
        return url;

    }

    public String uploadBak(String path,String requestId,int index){
        String key = getObjectBakKey(DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext()),requestId,index);
        try {
            return upload(key, path);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 上传普通图片
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public String uploadQData(String path,String requestId,int index) {
        String key = getObjectDataKey(DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext()),requestId,index);
        try {
            return upload(key, path);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param path
     * @param requestId
     * @param index
     * @return
     */
    public String uploadLog(String path,String requestId,int index) {
        String key = getObjectLogKey(DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext()),requestId,index);
        try {
            return upload(key, path);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String uploadImage(String path,String requestId, int index) {
        int idx = path.lastIndexOf(".");
        String sufix = path.substring(idx);
        String key = getObjectImageKey(DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext()),requestId,index,sufix);
        try {
            return upload(key, path);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param requestId
     * @param index
     * @return logs/date/device_id/req_no/indexNum.zip
     */
    private String getObjectImageKey(String deviceId,String requestId,int index,String sufix){
        String dateString = DatesUtil.getDateString();
        return String.format("images/%s/%s/%s/%s.%s",dateString,deviceId,requestId, index,sufix);
    }

    /**
     *
     * @param requestId
     * @param index
     * @return logs/date/device_id/req_no/indexNum.zip
     */
    private String getObjectLogKey(String deviceId,String requestId,int index){
        String dateString = DatesUtil.getDateString();
        return String.format("logs/%s/%s/%s/%s.zip",dateString,deviceId,requestId, index);
    }
    /**
     *
     * @param requestId
     * @param index
     * @return datas/date/device_id/req_no/indexNum.json
     */
    private String getObjectDataKey(String deviceId,String requestId,int index) {
        String dateString = DatesUtil.getDateString();
        return String.format("datas/%s/%s/%s/%s.json", dateString,deviceId, requestId,index);
    }

    /**
     *
     * @param requestId
     * @param index
     * @return backup/date/device_id/req_no/indexNum.json
     */
    private String getObjectBakKey(String deviceId,String requestId,int index) {
        String dateString = DatesUtil.getDateString();
        return String.format("backup/%s/%s/%s/%s.json", dateString,deviceId, requestId,index);
    }
}
