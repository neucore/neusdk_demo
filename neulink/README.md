# neulink 开发使用手册

## 描述

neulink sdk已经完成了mqtt网络的连接、断网重连机制；
实现了业务集成转发扩展机制；
实现了http登录授权回调机制
实现了Mqtt连接状态回调机制
实现了消息、用户、设备等服务默认实现及其扩展机制

### 注意事项

apk升级建议采用增量升级方式【即：patch方式，这样可以保留系统的业务数据】

## 安装

    拷贝neulink.jar 放到 app/libs目录下
    
## 依赖库
```
    implementation 'androidx.appcompat:appcompat:1.1.0'
    implementation 'androidx.localbroadcastmanager:localbroadcastmanager:1.0.0'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'androidx.test.ext:junit:1.1.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
    implementation 'com.google.code.gson:gson:2.8.5'
    implementation 'com.aliyun.dpa:oss-android-sdk:2.7.0'
    implementation 'org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.1'
    implementation 'org.eclipse.paho:org.eclipse.paho.android.service:1.1.1'
    implementation 'com.squareup.okhttp3:okhttp:4.5.0'
    implementation 'com.lzy.net:okgo:3.0.4'
    implementation 'com.yanzhenjie:permission:2.0.0-rc4'
    implementation 'com.qianwen:okhttp-utils:3.8.0'
    implementation 'com.qianwen:update-app:3.5.2'//实现自我更新
    implementation 'commons-net:commons-net:3.8.0'
    implementation 'cn.hutool:hutool-all:5.6.0'
    implementation 'org.greenrobot:greendao:3.3.0' // add library
```   
    
## 集成&扩展

### 集成

#### 配置
    服务注册【AndroidManifest.xml】

    在AndroidManifest.xml中添加下列内容

    ```

    <!-- Mqtt Service -->
    <service android:name="org.eclipse.paho.android.service.MqttService" />
    <!-- Log Service -->
    <service android:name="com.neucore.neulink.impl.LogService" />

    <uses-sdk
            android:minSdkVersion="21"
            android:targetSdkVersion="28" />
    
    <!--允许应用程序改变网络状态-->
    <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE"/>

    <!--允许应用程序改变WIFI连接状态-->
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>

    <!--允许应用程序访问有关的网络信息-->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>

    <!--允许应用程序访问WIFI网卡的网络信息-->
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>

    <!--允许应用程序完全使用网络-->
    <uses-permission android:name="android.permission.INTERNET"/>
  
    ```
#### 代码集成

参照：MyApplication内installSDK()方法；

#### neulink服务退出

```

NeulinkService.getInstance().destroy();

```

### 扩展

#### 扩展-HTTP安全登录
```
    /**
     * HTTP(S)安全登录 loginCallback
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

```

#### 扩展-MQTT联网状态

```
    /**
     * MQTT 网络、消息扩展
     */
    IMqttCallBack mqttCallBack = new IMqttCallBack() {
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            /**
             * 可以用在APP交互提示等
             */
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
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            /**
             * 可以用在APP交互提示等
             */
        }

        @Override
        public void connectSuccess(IMqttToken arg0) {
            /**
             * 可以用在APP交互提示等
             */
        }

        @Override
        public void connectFailed(IMqttToken arg0, Throwable arg1) {
            /**
             * 可以用在APP交互提示等
             */
        }
    };

```

#### 扩展-设备服务
```
    /**
     * 设备服务扩展
     */
    IDeviceService deviceService = new IDeviceService() {
        @Override
        public String getExtSN() {
            /**
             * 需要获取设备唯一标识【自定义，eg：YekerID】
             */
            return DeviceUtils.getCPUSN(getContext());
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
```
    
#### 扩展-设备信息上报
```
    /**
     * 设备信息上报扩展
     */
    IExtendInfoCallback extendInfoCallback = new IExtendInfoCallback(){
        @Override
        public List<SoftVInfo> getSubApps() {
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
    };
    
```

