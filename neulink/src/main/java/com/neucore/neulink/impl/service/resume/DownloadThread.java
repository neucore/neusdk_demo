package com.neucore.neulink.impl.service.resume;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import okhttp3.Response;

public class DownloadThread extends Thread {
    private static final String TAG = "DownloadThread";
    private File saveFile;
    private String downUrl;
    private long block;
    /* 下载开始位置  */
    private int threadId = -1;
    private long downLength;
    private boolean finish = false;
    private FileDownloader downloader;

    public DownloadThread(FileDownloader downloader, String downUrl, File saveFile, long block, long downLength, int threadId) {
        this.downUrl = downUrl;
        this.saveFile = saveFile;
        this.block = block;
        this.downloader = downloader;
        this.threadId = threadId;
        this.downLength = downLength;
        Log.i(TAG,String.format("threadId=%s, block=%s, downLength=%s",threadId,block,downLength));
    }

    @Override
    public void run() {
        Response response = null;
        if(downLength < block){//未下载完成
            try {

                Map<String,String> headers = new HashMap<>();
                headers.put("Accept","image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, application/x-img, */*");
                headers.put("Accept-Language","zh-CN");
                headers.put("Referer",downUrl);
                headers.put("Charset","UTF-8");
                headers.put("User-Agent","Mozilla/4.0{Neulink} (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729;)");
                headers.put("Connection", "Keep-Alive");

                long startPos = block * (threadId - 1) + downLength;//开始位置
                long endPos = block * threadId -1;//结束位置
                Log.i(TAG,"线程 "+ threadId + "，开始下载的位置: " + startPos+ "，结束位置："+ endPos);
                headers.put("Range", "bytes=" + startPos + "-"+ endPos);//设置获取实体数据的范围

                response = FileDownloader.getClient(5,15).newCall(FileDownloader.createRequest(downUrl,headers)).execute();
                InputStream inStream = response.body().byteStream();
                byte[] buffer = new byte[2048];
                int readed = 0;
                print("Thread " + this.threadId + " start download from position "+ startPos);
                RandomAccessFile threadfile = new RandomAccessFile(this.saveFile, "rwd");
                threadfile.seek(startPos);
                while ((readed = inStream.read(buffer, 0, buffer.length)) != -1) {
                    threadfile.write(buffer, 0, readed);
                    downLength += readed;
                    downloader.update(this.threadId, downLength);
                    /**
                     * 更新下载进度
                     */
                    downloader.append(readed);
                }
                this.finish = true;
                print("Thread " + this.threadId + " download finish");
                threadfile.close();
                inStream.close();
            } catch (Exception e) {
                Log.e(TAG,"Thread "+ this.threadId + " 下载失败",e);
                this.downLength = -1;
                print("Thread "+ this.threadId+ ":"+ e);
            }
        }
    }
    private static void print(String msg){
        Log.i(TAG, msg);
    }
    /**
     * 下载是否完成
     * @return
     */
    public boolean isFinish() {
        return finish;
    }
    /**
     * 已经下载的内容大小
     * @return 如果返回值为-1,代表下载失败
     */
    public long getDownLength() {
        return downLength;
    }
}