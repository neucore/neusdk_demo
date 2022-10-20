package com.neucore.neulink.impl.service.storage;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.neucore.neulink.IStorage;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.OssUtil;

import java.io.File;

import cn.hutool.json.JSONObject;

public class OSSStorage extends AbsStorage implements IStorage {

    private String TAG = TAG_PREFIX+"OSSStorage";

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

            JSONObject data = OssUtil.getOssConfig();
            String accessKeyId = data.get("accessKeyId",String.class);
            String accessKeySecret = data.get("accessKeySecret",String.class);
            String bucketName = data.get("bucketName",String.class);
            String ossEndpoint = data.get("ossEndpoint",String.class);
            String pathPrefix = data.get("pathPrefix",String.class);
            Long expiration = data.get("expiration",Long.class);
            String securityToken = data.get("securityToken",String.class);
            ClientConfiguration clientConfig = new ClientConfiguration();
            clientConfig.setConnectionTimeout(20 * 60 * 1000); //连接超时 默认15秒
            clientConfig.setSocketTimeout(20 * 60 * 1000); //socket超时 默认15秒
            clientConfig.setMaxConcurrentRequest(8);//最大并发请求计数 默认5个
            clientConfig.setMaxErrorRetry(0); //失败重试次数 默认2次
            OSSClient ossClient = new OSSClient(ContextHolder.getInstance().getContext(), ossEndpoint, new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken), clientConfig);

            PutObjectRequest request = new PutObjectRequest(bucketName,objectKey, originFileName);
            //上传获取结果
            PutObjectResult result = ossClient.putObject(request);

            //获取可访问的url
            String url = "https://"+ossEndpoint+"/"+objectKey;
            //格式打印输出
            //MyLogUtils.eTag(String.format("PublicObjectURL:%s", url));
            return url;
        }
        catch(Exception ex){
            NeuLogUtils.eTag(TAG,ex.getMessage());
        }
        return null;
    }

    @Override
    protected String getBucketName() {
        return ConfigContext.getInstance().getConfig(ConfigContext.OSS_BUCKET_NAME);
    }
}
