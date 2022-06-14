# neulink 开发使用手册

## 描述

neulink sdk已经完成了mqtt网络的连接、断网重连机制；
实现了业务集成转发扩展机制；
实现了http登录授权回调机制
实现了Mqtt连接状态回调机制
实现了响应上传回调机制
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
    implementation 'org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5'
    implementation 'org.eclipse.paho:org.eclipse.paho.android.service:1.1.1'
    //implementation 'com.squareup.okhttp3:okhttp:4.5.0'
    implementation 'com.squareup.okhttp3:okhttp:3.12.0'
    implementation 'com.squareup.okio:okio:1.14.0'
    implementation 'com.lzy.net:okgo:3.0.4'
    implementation 'com.yanzhenjie:permission:2.0.0-rc4'
    implementation 'com.qianwen:okhttp-utils:3.8.0'
    implementation 'com.qianwen:update-app:3.5.2'//实现自我更新
    implementation 'commons-net:commons-net:3.8.0'
    implementation 'cn.hutool:hutool-all:5.6.0'
    implementation 'org.greenrobot:greendao:3.3.0' // add library
    implementation 'log4j:log4j:1.2.17'
    implementation 'pub.devrel:easypermissions:2.0.1'
```   
    
## 集成&扩展

### 集成

#### 配置
    服务注册【AndroidManifest.xml】

    在AndroidManifest.xml中添加下列内容

    ```
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

MyApplication.installSDK();

#### neulink服务退出

```

NeulinkService.getInstance().destroy();

```

### 时序图

![时序图](images/secquence.png)

### 扩展实现 

#### 参考 MyInstaller

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
     * MQTT 网络、消息扩展 参考 MyMqttCallbackImpl
     */
     IMqttCallBack mqttCallBack = new MyMqttCallbackImpl();
```

#### 扩展-设备服务
```
    /**
     * 设备服务扩展
     */
    
```
    
#### 扩展-设备信息上报
```
    /**
     * 设备信息上报扩展 参考 MyDeviceServiceImpl
     */
    IDeviceService deviceService = new MyDeviceServiceImpl();

```

#### 扩展-通用业务开发

0，消息订阅扩展；可以在NeulinkSubscriberFacde中查看，目前已经完成了【rmsg/req/${dev_id}/#、rrpc/req/${dev_id}/#、upld/res/${dev_id}/#】订阅;

1，实现payload的pojo对象

2，新增一个XXXProcessor继承实现GProcessor；同时XXX就是topic第四段；且首字母大写

eg：授权处理器
topic：rrpc/req/${dev_id}/${auth}/v1.0/${req_no}[/${md5}]；
processor：包名com.neucore.neulink.extend.auth；类命名为AuthProcessor;

```

package com.neucore.neusdk_demo.neulink.extend.auth;

import android.content.Context;

import com.neucore.neulink.impl.registry.ServiceRegistry;
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
        return JSonUtils.toObject(payload, AuthSyncCmd.class);
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
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(t.getCmdStr());
        res.setCode(result.getCode());
        res.setMsg(result.getMessage());
        res.setData(result.getData());
        return res;
    }

    @Override
    protected AuthSyncCmdRes fail(AuthSyncCmd t, String error) {
        AuthSyncCmdRes res = new AuthSyncCmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(t.getCmdStr());
        res.setCode(500);
        res.setMsg("失败");
        res.setData(error);
        return res;
    }

    @Override
    protected AuthSyncCmdRes fail(AuthSyncCmd t, int code, String error) {
        AuthSyncCmdRes res = new AuthSyncCmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(t.getCmdStr());
        res.setCode(code);
        res.setMsg("失败");
        res.setData(error);
        return res;
    }
}


```

3，定义xxxCmdListener实现ICmdListener;eg:AuthCmdListener

#### 注意事项

```

package com.neucore.neusdk_demo.neulink.extend.auth.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.data.AuthActionResultData;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.AuthActionResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.data.AuthItemResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.data.DeviceResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.data.DomainResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.data.LinkResult;
import com.neucore.neusdk_demo.neulink.extend.auth.request.AuthSyncCmd;

/**
 * 协议可以参考：授权下发 https://project.neucore.com/zentao/doc-view-82.html
 * 云端下发至设备端的命令侦听器
 * 所有业务处理都在这地方处理
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

        AuthActionResultData data = new AuthActionResultData();/*@TODO 构造返回数据*/
        data.add(deviceResult);
        data.add(domainResult);
        data.add(linkResult);
        data.add(authItemResult);

        result.setData(data);

        return result;
    }
}

```

4, listener 的doAction 返回值 AuthActionResult

```
package com.neucore.neusdk_demo.neulink.extend.auth.listener.result;

import com.neucore.neulink.impl.ActionResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.data.AuthActionResultData;

public class AuthActionResult extends ActionResult<AuthActionResultData/*响应体data部分*/> {
    
}