#### 扩展-通用业务开发

0，消息订阅扩展；可以在NeulinkSubscriberFacde中查看，目前已经完成了【rmsg/req/${dev_id}/#、rrpc/req/${dev_id}/#、upld/res/${dev_id}/#】订阅;

1，实现payload的pojo对象

2，新增一个XXXProcessor继承实现GProcessor；同时XXX就是topic第四段；且首字母大写

eg：授权处理器
topic：rrpc/req/${dev_id}/${auth}/v1.0/${req_no}[/${md5}]；
processor：包名com.neucore.neulink.extend.auth；类命名为AuthProcessor;

```

import android.content.Context;

import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neusdk_demo.neulink.extend.auth.request.AuthSyncCmd;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.AuthActionResult;
import com.neucore.neusdk_demo.neulink.extend.auth.response.AuthSyncCmdRes;

/**
 * 设备授权下发
 * AuthSyncCmd:请求对象，
 * AuthSyncCmdRes：响应对象
 * AuthActionResult:actionListener的返回类型
 */
public class AuthProcessor  extends GProcessor<AuthSyncCmd, AuthSyncCmdRes, AuthActionResult> {

    public AuthProcessor(){
        this(ContextHolder.getInstance().getContext());
    }

    public AuthProcessor(Context context) {
        super(context);
    }

    @Override
    public AuthSyncCmd parser(String payload) {
        return (AuthSyncCmd) JSonUtils.toObject(payload, AuthSyncCmd.class);
    }

    /**
     *
     * @param t 同步请求
     * @param result listener.doAction 的返回值
     * @return
     */
    @Override
    protected AuthSyncCmdRes responseWrapper(AuthSyncCmd t, AuthActionResult result) {
        AuthSyncCmdRes res = new AuthSyncCmdRes();
        res.setCmdStr(t.getCmdStr());
        res.setCode(200);
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setData(result);
        res.setMsg("成功");
        return res;
    }

    @Override
    protected AuthSyncCmdRes fail(AuthSyncCmd t, String error) {
        AuthSyncCmdRes res = new AuthSyncCmdRes();
        res.setCmdStr(t.getCmdStr());
        res.setCode(500);
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setData(error);
        res.setMsg("失败");
        return res;
    }

    @Override
    protected AuthSyncCmdRes fail(AuthSyncCmd t, int code, String error) {
        AuthSyncCmdRes res = new AuthSyncCmdRes();
        res.setCmdStr(t.getCmdStr());
        res.setCode(code);
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setData(error);
        res.setMsg("失败");
        return res;
    }
}


```

3，定义xxxCmdListener实现ICmdListener;eg:AuthCmdListener

#### 注意事项

切记 listener 的doAction 返回值是 响应协议的data部分

```
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.AuthActionResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.AuthItemResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.DeviceResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.DomainResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.LinkResult;
import com.neucore.neusdk_demo.neulink.extend.auth.request.AuthSyncCmd;

/**
 * 协议可以参考：授权下发 https://project.neucore.com/zentao/doc-view-82.html
 */
public class AuthCmdListener implements ICmdListener<AuthActionResult, AuthSyncCmd> {
    @Override
    public AuthActionResult/*Auth Action 返回处理结果*/ doAction(NeulinkEvent<AuthSyncCmd/*授权指令*/> event) {
        AuthSyncCmd cmd = event.getSource();
        /**
         * @TODO: 实现业务。。。
         */
        DeviceResult deviceResult = new DeviceResult();/*@TODO: 构造返回结果*/
        DomainResult domainResult = new DomainResult();/*@TODO: 构造返回结果*/
        LinkResult linkResult = new LinkResult();/*@TODO: 构造返回结果*/
        AuthItemResult authItemResult = new AuthItemResult();/*@TODO: 构造返回结果*/
        AuthActionResult result = new AuthActionResult();/*@TODO: 构造返回结果*/
        /**
         * @TODO: 构造返回结果
         */
        result.add(deviceResult);
        result.add(domainResult);
        result.add(linkResult);
        result.add(authItemResult);
        return result;
    }
}
```

