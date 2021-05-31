# neusdk_demo使用说明

NeuSDK是Neucore实现的人脸人形相关的SDK，支持Linux和Android操作系统，包含多项专为嵌入式系统设计的算法模型。算法包在Amlogic多款AI芯片上优化和裁剪，在实时性和精度上都达到业内领先水准。

neusdk_demo是一个开源工程，为了演示如何使用NeuSDK。

将版本发布压缩包中,  
NeuSDK 文件夹下 libneucore.so 放到 neusdk_demo\app\libs\armeabi-v7a\ 目录

NeuSDK 文件夹下 nb/目录下所有的nb文件放在 neusdk_demo\app\src\main\assets\nb\ 目录下

NeuSDK_jar 文件夹下 NeuSDK.jar 放到 neusdk_demo\app\libs\ 目录

通过android studio 构建并运行

注意: 一定要确保libovxlib.so 加入到 vendor/etc/public.libraries.txt 文件中并重启过,否则算法会报dlopen 失败


# 算法

|                |功能描述|应用场景                        |
|----------------|-------------------------------|-----------------------------|
|人脸检测|单人脸检测、多人脸检测、验证活体（单目、双目）|门禁场景|
|人脸识别|1：1、1：N、支持口罩模式|门禁场景|
|人脸属性|年龄、性别、是否戴口罩、是否戴帽子、是否戴眼镜、表情|广告精准推送场景|
|人脸关键点|106个人脸关键点|疲劳检测、瞌睡、闭眼、专注度识别等场景|
|人形检测|头肩检测、全身检测|人数统计场景|
|骨骼关节点|全身18个骨骼关节点|识别卧倒、站立、健身等场景|
|手势识别|18种手势（1-10、Ok、No、Yes、比心、拳头、手掌）|手势控制场景|
|背景分割|将人和背景分开，实现虚拟背景，背景替换|会议室、远程教学场景|


# neulink集成及扩展开发使用说明

### 注意事项

apk升级建议采用增量升级方式【即：patch方式，这样可以保留系统的业务数据】

## 安装

    拷贝neulink.jar 放到 app/libs目录下

## 集成

    服务注册【AndroidManifest.xml】

    在AndroidManifest.xml中添加下列内容

    ```

    <!-- Mqtt Service -->
    <service android:name="org.eclipse.paho.android.service.MqttService" />
    <!-- Log Service -->
    <service android:name="com.neucore.neulink.impl.LogService" />

    ```

## 代码集成

    实现一个XXXApplication extends android.app.Application
    在XXXApplication的onCreate()方法内添加如下代码
    ```
    ContextHolder.getInstance().setContext(this);
    /**
     * 用户人脸数据库服务
     */
    IUserService service = UserService.getInstance(this);
    
    /**
     * 开始集成Neulink
     */
     扩展配置集成
    Properties extConfig = new Properties();
    /**
     * 配置扩展: key可以参考ConfigContext内的定义
     */
    extConfig.setProperty(ConfigContext.MQTT_SERVER,"tcp://mqtt.neucore.com:1883");
    /**
     * ⚠️注意；上报通道默认mqtt，如果需要http支持请在配置文件或者下列代码【外部扩展配置】的方式实现
     * 取消下列备注
     * 
     */
    //extConfig.setProperty(ConfigContext.UPLOAD_CHANNEL,"1");//end2cloud neulink 协议 切换至https通道
    //extConfig.setProperty(ConfigContext.REGIST_SERVER,"https://data.neuapi.com/v1/device/regist");//end2cloud neulink 注册的http服务地址
    SampleConnector register = new SampleConnector(this,callback,service,extConfig);

    ```

## neulink服务退出

```

NeulinkService.getInstance().destroy();

```

## 扩展实现（参考下列SampleXXXX）
    
    ```
    /**
     * 外部扩展
     */
    IExtendCallback callback = new IExtendCallback() {
        @Override
        public void onCallBack() {
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
    ```

## 配置文件管理

SD卡启用时存储在，存储在下列文件的可以通过neulink远程管理

Context.getExternalFilesDir()/

    + neucore/config 配置文件目录neuconfig.properties
    

## 配置文件介绍

neuconfig.properties
```
#MQTT-Server配置项变更之后应用需要重新启动
#测试环境tcp://10.18.9.99:1883
#生产环境tcp://10.18.105.254

MQTT-Server=tcp://mqtt.neucore.com:1883

#下列配置项不需要启动即能生效[默认]
Storage.Type=OSS
#################################################
#OSS配置项
OSS.EndPoint=xxxx
OSS.BucketName=ddd
OSS.AccessKeyID=xxxx
OSS.AccessKeySecret=xxxdd
#################################################

```
    
Context.getExternalCacheDir()/

    + logs 运行日志目录
    + tmp 临时目录

SD卡没有启用时存储在

Context.getCacheDir()/

    + neucore/logs 系统运行日志目录
    + neucore/tmp 系统临时目录


Context.getFilesDir()/

    + neucore/config 配置文件目录
    
## 配置扩展机制
```
扩展配置集成
Properties extConfig = new Properties();
/**
 * 配置扩展: key可以参考ConfigContext内的定义
 */
extConfig.setProperty(ConfigContext.MQTT_SERVER,"tcp://mqtt.neucore.com:1883");
/**
 * ⚠️注意；上报通道默认mqtt，如果需要http支持请在配置文件或者下列代码【外部扩展配置】的方式实现
 * 取消下列备注
 * 
 */
//extConfig.setProperty(ConfigContext.UPLOAD_CHANNEL,"1");//end2cloud neulink 协议 切换至https通道
//extConfig.setProperty(ConfigContext.REGIST_SERVER,"https://data.neuapi.com/v1/device/regist");//end2cloud neulink 注册的http服务地址
SampleConnector register = new SampleConnector(this,callback,service,extConfig);
```

## 人脸下发扩展

参考下列代码SampleFaceListener.java实现完成其团队的人脸存储

## 人脸识别上报

参考下列代码SampleFaceUpload.java


# 联系
任何技术或商务问题，请发送邮件至 support@neucore.com
