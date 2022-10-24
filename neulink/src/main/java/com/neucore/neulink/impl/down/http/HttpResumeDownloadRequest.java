package com.neucore.neulink.impl.down.http;

import android.content.Context;

import com.neucore.neulink.IDownloadProgressListener;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.impl.service.NeulinkSecurity;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.SSLSocketClient;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.util.ObjectUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpResumeDownloadRequest implements NeulinkConst {
    private static final String TAG = TAG_PREFIX+"HttpResumableDownloadRequest";
    private Context context;
    /* 已下载文件长度 */
    private Long downloadSize = 0L;
    /* 原始文件长度 */
    private long fileSize = 0l;
    /**
     * 线程数
     */
    private Integer taskNum;
    /* 线程数 */
    private HttpResumeDownloadTask[] tasks;
    /* 本地保存文件 */
    private File saveFile;
    /* 缓存各线程下载的长度*/
    private Map<Integer, Long> data = new ConcurrentHashMap<Integer, Long>();
    /* 每条线程下载的长度 */
    private long blockSize;
    private boolean mod;
    /* 下载路径  */
    private String reqNo;
    private String downloadUrl;

    protected final int CPU_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    protected final int MAX_CORE_POOL_SIZE = CPU_SIZE < 5 ? CPU_SIZE : 5;

    private Long BlockSize = 1024*1024L;

    private DecimalFormat formater = new DecimalFormat("##.0");

    private List<IDownloadProgressListener> listeners = new ArrayList<>();

    private HttpResumeDownloadRequestContext httpResumeDownloadRequestContext;

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
    static Request createRequest(String fileUrl, Map<String,String> headers){
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
    public void append(int size) {
        synchronized (downloadSize){
            downloadSize += size;
        }
    }

    public void store(int id,long size){
        httpResumeDownloadRequestContext.store(id,size);
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
             * 启动多线程下载
             */
            for (int id = 0; id < this.taskNum; id++) {//开启线程进行下载
                /**
                 * 历史下载长度
                 */
                long downLength = this.data.get(id);

                if(downLength < this.blockSize
                        && this.downloadSize < this.fileSize){//判断线程是否已经完成下载,否则继续下载

                    long downloaded = this.data.get(id);

                    this.tasks[id] = createTask(mod,id, taskNum, blockSize,downloaded);

                    this.tasks[id].setPriority(7);

                    this.tasks[id].start();
                }else{
                    this.tasks[id] = null;
                }
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
                for (int id = 0; id < taskNum; id++){
                    if (tasks[id] != null && !tasks[id].isFinish()) {//如果发现线程未完成下载
                        notFinish = true;//设置标志为下载没有完成
                        if(!tasks[id].isError()){
                            notError = true;
                        }
                    }
                }
                if(listeners!=null) {
                    /**
                     * 保证上报下载进度
                     */
                    if(notError){
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
                    for (int id = 0; id < taskNum; id++){
                        if(tasks[id] != null && !tasks[id].isFinish()){
                            finshed = false;
                            break;
                        }
                    }

                    if(finshed){
                        for (IDownloadProgressListener listener:listeners) {
                            listener.onFinished(saveFile);//通知目前已经下载完成的数据长度
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
     *
     * @param mod
     * @param id
     * @param blocks
     * @param blockSize
     * @param downloaded
     * @return
     */
    private HttpResumeDownloadTask createTask(boolean mod, int id, int blocks, long blockSize, long downloaded){

        HttpResumeDownloadTaskContext taskContext = createTaskContext(mod,id,blocks,blockSize,downloaded);

        HttpResumeDownloadTask httpResumeDownloadTask = new HttpResumeDownloadTask(id,this, downloadUrl, this.saveFile, taskContext);

        return httpResumeDownloadTask;
    }

    /**
     *
     * @param mod
     * @param id
     * @param blocks
     * @param blockSize
     * @return
     */
    private HttpResumeDownloadTaskContext createTaskContext(boolean mod, int id, int blocks, long blockSize, long downloaded){

        long startPos = id*blockSize;

        long endPos = 0;

        if(mod || id<blocks-1 ){
            endPos = (id+1)*blockSize;
        }
        else{
            endPos = fileSize-blockSize*(id+1);
        }
        return new HttpResumeDownloadTaskContext(startPos,endPos,downloaded);
    }

    /**
     * 获取文件名
     */
    private String getFileName(String urlStr, Response response) {
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

    private static void print(String msg){
        NeuLogUtils.iTag(TAG, msg);
    }

    /**
     *
     * @param context
     * @return
     * @throws IOException
     */
    public File start(Context context, String reqNo,String url, IDownloadProgressListener listener) throws Exception {
        this.reqNo = reqNo;
        String storeDir = DeviceUtils.getExternalCacheDir(ContextHolder.getInstance().getContext())+File.separator + reqNo;
        File fileSaveDir = new File(storeDir);
        if(!fileSaveDir.exists()) {
            fileSaveDir.mkdirs();
        }
        createTask(context,url,storeDir);
        addListener(listener);
        return start();
    }

    public void addListener(IDownloadProgressListener iDownloadProgressListener){
        listeners.add(iDownloadProgressListener);
    }

    public HttpResumeDownloadRequestContext getExecutionContext() {
        return httpResumeDownloadRequestContext;
    }

    /**
     * 初始化
     * @param context
     * @param url
     * @param fileSaveDir
     */
    private void createTask(Context context, String url, String fileSaveDir){
        Response response = null;
        try {

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

            this.context = context;
            this.downloadUrl = url;

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
                    || code == 200) {

                this.fileSize = Long.valueOf(response.header("Content-Length"));//根据响应获取文件大小

                if (this.fileSize <= 0) throw new RuntimeException("Unkown file size ");

                if(this.fileSize<1024*1024*5){
                    this.taskNum = Long.valueOf(fileSize/(1024*1024)).intValue();
                    if(this.taskNum==0){
                        this.taskNum = 1;
                    }
                }
                else{
                    this.taskNum = MAX_CORE_POOL_SIZE;
                }

                httpResumeDownloadRequestContext = new HttpResumeDownloadRequestContext(fileSaveDir,reqNo,this.taskNum);

                this.data = httpResumeDownloadRequestContext.init();

                this.tasks = new HttpResumeDownloadTask[this.taskNum];

                String filename = getFileName(url,response);//获取文件名称

                this.saveFile = new File(fileSaveDir, filename);//构建保存文件

                NeuLogUtils.iTag(TAG,"开始下载到："+ saveFile.getAbsolutePath());
                /**
                 * 获取历史下载进度
                 */
                if(this.data.size()==this.taskNum){//下面计算所有线程已经下载的数据长度

                    for (int id = 0; id < this.taskNum; id++) {
                        this.downloadSize += this.data.get(id);
                    }

                    print("已经下载的长度"+ this.downloadSize);
                }
                //计算每条线程下载的数据长度
                this.blockSize = this.fileSize / this.taskNum;
                this.mod = this.fileSize%this.taskNum ==0;

            }else{
                throw new RuntimeException("server no response "+code);
            }
        }
        catch (Exception e) {
            NeuLogUtils.eTag(TAG,"下载失败",e);
            print(e.toString());
            throw new RuntimeException(e);
        }
    }
}
