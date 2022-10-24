package com.neucore.neulink.impl.down.http;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

import com.neucore.neulink.IDownloadProgressListener;
import com.neucore.neulink.IFileService;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.IResumeDownloader;
import com.neucore.neulink.impl.service.NeulinkSecurity;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.RequestContext;
import com.neucore.neulink.util.SSLSocketClient;

import cn.hutool.core.util.ObjectUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 文件下载器
 * OTAFileResumeDownloader loader = new OTAFileResumeDownloader(context, "http://browse.babasport.com/ejb3/ActivePort.exe",
 new File("D:\\androidsoft\\test"), 2);
 loader.getFileSize();//得到文件总大小
 try {
 loader.download(new DownloadProgressListener(){
 public void onDownloadSize(int size) {
 print("已经下载："+ size);
 }
 });
 } catch (Exception e) {
 e.printStackTrace();
 }
 */
public class HttpResumeDownloader implements IResumeDownloader, NeulinkConst{

    /**
     * 构建文件下载器
     */
    public HttpResumeDownloader() {
    }
    /**
     *
     * @param context
     * @return
     * @throws IOException
     */
    @Override
    public File start(Context context, String reqNo,String url, IDownloadProgressListener listener) throws Exception {
        HttpResumableDownloadRequest resumableDownloadRequest = new HttpResumableDownloadRequest();
        return resumableDownloadRequest.start(context,reqNo,url,listener);
    }
}