```

5, listener 的doAction 返回值 AuthActionResultData

```
package com.neucore.neusdk_demo.neulink.extend.auth.listener.result.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AuthActionResultData {
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
     * 外部扩展 参照 MyExtendCallbackImpl
     */
    IExtendCallback callback = new MyExtendCallbackImpl();
```


### 上报消息到云端

0，在NeulinkPublisherFacde中实现

1，在Apk应用中采用NeulinkService.getInstance().getPublisherFacde()获取消息发送接口进行进行消息发送;

```

    /**
     * 车牌抓拍上报
     * upld/req/carplateinfo/v1.0/${req_no}[/${md5}], qos=0
     */
    public void upldLic(String num,String color,String imageUrl,String cmpCode,String locationCode,String position)

    /**
     * 车牌抓拍上报
     * @param num
     * @param color
     * @param imageUrl
     * @param cmpCode
     * @param locationCode
     * @param position
     * @param callback
     */
    public void upldLic(String num, String color, String imageUrl, String cmpCode, String locationCode, String position, IResCallback callback)

    /**
     * 体温检测上报
     * upld/req/facetemprature/v1.0/${req_no}[/${md5}], qos=0
     */
    public void upldFacetmp(FaceTemp[] data)

    /**
     * 体温检测上报
     * @param data
     * @param callback
     */
    public void upldFacetmp(FaceTemp[] data, IResCallback callback)

    /**
     * 人脸抓拍上报
     * 1.2版本协议
     * @param url 人脸照片的url
     * @param info 人脸识别信息
     */
    public void upldFaceInfo$1$2(String url, FaceUpload12 info)

    /**
     * 人脸抓拍上报
     * @param url
     * @param info
     * @param callback
     */
    public void upldFaceInfo$1$2(String url, FaceUpload12 info, IResCallback callback)

    /**
     * 上报升级包下载进度
     * @param topicPrefix
     * @param reqId
     * @param progress
     */
    public void upldDownloadProgress(String topicPrefix,String reqId,String progress)

    /**
     * rmsg请求异步处理响应
     * rmsg/res/${biz}/${version}
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     */
    public void rmsgResponse(String biz,String version,String reqId,String mode,Integer code,String message,String payload)

    /**
     * rmsg请求异步处理响应
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     * @param callback
     */
    public void rmsgResponse(String biz, String version, String reqId, String mode, Integer code, String message, String payload, IResCallback callback)
    
    /**
     * 设备发起的主动请求【人脸抓拍、体温检测、车牌抓拍】
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     */
    public void upldRequest(String biz,String version,String reqId,String mode,Integer code,String message,Object payload)

    /**
     * 设备发起的主动请求【人脸抓拍、体温检测、车牌抓拍】
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     * @param callback
     */
    public void upldRequest(String biz, String version, String reqId, String mode, Integer code, String message, Object payload, IResCallback callback)

    /**
     * rrpc请求异步处理响应
     * rrpc/res/${biz}/${version}
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     */
    public void rrpcResponse(String biz,String version,String reqId,String mode,Integer code,String message,String payload)

    /**
     * rrpc请求异步处理响应
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     * @param callback
     */
    public void rrpcResponse(String biz, String version, String reqId, String mode, Integer code, String message, String payload, IResCallback callback)

```

### 异步响应注意事项

异步响应必须在NeulinkService.getInstance().isNeulinkServiceInited()==true之后调用，否则不会成功；

2，异步响应-绑定接收成功
```
    IPublishCallback iResCallback = new IPublishCallback<Result>() {
        @Override
        public Class<Result> getResultType() {
            return Result.class;
        }

        @Override
        public void onFinished(Result result) {
            Log.i(TAG, result.getReqId());
        }
    };
    //从数据库或者ActionListener中获取到获取到云端下发的Cmd【biz、协议版本、请求Id，命令模式】
    String biz = "binding";
    String version = "v1.0";
    String reqId = "3214323ewadfdsad";
    String mode = "bind";
    NeulinkService.getInstance().getPublisherFacde().rrpcResponse(biz, "v1.0", reqId, mode, 202, NeulinkConst.MESSAGE_PROCESSING, "",iResCallback);
    
```

3，异步响应-绑定同意
```
    IPublishCallback iResCallback = new IPublishCallback<Result>() {
        @Override
        public Class<Result> getResultType() {
            return Result.class;
        }

        @Override
        public void onFinished(Result result) {
            Log.i(TAG, result.getReqId());
        }
    };
    //从数据库或者ActionListener中获取到获取到云端下发的Cmd【biz、协议版本、请求Id，命令模式】
    String biz = "binding";
    String version = "v1.0";
    String reqId = "3214323ewadfdsad";
    String mode = "bind";
    NeulinkService.getInstance().getPublisherFacde().rrpcResponse(biz, "v1.0", reqId, mode, 200, NeulinkConst.MESSAGE_AGREE, "",iResCallback);
    
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
