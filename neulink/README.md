# neulink 开发使用手册

## 描述

+ neulink sdk已经完成了mqtt网络的连接、断网重连机制；
+ 实现了业务集成转发扩展机制；
+ 实现了http登录授权回调机制
+ 实现了Mqtt连接状态回调机制
+ 实现了响应上传回调机制
+ 实现了消息、用户、设备等服务默认实现及其扩展机制

### 图

#### 通道通信说明图
![通道通信图](https://github.com/neucore/neusdk_demo/blob/master/neulink/images/mqtt.png)

#### 时序图说明图

##### Http_mqtt时序图
![http_mqtt时序图](https://github.com/neucore/neusdk_demo/blob/master/neulink/images/http_mqtt_seq.png)

##### Mqtt_mqtt时序图
![mqtt时序图](https://github.com/neucore/neusdk_demo/blob/master/neulink/images/mqtt_mqtt_seq.png)

### 协议说明

#### 鉴权规范

##### 老版本
+ mqttClientId
  + 当用户启用(YekerID)规则时：${YekerID}@${macAddress}
  + 当用户启用(自定义ID)规则时：${自定义ID}@${macAddress}
+ mqttHost
  + 通过【远程配置接口】获取
+ mqttUsername
  + 通过【远程配置接口】获取
+ mqttPassword
  + 通过【远程配置接口】获取
##### 一机一密
+ 新版本所有设备都必须是一机一密机制
+ 授权时每台设备都会有
  + productKey：产品id
  + deviceName：椰壳Id或者自定义Id
  + deviceSecret：设备密钥，建议设备加密保存
+ 设备连接规则
  + 当前时间戳：timestamp
+ mqttUsername:
  + String mqttUsername = ${deviceName} + "|" + ${productKey};
+ mqttPassword
  + hmacSha256(plainTxt,deviceSecret)签名后的值
```java
public class CryptoUtil {
    private static String hmac(String plainText, String key, String algorithm, String format) throws Exception {
        if (plainText == null || key == null) {
            return null;
        }

        byte[] hmacResult = null;

        Mac mac = Mac.getInstance(algorithm);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), algorithm);
        mac.init(secretKeySpec);
        hmacResult = mac.doFinal(plainText.getBytes());
        return String.format(format, new BigInteger(1, hmacResult));
    }
    public static String hmacSha256(String plainText, String key) throws Exception {
        return hmac(plainText,key,"HmacSHA256","%064x");
    }
}
```  
+ 其中macAddress：去除冒号连接符且大写，即：02AD3D0110D8
+ 其中plainTxt
```java
String plainTxt = "clientId:" + ${productKey} + "." + ${deviceName} + ",deviceName:" + ${deviceName} 
    + ",productKey:" + ${productKey} +",macAddress:" + ${macAddress} + "timestamp:" + ${timestamp}
```
+ mqttClientId
  + 当用户启用(YekerID)规则时：${productKey}|${YekerID}@${macAddress}|timestamp=${timestamp},securemode=2,signmethod=hmacsha256,_v=paho-1.0.0|
  + 当用户启用(自定义ID)规则时：${productKey}|${自定义ID}@${macAddress}|timestamp=${timestamp},securemode=2,signmethod=hmacsha256,_v=paho-1.0.0|
#### topic 规范

##### topic[1.0]

+ 0，消息订阅扩展；可以在NeulinkSubscriberFacde中查看，目前已经完成了【rmsg/req/${dev_id}/#、rrpc/req/${dev_id}/#、upld/res/${dev_id}/#】订阅;

+ 1，实现payload的pojo对象【xxxCmd **extends Cmd**、xxxRes **extends CmdRes**、xxxActionResult **extends ActionResult**】

+ 2，新增一个XXXProcessor继承实现GProcessor；同时XXX就是topic第四段；且首字母大写

  + eg：授权处理器
  + topic：rrpc/req/${dev_id}/${auth}/v1.0/${req_no}[/${md5}]；
  + processor：包名com.neucore.neulink.extend.auth；类命名为AuthProcessor;

##### topic[1.2]

+ 0，消息订阅扩展；可以在NeulinkSubscriberFacde中查看，目前已经完成了【rmsg/req/${dev_id}/#、rrpc/req/${dev_id}/#、upld/res/${dev_id}/#】订阅;

+ 1，实现payload的pojo对象【xxxCmd **extends Cmd**、xxxRes **extends CmdRes**、xxxActionResult **extends ActionResult**】

+ 2，新增一个XXXProcessor继承实现GProcessor；同时XXX就是topic第四段；且首字母大写

  + eg：授权处理器
  + topic：rrpc/req/${dev_id}/v1.0；
  + processor：包名com.neucore.neulink.extend.auth；类命名为AuthProcessor; 

+ 3，变化点&注意事项：

  + 0，新增了header

  + 1，biz、req_no、md5三个字段从topic移到了header
##### topic[一机一密]

+ 0，消息订阅扩展；可以在NeulinkSubscriberFacde中查看，目前已经完成了【${productId}/rmsg/req/${dev_id}/#、${productId}/rrpc/req/${dev_id}/#、${productId}/upld/res/${dev_id}/#】订阅;

+ 1，实现payload的pojo对象【xxxCmd **extends Cmd**、xxxRes **extends CmdRes**、xxxActionResult **extends ActionResult**】

+ 2，新增一个XXXProcessor继承实现GProcessor；同时XXX就是topic第四段；且首字母大写

  + eg：授权处理器
  + topic：rrpc/req/${dev_id}/v1.0；
  + processor：包名com.neucore.neulink.extend.auth；类命名为AuthProcessor;

+ 3，变化点&注意事项：

  + 0，新增了header

  + 1，biz、req_no、md5三个字段从topic移到了header

#### 协议体规范【neulink[2.0]】

+ 变化点&注意事项：

+ 新增了统一的data对象【这个对象完全由具体的业务开发自己定义】

##### 请求协议[2.0]

```json
{
    "headers":
       {
             "biz":"${biz}",//业务标识：[qlib|blib….]
             "reqNo":"${reqNo}",//请求ID
             "md5":"${md5}",//消息体的md5
             "time":"${time}",//请求时间
             "${keyn}":"${valuen}"//自定义
       },
      "data": {}    //可选
}
```
##### 响应协议[2.0]
```json
{    
    "headers":
        {
            "code":200, //响应码
            "msg":"success", //响应消息
            "biz":"${biz}",//业务标识：[qlib|blib….]
            "reqNo":"${reqNo}",//请求ID
            "md5":"${md5}",//消息体md5
            "devid":"${devid}",//设备ID
            "custid":"${custid}",//租户ID
            "storeid":"${storeid}",//门店场所ID
            "zoneid":"${zoneid}",//集群ID
            "time":"${time}",//请求时间
            "${keyn}":"${valuen}"//自定义
        },    
    "data": {}         //可选
}
```
##### 上报协议[2.0]
```json
{    
    "headers":
        {
            "biz":"${biz}",//业务标识：[qlib|blib….]
            "reqNo":"${reqNo}",//请求ID
            "md5":"${md5}",//消息体md5
            "devid":"${devid}",//设备ID
            "custid":"${custid}",//租户ID
            "storeid":"${storeid}",//门店场所ID
            "zoneid":"${zoneid}",//集群ID
            "time":"${time}",//请求时间
            "${keyn}":"${valuen}"//自定义
        },    
    "data": {}         //可选
}
```

### 升级注意事项

apk升级建议采用增量升级方式【即：patch方式，这样可以保留系统的业务数据】

## 安装

    拷贝neulink.jar 放到 app/libs目录下
    
## 依赖库
```
    implementation files('libs/xloger.jar')
    implementation 'androidx.appcompat:appcompat:1.1.0'
    implementation 'androidx.localbroadcastmanager:localbroadcastmanager:1.0.0'
    implementation 'com.google.code.gson:gson:2.9.0'
    implementation 'com.aliyun.dpa:oss-android-sdk:2.9.13'
    implementation 'org.eclipse.paho:org.eclipse.paho.mqttv5.client:1.2.5'
    implementation 'org.eclipse.paho:org.eclipse.paho.android.service:1.1.1'
    implementation 'com.squareup.okhttp3:okhttp:3.12.0'
    implementation 'com.squareup.okio:okio:1.14.0'
    implementation 'com.qianwen:okhttp-utils:3.8.0'
    implementation 'commons-net:commons-net:3.8.0'
    implementation 'cn.hutool:hutool-all:5.6.0'
    implementation 'com.blankj:utilcode:1.30.6'
    implementation 'log4j:log4j:1.2.17'
```   
    
## 集成&扩展

### 集成

#### 配置
    服务注册【AndroidManifest.xml】

    在AndroidManifest.xml中添加下列内容

```xml
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

```java
    MyApplication.installSDK();
```


#### neulink服务退出

```java

    NeulinkService.getInstance().destroy();

```

#### 扩展实现 

##### 参考 MyInstaller[详见代码]

##### 安全认证

###### 扩展-HTTP安全登录[过期]
```java
    /**
    * 老版本需要实现 
     * HTTP(S)安全登录
     * 参考：MyLoginCallbackImpl
     */
    

```
###### [http请求]新版设备http请求【推荐】
+ 每台设备都烧入配置了
  + productKey 产品id
  + deviceName 椰壳Id或者自定义设备id
  + deviceSecret 设备密钥
+ header
  + clientId
    + 当用户启用(YekerID)规则时：${productKey}|${YekerID}@${mac地址}|timestamp=${timestamp},securemode=2,signmethod=hmacsha256,_v=paho-1.0.0|
    + 当用户启用(自定义ID)规则时：${productKey}|${自定义ID}@${mac地址}|timestamp=${timestamp},securemode=2,signmethod=hmacsha256,_v=paho-1.0.0|
  + sign: 签名
```java
        /**
        * 生成签名
        */
        SecuretSign securetSign = ServiceRegistry.getInstance().getDeviceService().sign();
        
        /**
        * 新增http请求头
        * "clientId": ${clientId}
        * "sign": ${sign}
        * 
        */
        Map<String,String> headers = new HashMap();
        headers.put("clientId",securetSign.getClientId());
        headers.put("sign",securetSign.getSign());
        String response = NeuHttpHelper.get(context,headers, reqId,String url);
```
##### 扩展-MQTT联网状态

```java
    /**
     * MQTT 网络、消息扩展 
     * 参考 MyMqttCallbackImpl
     */
```

##### 扩展-设备服务
```java
    /**
     * 设备服务扩展【实现productKey、deviceName、deviceSecret 烧录信息的读取】
     * 参考：MyDeviceExtendServiceImpl 
     */
    
```
    
##### 扩展-设备信息扩展
```java
    /**
     * 设备信息上报扩展 参考 MyDeviceServiceImpl
     * 参考：MyDeviceExtendInfoCallBack
     */

```

##### 扩展-Neulink外部扩展注册器
```java
    /**
     * 外部扩展注册器
     * 参考：MyBizExtendRegistCallbackImpl
     */
```

##### 扩展-权限检测扩展
```java
    /**
     * 默认：READ_EXTERNAL_STORAGE WRITE_EXTERNAL_STORAGE 权限检测
     * 参考：MyPermissionChecker
     */
```

##### 扩展-文件下载器
```java
    
    /**
     * 下载器使用方式
     * 文件下载器【人脸目标库、OTA升级】
     * 单线程文件下载器：HttpDownloader
     * 多线程文件下载器：HttpResumeDownloader
     * Oss单线程文件下载器：OssDownloader
     * Oss多线程文件下载器：OssResumeDownloader
     */
    IDownloder downloader = ServiceRegistry.getInstance().getDownloder();
    File saveFile = downloader.start(ContextHolder.getInstance().getContext(),cmd.getReqNo(),cmd.getUrl(),new IDownloadProgressListener() {
        @Override
        public void onDownload(Double percent) {
            //TODO
        }
        @Override
        public void onFinished(File file){
            /**
             * 开始安装处理
             */
            Log.i(TAG,"成功下载完成");
        }
    } );
    //下载完毕
    //TODO 业务
```


##### 扩展-系统属性改变侦听器
可以自定义PropChgListener，来同步系统属性设置信息到设备数据库记录；
eg：人脸识别的时候，摄像头抓到图片，通过算法提起人脸特征； 与设备端数据库人脸特征库进行比较，当数据库某一条记录为debug数据库时，
则其后续处理相关日志级别为debug【方便调试】

```java
    /**
     * 系统属性修改侦听器
     */
    IPropChgListener listener = new MyPropChgListener();
```

##### 扩展-通用业务开发

###### 业务扩展实现步骤

+ 0，消息订阅扩展；可以在NeulinkSubscriberFacde中查看，目前已经完成了【rmsg/req/${dev_id}/#、rrpc/req/${dev_id}/#、upld/res/${dev_id}/#】订阅;
+ 1，实现payload的pojo对象【xxxCmd **extends NewCmd<xxReqData>**、xxxRes **extends NewCmdRes<xxxResData>**、xxxActionResult **extends ActionResult**】
+ 2，新增一个XXXProcessor继承实现GProcessor；同时XXX就是topic第四段；且首字母大写
  + eg：授权处理器
  + topic：rrpc/req/${dev_id}/v1.0；
  + processor：包名com.neucore.neulink.extend.auth；类命名为AuthProcessor;
+ 3，定义xxxCmdListener实现ICmdListener;eg:AuthCmdListener
+ 4, listener 的doAction 返回值 AuthActionResult
+ 5, listener 的doAction 返回值 AuthActionResultData

###### Processor样例

```java

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

###### CmdListener样例

```java

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

###### ActionResult样例

```java
package com.neucore.neusdk_demo.neulink.extend.auth.listener.result;

import com.neucore.neulink.impl.ActionResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.data.AuthActionResultData;

public class AuthActionResult extends ActionResult<AuthActionResultData/*响应体data部分*/> {
    
}

```
###### ActionResultData样例

```java
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

##### 扩展业务集成

参照：MyApplication内installSDK()方法；

```
    /**
     * 外部扩展 参照 MyBizExtendRegistCallbackImpl
     */
    IExtendCallback callback = new MyBizExtendRegistCallbackImpl();
```

参照：MyBizExtendRegistCallbackImpl.onCallBack()方法；

```java

    ProcessRegistry.regist(NeulinkConst.NEULINK_BIZ_AUTH,new AuthProcessor(),new AuthCmdListener());
    
    ProcessRegistry.regist(NeulinkConst.NEULINK_BIZ_BINDING,new BindProcessor(),new BindCmdListener());
    
    /**
     * SDK 自定义业务扩展实现
     * 框架已经实现消息的接收及响应处理机制
     * doAction返回结果后框架会把处理结果返回给云端；同时把云端处理状态返回给HellResCallback
     * 新业务可以参考Hello业务的实现业务就行
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
```


##### 上报消息到云端

0，在NeulinkPublisherFacde中实现

1，在Apk应用中采用NeulinkService.getInstance().getPublisherFacde()获取消息发送接口进行进行消息发送;

```java

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
     * 设备发起的主动请求【人脸抓拍、体温检测、车牌抓拍、设备配置等上传】
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
     * 设备发起的主动请求【人脸抓拍、体温检测、车牌抓拍、设备配置等上传】
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

##### 异步响应注意事项

异步响应必须在NeulinkService.getInstance().isNeulinkServiceInited()==true之后调用，否则不会成功；

2，异步响应-绑定接收成功
```java
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

3，异步响应-绑定业务处理回调
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
    
### 配置扩展机制
```java
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

+ 0，优先级【扩展配置>配置文件>框架默认配置】
+ 1,扩展配置使用方式：参考MyApplication
+ 2，配置文件使用方式：参考ConfigContext的实现，这个实现可以通过云端管理
+ 3，默认配置：参考：ConfigContext内的defaultConfig【加密写死】

## 人脸下发扩展

+ 参考下列代码SampleFaceListener.java实现完成其团队的人脸存储

## 人脸识别上报

参考下列代码SampleFaceUpload.java

## 通用图片&文件上传

StorageFactory.getInstance().uploadBak("/sdcard/twocamera/icon/1593399670069.jpg", UUID.randomUUID().toString(),1);

## debug规范

+ 单个请求debug
  在消息topic的末尾加上【/debug】即可开启当前请求的debug机制，不压缩，打印更详细的日志等；
+ 单条数据
  通过设置系统属性的机制动态设置[eg:android setprop]
    + 让人员Id为22的数据为debug数据，即：setprop person.id.22 on;
    + 让人员Id为22的数据为debug正常数据，即：setprop person.id.22 off;
