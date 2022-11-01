package com.neucore.neusdk_demo.neulink;

import android.app.Application;

import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.IDownloder;
import com.neucore.neulink.IExtendCallback;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.IPermissionChecker;
import com.neucore.neulink.IPropChgListener;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.ResCallback2Log;
import com.neucore.neulink.impl.SampleConnector;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.down.http.HttpDownloader;
import com.neucore.neulink.impl.down.http.HttpResumeDownloader;
import com.neucore.neulink.impl.down.oss.OssDownloader;
import com.neucore.neulink.impl.down.oss.OssResumeDownloader;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neusdk_demo.neulink.extend.MyBizExtendRegistCallbackImpl;
import com.neucore.neusdk_demo.neulink.extend.MyDeviceExtendServiceImpl;
import com.neucore.neusdk_demo.neulink.extend.MyLoginCallbackImpl;
import com.neucore.neusdk_demo.neulink.extend.MyMqttCallbackImpl;
import com.neucore.neusdk_demo.neulink.extend.MyPermissionChecker;
import com.neucore.neusdk_demo.neulink.extend.MyPropChgListener;
import com.neucore.neusdk_demo.service.impl.UserService;

import java.util.Properties;

/**
 * 可以扩展实现
 */
public class MyInstaller implements NeulinkConst {

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
                 * 人脸服务初始化：可选
                 */
                UserService.getInstance(application.getApplicationContext());

                /**
                 * 设置Context【必须】
                 */
                ContextHolder.getInstance().setContext(application);

                /**
                 * 可选
                 * 注册：系统属性设置侦听器[setprop getprop操作事件]
                 */
                ListenerRegistry.getInstance().addPropChgListener(listener);

                /**
                 * 构造扩展配置【必须】
                 */
                Properties extConfig = buildExtConfig();

                /**
                 * 集成SDK【必须】
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
         * 相关权限检测器【必须】
         */
        connector.setPermissionChecker(permissionChecker);
        /**
         * http登录授权回调[当系统不需要安全认证时，可以不设置]【可选】
         */
        connector.setLoginCallback(loginCallback);
        /**
         * 设备服务，当没有扩展需要时，可以不设置，即：默认实现【必须】
         */
        connector.setDeviceService(deviceService);
        /**
         * neulink业务外部扩展回调；【必须】
         */
        connector.setExtendCallback(callback);
        /**
         * mqtt回调，当需要监控mqtt状态时，需要设置【可选】
         */
        //connector.setMqttCallBack(mqttCallBack);
        /**
         * neulink消息线性处理存储服务【可选】
         */
        //connector.setMessageService(new MessageService(application));
        /**
         * 默认文件下载器【启用默认的process实现时，必须】
         * eg：SDK的默认处理器【人脸同步、固件等】采用了IDownloader接口集成了
         *
         * 目前已经实现了下列下载器，可以根据部署情况选择
         *
         * HttpDownloader【单线程：待下载的资源存储在http服务器】
         * HttpResumeDownloader【多线程：待下载的资源存储在http服务器】
         * OssDownloader【单线程：待下载的资源存储在Oss服务器】
         * OssResumeDownloader【单线程：待下载的资源存储在Oss服务器】
         */
        connector.setDownloder(resumeDownloader);

