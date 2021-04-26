# neulink 开发使用手册

## 订阅云端消息

0，消息订阅扩展；可以在NeulinkSubscriberFacde中查看，目前已经完成了【rmsg/req/$cpu_sn/#、rrpc/req/$cpu_sn/#、upld/res/$cpu_sn/#】订阅;

1，实现IProcessor接口，放到com.neucore.neulink.proc包内，eg：xxxProcessor

2，在xxxProcessor的process方法中处理，及集成外部扩展机制；具体可以参考ALogProcessor的实现

3，在NeulinkProcessorFactory注册第一步的xxxProcessor的实现类

4，定义xxxCmdListener实现ICmdListener

5，在ListenerFactory中实现默认xxxCmdListener，具体可以参考cfgListener的实现；

## 发送消息到云端

0，在NeulinkPublisherFacde中实现

1，在Apk应用中采用NeulinkService.getInstance().getPublisherFacde()获取消息发送接口进行进行消息发送;

## 配置

0，优先级【扩展配置>配置文件>框架默认配置】

1,扩展配置使用方式：参考SampleConnector

2，配置文件使用方式：参考ConfigContext的实现，这个实现可以通过云端管理

3，默认配置：参考：ConfigContext内的defaultConfig【加密写死】