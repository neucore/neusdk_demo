# neusdk_demo使用说明

NeuSDK是Neucore实现的人脸人形相关的SDK，支持Linux和Android操作系统，包含多项专为嵌入式系统设计的算法模型。算法包在Amlogic多款AI芯片上优化和裁剪，在实时性和精度上都达到业内领先水准。

neusdk_demo是一个开源工程，为了演示如何使用NeuSDK。

将版本发布压缩包中,  
NeuSDK 文件夹下 libneucore.so 放到 neusdk_demo\app\libs\armeabi-v7a\ 目录

NeuSDK 文件夹下 nb/目录下所有的nb文件放在 neusdk_demo\app\src\main\assets\nb\ 目录下

NeuSDK_jar 文件夹下 NeuSDK.jar 放到 neusdk_demo\app\libs\ 目录

通过android studio 构建并运行

注意: 一定要确保libovxlib.so 加入到 vendor/etc/public.libraries.txt 文件中并重启过,否则算法会报dlopen 失败


# 算法

|                |功能描述|应用场景                        |
|----------------|-------------------------------|-----------------------------|
|人脸检测|单人脸检测、多人脸检测、验证活体（单目、双目）|门禁场景|
|人脸识别|1：1、1：N、支持口罩模式|门禁场景|
|人脸属性|年龄、性别、是否戴口罩、是否戴帽子、是否戴眼镜、表情|广告精准推送场景|
|人脸关键点|106个人脸关键点|疲劳检测、瞌睡、闭眼、专注度识别等场景|
|人形检测|头肩检测、全身检测|人数统计场景|
|骨骼关节点|全身18个骨骼关节点|识别卧倒、站立、健身等场景|
|手势识别|18种手势（1-10、Ok、No、Yes、比心、拳头、手掌）|手势控制场景|
|背景分割|将人和背景分开，实现虚拟背景，背景替换|会议室、远程教学场景|


## [Neulink](neulink/README.md)

# 联系
任何技术或商务问题，请发送邮件至 support@neucore.com
