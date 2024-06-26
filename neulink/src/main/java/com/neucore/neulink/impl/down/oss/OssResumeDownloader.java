package com.neucore.neulink.impl.down.oss;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ResumableDownloadRequest;
import com.alibaba.sdk.android.oss.model.ResumableDownloadResult;
import com.neucore.neulink.IDownloadProgressListener;
import com.neucore.neulink.IDownloder;
import com.neucore.neulink.IFileService;
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
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;

public class OssResumeDownloader implements IDownloder, NeulinkConst {

    private static String TAG = NeulinkConst.TAG_PREFIX+"OssDownloader";

    private static AtomicInteger completedBlocks = new AtomicInteger(0);
    private static ExecutorService executorService = Executors.newFixedThreadPool(5);
    private DecimalFormat formater = new DecimalFormat("##.0");

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
        String ossStsAuthUrl = ConfigContext.getInstance().getConfig(ConfigContext.OSS_STS_AUTH_URL,String.format("https://dev.neucore.com/api/storage/v1/%s/authorization",ConfigContext.getInstance().getConfig(ConfigContext.SCOPEID)));
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


            ResumableDownloadRequest request = new ResumableDownloadRequest(bucketName, objectKey, localFile);
            request.setProgressListener(new OSSProgressCallback() {
                @Override
                public void onProgress(Object request, long currentSize, long totalSize) {

                    Double percent = currentSize*1.0/totalSize*1.0*100;

                    String progress = formater.format(percent);
                    if(progress.length()>3
                            && progress.endsWith("0.0")){
                        if(ObjectUtil.isNotEmpty(listener)){
                            listener.onDownload(percent);
                        }
                    }
                }
            });

            OSSAsyncTask<ResumableDownloadResult> task = ossClient.asyncResumableDownload(request, callback);

            task.waitUntilFinished();

            ClientException exception = callback.getClientException();
            if(ObjectUtil.isNotEmpty(exception)){
                throw exception;
            }
            return new File(localFile);

        }catch (ClientException e) {
            NeuLogUtils.iTag(TAG,"Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            NeuLogUtils.eTag(TAG,"Error Message: " + e.getMessage(),e);
        }
        return null;
    }

    MyOssCompletedCallback callback = new MyOssCompletedCallback();
}
