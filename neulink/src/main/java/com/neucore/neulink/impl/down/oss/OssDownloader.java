package com.neucore.neulink.impl.down.oss;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.neucore.neulink.IDownloadProgressListener;
import com.neucore.neulink.IDownloder;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.impl.service.NeulinkSecurity;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.NeuHttpHelper;
import com.neucore.neulink.util.RequestContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;

public class OssDownloader implements IDownloder, NeulinkConst {

    private static String TAG = NeulinkConst.TAG_PREFIX+"OssDownloader";

    private static AtomicInteger completedBlocks = new AtomicInteger(0);
    private static ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Override
    public File start(Context context, String reqNo, String url, IDownloadProgressListener listener) throws IOException {
        /**
         * 登录
         */
        ILoginCallback loginCallback = ServiceRegistry.getInstance().getLoginCallback();
        String token = loginCallback.login();
        if(token!=null){
            int index = token.indexOf(" ");
            if(index!=-1){
                token = token.substring(index+1);
            }
        }
        if(ObjectUtil.isNotEmpty(token)){
            NeulinkSecurity.getInstance().setToken(token);
        }

        HashMap<String,String> headers = new HashMap<>();
        headers.put("Authorization","bearer "+token);

        /**
         * 获取OSS临时授权
         */
        String ossStsAuthUrl = ConfigContext.getInstance().getConfig(ConfigContext.OSS_STS_AUTH_URL);
        Map<String,String> params = new HashMap<>();
        params.put("action","7");
        String response = NeuHttpHelper.post(ossStsAuthUrl,params,headers,10,60,3,null);
        JSONObject jsonObject = new JSONObject(response);
        JSONObject data = jsonObject.getJSONObject("data");
        String accessKeyId = data.get("accessKeyId",String.class);
        String accessKeySecret = data.get("accessKeySecret",String.class);
        final String bucketName = data.get("bucketName",String.class);
        String ossEndpoint = data.get("ossEndpoint",String.class);
        String pathPrefix = data.get("pathPrefix",String.class);
        Long expiration = data.get("expiration",Long.class);
        String securityToken = data.get("securityToken",String.class);

        String tmpPath = DeviceUtils.getTmpPath(context);
        String reqdir = tmpPath+File.separator+ RequestContext.getId();
        File toDir = new File(reqdir);
        toDir.mkdirs();

        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setConnectionTimeout(20 * 60 * 1000); //连接超时 默认15秒
        clientConfig.setSocketTimeout(20 * 60 * 1000); //socket超时 默认15秒
        clientConfig.setMaxConcurrentRequest(8);//最大并发请求计数 默认5个
        clientConfig.setMaxErrorRetry(0); //失败重试次数 默认2次
        OSSClient ossClient = new OSSClient(ContextHolder.getInstance().getContext(), ossEndpoint, new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken), clientConfig);
        try {

            String objectKey = new URL(url).getPath();
            if (objectKey.startsWith("/")) {
                objectKey = objectKey.substring(1);
            }

            String fileName = new File(objectKey).getName();
            String localFile = toDir.getPath() + "/" + fileName;

            GetObjectRequest oriGet = new GetObjectRequest(bucketName, objectKey);
            GetObjectResult oriResult = ossClient.getObject(oriGet);
            int oriStatusCode = oriResult.getStatusCode();
            NeuLogUtils.iTag(TAG, "startDownload()..oriResult.getStatusCode()..oriStatusCode: " + oriStatusCode);
            if (oriStatusCode == 200) {
                long oriLength = oriResult.getContentLength();
                NeuLogUtils.iTag(TAG, "startDownload()..oriLength: " + oriLength);
                if (oriLength > 0) {
                    byte[] oriBuffer = new byte[1024*1024];
                    int oriReadCount = 0;
                    FileOutputStream fileOutputStream = new FileOutputStream(new File(localFile));
                    InputStream inputStream = oriResult.getObjectContent();
                    while (oriReadCount < oriLength) {
                        int readed = inputStream.read(oriBuffer);
                        fileOutputStream.write(oriBuffer,0,readed);
                        oriReadCount += readed;
                    }
                    fileOutputStream.close();
                    return new File(localFile);
                }
            }
        }catch (ClientException e) {
            NeuLogUtils.iTag(TAG,"Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            NeuLogUtils.eTag(TAG,"Error Message: " + e.getMessage(),e);
        } catch (ServiceException e) {
            NeuLogUtils.eTag(TAG,"Error Message: " + e.getMessage(),e);
        }
        return null;
    }

}
