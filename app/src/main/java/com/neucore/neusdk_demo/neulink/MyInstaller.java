package com.neucore.neusdk_demo.neulink;

import android.app.Application;

import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.IExtendCallback;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.impl.SampleConnector;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neusdk_demo.neulink.extend.MyBizExtendRegistCallbackImpl;
import com.neucore.neusdk_demo.neulink.extend.MyLoginCallbackImpl;
import com.neucore.neusdk_demo.neulink.extend.MyMqttCallbackImpl;
import com.neucore.neusdk_demo.neulink.extend.MyDeviceExtendServiceImpl;
import com.neucore.neusdk_demo.service.impl.UserService;

import java.util.Properties;

/**
 * 可以扩展实现
 */
public class MyInstaller {

    private String TAG = "MyInstaller";

    private static MyInstaller installer = new MyInstaller();
    private boolean init = false;
    public static MyInstaller getInstance(){
        return installer;
    }

    /**
     * after 成功获得到授权之后调用
     * @param application
     */
    public final void install(Application application){
        synchronized (this){
            if(!init){
                /**
                 *
                 */
                ContextHolder.getInstance().setContext(application);

                /**
                 * 人脸服务初始化
                 */
                UserService.getInstance(application.getApplicationContext());
                /**
                 * 构造扩展配置
                 */
                Properties extConfig = buildConfig();
                /**
                 * 集成SDK
                 */
                buildConnector(application,extConfig);

                init = true;
            }
        }
    }

    /**
     * 集成SDK
     */
    private final void buildConnector(Application application,Properties extConfig){

        /**
         * 连接器
         */
        SampleConnector connector = new SampleConnector(application,extConfig);
        //##########################################################################################
        /**
         * 扩展实现。。。
         */
        /**
         * http登录授权回调[当系统不需要安全认证时，可以不设置]
         */
        connector.setLoginCallback(loginCallback);
        /**
         * 设备服务，当没有扩展需要时，可以不设置，即：默认实现
         */
        connector.setDeviceService(deviceService);
        /**
         * neulink业务外部扩展回调；
         */
        connector.setExtendCallback(callback);
        /**
         * mqtt回调，当需要监控mqtt状态时，需要设置
         */
        connector.setMqttCallBack(mqttCallBack);
        /**
         * neulink消息线性处理存储服务
         */
        connector.setMessageService(null);
        /**
         * OTA文件断点续传文件服务
         */
        connector.setFileService(null);
        /**
         * neulink执行结果回调处理接口
         */
        connector.setDefaultResCallback(null);
        //##########################################################################################
        /**
         * 开始连接
         */
        connector.start();
    }

