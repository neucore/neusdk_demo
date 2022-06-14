package com.neucore.neusdk_demo.neulink;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.IExtendCallback;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.SampleConnector;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.registry.ProcessRegistry;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neusdk_demo.neulink.extend.auth.AuthProcessor;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.AuthCmdListener;
import com.neucore.neusdk_demo.neulink.extend.bind.BindProcessor;
import com.neucore.neusdk_demo.neulink.extend.bind.listener.BindCmdListener;
import com.neucore.neusdk_demo.neulink.extend.device.MyDeviceServiceImpl;
import com.neucore.neusdk_demo.neulink.extend.hello.HelloProcessor;
import com.neucore.neusdk_demo.neulink.extend.hello.listener.HelloCmdListener;
import com.neucore.neusdk_demo.neulink.extend.hello.response.HellResCallback;
import com.neucore.neusdk_demo.service.impl.UserService;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;

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
    public void install(Application application){
        synchronized (this){
            if(!init){

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
                /**
                 * Demo publish
                 */
//
//                new Thread(){
//                    public void run(){
//                        while(!NeulinkService.getInstance().isNeulinkServiceInited()){
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                            }
//                        }
//                        /**
//                         * ⚠️注意：
//                         * 异步响应必须在NeulinkService.getInstance().isNeulinkServiceInited()==true之后调用，否则不会成功
//                         */
//                        //从数据库或者ActionListener中获取到获取到云端下发的Cmd【biz、协议版本、请求Id，命令模式】
//                        String biz = "binding";
//                        String version = "v1.0";
//                        String reqId = "3214323ewadfdsad";
//                        String mode = "bind";
//                        String payload = "{}";//绑定响应协议体
//                        NeulinkService.getInstance().getPublisherFacde().rrpcResponse(biz, "v1.0", reqId, mode, 202, NeulinkConst.MESSAGE_PROCESSING, payload, new ResCallback2Log());
//                    }
//                }.start();
                init = true;
            }
        }
    }

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
        return extConfig;
    }

    /**
     * 集成SDK
     */
    private void buildConnector(Application application,Properties extConfig){

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
    IDeviceService deviceService = new MyDeviceServiceImpl();
    /**
     * 登录loginCallback
     */
    ILoginCallback loginCallback = new ILoginCallback() {
        @Override
        public String login() {
            /**
             * 实现登录返回token
             */
            return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsicmVzMSJdLCJzY29wZUlkIjoxLCJyb2xlIjoxLCJ1c2VyX2lkIjoxLCJ1c2VyX25hbWUiOiJ7XCJpZFwiOjEsXCJzY29wZUlkXCI6MSxcInVzZXJuYW1lXCI6XCJhZG1pblwiLFwiZnVsbG5hbWVcIjpcIuW5s-WPsOeuoeeQhuWRmDFcIixcImVtYWlsXCI6XCJhb3FpLnN1bkBuZXVjb3JlLmNvbVwiLFwicGhvbmVOdW1iZXJcIjpcIjE1MjAxOTM2NTQxMlwiLFwiZXh0ZXJuYWxJZFwiOlwiXCIsXCJoZWFkUG9ydHJhaXRcIjpcIi9nZnJhbWUvMS91c2Vycy8xL2hwL2F2YXRhci5qcGdcIixcInR5cGVcIjoxLFwicm9sZVwiOjEsXCJvcHRsb2NrXCI6MSxcImV4cGlyYXRpb25EYXRlXCI6MTY1MTczOTk4MDAwMCxcInN0YXR1c1wiOjAsXCJpc0RlbFwiOjAsXCJjcmVhdGVkT25cIjoxNjQ2MDM5ODA1MDAwLFwibW9kaWZpZWRPblwiOjE2NTUxMTk4NzQwMDB9Iiwic2NvcGUiOlsiUk9MRV9BRE1JTiIsIlJPTEVfVVNFUiIsIlJPTEVfQVBJIl0sImV4cCI6MTY1NTM4MDM4MiwidHlwZSI6MSwiYXV0aG9yaXRpZXMiOlsiYWxsIl0sImp0aSI6ImNlMDBlOTQ1LTEyZjUtNGRkNy05YjVjLTMyOGNkZjI3YWY0MyIsImNsaWVudF9pZCI6ImdlbWluaSJ9.qR74OEfYyz1occcwpewoI_VDfQD_o3fervlS4lYRtyU";
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
