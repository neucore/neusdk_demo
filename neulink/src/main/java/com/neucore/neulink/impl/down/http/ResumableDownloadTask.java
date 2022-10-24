package com.neucore.neulink.impl.down.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import com.neucore.neulink.impl.service.NeulinkSecurity;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.NeulinkConst;

import okhttp3.Response;

public class ResumableDownloadTask extends Thread implements NeulinkConst {
    private static final String TAG = TAG_PREFIX+"DownloadThread";
    private File saveFile;
    private String downUrl;
    private DownloadTaskContext downloadTaskContext;
    /* 下载开始位置  */
    private int id = -1;
    private long downLength;
    private boolean finish = false,error = false;
    private ResumeDownloadRequest resumeDownloadRequest;
    private ExecutionContext context;

    public ResumableDownloadTask(ResumeDownloadRequest resumeDownloadRequest, ExecutionContext context, String url, File saveFile, DownloadTaskContext downloadTaskContext, long downLength, int id) {
        super("DownloadThread@"+ id);
        this.downUrl = url;
        this.saveFile = saveFile;
        this.downloadTaskContext = downloadTaskContext;
        this.resumeDownloadRequest = resumeDownloadRequest;
        this.context = context;
        this.id = id;
        this.downLength = downLength;
        NeuLogUtils.iTag(TAG,String.format("threadId=%s, downloadTaskContext=%s, downLength=%s", id, downloadTaskContext,downLength));
    }

    @Override
    public void run() {
        Response response = null;
        if(downLength < downloadTaskContext.getData()){//未下载完成
            int trys = 1;
            while(trys<=3){
                try {
                    NeuLogUtils.iTag(TAG,String.format("Thread=%s, trys=%s",this.id,trys));
                    download();
                    break;
                } catch (Exception e) {
                    if(trys>3){
                        NeuLogUtils.eTag(TAG,"Thread "+ this.id + " 下载失败",e);
                        this.downLength = -1;
                        print("Thread "+ this.id + ":"+ e);
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

        long startPos = downloadTaskContext.getStartPos() + downLength;//开始位置

        long endPos = downloadTaskContext.getEndPos();//结束位置

        NeuLogUtils.iTag(TAG,"线程 "+ id + "，开始下载的位置: " + startPos+ "，结束位置："+ endPos);

        headers.put("Range", "bytes=" + startPos + "-"+ endPos);//设置获取实体数据的范围

        Response response = null;
        try {
            response = ResumeDownloadRequest.getClient(5, 15).newCall(ResumeDownloadRequest.createRequest(downUrl, headers)).execute();
            InputStream inStream = response.body().byteStream();
            byte[] buffer = new byte[2048];
            int readed = 0;
            print("Thread " + this.id + " start download from position " + startPos);
            RandomAccessFile threadfile = new RandomAccessFile(this.saveFile, "rwd");
            threadfile.seek(startPos);
            while ((readed = inStream.read(buffer, 0, buffer.length)) != -1) {
                threadfile.write(buffer, 0, readed);
                downLength += readed;
                context.store(id,downLength);
                /**
                 * 更新下载进度
                 */
                resumeDownloadRequest.append(readed);
            }
            this.finish = true;
            print("Thread " + this.id + " download finish");
            threadfile.close();
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
    /**
     * 已经下载的内容大小
     * @return 如果返回值为-1,代表下载失败
     */
    public long getDownLength() {
        return downLength;
    }
}