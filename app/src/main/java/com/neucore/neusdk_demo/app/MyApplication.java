package com.neucore.neusdk_demo.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IExtendCallback;
import com.neucore.neulink.IExtendInfoCallback;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.IUserService;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.cmd.msg.DeviceInfo;
import com.neucore.neulink.cmd.msg.SubApp;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.extend.SampleConnector;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.impl.NeulinkProcessorFactory;
import com.neucore.neulink.impl.service.device.DeviceInfoDefaultBuilder;
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
import com.neucore.neusdk_demo.neulink.extend.auth.listener.AuthCmdListener;
import com.neucore.neusdk_demo.neulink.extend.auth.AuthProcessor;
import com.neucore.neusdk_demo.neulink.extend.hello.HelloCmdListener;
import com.neucore.neusdk_demo.neulink.extend.hello.HelloProcessor;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import java.util.List;
import java.util.Map;
import java.util.Properties;

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
        IUserService userService = UserService.getInstance(this);
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
        extConfig.setProperty(ConfigContext.MQTT_SERVER,"tcp://dev.neucore.com:1883");
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
         * 30分钟
         */
        extConfig.setProperty(ConfigContext.HTTP_SESSION_TIMEOUT,String.valueOf(30*60*1000L));
        /**
         * 设置设备注册服务地址
         */
        extConfig.setProperty(ConfigContext.REGIST_SERVER,"https://dev.neucore.com/api/v1/neulink/upload2cloud");
        /**
         * FTP 实现
         */
        extConfig.setProperty(ConfigContext.STORAGE_TYPE,ConfigContext.STORAGE_MYFTP);

        extConfig.setProperty(ConfigContext.FTP_SERVER,"dev.neucore.com");


        /**
         * ⚠️注意；http 通道启用时打开
         */
        //extConfig.setProperty(ConfigContext.UPLOAD_CHANNEL,"1");//end2cloud neulink 协议 切换至https通道
        //extConfig.setProperty(ConfigContext.REGIST_SERVER,"https://dev.neucore.com/api/v1/neulink/upload2cloud");//设置http通道注册服务地址
        SampleConnector connector = new SampleConnector(this,extConfig);

        /**
         * http登录授权回调
         */
        connector.setLoginCallback(loginCallback);
        /**
         * 设备服务
         */
        connector.setDeviceService(deviceService);
        /**
         * 扩展回调
         */
        connector.setExtendCallback(callback);
        /**
         * mqtt回调
         */
        connector.setMqttCallBack(mqttCallBack);

        /**
         * 用户人脸数据库服务
         */
        connector.setUserService(userService);
        /**
         * neulink消息线性处理存储服务
         */
        connector.setMessageService(null);
        /**
         * 开始连接
         */
        connector.start();
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
     * MQTT 网络、消息扩展
     */
    IMqttCallBack mqttCallBack = new IMqttCallBack() {
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            /**
             * 可以用在APP交互提示等
             */
            Log.i(TAG,"connectComplete");
        }

        @Override
        public void messageArrived(String topic, String message, int qos) throws Exception {
            /**
             * 可以不用管
             */
        }

        @Override
        public void connectionLost(Throwable arg0) {
            /**
             * 可以用在APP交互提示等
             */
            Log.i(TAG,"connectionLost");
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            /**
             * 可以用在APP交互提示等
             */
            Log.i(TAG,"deliveryComplete");
        }

        @Override
        public void connectSuccess(IMqttToken arg0) {
            /**
             * 可以用在APP交互提示等
             */
            Log.i(TAG,"connectSuccess");
        }

        @Override
        public void connectFailed(IMqttToken arg0, Throwable arg1) {
            /**
             * 可以用在APP交互提示等
             */
            Log.i(TAG,"connectFailed");
        }
    };
    /**
     * 设备服务扩展
     */
    IDeviceService deviceService = new IDeviceService() {
        @Override
        public String getExtSN() {
            /**
             * 需要获取设备唯一标识【自定义，eg：YekerID】
             */
            return "0BLB11Y202080404200100000002";//DeviceUtils.getCPUSN(getContext());
        }

        @Override
        public DeviceInfo getInfo() {
            /**
             * 需要上报应用列表【名称及其相关版本；】
             * OTA升级文件规则
             *
             * ota_[sys|apk|app]_设备硬件型号_设备产品型号(对应neulink的cpu型号)_产品当前版本识别号，其中设备硬件型号和设备产品型号，以及产品当前版本识别号不能有下划线。
             *
             * ota升级文件包的【设备产品型号】字段需要和neulink内的 -- cpumd 进行一致；
             */
            return DeviceInfoDefaultBuilder.getInstance().build(extendInfoCallback);
        }
    };
    /**
     * 设备信息上报扩展
     */
    IExtendInfoCallback extendInfoCallback = new IExtendInfoCallback(){


        @Override
        public List<SubApp> getSubApps() {
            /**
             * 子应用列表
             */
            return null;
        }

        @Override
        public List<Map<String, String>> getAttrs() {
            /**
             * 扩展属性
             */
            return null;
        }

        @Override
        public String getModel() {
            /**
             * 产品型号
             */
            return null;
        }

        @Override
        public String getImei() {
            return null;
        }

        @Override
        public String getImsi() {
            return null;
        }

        @Override
        public String getIccid() {
            return null;
        }

        @Override
        public String getLat() {
            /**
             * 设备所在经度
             */
            return null;
        }

        @Override
        public String getLng() {
            /**
             * 设备所在纬度
             */
            return null;
        }

        @Override
        public String getInterface() {
            return null;
        }

        @Override
        public String getWifiModel() {
            /**
             * wifi模组型号
             */
            return null;
        }

        @Override
        public String getNpuModel() {
            /**
             * npu型号
             */
            return null;
        }

        @Override
        public String getScreenSize() {
            return null;
        }

        @Override
        public String getScreenInterface() {
            return null;
        }

        @Override
        public String getScreenResolution() {
            return null;
        }

        @Override
        public String getBiosVersion() {
            return null;
        }

        @Override
        public String getOsName() {
            return null;
        }

        @Override
        public String getOsVersion() {
            return null;
        }

        @Override
        public String getFirName() {
            return null;
        }

        @Override
        public String getFirVersion() {
            return null;
        }

        @Override
        public String getMainAppName() {
            return null;
        }

        @Override
        public String getMainAppVersion() {
            return null;
        }

        @Override
        public String getJvmVersion() {
            return null;
        }
    };
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
             * 自定义Processor注册
             * 框架已经实现消息的接收及响应处理机制
             * 新业务可以参考Hello业务的实现业务就行
             */
            NeulinkProcessorFactory.regist("hello",new HelloProcessor(),new HelloCmdListener());

            NeulinkProcessorFactory.regist("auth",new AuthProcessor(),new AuthCmdListener());
        }
    };
}
