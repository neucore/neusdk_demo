package com.neucore.neulink.app;

import android.os.Environment;

public interface NeulinkConst {

    String TAG_PREFIX = "Neulink";
    String filePath= Environment.getExternalStorageDirectory() + "/twocamera/";
    String picPath= Environment.getExternalStorageDirectory() + "/twocamera/icon/";//头像
    String photoPath= Environment.getExternalStorageDirectory() + "/twocamera/photo/";//抓拍
    String fileExport= Environment.getExternalStorageDirectory() + "/twocamera/export/";//导出

    String yanzheng_type="yanzheng_type";//主机认证类型
    String fanqianhui="fanqianhui";//反潜回
    String chaoci="chaoci";//认证超次报警
    String chaoci_num="chaoci_num";//认证做多次数

    final String LOG_CARSH = ".carsh";

    final String LOG_LOG = ".log";

    String Backup_Obj_Cfg = "cfg";

    String Backup_Obj_Syscfg = "syscfg";

    String Backup_Obj_Data = "data";

    String V1$0 = "v1.0";

    String V1$1 = "v1.1";

    String V1$2 = "v1.2";

    /**
     * 添加
     */
    String NEULINK_MODE_ADD = "add";
    /**
     * 更新
     */
    String NEULINK_MODE_UPDATE = "update";
    /**
     * 添加或者更新
     */
    String NEULINK_MODE_ADD_OR_UPDATE = "addOrUpdate";
    /**
     * 替换
     */
    String NEULINK_MODE_REPLACE = "replace";
    /**
     * 删除
     */
    String NEULINK_MODE_DEL = "del";
    /**
     * 同步
     */
    String NEULINK_MODE_SYNC = "sync";
    /**
     * 异步同步
     */
    String NEULINK_MODE_ASYNC = "async";
    /**
     * 推送
     */
    String NEULINK_MODE_PUSH = "push";
    /**
     * 接收
     */
    String NEULINK_MODE_RECEIVE = "receive";
    /**
     * 下载
     */
    String NEULINK_MODE_DOWNLOAD = "download";
    /**
     * 升级
     */
    String NEULINK_MODE_UPGRADE = "upgrade";
    /**
     * 绑定
     */
    String NEULINK_MODE_BIND = "bind";
    /**
     * 解绑
     */
    String NEULINK_MODE_UNBIND = "unbind";
    /**
     * 成功
     */
    Integer STATUS_200 = 200;
    /**
     * 成功，新增了资源｜消息已送达
     */
    Integer STATUS_201 = 201;
    /**
     * 成功，请求被接受，正在处理中
     */
    Integer STATUS_202 = 202;
    /**
     * 错误的请求
     */
    Integer STATUS_400 = 400;
    /**
     * 未认证
     */
    Integer STATUS_401 = 401;
    /**
     * 被禁止｜拒绝
     */
    Integer STATUS_403 = 403;
    /**
     * 资源不存在
     */
    Integer STATUS_404 = 404;
    /**
     * 请求过多
     */
    Integer STATUS_429 = 429;
    /**
     * (服务器内部错误) 服务器遇到错误，无法完成请求。
     */
    Integer STATUS_500 = 500;
    /**
     * 功能未实现。 例如，终端设备或者服务器无法识别请求时会返回此代码。
     */
    Integer STATUS_501 = 501;
    /**
     * (服务不可用) 服务器目前无法使用(由于超载或停机维护)【eg：终端下载人脸同步包时出现网络异常（连接超时）】
     */
    Integer STATUS_503 = 503;
    /**
     * 网络超时【eg：终端下载人脸同步包时出现网络异常（执行超时）】
     */
    Integer STATUS_504 = 504;
    /**
     * 协议版本不支持
     */
    Integer STATUS_505 = 505;
    /**
     * 存储空间不足
     */
    Integer STATUS_507 = 507;
    /**
     *  同步包校验失败
     */
    Integer STATUS_40000 = 40000;
    /**
     *  同步包格式错误
     */
    Integer STATUS_40001 = 40001;
    /**
     * 同步包下载时连接服务器失败
     */
    Integer STATUS_50000 = 50000;
    /**
     * 同步包下载过程中连接异常
     */
    Integer STATUS_50001 = 50001;
    /**
     * 同步包处理过程中出错
     */
    Integer STATUS_50002 = 50002;
    /**
     * 成功
     */
    String MESSAGE_SUCCESS = "success";
    /**
     * 失败
     */
    String MESSAGE_FAILED = "failed";
    /**
     * 处理中
     */
    String MESSAGE_PROCESSING = "processing";
    /**
     * 同意
     */
    String MESSAGE_AGREE = "agree";
    /**
     * 拒绝
     */
    String MESSAGE_REFUSE = "refuse";

}
