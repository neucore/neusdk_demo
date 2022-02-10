# neulink 开发使用手册

## 集成开发

0，消息订阅扩展；可以在NeulinkSubscriberFacde中查看，目前已经完成了【rmsg/req/$cpu_sn/#、rrpc/req/$cpu_sn/#、upld/res/$cpu_sn/#】订阅;

1，实现payload的pojo对象

2，新增一个XXXProcessor继承实现GProcessor；同时XXX就是topic第四段；且首字母大写

eg：授权处理器
topic：rrpc/req/${dev_id}/auth/v1.0/${req_no}[/${md5}]；
processor：包名com.neucore.neulink.extend.impl；类命名为AuthProcessor;

```
package com.xxx.neulink.extend.impl;

import android.content.Context;

import com.xxx.neulink.cmd.rrpc.AuthSyncCmd;
import com.xxx.neulink.cmd.rrpc.AuthSyncCmdRes;
import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;

/**
 * 设备授权下发
 * AuthSyncCmd:请求对象，
 * AuthSyncCmdRes：响应对象
 * String:actionListener的返回类型
 */
public class AuthProcessor  extends GProcessor<AuthSyncCmd, AuthSyncCmdRes,String> {

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
     * @param result listener.doAction 的返回值即响应协议的data部分
     * @return
     */
    @Override
    protected AuthSyncCmdRes responseWrapper(AuthSyncCmd t, String result) {
        AuthSyncCmdRes res = new AuthSyncCmdRes();
        res.setCmdStr(t.getCmdStr());
        res.setCode(200);
        res.setDeviceId(DeviceUtils.getDeviceId(getContext()));
        res.setData(result);
        res.setMsg("成功");
        return res;
    }

    @Override
    protected AuthSyncCmdRes fail(AuthSyncCmd t, String error) {
        AuthSyncCmdRes res = new AuthSyncCmdRes();
        res.setCmdStr(t.getCmdStr());
        res.setCode(500);
        res.setDeviceId(DeviceUtils.getDeviceId(getContext()));
        res.setData(error);
        res.setMsg("失败");
        return res;
    }

    @Override
    protected AuthSyncCmdRes fail(AuthSyncCmd t, int code, String error) {
        AuthSyncCmdRes res = new AuthSyncCmdRes();
        res.setCmdStr(t.getCmdStr());
        res.setCode(code);
        res.setDeviceId(DeviceUtils.getDeviceId(getContext()));
        res.setData(error);
        res.setMsg("失败");
        return res;
    }
}


```

3，定义xxxCmdListener实现ICmdListener;eg:AuthCmdListener

```
ICmdListener listener = new AuthCmdListener();
备注：切记！！！
上面listener 的doAction 返回值是 响应协议的data部分
```

4,AuthProcessor注册；
  
  ```
  NeulinkProcessorFactory.regist("auth",new AuthProcessor(),listener);
  ```

## 发送消息到云端

0，在NeulinkPublisherFacde中实现

1，在Apk应用中采用NeulinkService.getInstance().getPublisherFacde()获取消息发送接口进行进行消息发送;

## 配置

0，优先级【扩展配置>配置文件>框架默认配置】

1,扩展配置使用方式：参考SampleConnector

2，配置文件使用方式：参考ConfigContext的实现，这个实现可以通过云端管理

3，默认配置：参考：ConfigContext内的defaultConfig【加密写死】