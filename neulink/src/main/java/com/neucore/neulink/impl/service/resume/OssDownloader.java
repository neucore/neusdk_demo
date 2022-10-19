package com.neucore.neulink.impl.service.resume;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.ResumableDownloadRequest;
import com.alibaba.sdk.android.oss.model.ResumableDownloadResult;
import com.blankj.utilcode.util.FileIOUtils;
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
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;

public class OssDownloader implements IDownloder, NeulinkConst {
    String TAG = NeulinkConst.TAG_PREFIX+"OssDownloader";
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
        String bucketName = data.get("bucketName",String.class);
        String ossEndpoint = data.get("ossEndpoint",String.class);
        String pathPrefix = data.get("pathPrefix",String.class);
        Long expiration = data.get("expiration",Long.class);
        String securityToken = data.get("securityToken",String.class);

        OSSCredentialProvider provider = new OSSFederationCredentialProvider() {
            @Override
            public OSSFederationToken getFederationToken() throws ClientException {
                return new OSSFederationToken(accessKeyId,accessKeySecret,securityToken,expiration);
            }
        };
        String objectKey = new URL(url).getPath();
        OSS ossClient = new OSSClient(ContextHolder.getInstance().getContext(),ossEndpoint, provider);
        try {
            String tmpPath = DeviceUtils.getTmpPath(context);
            String reqdir = tmpPath+File.separator+ RequestContext.getId();
            File toDir = new File(reqdir);
            toDir.mkdirs();

            if (objectKey.startsWith("/")) {
                objectKey = objectKey.substring(1);
            }
            String fileName = new File(objectKey).getName();
            String localFile = toDir.getPath() + "/" + fileName;


            GetObjectResult oriResult = ossClient.getObject(new GetObjectRequest(bucketName,objectKey));
            int oriStatusCode = oriResult.getStatusCode();
            if(oriStatusCode==200){
                long oriLength = oriResult.getContentLength();
                NeuLogUtils.iTag(TAG, "startDownload()..oriLength: " + oriLength);
                if (oriLength > 0) {
                    byte[] oriBuffer = new byte[(int) oriLength];
                    int oriReadCount = 0;
                    while (oriReadCount < oriLength) {
                        oriReadCount += oriResult.getObjectContent().read(oriBuffer, oriReadCount, (int) oriLength - oriReadCount);
                    }
                    boolean isSaveResult = FileIOUtils.writeFileFromBytesByStream(localFile, oriBuffer);
                    if (isSaveResult){
                        return new File(localFile);
                    }
                }
            }
        } catch (ClientException e) {
            NeuLogUtils.eTag(TAG,e.getMessage(),e);
        } catch (ServiceException e) {
            NeuLogUtils.eTag(TAG,e.getMessage(),e);
        }
        return null;
    }
}