4, listener 的doAction 返回值 AuthActionResult

```
import com.google.gson.annotations.SerializedName;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.AuthItemResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.DeviceResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.DomainResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.LinkResult;

import java.util.ArrayList;
import java.util.List;

public class AuthActionResult {

    @SerializedName("device")
    private List<DeviceResult> devices;
    @SerializedName("domain")
    private List<DomainResult> domains;
    @SerializedName("link")
    private List<LinkResult> links;
    @SerializedName("auth")
    private List<AuthItemResult> authItems;

    public List<DeviceResult> getDevices() {
        return devices;
    }

    public void add(DeviceResult result){
        if(result!=null && getDevices()==null){
            devices = new ArrayList<>();
        }
        if(result!=null ){
            devices.add(result);
        }
    }

    public void setDevices(List<DeviceResult> devices) {
        this.devices = devices;
    }

    public List<DomainResult> getDomains() {
        return domains;
    }

    public void add(DomainResult result){
        if(result!=null && getDomains()==null){
            domains = new ArrayList<>();
        }
        if(result!=null ){
            domains.add(result);
        }
    }

    public void setDomains(List<DomainResult> domains) {
        this.domains = domains;
    }

    public List<LinkResult> getLinks() {
        return links;
    }

    public void add(LinkResult result){
        if(result!=null && getLinks()==null){
            links = new ArrayList<>();
        }
        if(result!=null ){
            links.add(result);
        }
    }

    public void setLinks(List<LinkResult> links) {
        this.links = links;
    }

    public List<AuthItemResult> getAuthItems() {
        return authItems;
    }

    public void add(AuthItemResult result){
        if(result!=null && getAuthItems()==null){
            authItems = new ArrayList<>();
        }
        if(result!=null ){
            authItems.add(result);
        }
    }
    public void setAuthItems(List<AuthItemResult> authItems) {
        this.authItems = authItems;
    }
}
```



#### 扩展业务集成

参照：MyApplication内installSDK()方法；

```
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
                    /
                    return DeviceUtils.getCPUSN(getContext());
                }
                public DeviceInfo getInfo(){
                    /**
                     * @TODO 需要实现
                     */
                    return null;
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
             * 固件$APK 扩展
             */
            ListenerFactory.getInstance().setFireware$ApkListener(new ApkUpgrdActionListener());

            /**
             * 备份现 扩展
             */
            ListenerFactory.getInstance().setBackupListener(new BackupActionListener());

            /**
             * neulink消息线性处理存储服务
             */
            ServiceFactory.getInstance().setMessageService(new MessageService(getContext()));

            /**
             * 自定义Processor注册
             * 框架已经实现消息的接收及响应处理机制
             * 新业务可以参考Auth业务的实现业务就行
             */
            NeulinkProcessorFactory.regist("auth",new AuthProcessor(),new AuthCmdListener());
        }
    };

```


### 上报消息到云端

0，在NeulinkPublisherFacde中实现

1，在Apk应用中采用NeulinkService.getInstance().getPublisherFacde()获取消息发送接口进行进行消息发送;


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

0，优先级【扩展配置>配置文件>框架默认配置】


1,扩展配置使用方式：参考MyApplication

2，配置文件使用方式：参考ConfigContext的实现，这个实现可以通过云端管理

3，默认配置：参考：ConfigContext内的defaultConfig【加密写死】

## 人脸下发扩展

参考下列代码SampleFaceListener.java实现完成其团队的人脸存储

## 人脸识别上报

参考下列代码SampleFaceUpload.java


## 通用图片&文件上传

StorageFactory.getInstance().uploadBak("/sdcard/twocamera/icon/1593399670069.jpg", UUID.randomUUID().toString(),1);
