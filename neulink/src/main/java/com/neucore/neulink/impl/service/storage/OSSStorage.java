package com.neucore.neulink.impl.service.storage;

import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.neucore.neulink.IStorage;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.util.ContextHolder;

public class OSSStorage extends AbsStorage implements IStorage {

    private String TAG = "OSSStorage";

    private static OSS getOSSClient() {
        OSSCredentialProvider credentialProvider =
                new OSSPlainTextAKSKCredentialProvider(ConfigContext.getInstance().getConfig(ConfigContext.OSS_ACCESS_KEY_ID) ,
                        ConfigContext.getInstance().getConfig(ConfigContext.OSS_ACCESS_KEY_SECRET));
        ClientConfiguration clientConfiguration = ClientConfiguration.getDefaultConf();

        int connectTimeOut = ConfigContext.getInstance().getConfig(ConfigContext.CONN_TIME_OUT,15*1000);
        int readTimeOut = ConfigContext.getInstance().getConfig(ConfigContext.READ_TIME_OUT,15*1000);

        clientConfiguration.setConnectionTimeout(connectTimeOut);
        clientConfiguration.setSocketTimeout(readTimeOut);

        return new OSSClient(ContextHolder.getInstance().getContext(), ConfigContext.getInstance().getConfig(ConfigContext.OSS_END_POINT), credentialProvider, clientConfiguration);
    }

    /**
     * 上传文件
     *
     * @param savePath     oss服务保存地址  (不带文件名)
     * @param saveFileName 上传到ftp的文件名
     * @param originFileName  待上传文件的名称（绝对地址） *
     * @return
     */
    @Override
    protected String uploadFile(String savePath, String saveFileName, String originFileName){
        // 构造上传请求
        try {
            String objectKey = savePath + "/" + saveFileName;

            PutObjectRequest request =
                    new PutObjectRequest(ConfigContext.getInstance().getConfig(ConfigContext.OSS_BUCKET_NAME),
                            objectKey, originFileName);
            //得到client
            OSS client = getOSSClient();
            //上传获取结果
            PutObjectResult result = client.putObject(request);
            //获取可访问的url
            String url = client.presignPublicObjectURL(ConfigContext.getInstance().getConfig(ConfigContext.OSS_BUCKET_NAME), objectKey);
            //格式打印输出
            //MyLog.e(String.format("PublicObjectURL:%s", url));
            return url;
        }
        catch(Exception ex){
            Log.e(TAG,ex.getMessage());
        }
        return null;
    }

    @Override
    protected String getBucketName() {
        return ConfigContext.getInstance().getConfig(ConfigContext.OSS_BUCKET_NAME);
    }
}
