package com.neucore.neusdk_demo.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IExtendCallback;
import com.neucore.neulink.IStorage;
import com.neucore.neulink.IUserService;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.extend.StorageFactory;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neusdk_demo.db.UserService;
import com.neucore.neusdk_demo.neulink.extend.AlogUpgrdActionListener;
import com.neucore.neusdk_demo.neulink.extend.ApkUpgrdActionListener;
import com.neucore.neusdk_demo.neulink.extend.AwakenActionListener;
import com.neucore.neusdk_demo.neulink.extend.BackupActionListener;
import com.neucore.neusdk_demo.neulink.extend.CfgActionListener;
import com.neucore.neusdk_demo.neulink.extend.HibrateActionListener;
import com.neucore.neusdk_demo.neulink.SampleConnector;
import com.neucore.neusdk_demo.db.MessageService;
import com.neucore.neusdk_demo.neulink.extend.SampleFaceCheckListener;
import com.neucore.neusdk_demo.neulink.extend.SampleFaceListener;
import com.neucore.neusdk_demo.neulink.extend.SampleFaceQueryListener;

import java.util.Properties;
import java.util.UUID;

public class MyApplication extends Application
{
    private static MyApplication instance ;
    private String TAG = "MyApplication";
    public static MyApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        instance=this;
        ContextHolder.getInstance().setContext(this);
        /**
         * 用户人脸数据库服务
         */
        IUserService service = UserService.getInstance(this);
        /**
         * 集成Neulink
         */
        Properties extConfig = new Properties();

        /**
         * 配置扩展: key可以参考ConfigContext内的定义
         */
        extConfig.setProperty(ConfigContext.MQTT_SERVER,"tcp://47.118.59.46:1883");
        /**
         * 设备类型：根据APK功能决定进行配置
         * 设备类型【0:客流机；1:智能门禁；2:刷卡器；3:门磁；4:智能网关；5:智能中控;6:展示设备;7:人脸IPC;8:控制面板;9:车牌IPC】
         */
        extConfig.setProperty(ConfigContext.DEVICE_TYPE,"5");//默认为客流机器

        /**
         * FTP 实现
         */
        extConfig.setProperty(ConfigContext.STORAGE_TYPE,ConfigContext.STORAGE_MYFTP);

        extConfig.setProperty(ConfigContext.FTP_SERVER,"47.118.59.46");
        /**
         * ⚠️注意；http 通道启用时打开
         */
        //extConfig.setProperty(ConfigContext.UPLOAD_CHANNEL,"1");//end2cloud neulink 协议 切换至https通道
        //extConfig.setProperty(ConfigContext.REGIST_SERVER,"http://10.18.9.232:18093/v1/smrtlibs/neulink/regist");//设置http通道注册服务地址
        SampleConnector register = new SampleConnector(this,callback,service,extConfig);
        /**
         * FTP 测试
         */
        new Thread(){
            public void run(){
                int count = 0;
                while (true){
                    String requestId = UUID.randomUUID().toString();
                    int index = 0;
                    String path = "/sdcard/twocamera/572836928.jpg";//待上传的图片路径
                    IStorage storage = StorageFactory.getInstance();//目前只实现了OSS|FTP[]
                    //上传人脸图片至存储服务器上
                    String urlStr = storage.uploadImage(path,requestId,index);//返回图片FTP|OSS路径
                    if(urlStr==null){
                        break;
                    }
                    Log.i(TAG,String.format("上传成功 url=%s, count=%s",count,urlStr));
                    count++;
                    try {
                        int time = 0;
                        while(time==0){
                            time = Double.valueOf(Math.random()*10).intValue();
                        }
                        Log.i(TAG,"Sleep: "+(time)+" 分钟");
                        Thread.sleep(time*60*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.i(TAG,"上传失败 count: "+count);
            }
        }.start();
    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }

    private static int threadAlive = 0;

    public static int getThreadAlive() {
        return threadAlive;
    }

    public static void setThreadAlive(int alive) {
        MyApplication.threadAlive = alive;
    }

    /**
     * 外部扩展
     */
    IExtendCallback callback = new IExtendCallback() {
        @Override
        public void onCallBack() {

//            /**
//             * 设备序列号生成器；主要是为了扩展支持自己不想建立云服务，想使用neucore云服务
//             */
//            ListenerFactory.getInstance().setDeviceService(new IDeviceService() {
//                /**
//                 * 这个主要是为了支持非neucore生产的硬件；
//                 * 规则：必须客户代码开头：这个从neucore云注册开通后获取
//                 * @return
//                 */
//                @Override
//                public String getSN() {
//                    //@TODO 必须实现
//                    return null;
//                }
//            });
            /**
             * 配置扩展
             */
            ListenerFactory.getInstance().setCfgListener(new CfgActionListener());
            /**
             * 人脸下发 扩展
             */
            ListenerFactory.getInstance().setFaceListener(new SampleFaceListener());
            /**
             * 人脸比对 扩展
             */
            ListenerFactory.getInstance().setFaceCheckListener(new SampleFaceCheckListener());
            /**
             * 人脸查询 扩展
             */
            ListenerFactory.getInstance().setFaceQueryListener(new SampleFaceQueryListener());

            /**
             * 唤醒 扩展
             */
            ListenerFactory.getInstance().setAwakenListener(new AwakenActionListener());
            /**
             * 休眠 扩展
             */
            ListenerFactory.getInstance().setHibrateListener(new HibrateActionListener());

            /**
             * 算法升级 扩展
             */
            ListenerFactory.getInstance().setAlogListener("auth", new AlogUpgrdActionListener());

            /**
             * 固件$APK 升级扩展
             */
            ListenerFactory.getInstance().setFireware$ApkListener(new ApkUpgrdActionListener());

            /**
             * 备份实现
             */
            ListenerFactory.getInstance().setBackupListener(new BackupActionListener());

            /**
             * 图片&文件上传
             */
            //StorageFactory.getInstance().uploadBak("/sdcard/twocamera/icon/1593399670069.jpg", UUID.randomUUID().toString(),1);

            /**
             * 人脸上报
             * 启用
             */
            //new SampleFaceUpload().v12sample();

            /**
             * neulink消息线性处理存储服务
             */
            ServiceFactory.getInstance().setMessageService(new MessageService(getContext()));

            /**
             * 扩展其他Listener实现【auth】
             * XXX就是topic第四段；且首字母大写
             * eg:topic：rrpc/req/${dev_id}/auth/v1.0/${req_no}[/${md5}]；
             * 用auth替换下面的xxx;
             *
             * ListenerFactory.getInstance().setListener("auth", new ICmdListener() {
             *                 @Override
             *                 public Object doAction(NeulinkEvent event) {
             *                     return null;
             *                 }
             *             });
             *
             */
            ListenerFactory.getInstance().setAlogListener("auth",new ICmdListener<String>(){
                @Override
                public String doAction(NeulinkEvent event) {
                    return "hello";
                }
            });
        }
    };
}
