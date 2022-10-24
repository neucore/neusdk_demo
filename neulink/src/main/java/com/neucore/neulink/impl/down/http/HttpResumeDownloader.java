package com.neucore.neulink.impl.down.http;

import android.content.Context;

import com.neucore.neulink.IDownloadProgressListener;
import com.neucore.neulink.IResumeDownloader;
import com.neucore.neulink.NeulinkConst;

import java.io.File;
import java.io.IOException;

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
        HttpResumeDownloadRequest httpResumeDownloadRequest = new HttpResumeDownloadRequest();
        return httpResumeDownloadRequest.start(context,reqNo,url,listener);
    }
}