        /**
         * neulink执行结果回调处理接口【可选】
         */
        connector.setDefaultResCallback(new ResCallback2Log());

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
    private Properties buildExtConfig(){
        /**
         * 集成Neulink
         */
        Properties extConfig = new Properties();

        /**
         * 设备类型：根据APK功能决定进行配置【必须】
         * 设备类型【0:客流机；1:智能门禁；2:刷卡器；3:门磁；4:智能网关；5:智能中控;6:展示设备;7:人脸IPC;8:控制面板;9:车牌IPC  14:相框-Android;15:相框-Lunix】
         */
        extConfig.setProperty(ConfigContext.DEVICE_TYPE,"10");//默认为客流机器
        /**
         * 设置设备所属时区
         */
        extConfig.setProperty(ConfigContext.NEULINK_HEADERS_LZR,TimeZoneId_Asia$ShangHai);

        /**
         * 设置租户Id【必须】
         */
        extConfig.setProperty(ConfigContext.SCOPEID,"Mg");//租户id
        /**
         * 启用远程配置【可选】
         * 默认值为false，即：本地配置起效
         */
        extConfig.setProperty(ConfigContext.ENABLE_REMOTE_CONFIG,"true"); //开启远程配置状态【mqtt-server，http-upload-server】
        /**
         * 远程配置服务接口地址，当ENABLE_REMOTE_CONFIG=true时起效
         *
         * 获取cloud2end【mqtt下发】 & end2cloud【mqtt或者http】通信通道的配置信息
         *
         * 默认配置值：https://dev.neucore.com/api/user/v1/configs【C端云平台】
         * 智能楼宇地址：https://dev.neucore.com/v1/smrtlibs/devices/configs【智能楼宇平台】
         *
         */
        extConfig.setProperty(ConfigContext.CONDIG_SERVER_URL,"http://10.18.104.250/v1/smrtlibs/devices/configs");//智能楼宇平台
        /**
         * 当存储服务为OSS时需要开启&设置【可选，本地部署时一般不需要，eg：智能楼宇系统】
         */
        extConfig.setProperty(ConfigContext.OSS_STS_AUTH_URL,String.format("https://dev.neucore.com/api/storage/v1/%s/authorization",extConfig.getProperty(ConfigContext.SCOPEID)));//OSS存储临时授权地址

        /**
         * Neulink通道设置【可选，默认为mqtt通道】
         */
        extConfig.setProperty(ConfigContext.UPLOAD_CHANNEL,"0");//上报通道设置【0：mqtt；1：http】，默认mqtt

        //##########################################################################################
        /**
         * ENABLE_REMOTE_CONFIG = false起效
         * ⚠️注意；mqtt通道启用时打开
         * 设置登录用户名密码
         * MQTT_SERVER 可以用逗号连接多个服务器地址【集群、需要paho库的支持】;
         * eg：tcp://10.18.9.240:1883,tcp://10.18.9.241:1883,tcp://10.18.9.242:1883,tcp://10.18.9.243:1883,tcp://10.18.9.244:1883
         */
        extConfig.setProperty(ConfigContext.MQTT_USERNAME,"zXzc3gkY1RGS626w");
        extConfig.setProperty(ConfigContext.MQTT_PASSWORD,"702c08e642f6330ac1d8141242eb5214a9fcb599");
        extConfig.setProperty(ConfigContext.MQTT_SERVER,"tcp://dev.neucore.com:1883");
        extConfig.setProperty(ConfigContext.KEEP_ALIVE_INTERVAL,"60");
        //##########################################################################################
        /**
         * ENABLE_REMOTE_CONFIG = false & UPLOAD_CHANNEL = "0" 起效
         * ⚠️注意；http 通道启用时打开
         * 设置设备注册服务地址
         */
        extConfig.setProperty(ConfigContext.HTTP_UPLOAD_SERVER,"http://dev.neucore.com/api/v1/neulink/upload2cloud");

        /**
         * 设置设备端与云端的通信通道；
         * 默认为mqtt【下发、上报都走mqtt】；
         * 即：所有End2Cloud的neulink上报都是mqtt消息；Cloud2End的neulink的下发都是mqtt消息；
         * 当channel设置为http时，所有End2Cloud的neulink上报都是http报文；Cloud2End的neulink的下发都是mqtt消息；
         *
         * 扩展设置：
         * 设置topic模式，默认：TOPIC_SHORT
         * extConfig.setProperty(ConfigContext.TOPIC_MODE,ConfigContext.TOPIC_LONG);
         * 下发内容默认压缩
         * extConfig.setProperty(ConfigContext.CUSTMER_COMPRESS,"false"); //关闭下发内容压缩处理
         * 上传内容默认压缩
         * extConfig.setProperty(ConfigContext.PRODUCT_COMPRESS,"false"); //关闭上传内容压缩处理
         */
        extConfig.setProperty(ConfigContext.CUSTMER_COMPRESS,"true"); //关闭下发内容压缩处理
        extConfig.setProperty(ConfigContext.PRODUCT_COMPRESS,"true"); //关闭下发内容压缩处理
        extConfig.setProperty(ConfigContext.ENABLE_HEARTBEAT,"false"); //开启心跳
        extConfig.setProperty(ConfigContext.ENABLE_RUNTIME,"false"); //开启运行状态

        //##########################################################################################
        /**
         * FTP 存储方式
         * STORAGE_TYPE = STORAGE_MYFTP 起效
         *
         * ConfigContext.FTP_SERVER：服务器地址
         * ConfigContext.FTP_USER_NAME：用户名
         * ConfigContext.FTP_PASSWORD：密码
         * ConfigContext.CONN_TIME_OUT：连接超时
         * ConfigContext.READ_TIME_OUT：执行超时
         * ConfigContext.FTP_BUCKET_NAME: ftp根目录
         */
//        extConfig.setProperty(ConfigContext.STORAGE_TYPE,ConfigContext.STORAGE_MYFTP);
//        extConfig.setProperty(ConfigContext.FTP_SERVER,"dev.neucore.com");
//        extConfig.setProperty(ConfigContext.FTP_USER_NAME,"neu2ftp");
//        extConfig.setProperty(ConfigContext.FTP_PASSWORD,"123456");
//        extConfig.setProperty(ConfigContext.FTP_BUCKET_NAME,"ftproot");

        //##########################################################################################
        /**
         * Oss 存储方式
         *
         * STORAGE_TYPE = STORAGE_OSS 起效
         */
        //##########################################################################################
//        extConfig.setProperty(ConfigContext.STORAGE_TYPE,ConfigContext.STORAGE_OSS);
        /**
         * neulink topic 模式【可选】
         * 默认为 TOPIC_SHORT
         */
        extConfig.setProperty(ConfigContext.TOPIC_MODE,ConfigContext.TOPIC_SHORT);
        return extConfig;
    }

    /**
     * TODO 默认：READ_EXTERNAL_STORAGE WRITE_EXTERNAL_STORAGE 权限检测
     */
    IPermissionChecker permissionChecker = new MyPermissionChecker();
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
    /**
     * TODO 单线程文件下载器
     * 根据需要可以扩展实现
     */
    IDownloder downloder = new HttpDownloader();
    /**
     * TODO 多线程下载器
     * 根据需要可以扩展实现
     */
    IDownloder resumeDownloader = new HttpResumeDownloader();
    /**
     * TODO Oss下载器
     * 根据需要可以扩展实现
     */
    IDownloder ossDownloader = new OssDownloader();
    /**
     * TODO Oss多线程续传下载器
     * 根据需要可以扩展实现
     */
    IDownloder ossResumeDownloader = new OssResumeDownloader();
    /**
     * TODO 系统属性修改侦听器
     */
    IPropChgListener listener = new MyPropChgListener();
}
