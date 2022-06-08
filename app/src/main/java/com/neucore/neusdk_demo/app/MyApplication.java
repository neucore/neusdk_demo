package com.neucore.neusdk_demo.app;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.neucore.neulink.IExtendCallback;
import com.neucore.neulink.IDeviceExtendInfoCallback;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.impl.cmd.msg.SoftVInfo;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.impl.service.device.DefaultDeviceServiceImpl;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.cmd.msg.DeviceInfo;
import com.neucore.neulink.impl.cmd.msg.SubApp;
import com.neucore.neulink.impl.SampleConnector;
import com.neucore.neulink.impl.ResCallback2Log;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.impl.registry.ProcessRegistry;
import com.neucore.neulink.impl.service.device.DeviceInfoDefaultBuilder;
import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.AuthCmdListener;
import com.neucore.neusdk_demo.neulink.extend.auth.AuthProcessor;

import com.neucore.neusdk_demo.neulink.extend.bind.BindProcessor;
import com.neucore.neusdk_demo.neulink.extend.bind.listener.BindCmdListener;
import com.neucore.neusdk_demo.neulink.extend.hello.listener.HelloCmdListener;
import com.neucore.neusdk_demo.neulink.extend.hello.HelloProcessor;
import com.neucore.neusdk_demo.neulink.extend.hello.response.HellResCallback;
import com.neucore.neusdk_demo.neulink.extend.other.SampleAlogUpgrdActionListener;
import com.neucore.neusdk_demo.neulink.extend.other.SampleAwakenActionListener;
import com.neucore.neusdk_demo.neulink.extend.other.SampleBackupActionListener;
import com.neucore.neusdk_demo.neulink.extend.other.SampleCarCheckListener;
import com.neucore.neusdk_demo.neulink.extend.other.SampleCarQueryListener;
import com.neucore.neusdk_demo.neulink.extend.other.SampleCarSyncListener;
import com.neucore.neusdk_demo.neulink.extend.other.SampleCfgActionListener;
import com.neucore.neusdk_demo.neulink.extend.other.SampleDebugCmdListener;
import com.neucore.neusdk_demo.neulink.extend.other.SampleFaceCheckListener;
import com.neucore.neusdk_demo.neulink.extend.other.SampleFaceQueryListener;
import com.neucore.neusdk_demo.neulink.extend.other.SampleFaceSyncListener;
import com.neucore.neusdk_demo.neulink.extend.other.SampleFirewareResumeCmdListener;
import com.neucore.neusdk_demo.neulink.extend.other.SampleFirewareUpgrdActionListener;
import com.neucore.neusdk_demo.neulink.extend.other.SampleHibrateActionListener;
import com.neucore.neusdk_demo.neulink.extend.other.SampleLicCheckListener;
import com.neucore.neusdk_demo.neulink.extend.other.SampleLicQueryListener;
import com.neucore.neusdk_demo.neulink.extend.other.SampleRebootCmdListener;
import com.neucore.neusdk_demo.neulink.extend.other.SampleRecoverActionListener;
import com.neucore.neusdk_demo.neulink.extend.other.SampleResetActionListener;
import com.neucore.neusdk_demo.neulink.extend.other.SampleShellCmdListener;
import com.neucore.neusdk_demo.service.impl.UserService;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
         * 人脸服务初始化
         */
        UserService.getInstance(getContext());

        /**
         * 集成LogUtils
         */
        initLog();
        /**
         * 集成SDK
         */
        installSDK();

        new Thread(){
            public void run(){
                while(!NeulinkService.getInstance().isNeulinkServiceInited()){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
                /**
                 * ⚠️注意：
                 * 异步响应必须在NeulinkService.getInstance().isNeulinkServiceInited()==true之后调用，否则不会成功
                 */
                //从数据库或者ActionListener中获取到获取到云端下发的Cmd【biz、协议版本、请求Id，命令模式】
                String biz = "binding";
                String version = "v1.0";
                String reqId = "3214323ewadfdsad";
                String mode = "bind";
                String payload = "{}";//绑定响应协议体
                NeulinkService.getInstance().getPublisherFacde().rrpcResponse(biz, "v1.0", reqId, mode, 202, NeulinkConst.MESSAGE_PROCESSING, payload, new ResCallback2Log());
            }
        }.start();
    }

    /**
     * 集成SDK
     */
    private void installSDK(){

        ContextHolder.getInstance().setContext(this);

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
        extConfig.setProperty(ConfigContext.DEVICE_TYPE,"14");//默认为客流机器
        /**
         * 设置设备端与云端的通信通道；
         * 默认为mqtt【下发、上报都走mqtt】；
         * 即：所有End2Cloud的neulink上报都是mqtt消息；Cloud2End的neulink的下发都是mqtt消息；
         *
         * 当channel设置为http时，所有End2Cloud的neulink上报都是http报文；Cloud2End的neulink的下发都是mqtt消息；
         */
        extConfig.setProperty(ConfigContext.UPLOAD_CHANNEL,"1");//0：mqtt；1：http
        //##########################################################################################
        /**
         * ⚠️注意；mqtt通道启用时打开
         * 设置登录用户名密码
         */
        //extConfig.setProperty(ConfigContext.USERNAME,"admin");
        //extConfig.setProperty(ConfigContext.PASSWORD,"password");
        //extConfig.setProperty(ConfigContext.MQTT_SERVER,"tcp://dev.neucore.com:1883");
        //##########################################################################################
        /**
         * ⚠️注意；http 通道启用时打开
         * 设置设备注册服务地址
         */
        extConfig.setProperty(ConfigContext.REGIST_SERVER,"https://dev.neucore.com/api/v1/neulink/upload2cloud");
        /**
         * 30分钟
         */
        extConfig.setProperty(ConfigContext.HTTP_SESSION_TIMEOUT,String.valueOf(30*60*1000L));
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
        /**
         * 连接器
         */
        SampleConnector connector = new SampleConnector(this,extConfig);
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

    private void initLog() {
        LogUtils.getConfig()
                .setLogSwitch(true) //isAppDebug // 设置 log 总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(true)// 设置是否输出到控制台开关，默认开
                .setGlobalTag("gemini")// 设置 log 全局标签【应用名称】，默认为空
                // 当全局标签不为空时，我们输出的 log 全部为该 tag，
                // 为空时，如果传入的 tag 为空那就显示类名，否则显示 tag
                .setLogHeadSwitch(false)// 设置 log 头信息开关，默认为开
                .setLog2FileSwitch(true)// 打印 log 时是否存到文件的开关，默认关
                .setDir(DeviceUtils.getLogPath(getContext()))// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setFilePrefix("Log")// 当文件前缀为空时，默认为"util"，即写入文件为"util-yyyy-MM-dd$fileExtension"
                .setFileExtension(".log")// 设置日志文件后缀
                .setBorderSwitch(false)// 输出日志是否带边框开关，默认开
                .setSingleTagSwitch(true)// 一条日志仅输出一条，默认开，为美化 AS 3.1 的 Logcat
                .setConsoleFilter(LogUtils.V)// log 的控制台过滤器，和 logcat 过滤器同理，默认 Verbose
                .setFileFilter(LogUtils.V)// log 文件过滤器，和 logcat 过滤器同理，默认 Verbose
                .setStackDeep(1)// log 栈深度，默认为 1
                .setStackOffset(0)// 设置栈偏移，比如二次封装的话就需要设置，默认为 0
                .setSaveDays(3)// 设置日志可保留天数，默认为 -1 表示无限时长
                // 新增 ArrayList 格式化器，默认已支持 Array, Throwable, Bundle, Intent 的格式化输出
                .addFormatter(new LogUtils.IFormatter<ArrayList>() {
                    @Override
                    public String format(ArrayList arrayList) {
                        return "LogUtils Formatter ArrayList { " + arrayList.toString() + " }";
                    }
                });
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
    IDeviceService deviceService = new DefaultDeviceServiceImpl() {
        @Override
        public String getExtSN() {
            /**
             * 需要获取设备唯一标识【自定义，eg：YekerID@MacAddress】
             */
            return DeviceUtils.getDeviceId(getContext());
        }
        @Override
        public Locale getLocale(){
            /**
             * 需要读取apk国际化设置后的值
             */
            return Locale.getDefault();
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
            return DeviceInfoDefaultBuilder.getInstance().build(new IDeviceExtendInfoCallback(){


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
                    /**
                     * 移动设备国际身份码
                     */
                    return null;
                }

                @Override
                public String getImsi() {
                    /**
                     * 移动用户国际识别码
                     */
                    return null;
                }

                @Override
                public String getIccid() {
                    /**
                     * SIM卡卡号
                     */
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
                    /**
                     * 网卡名称
                     */
                    return null;
                }

                @Override
                public String getWifiModel() {
                    /**
                     * wifi模组型号:参照对照表
                     */
                    return null;
                }

                @Override
                public String getCpuModel(){

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
                public SoftVInfo getMain() {
                    return null;
                }

                @Override
                public String getScreenSize() {
                    /**
                     * 屏幕大小:参照对照表
                     */
                    return null;
                }

                @Override
                public String getScreenInterface() {
                    /**
                     * 屏幕接口:参照对照表
                     */
                    return null;
                }

                @Override
                public String getScreenResolution() {
                    /**
                     * 分辨率:参照对照表
                     */
                    return null;
                }

                @Override
                public String getBno(){
                    /**
                     * 批次号
                     */
                    return null;
                }
            });
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
            return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsicmVzMSJdLCJ1c2VyX2lkIjoxLCJ1c2VyX25hbWUiOiJ7XCJjcmVhdGVkT25cIjoxNjQ2MDM5ODA1MDAwLFwiZW1haWxcIjpcInh4eEBtYWlsLmNvbVwiLFwiZnVsbG5hbWVcIjpcIui2hee6p-euoeeQhuWRmFwiLFwiaWRcIjoxLFwiaXNEZWxcIjowLFwibW9kaWZpZWRPblwiOjE2NDY4ODMzOTUwMDAsXCJwaG9uZU51bWJlclwiOlwiMTU4MDA4NjA4MDZcIixcInNjb3BlSWRcIjoxLFwic3RhdHVzXCI6MCxcInR5cGVcIjoxLFwidXNlcm5hbWVcIjpcImFkbWluXCJ9Iiwic2NvcGUiOlsiUk9MRV9BRE1JTiIsIlJPTEVfVVNFUiIsIlJPTEVfQVBJIl0sImV4cCI6MTY0ODA5Mzg3MSwiYXV0aG9yaXRpZXMiOlsiYWxsIl0sImp0aSI6IjcyOGU3NmVlLTAzZTQtNGNhMi1hOTVjLTdiMTE3MTQ0YWM1NiIsImNsaWVudF9pZCI6ImdlbWluaSJ9.Gcz4TJwyiC_66aOwl1vpGZr5nMNJhJEuyzUSN1tKFI0";
        }
    };

    /**
     * 外部扩展
     */
    IExtendCallback callback = new IExtendCallback() {
        @Override
        public void onCallBack() {

            /**
             * SDK默认实现扩展
             */
            //######################################################################################
            /**
             * 配置下发 扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_CFG,new SampleCfgActionListener());
            /**
             * 人脸下发 扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_BLIB_FACE,new SampleFaceSyncListener());
            /**
             * 车辆下发 扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_BLIB_CAR,new SampleCarSyncListener());
            /**
             * 车牌下发 扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_BLIB_LIC,new SampleFaceSyncListener());
            /**
             * 人脸比对 扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_CLIB_FACE,new SampleFaceCheckListener());
            /**
             * 车辆比对 扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_CLIB_CAR,new SampleCarCheckListener());
            /**
             * 车牌比对 扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_CLIB_LIC,new SampleLicCheckListener());
            /**
             * 人脸查询 扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_QLIB_FACE,new SampleFaceQueryListener());
            /**
             * 车辆查询 扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_QLIB_CAR,new SampleCarQueryListener());
            /**
             * 车牌查询 扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_QLIB_LIC,new SampleLicQueryListener());

            /**
             * 重启 扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_REBOOT,new SampleRebootCmdListener());

            /**
             * Shell 扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_SHELL,new SampleShellCmdListener());

            /**
             * 唤醒 扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_AWAKEN,new SampleAwakenActionListener());
            /**
             * 休眠 扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_HIBRATE,new SampleHibrateActionListener());
            /**
             * 算法升级 扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_ALOG,new SampleAlogUpgrdActionListener());

            /**
             * 固件$APK 升级扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_FIRMWARE,new SampleFirewareUpgrdActionListener());

            /**
             * 固件$APK 断点续传升级扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_FIRMWARE_RESUME,new SampleFirewareResumeCmdListener());

            /**
             * 备份 扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_BACKUP,new SampleBackupActionListener());

            /**
             * 恢复 扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_RECOVER,new SampleRecoverActionListener());

            /**
             * 重置 扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_RESET,new SampleResetActionListener());

            /**
             * Debug 扩展【取消注释，覆盖默认实现】
             */
            //ListenerRegistry.getInstance().setExtendListener(NeulinkConst.NEULINK_BIZ_DEBUG,new SampleDebugCmdListener());
            
            //######################################################################################
            /**
             * SDK 自定义业务扩展实现
             * 框架已经实现消息的接收及响应处理机制
             * 新业务可以参考Hello业务的实现业务就行
             */
            ProcessRegistry.regist(NeulinkConst.NEULINK_BIZ_AUTH,new AuthProcessor(),new AuthCmdListener());
            
            ProcessRegistry.regist(NeulinkConst.NEULINK_BIZ_BINDING,new BindProcessor(),new BindCmdListener());
            /**
             * doAction返回结果后框架会把处理结果返回给云端；同时把云端处理状态返回给HellResCallback
             */
            ProcessRegistry.regist("hello",new HelloProcessor(),new HelloCmdListener(),new HellResCallback());
            //######################################################################################
            /**
             * 上传结果给到云端
             * 这个业务一般用于端侧自动抓拍、日志自动上报
             * 端侧审核操作【同意、拒绝】结果给到云端
             * NeulinkPublisherFacde publisher = NeulinkService.getInstance().getPublisherFacde()
             * 具体参考Neulink 使用手册《上报消息到云端》部分
             */
        }
    };
}