    /**
     * TODO 配置扩展实现
     * @return
     */
    private Properties buildConfig(){
        /**
         * 集成Neulink
         */
        Properties extConfig = new Properties();

        /**
         * 设置租户Id
         */
        extConfig.setProperty(ConfigContext.SCOPEID,"1");//租户id

        /**
         * 设备类型：根据APK功能决定进行配置
         * 设备类型【0:客流机；1:智能门禁；2:刷卡器；3:门磁；4:智能网关；5:智能中控;6:展示设备;7:人脸IPC;8:控制面板;9:车牌IPC  14:相框-Android;15:相框-Lunix】
         */
        extConfig.setProperty(ConfigContext.DEVICE_TYPE,"10");//默认为客流机器
        /**
         * 设置设备端与云端的通信通道；
         * 默认为mqtt【下发、上报都走mqtt】；
         * 即：所有End2Cloud的neulink上报都是mqtt消息；Cloud2End的neulink的下发都是mqtt消息；
         * 当channel设置为http时，所有End2Cloud的neulink上报都是http报文；Cloud2End的neulink的下发都是mqtt消息；
         *
         * 扩展设置：
         * 上报通道设置【0：mqtt；1：http】，默认mqtt
         * extConfig.setProperty(ConfigContext.UPLOAD_CHANNEL,"1");
         * 设置topic模式，默认：TOPIC_SHORT
         * extConfig.setProperty(ConfigContext.TOPIC_MODE,ConfigContext.TOPIC_LONG);
         * 下发内容默认压缩
         * extConfig.setProperty(ConfigContext.CUSTMER_COMPRESS,"false"); //关闭下发内容压缩处理
         * 上传内容默认压缩
         * extConfig.setProperty(ConfigContext.PRODUCT_COMPRESS,"false"); //关闭上传内容压缩处理
         */
        extConfig.setProperty(ConfigContext.CUSTMER_COMPRESS,"false"); //关闭下发内容压缩处理
        extConfig.setProperty(ConfigContext.PRODUCT_COMPRESS,"false"); //关闭下发内容压缩处理
        extConfig.setProperty(ConfigContext.ENABLE_HEARTBEAT,"true"); //开启心跳
        extConfig.setProperty(ConfigContext.ENABLE_RUNTIME,"true"); //开启运行状态
        //##########################################################################################
        /**
         * ⚠️注意；mqtt通道启用时打开
         * 设置登录用户名密码
         * MQTT_SERVER 可以用逗号连接多个服务器地址【集群、需要paho库的支持】;
         * eg：tcp://10.18.9.240:1883,tcp://10.18.9.241:1883,tcp://10.18.9.242:1883,tcp://10.18.9.243:1883,tcp://10.18.9.244:1883
         */
        extConfig.setProperty(ConfigContext.USERNAME,"zXzc3gkY1RGS626w");
        extConfig.setProperty(ConfigContext.PASSWORD,"702c08e642f6330ac1d8141242eb5214a9fcb599");
        extConfig.setProperty(ConfigContext.MQTT_SERVER,"tcp://dev.neucore.com:1883");
        //##########################################################################################
        /**
         * ⚠️注意；http 通道启用时打开
         * 设置设备注册服务地址
         */
//        extConfig.setProperty(ConfigContext.REGIST_SERVER,"http://dev.neucore.com/api/v1/neulink/upload2cloud");
        /**
         * 30分钟
         */
//        extConfig.setProperty(ConfigContext.HTTP_SESSION_TIMEOUT,String.valueOf(30*60*1000L));
        //##########################################################################################
        /**
         * FTP 实现
         * ConfigContext.FTP_SERVER：服务器地址
         * ConfigContext.FTP_USER_NAME：用户名
         * ConfigContext.FTP_PASSWORD：密码
         * ConfigContext.CONN_TIME_OUT：连接超时
         * ConfigContext.READ_TIME_OUT：执行超时
         */
        extConfig.setProperty(ConfigContext.STORAGE_TYPE,ConfigContext.STORAGE_MYFTP);
        extConfig.setProperty(ConfigContext.FTP_SERVER,"dev.neucore.com");
        //##########################################################################################
        /**
         * OSS存储服务开启注释
         * ConfigContext.OSS_ACCESS_KEY_ID
         * ConfigContext.OSS_ACCESS_KEY_SECRET
         * ConfigContext.OSS_END_POINT
         * ConfigContext.OSS_BUCKET_NAME
         * ConfigContext.CONN_TIME_OUT
         * ConfigContext.READ_TIME_OUT
         */
        //extConfig.setProperty(ConfigContext.STORAGE_TYPE,ConfigContext.STORAGE_OSS);
        //##########################################################################################

        extConfig.setProperty(ConfigContext.TOPIC_MODE,ConfigContext.TOPIC_SHORT);
        return extConfig;
    }

    /**
     * TODO MQTT 网络、消息扩展
     */
    IMqttCallBack mqttCallBack = new MyMqttCallbackImpl();
    /**
     * TODO 设备服务扩展
     */
    IDeviceService deviceService = new MyDeviceExtendServiceImpl();
    /**
     * TODO 登录loginCallback
     */
    ILoginCallback loginCallback = new MyLoginCallbackImpl();
    /**
     * TODO 外部扩展
     */
    IExtendCallback callback = new MyBizExtendRegistCallbackImpl();
}
