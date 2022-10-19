package com.neucore.neulink.impl.service.resume;

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
import com.neucore.neulink.IResumeDownloader;
import com.neucore.neulink.impl.cmd.upd.UgrdeCmd;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
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
    private static final String TAG = TAG_PREFIX+"FileDownloader";
    private Context context;
    private IFileService fileService = ServiceRegistry.getInstance().getFileService();
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
    private String reqNo;
    private String downloadUrl;

    private Long BlockSize = 1024*1024L;

    private List<IDownloadProgressListener> listeners = new ArrayList<>();
    static OkHttpClient getClient(int connTimeout, int readTimeout){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(connTimeout, TimeUnit.MINUTES)//设置连接超时时间
                .readTimeout(readTimeout, TimeUnit.MINUTES)//设置读取超时时间
                .writeTimeout(readTimeout,TimeUnit.MINUTES)
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.trustManager)
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier()) //支持HTTPS请求，跳过证书验证
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
     * 构建文件下载器
     */
    public HttpResumeDownloader() {
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
    public synchronized void append(int size) {
        downloadSize += size;
        if(downloadSize>=fileSize){
            NeuLogUtils.iTag(TAG,String.format("fileSize=%s, downloadSize=%s",fileSize,downloadSize));
            downloadSize = fileSize;
        }
    }
    /**
     * 更新指定线程最后下载的位置
     * @param threadId 线程id
     * @param pos 最后下载的位置
     */
    public synchronized void update(int threadId, long pos) {
        if(ObjectUtil.isNotEmpty(fileService)){
            this.fileService.update(this.downloadUrl, threadId,pos);
        }
    }
    /**
     *  开始下载文件
     * @return 已下载文件大小
     * @throws Exception
     */
    private File start() throws Exception{
        try {
            RandomAccessFile randOut = new RandomAccessFile(this.saveFile, "rw");
            if(this.fileSize>0) {
                randOut.setLength(this.fileSize);
            }
            randOut.close();
            /**
             * 初始化多线程状态
             */
            if(this.data.size() != this.threads.length){
                this.data.clear();
                for (int i = 0; i < this.threads.length; i++) {
                    this.data.put(i+1, 0l);//初始化每条线程已经下载的数据长度为0
                }
            }
            /**
             * 启动多线程下载
             */
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
            if(ObjectUtil.isNotEmpty(fileService)){
                this.fileService.save(this.downloadUrl, this.data);
            }

            boolean notError = true;
            boolean notFinish = true;//下载未完成
            while (notFinish && notError) {// 循环判断所有线程是否完成下载
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                notFinish = false;//假定全部线程下载完成
                notError = false;//假设全部遇到错误
                for (int i = 0; i < threads.length; i++){
                    if (threads[i] != null && !threads[i].isFinish()) {//如果发现线程未完成下载
                        notFinish = true;//设置标志为下载没有完成
                        if(!threads[i].isError()){
                            notError = true;
                        }
                        if(threads[i].getDownLength() == -1){//如果下载失败,再重新下载
                            threads[i] = new DownloadThread(HttpResumeDownloader.this, downloadUrl, saveFile, block, data.get(i+1), i+1);
                            threads[i].setPriority(7);
                            threads[i].start();
                        }
                    }
                }
                if(listeners!=null) {
                    /**
                     * 保证上报下载进度
                     */
                    if(notError){
                        DecimalFormat formater = new DecimalFormat("##.0");
                        String progress = formater.format(downloadSize*1.0/fileSize*1.0*100);
                        if(progress.length()>3
                                && progress.endsWith("0.0")){

                            long total = getFileSize();
                            Double percent = downloadSize*1.0/total*1.0*100;
                            NeuLogUtils.iTag(TAG,reqNo+ " progress: "+progress);

                            for (IDownloadProgressListener listener:listeners) {

                                listener.onDownload(percent);//通知目前已经下载完成的数据长度
                            }
                        }
                    }

                    boolean finshed = true;
                    for (int i = 0; i < threads.length; i++){
                        if(threads[i] != null && !threads[i].isFinish()){
                            finshed = false;
                            break;
                        }
                    }

                    if(finshed){
                        for (IDownloadProgressListener listener:listeners) {
                            listener.onFinished(saveFile);//通知目前已经下载完成的数据长度
                        }
                        if(ObjectUtil.isNotEmpty(fileService)){
                            fileService.delete(downloadUrl);
                        }
                    }
                }
            }
        } catch (Exception e) {
            print(e.toString());
            throw e;
        }
        return saveFile;
    }

    /**
     * 获取文件名
     */
    private String getFileName(String urlStr,Response response) {
        String filename = response.header("Content-Disposition");
        if(ObjectUtil.isNotEmpty(filename)){
            Matcher m = Pattern.compile(".*filename=(.*)").matcher(filename);
            if(m.find()) {
                return m.group(1);
            }
            return UUID.randomUUID()+ ".tmp";//默认取一个文件名
        }
        else{
            filename = new File(urlStr).getName();
            return filename;
        }
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

    private static void print(String msg){
        NeuLogUtils.iTag(TAG, msg);
    }

    /**
     *
     * @param context
     * @return
     * @throws IOException
     */
    @Override
    public File start(Context context, String reqNo,String url, IDownloadProgressListener listener) throws Exception {
        this.reqNo = reqNo;
        String storeDir = DeviceUtils.getExternalCacheDir(ContextHolder.getInstance().getContext())+File.separator + reqNo;
        init(context,url,new File(storeDir));
        addListener(listener);
        return start();
    }
    
    public void addListener(IDownloadProgressListener iDownloadProgressListener){
        listeners.add(iDownloadProgressListener);
    }

    /**
     * 初始化
     * @param context
     * @param url
     * @param fileSaveDir
     */
    private void init(Context context, String url, File fileSaveDir){
        Response response = null;
        try {
            this.context = context;
            this.downloadUrl = url;
            if(!fileSaveDir.exists()) {
                fileSaveDir.mkdirs();
            }
            Map<String,String> headers = new HashMap<>();
            headers.put("Accept","image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, application/x-img, */*");
            headers.put("Accept-Language","zh-CN");
            headers.put("Referer",url);
            headers.put("Charset","UTF-8");
            headers.put("User-Agent","Mozilla/4.0{Neulink} (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729;)");
            headers.put("Connection", "Keep-Alive");
            response = getClient(5,15).newCall(createRequest(url,headers)).execute();
            int code = response.code();
            NeuLogUtils.iTag(TAG,code+"");
            if (code == 206
                    ||code == 200) {
                this.fileSize = Long.valueOf(response.header("Content-Length"));//根据响应获取文件大小
                if (this.fileSize <= 0) throw new RuntimeException("Unkown file size ");
                Long threads = fileSize / BlockSize;
                Long mod = fileSize%BlockSize;
                if(mod>0){
                    threads+=1;
                }
                Integer threadNum = threads.intValue();
                this.threads = new DownloadThread[threadNum];

                String filename = getFileName(url,response);//获取文件名称

                this.saveFile = new File(fileSaveDir, filename);//构建保存文件
                NeuLogUtils.iTag(TAG,"开始下载到："+saveFile.getAbsolutePath());
                /**
                 * 获取历史下载进度
                 */
                if(ObjectUtil.isNotEmpty(fileService)){
                    Map<Integer, Long> logdata = fileService.getData(url);//获取下载记录
                    NeuLogUtils.iTag(TAG,"历史下载记录 "+logdata);
                    if(logdata.size()>0){//如果存在下载记录
                        for(Map.Entry<Integer, Long> entry : logdata.entrySet())
                            data.put(entry.getKey(), entry.getValue());//把各条线程已经下载的数据长度放入data中
                    }
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
        } catch (RuntimeException e){
            NeuLogUtils.eTag(TAG,"下载失败",e);
            print(e.toString());
            throw e;
        }
        catch (Exception e) {
            NeuLogUtils.eTag(TAG,"下载失败",e);
            print(e.toString());
            throw new RuntimeException(e);
        }
    }
}