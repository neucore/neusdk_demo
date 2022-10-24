package com.neucore.neulink.impl.down.http;

import android.content.Context;

import com.neucore.neulink.IDownloadProgressListener;
import com.neucore.neulink.IResumeDownloader;
import com.neucore.neulink.NeulinkConst;

import java.io.File;
import java.io.IOException;

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