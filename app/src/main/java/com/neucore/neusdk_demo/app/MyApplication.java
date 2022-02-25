package com.neucore.neusdk_demo.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IExtendCallback;
import com.neucore.neulink.IStorage;
import com.neucore.neulink.IUserService;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.cmd.msg.DeviceInfo;
import com.neucore.neulink.extend.ILoginCallback;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.extend.SampleConnector;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.extend.StorageFactory;
import com.neucore.neulink.impl.NeulinkProcessorFactory;
import com.neucore.neulink.impl.service.device.DeviceInfoBuilder;
import com.neucore.neulink.impl.service.device.IDeviceService;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neusdk_demo.db.MessageService;
import com.neucore.neusdk_demo.db.UserService;
import com.neucore.neusdk_demo.neulink.extend.AlogUpgrdActionListener;
import com.neucore.neusdk_demo.neulink.extend.ApkUpgrdActionListener;
import com.neucore.neusdk_demo.neulink.extend.AwakenActionListener;
import com.neucore.neusdk_demo.neulink.extend.BackupActionListener;
import com.neucore.neusdk_demo.neulink.extend.CfgActionListener;
import com.neucore.neusdk_demo.neulink.extend.HibrateActionListener;
import com.neucore.neusdk_demo.neulink.extend.SampleFaceCheckListener;
import com.neucore.neusdk_demo.neulink.extend.SampleFaceListener;
import com.neucore.neusdk_demo.neulink.extend.SampleFaceQueryListener;
import com.neucore.neusdk_demo.neulink.extend.hello.HelloCmdListener;
import com.neucore.neusdk_demo.neulink.extend.hello.HelloProcessor;

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
        /**
         * 集成SDK
         */
        installSDK();
    }

    /**
     * 集成SDK
     */
    private void installSDK(){

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
         * 设置登录用户名密码
         */
        extConfig.setProperty(ConfigContext.USERNAME,"admin");
        extConfig.setProperty(ConfigContext.PASSWORD,"password");

        /**
         * 配置扩展: key可以参考ConfigContext内的定义
         */
        extConfig.setProperty(ConfigContext.MQTT_SERVER,"tcp://10.18.9.83:1883");
        /**
         * 设备类型：根据APK功能决定进行配置
         * 设备类型【0:客流机；1:智能门禁；2:刷卡器；3:门磁；4:智能网关；5:智能中控;6:展示设备;7:人脸IPC;8:控制面板;9:车牌IPC】
         */
        extConfig.setProperty(ConfigContext.DEVICE_TYPE,"5");//默认为客流机器
        /**
         * 设置设备端2Cloud的通信通道；默认为mqtt
         */
        extConfig.setProperty(ConfigContext.UPLOAD_CHANNEL,"1");//0：mqtt；1：http
        /**
         * 设置设备注册服务地址
         */
        extConfig.setProperty(ConfigContext.REGIST_SERVER,"https://data.neuapi.com/v1/device/regist");
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
        SampleConnector register = new SampleConnector(this,service,extConfig,loginCallback,callback);
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
     * 登录loginCallback
     */
    ILoginCallback loginCallback = new ILoginCallback() {
        @Override
        public String login() {
            /**
             * 实现登录返回token
             */
            return null;
        }
    };
    /**
     * 外部扩展
     */
    IExtendCallback callback = new IExtendCallback() {
        @Override
        public void onCallBack() {

            /**
             * 设备序列号生成器；主要是为了扩展支持自己有业务意义的SN
             */
            ServiceFactory.getInstance().setDeviceService(new IDeviceService() {
                /**
                 * 这个主要是为了支持非neucore生产的硬件；
                 * 规则：必须客户代码开头：这个从neucore云注册开通后获取
                 * @return
                 */
                @Override
                public String getExtSN() {
                    /**
                     * 默认实现，可以替换
                     */
                    return DeviceUtils.getCPUSN(getContext());
                }
                public DeviceInfo getInfo(){
                    /**
                     * @TODO 可以实现
                     */
                    return DeviceInfoBuilder.getInstance().build();
                }
            });

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
             * neulink消息线性处理存储服务
             */
            ServiceFactory.getInstance().setMessageService(new MessageService(getContext()));

            /**
             * 自定义Processor注册
             */
            NeulinkProcessorFactory.regist("hello",new HelloProcessor(),new HelloCmdListener());
        }
    };
}
