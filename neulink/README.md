# neulink 开发使用手册

## 订阅云端消息

0，消息订阅扩展；可以在NeulinkSubscriberFacde中查看，目前已经完成了【rmsg/req/$cpu_sn/#、rrpc/req/$cpu_sn/#、upld/res/$cpu_sn/#】订阅;

1，实现payload的pojo对象

2，新增一个XXXProcessor继承实现GProcessor；同时XXX就是topic第四段；且首字母大写

eg：授权处理器
topic：rrpc/req/${dev_id}/auth/v1.0/${req_no}[/${md5}]；
processor：包名com.neucore.neulink.extend.impl；类命名为AuthProcessor;

```
package com.neucore.neulink.extend.impl;

import android.content.Context;

import com.neucore.neulink.cmd.rrpc.AuthSyncCmd;
import com.neucore.neulink.cmd.rrpc.AuthSyncCmdRes;
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

3，Processor注册；

```

NeulinkProcessorFactory.regist("auth",new AuthProcessor());
```

4，定义xxxCmdListener实现ICmdListener;eg:AuthCmdListener

5，在ListenerFactory中实现默认xxxCmdListener，具体可以参考cfgListener的实现；

6,其他Listener扩展注册

```
ListenerFactory.getInstance().setExtendListener("xxx",new ICmdListener<String>(){
                @Override
                public String doAction(NeulinkEvent event) {
                    /**
                     * 该返回值类型对应命令响应结构的data元素的内容；
                     */
                    return "hello";
                }
            });
```

## 发送消息到云端

0，在NeulinkPublisherFacde中实现

1，在Apk应用中采用NeulinkService.getInstance().getPublisherFacde()获取消息发送接口进行进行消息发送;

## 配置

0，优先级【扩展配置>配置文件>框架默认配置】

1,扩展配置使用方式：参考SampleConnector

2，配置文件使用方式：参考ConfigContext的实现，这个实现可以通过云端管理

3，默认配置：参考：ConfigContext内的defaultConfig【加密写死】