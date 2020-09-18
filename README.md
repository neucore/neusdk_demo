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
    ContextHolder.getInstance().setContext(getApplicationContext());
    Intent logService =  new Intent (this, LogService.class);
    startService( logService );

    if (android.os.Build.VERSION.SDK_INT > 9) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
    //注册服务
    NeulinkService service = NeulinkService.getInstance();
    service.buildMqttService(ConfigContext.getInstance().getConfig(ConfigContext.MQTT_SERVER));
    
    //neulink扩展入口: 人脸库同步扩展注册
    ListenerFactory.getInstance().setFaceListener(new SampleFaceListener());
    
    //处理初始化应用carsh【异常日志上报】
    CarshHandler crashHandler = CarshHandler.getIntance();
    crashHandler.init();

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

MQTT-Server=tcp://10.18.9.99:1883

#下列配置项不需要启动即能生效
Storage.Type=OSS
#################################################
#OSS配置项
OSS.EndPoint=https://oss-cn-shanghai.aliyuncs.com
OSS.BucketName=neudevice
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
    

人脸同步扩展实现；参考下列代码faceListener实现完成其团队的人脸存储。。。。


```
 //人脸识别上报代码sample
NeulinkService service = NeulinkService.getInstance();
NeulinkPublisherFacde publisher = service.getPublisherFacde();

FaceInfo faceDetect = new FaceInfo();
//设置抓拍到的时间点unixtime_stamp
faceDetect.setTimestamp(12345);
//类型: 1 检测  2 检测+识别 3 检测+人脸属性
faceDetect.setType(1);
//检测对象信息
DetectInfo detectInfo = new DetectInfo();
//发送的人脸图片/face_id/背景图的地址 oss存储的路径【不包括EndPoint内容】
detectInfo.setDir("290b1000010e1200000337304e424e50/20200624/073828_71");
//人脸照片文件名
detectInfo.setFaceImage("face_0328.jpg");

//调用算法得到的人脸id
detectInfo.setFaceId("0");

faceDetect.setDetectInfo(detectInfo);

//识别对象信息【可选，识别出来的需要】
RecogInfo recogInfo = new RecogInfo();
//是否识别 0:未识别；1：已识别
recogInfo.setRecognized(1);
//该人脸和数据中所有条目进行对比得到的最大相似度
recogInfo.setSimilarityValue("0.5736");
//人脸相似度阈值
recogInfo.setSimilarityThreshold("0.5");
//卡号
recogInfo.setExtId("434343243");
//姓名
recogInfo.setName("张三");
//识别对象
faceDetect.setRecogInfo(recogInfo);

publisher.upldFaceInfo(faceDetect);


