package com.neucore.neulink.impl.service.resume;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.util.NeuHttpHelper;

import cn.hutool.core.util.ObjectUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 文件下载器
 * FileDownloader loader = new FileDownloader(context, "http://browse.babasport.com/ejb3/ActivePort.exe",
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
public class FileDownloader implements NeulinkConst{
    private static final String TAG = TAG_PREFIX+"FileDownloader";
    private Context context;
    private FileService fileService = FileService.getInstance();
    /* 已下载文件长度 */
    private long downloadSize = 0;
    /* 原始文件长度 */
    private long fileSize = 0l;
    /* 线程数 */
    private DownloadThread[] threads;
    /* 本地保存文件 */
    private File saveFile;
    /* 缓存各线程下载的长度*/
    private Map<Integer, Long> data = new ConcurrentHashMap<Integer, Long>();
    /* 每条线程下载的长度 */
    private long block;
    /* 下载路径  */
    private String downloadUrl;
    static OkHttpClient getClient(int connTimeout, int readTimeout){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(connTimeout, TimeUnit.MINUTES)//设置连接超时时间
                .readTimeout(readTimeout, TimeUnit.MINUTES)//设置读取超时时间
                .writeTimeout(readTimeout,TimeUnit.MINUTES)
                .build();
        return okHttpClient;
    }
    static Request createRequest(String fileUrl,Map<String,String> headers){
        //第二步构建Request对象
        Request.Builder builder = new Request.Builder()
                .url(fileUrl)
                .get();
        String[] headerKeys = new String[headers.size()];
        headers.keySet().toArray(headerKeys);
        for (String key:headerKeys) {
            builder.addHeader(key,headers.get(key));
        }
        return builder.build();
    }
    /**
     * 获取线程数
     */
    public int getThreadSize() {
        return threads.length;
    }
    /**
     * 获取文件大小
     * @return
     */
    public long getFileSize() {
        return fileSize;
    }
    /**
     * 累计已下载大小
     * @param size
     */
    protected synchronized void append(int size) {
        downloadSize += size;
        if(downloadSize>=fileSize){
            Log.i(TAG,String.format("fileSize=%s, downloadSize=%s",fileSize,downloadSize));
            downloadSize = fileSize;
        }
    }
    /**
     * 更新指定线程最后下载的位置
     * @param threadId 线程id
     * @param pos 最后下载的位置
     */
    protected synchronized void update(int threadId, long pos) {
        this.fileService.update(this.downloadUrl, threadId,pos);
    }

    /**
     * 构建文件下载器
     * @param downloadUrl 下载路径
     * @param fileSaveDir 文件保存目录
     * @param threadNum 下载线程数
     */
    public FileDownloader(Context context, String downloadUrl, File fileSaveDir, int threadNum) {
        Response response = null;
        try {
            this.context = context;
            this.downloadUrl = downloadUrl;
            this.threads = new DownloadThread[threadNum];
            if(!fileSaveDir.exists()) {
                fileSaveDir.mkdirs();
            }
            Map<String,String> headers = new HashMap<>();
            headers.put("Accept","image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, application/x-img, */*");
            headers.put("Accept-Language","zh-CN");
            headers.put("Referer",downloadUrl);
            headers.put("Charset","UTF-8");
            headers.put("User-Agent","Mozilla/4.0{Neulink} (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729;)");
            headers.put("Connection", "Keep-Alive");
            response = getClient(5,15).newCall(createRequest(downloadUrl,headers)).execute();
            int code = response.code();
            Log.i(TAG,code+"");
            if (code == 206||code == 200) {
                this.fileSize = Long.valueOf(response.header("Content-Length"));//根据响应获取文件大小
                if (this.fileSize <= 0) throw new RuntimeException("Unkown file size ");

                String filename = getFileName(response);//获取文件名称
                this.saveFile = new File(fileSaveDir, filename);//构建保存文件
                Log.i(TAG,"开始下载到："+saveFile.getAbsolutePath());
                /**
                 * 获取历史下载进度
                 */
                Map<Integer, Long> logdata = fileService.getData(downloadUrl);//获取下载记录
                Log.i(TAG,"历史下载记录 "+logdata);
                if(logdata.size()>0){//如果存在下载记录
                    for(Map.Entry<Integer, Long> entry : logdata.entrySet())
                        data.put(entry.getKey(), entry.getValue());//把各条线程已经下载的数据长度放入data中
                }
                if(this.data.size()==this.threads.length){//下面计算所有线程已经下载的数据长度
                    for (int i = 0; i < this.threads.length; i++) {
                        this.downloadSize += this.data.get(i+1);
                    }
                    print("已经下载的长度"+ this.downloadSize);
                }
                //计算每条线程下载的数据长度
                this.block = (this.fileSize % this.threads.length)==0? this.fileSize / this.threads.length : this.fileSize / this.threads.length + 1;
            }else{
                throw new RuntimeException("server no response "+code);
            }
        } catch (Exception e) {
            Log.e(TAG,"下载失败",e);
            print(e.toString());
            throw new RuntimeException(e);
        }
    }

    /**
     *  开始下载文件
     * @param listener 监听下载数量的变化,如果不需要了解实时下载的数量,可以设置为null
     * @return 已下载文件大小
     * @throws Exception
     */
    public long download(DownloadProgressListener listener) throws Exception{
        try {
            RandomAccessFile randOut = new RandomAccessFile(this.saveFile, "rw");
            if(this.fileSize>0) {
                randOut.setLength(this.fileSize);
            }
            randOut.close();
            if(this.data.size() != this.threads.length){
                this.data.clear();
                for (int i = 0; i < this.threads.length; i++) {
                    this.data.put(i+1, 0l);//初始化每条线程已经下载的数据长度为0
                }
            }
            for (int i = 0; i < this.threads.length; i++) {//开启线程进行下载
                /**
                 * 历史下载长度
                 */
                long downLength = this.data.get(i+1);
                if(downLength < this.block && this.downloadSize<this.fileSize){//判断线程是否已经完成下载,否则继续下载
                    long downloaded = this.data.get(i+1);
                    this.threads[i] = new DownloadThread(this, downloadUrl, this.saveFile, this.block,downloaded , i+1);
                    this.threads[i].setPriority(7);
                    this.threads[i].start();
                }else{
                    this.threads[i] = null;
                }
            }
            this.fileService.save(this.downloadUrl, this.data);

            boolean notError = true;
            boolean notFinish = true;//下载未完成
            while (notFinish && notError) {// 循环判断所有线程是否完成下载
                Thread.sleep(900);
                notFinish = false;//假定全部线程下载完成
                notError = false;//假设全部遇到错误
                for (int i = 0; i < this.threads.length; i++){
                    if (this.threads[i] != null && !this.threads[i].isFinish()) {//如果发现线程未完成下载
                        notFinish = true;//设置标志为下载没有完成
                        if(!this.threads[i].isError()){
                            notError = true;
                        }
                        if(this.threads[i].getDownLength() == -1){//如果下载失败,再重新下载
                            this.threads[i] = new DownloadThread(this, downloadUrl, this.saveFile, this.block, this.data.get(i+1), i+1);
                            this.threads[i].setPriority(7);
                            this.threads[i].start();
                        }
                    }
                }
                if(listener!=null) {
                    /**
                     * 保证上报100%
                     */
                    if(notError){
                        listener.onDownloadSize(this.downloadSize);//通知目前已经下载完成的数据长度
                    }
                }
            }
            boolean finshed = true;
            for (int i = 0; i < this.threads.length; i++){
                if(!this.threads[i].isFinish()){
                    finshed = false;
                    break;
                }
            }
            if(finshed){
                fileService.delete(this.downloadUrl);
            }

        } catch (Exception e) {
            print(e.toString());
            throw e;
        }
        return this.downloadSize;
    }

    /**
     * 获取文件名
     */
    private String getFileName(Response response) {
        String filename = response.header("Content-Disposition");
        if(ObjectUtil.isNotEmpty(filename)){
            Matcher m = Pattern.compile(".*filename=(.*)").matcher(filename);
            if(m.find()) {
                return m.group(1);
            }
        }
        return UUID.randomUUID()+ ".tmp";//默认取一个文件名
    }
    /**
     * 获取Http响应头字段
     * @param http
     * @return
     */
    public static Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
        Map<String, String> header = new LinkedHashMap<String, String>();
        for (int i = 0;; i++) {
            String mine = http.getHeaderField(i);
            if (mine == null) break;
            header.put(http.getHeaderFieldKey(i), mine);
        }
        return header;
    }
    /**
     * 打印Http头字段
     * @param http
     */
    public static void printResponseHeader(HttpURLConnection http){
        Map<String, String> header = getHttpResponseHeader(http);
        for(Map.Entry<String, String> entry : header.entrySet()){
            String key = entry.getKey()!=null ? entry.getKey()+ ":" : "";
            print(key+ entry.getValue());
        }
    }

    private static void print(String msg){
        Log.i(TAG, msg);
    }

    public File getSaveFile() {
        return saveFile;
    }
}