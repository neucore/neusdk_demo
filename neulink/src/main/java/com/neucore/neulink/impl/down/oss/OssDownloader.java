package com.neucore.neulink.impl.down.oss;

import android.content.Context;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.DownloadFileRequest;
import com.aliyun.oss.model.DownloadFileResult;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.neucore.neulink.IDownloadProgressListener;
import com.neucore.neulink.IDownloder;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.impl.service.NeulinkSecurity;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.NeuHttpHelper;
import com.neucore.neulink.util.RequestContext;

import java.io.File;
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

        OSS ossClient = new OSSClientBuilder().build(ossEndpoint, accessKeyId, accessKeySecret);

        try {

            String objectKey = new URL(url).getPath();
            if (objectKey.startsWith("/")) {
                objectKey = objectKey.substring(1);
            }

            String fileName = new File(objectKey).getName();
            String localFile = toDir.getPath() + "/" + fileName;


            ObjectMetadata metadata = ossClient.getObjectMetadata(bucketName, objectKey);
            long objectSize = metadata.getContentLength();
            RandomAccessFile raf = new RandomAccessFile(localFile, "rw");
            raf.setLength(objectSize);
            raf.close();

            /*
             * Calculate how many blocks to be divided
             */
            final long blockSize = 5 * 1024 * 1024L;   // 5MB
            int blockCount = (int) (objectSize / blockSize);
            if (objectSize % blockSize != 0) {
                blockCount++;
            }

            NeuLogUtils.iTag(TAG,"Total blocks count " + blockCount + "\n");

            /*
             * Download the object concurrently
             */
            NeuLogUtils.iTag(TAG,"Start to download " + objectKey + "\n");
            for (int i = 0; i < blockCount; i++) {
                long startPos = i * blockSize;
                long endPos = (i + 1 == blockCount) ? objectSize - 1 : (i + 1) * blockSize;
                executorService.execute(new BlockFetcher(ossClient,bucketName,objectKey,startPos, endPos, i + 1,localFile));
            }

            DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, objectKey);
            // Sets the local file to download to
            downloadFileRequest.setDownloadFile(localFile);
            // Sets the concurrent task thread count 5. By default it's 1.
            downloadFileRequest.setTaskNum(5);
            // Sets the part size, by default it's 100K.
            downloadFileRequest.setPartSize(1024 * 1024 * 1);
            // Enable checkpoint. By default it's false.
            downloadFileRequest.setEnableCheckpoint(true);

            DownloadFileResult downloadResult = ossClient.downloadFile(downloadFileRequest);

            ObjectMetadata objectMetadata = downloadResult.getObjectMetadata();
            if(objectMetadata.isRestoreCompleted()){
                return new File(localFile);
            }

        } catch (OSSException oe) {
            NeuLogUtils.iTag(TAG,"Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            NeuLogUtils.iTag(TAG,"Error Message: " + oe.getErrorMessage());
            NeuLogUtils.iTag(TAG,"Error Code:       " + oe.getErrorCode());
            NeuLogUtils.iTag(TAG,"Request ID:      " + oe.getRequestId());
            NeuLogUtils.iTag(TAG,"Host ID:           " + oe.getHostId());
        } catch (ClientException ce) {
            NeuLogUtils.iTag(TAG,"Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            NeuLogUtils.iTag(TAG,"Error Message: " + ce.getMessage());
        } catch (Throwable e) {
            NeuLogUtils.eTag(TAG,e.getMessage(),e);
        } finally {
            ossClient.shutdown();
        }
        return null;
    }

    private static class BlockFetcher implements Runnable {

        private OSS ossClient;
        private String bucketName;
        private String objectKey;
        private long startPos;
        private long endPos;
        private int blockNumber;
        private String localFilePath;


        public BlockFetcher(OSS ossClient, String bucketName, String objectKey, long startPos, long endPos, int blockNumber, String localFilePath) {
            this.ossClient = ossClient;
            this.bucketName = bucketName;
            this.objectKey = objectKey;
            this.startPos = startPos;
            this.endPos = endPos;
            this.blockNumber = blockNumber;
            this.localFilePath = localFilePath;
        }

        @Override
        public void run() {
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile(localFilePath, "rw");
                raf.seek(startPos);

                GetObjectRequest request = new GetObjectRequest(bucketName, objectKey).withRange(startPos, endPos);
                request.addHeader("x-oss-range-behavior", "standard");
                OSSObject object = ossClient.getObject(request);
                InputStream objectContent = object.getObjectContent();
                try {
                    byte[] buf = new byte[4096];
                    int bytesRead = 0;
                    while ((bytesRead = objectContent.read(buf)) != -1) {
                        raf.write(buf, 0, bytesRead);
                    }
                    completedBlocks.incrementAndGet();
                    NeuLogUtils.iTag(TAG,"Block#" + blockNumber + " done\n");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    objectContent.close();
                }
            } catch (Exception e) {
                NeuLogUtils.eTag(TAG,e.getMessage(),e);
            } finally {
                if (raf != null) {
                    try {
                        raf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
