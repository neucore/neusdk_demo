# neulink 开发使用手册

## 订阅云端消息

0，消息订阅扩展；可以在NeulinkSubscriberFacde中查看，目前已经完成了【rmsg/req/$cpu_sn/#、rrpc/req/$cpu_sn/#、upld/res/$cpu_sn/#】订阅;

1，实现IProcessor接口，放到com.neucore.neulink.proc包内，eg：xxxProcessor

2，在xxxProcessor的process方法中处理，及集成外部扩展机制；具体可以参考ALogProcessor的实现

3，定义xxxCmdListener实现ICmdListener

4，在ListenerFactory中实现默认xxxCmdListener，具体可以参考cfgListener的实现；

5，在NeulinkProcessorFactory注册第一步的xxxProcessor的实现类

## 发送消息到云端

0，在NeulinkPublisherFacde中实现

1，在Apk应用中采用NeulinkService.getInstance().getPublisherFacde()获取消息发送接口进行进行消息发送;