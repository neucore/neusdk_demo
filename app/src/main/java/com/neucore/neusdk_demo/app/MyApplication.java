package com.neucore.neusdk_demo.app;

import android.app.Application;
import android.content.Context;

import com.neucore.neulink.IExtendCallback;
import com.neucore.neulink.IUserService;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neusdk_demo.db.UserService;
import com.neucore.neusdk_demo.neulink.extend.AlogUpgrdActionListener;
import com.neucore.neusdk_demo.neulink.extend.ApkUpgrdActionListener;
import com.neucore.neusdk_demo.neulink.extend.AwakenActionListener;
import com.neucore.neusdk_demo.neulink.extend.BackupActionListener;
import com.neucore.neusdk_demo.neulink.extend.HibrateActionListener;
import com.neucore.neusdk_demo.neulink.SampleConnector;
import com.neucore.neusdk_demo.neulink.extend.SampleFaceCheckListener;
import com.neucore.neusdk_demo.neulink.extend.SampleFaceListener;
import com.neucore.neusdk_demo.neulink.extend.SampleFaceQueryListener;

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
        SampleConnector register = new SampleConnector(this,callback,service);
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
            ListenerFactory.getInstance().setHibrateListener(new AlogUpgrdActionListener());

            /**
             * APK 升级扩展
             */
            ListenerFactory.getInstance().setHibrateListener(new ApkUpgrdActionListener());

            /**
             *
             */
            ListenerFactory.getInstance().setBackupListener(new BackupActionListener());

            /**
             * 图片&文件上传
             */
            //StorageFactory.getInstance().uploadBak("/sdcard/twocamera/icon/1593399670069.jpg", UUID.randomUUID().toString(),1);

            /**
             * 人脸上报
             */
            //new SampleFaceUpload().v10sample();

            /**
             * 人脸上报
             */
            //new SampleFaceUpload().v11sample();
        }
    };
}
