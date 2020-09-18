# neucore_test_live
neucore face test for double camera use JAR

使用方法

github 下载最新的代码

将版本发布压缩包中,  
NeuSDK 文件夹下 libneucore.so 放到 neucore_test_live\app\libs\armeabi-v7a\ 目录

NeuSDK 文件夹下 nb/目录下所有的nb文件放在 neucore_test_live\app\src\main\assets\nb\ 目录下

NeuSDK_jar 文件夹下 NeuSDK.jar 放到 neucore_test_live\app\libs\ 目录

通过android studio 构建并运行

注意: 一定要确保libovxlib.so 加入到 vendor/etc/public.libraries.txt 文件中并重启过,否则算法会报dlopen 失败



neulink使用说明

安装

    拷贝neulink.jar 放到 app/libs目录下

集成

    服务注册【AndroidManifest.xml】

    在AndroidManifest.xml中添加下列内容

    ```

    <!-- Mqtt Service -->
    <service android:name="org.eclipse.paho.android.service.MqttService" />
    <!-- Log Service -->
    <service android:name="com.neucore.neulink.impl.LogService" />

    ```

代码集成

    实现一个XXXApplication extends android.app.Application
    在XXXApplication的onCreate()方法内添加如下代码
    ```
    ContextHolder.getInstance().setContext(this);
    /**
     * 用户人脸数据库服务
     */
    IUserService service = UserService.getInstance(this);
    /**
     * 集成Neulink
     */
    SampleConnector register = new SampleConnector(this,callback,service);

    ```
    
    扩展实现
    
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

配置文件管理

SD卡启用时存储在

Context.getExternalFilesDir()/

    + neucore/config 配置文件目录neuconfig.properties
    

配置文件介绍
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
    

人脸同步扩展实现,参考下列代码
SampleFaceListener.java实现完成其团队的人脸存储。。。。

人脸识别上报代码sample,参考下列代码
SampleFaceUpload.java


