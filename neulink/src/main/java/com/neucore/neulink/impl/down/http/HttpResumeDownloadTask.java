package com.neucore.neulink.impl.down.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.neucore.neulink.impl.service.NeulinkSecurity;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.NeulinkConst;

import okhttp3.Response;

public class HttpResumeDownloadTask extends Thread implements NeulinkConst {
    private static final String TAG = TAG_PREFIX+"DownloadTask";

    private HttpResumeDownloadRequest httpResumeDownloadRequest;
    private HttpResumeDownloadRequestContext httpResumeDownloadRequestContext;
    private File saveFile;
    private String downUrl;
    private HttpResumeDownloadTaskContext httpResumeDownloadTaskContext;
    private DecimalFormat formater = new DecimalFormat("##.0");
    private long downloaded;
    /* 下载开始位置  */
    private int id = -1;
    private boolean finish = false,error = false;


    public HttpResumeDownloadTask(int id, HttpResumeDownloadRequest httpResumeDownloadRequest, String url, File saveFile, HttpResumeDownloadTaskContext httpResumeDownloadTaskContext) {
        super("DownloadTask@"+ id);
        this.id = id;
        this.httpResumeDownloadRequest = httpResumeDownloadRequest;
        this.downUrl = url;
        this.saveFile = saveFile;
        this.httpResumeDownloadTaskContext = httpResumeDownloadTaskContext;
        this.downloaded = httpResumeDownloadTaskContext.getDownloaded();
        NeuLogUtils.iTag(TAG,String.format("TaskId=%s, taskContext=%s", id, httpResumeDownloadTaskContext));
    }

    @Override
    public void run() {
        Response response = null;

        if(downloaded < httpResumeDownloadTaskContext.getSize()){//未下载完成
            int trys = 1;
            while(trys<=3){
                try {
                    NeuLogUtils.iTag(TAG,String.format("Task=%s, trys=%s",this.id,trys));
                    download();
                    break;
                } catch (Exception e) {
                    if(trys>3){
                        NeuLogUtils.eTag(TAG,"Task "+ this.id + " 下载失败",e);
                        this.downloaded = -1;
                        print("Task "+ this.id + ":"+ e);
                        this.error = true;
                    }
                    else {
                        try {
                            Thread.sleep(3000);
                        }
                        catch (Exception ex){}
                        trys++;
                    }
                }
            }
        }
    }

    private void download(){

        Map<String,String> headers = new HashMap<>();

        String token = NeulinkSecurity.getInstance().getToken();
        headers.put("Authorization","bearer "+token);
        headers.put("Accept","image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, application/x-img, */*");
        headers.put("Accept-Language","zh-CN");
        headers.put("Referer",downUrl);
        headers.put("Charset","UTF-8");
        headers.put("User-Agent","Mozilla/4.0{Neulink} (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729;)");
        headers.put("Connection", "Keep-Alive");

        long startPos = httpResumeDownloadTaskContext.getStartPos() + downloaded;//开始位置

        long endPos = httpResumeDownloadTaskContext.getEndPos();//结束位置

        NeuLogUtils.iTag(TAG,"Task "+ id + "，开始下载的位置: " + startPos+ "，结束位置："+ endPos);

        headers.put("Range", "bytes=" + startPos + "-"+ endPos);//设置获取实体数据的范围

        Response response = null;
        try {
            response = HttpResumeDownloadRequest.getClient(5, 15).newCall(HttpResumeDownloadRequest.createRequest(downUrl, headers)).execute();
            InputStream inStream = response.body().byteStream();
            byte[] buffer = new byte[2048];
            int readed = 0;

            print("Task " + this.id + " start download from position " + startPos);

            RandomAccessFile taskfile = new RandomAccessFile(this.saveFile, "rwd");

            taskfile.seek(startPos);

            while ((readed = inStream.read(buffer, 0, buffer.length)) != -1) {

                taskfile.write(buffer, 0, readed);

                downloaded += readed;

                if(downloaded<=httpResumeDownloadTaskContext.getSize()){
                    httpResumeDownloadRequest.store(id,downloaded);
                    /**
                     * 更新下载进度
                     */
                    httpResumeDownloadRequest.append(readed);
                }
            }
            this.finish = true;
            NeuLogUtils.iTag(TAG,String.format("TaskId=%s finished, taskContext=%s, fileSize=%s, executed=%s", id, httpResumeDownloadTaskContext,httpResumeDownloadRequest.getFileSize(),downloaded));
            taskfile.close();
            inStream.close();
        }
        catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    private static void print(String msg){
        NeuLogUtils.iTag(TAG, msg);
    }
    /**
     * 下载是否完成
     * @return
     */
    public boolean isFinish() {
        return finish;
    }

    public boolean isError(){
        return error;
    }
}