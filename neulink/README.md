# neulink 开发使用手册

# neulink集成及扩展开发使用说明

### 注意事项

apk升级建议采用增量升级方式【即：patch方式，这样可以保留系统的业务数据】

## 安装

    拷贝neulink.jar 放到 app/libs目录下
    
## 集成&扩展

### 集成
+ 配置文件
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
+ 代码调用    
    neulink集成参考MyApplication

## neulink服务退出

```

NeulinkService.getInstance().destroy();

```

## 扩展

## 扩展-安全登录

ILoginCallback loginCallback = new ILoginCallback() {
        @Override
        public String login() {
            /**
             * 实现登录返回token
             */
            return null;
        }
    };

## 扩展-设备服务

```
ServiceFactory.getInstance().setDeviceService(new IDeviceService() {
    /**
     * 这个主要是为了支持非neucore生产的硬件；
     * 规则：必须客户代码开头：这个从neucore云注册开通后获取
     * @return
     */
    @Override
    public String getExtSN() {
        return DeviceUtils.getCPUSN(getContext());
    }
    public DeviceInfo getInfo(){
        /**
         * @TODO 需要实现
         */
        return null;
    }
});

```

## 扩展-业务

0，消息订阅扩展；可以在NeulinkSubscriberFacde中查看，目前已经完成了【rmsg/req/$ext_sn/#、rrpc/req/$ext_sn/#、upld/res/$ext_sn/#】订阅;

1，实现payload的pojo对象

2，新增一个XXXProcessor继承实现GProcessor；同时XXX就是topic第四段；且首字母大写

eg：授权处理器
topic：rrpc/req/${ext_sn}/Hello/v1.0/${req_no}[/${md5}]；
processor：包名com.neucore.neulink.extend.impl；类命名为HelloProcessor;

```

import android.content.Context;

import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;

/**
 * 设备授权下发
 * HelloCmd:请求对象，
 * HelloCmdRes：响应对象
 * String:actionListener的返回类型
 */
public class HelloProcessor  extends GProcessor<HelloCmd, HelloCmdRes,String> {

    public HelloProcessor(){
        this(ContextHolder.getInstance().getContext());
    }

    public HelloProcessor(Context context) {
        super(context);
    }

    @Override
    public HelloCmd parser(String payload) {
        return (HelloCmd) JSonUtils.toObject(payload, HelloCmd.class);
    }

    /**
     *
     * @param t 同步请求
     * @param result listener.doAction 的返回值即响应协议的data部分
     * @return
     */
    @Override
    protected HelloCmdRes responseWrapper(HelloCmd t, String result) {
        HelloCmdRes res = new HelloCmdRes();
        res.setCmdStr(t.getCmdStr());
        res.setCode(200);
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getSN());
        res.setData(result);
        res.setMsg("成功");
        return res;
    }

    @Override
    protected HelloCmdRes fail(HelloCmd t, String error) {
        HelloCmdRes res = new HelloCmdRes();
        res.setCmdStr(t.getCmdStr());
        res.setCode(500);
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getSN());
        res.setData(error);
        res.setMsg("失败");
        return res;
    }

    @Override
    protected HelloCmdRes fail(HelloCmd t, int code, String error) {
        HelloCmdRes res = new HelloCmdRes();
        res.setCmdStr(t.getCmdStr());
        res.setCode(code);
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getSN());
        res.setData(error);
        res.setMsg("失败");
        return res;
    }
}


```

3，定义xxxCmdListener实现ICmdListener;eg:HelloCmdListener

```
ICmdListener helloListener = new HelloCmdListener();
备注：切记！！！
上面listener 的doAction 返回值是 响应协议的data部分
```

4,HelloProcessor注册；
  
  ```
  NeulinkProcessorFactory.regist("Hello",new HelloProcessor(),helloListener);
  ```

## 发送消息到云端

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