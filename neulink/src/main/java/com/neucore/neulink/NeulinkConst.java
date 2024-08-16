package com.neucore.neulink;

import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;

public interface NeulinkConst {

    String TAG_PREFIX = "com.neucore.neulink.Neulink";
    String filePath= DeviceUtils.getNeucore(ContextHolder.getInstance().getContext()) + "/twocamera/";
    String picPath= DeviceUtils.getNeucore(ContextHolder.getInstance().getContext()) + "/twocamera/icon/";//头像
    String photoPath= DeviceUtils.getNeucore(ContextHolder.getInstance().getContext()) + "/twocamera/photo/";//抓拍
    String fileExport= DeviceUtils.getNeucore(ContextHolder.getInstance().getContext()) + "/twocamera/export/";//导出

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
     *
     */
    String NEULINK_BIZ_ONLINE = "online";

    String NEULINK_BIZ_REBOOT = "reboot";

    String NEULINK_BIZ_AWAKEN = "awaken";

    String NEULINK_BIZ_HIBRATE = "hibrate";

    String NEULINK_BIZ_SHELL = "shell";

    String NEULINK_BIZ_ALOG = "alog";

    String NEULINK_BIZ_QLOG = "qlog";

    String NEULINK_BIZ_CFG = "cfg";

    String NEULINK_BIZ_QCFG = "qcfg";

    String NEULINK_BIZ_BLIB = "blib";

    String NEULINK_BIZ_OBJTYPE_FACE = "face";

    String NEULINK_BIZ_OBJTYPE_CAR = "car";

    String NEULINK_BIZ_OBJTYPE_LIC = "lic";

    String NEULINK_BIZ_CLIB = "check";

    String NEULINK_BIZ_QLIB = "qlib";

    String NEULINK_BIZ_FIRMWARE = "firmware";

    String NEULINK_BIZ_FIRMWARE_RESUME = "firmwareresume";

    String NEULINK_BIZ_BACKUP = "backup";

    String NEULINK_BIZ_RECOVER = "recover";

    String NEULINK_BIZ_RESET = "reset";

    String NEULINK_BIZ_DEBUG = "debug";

    String NEULINK_BIZ_BINDING = "binding";

    String NEULINK_BIZ_AUTH = "auth";

    String NEULINK_BIZ_RSVCTRL = "rsvctrl";

    String NEULINK_BIZ_RESERVE = "reserve";

    String NEULINK_BIZ_HKIT = "hkit";

    String NEULINK_BIZ_SCENE = "scene";

    String NEULINK_BIZ_FILE = "file";

    String NEULINK_BIZ_BFILE = "bfile";

    String NEULINK_BIZ_TODOLIST = "todolist";

    String NEULINK_BIZ_BTODOLIST = "btodolist";

    String NEULINK_BIZ_FACE_INFO = "faceinfo";

    String NEULINK_BIZ_PERSON_INFO = "personinfo";

    String NEULINK_BIZ_CAR_PLATE_INFO = "carplateinfo";

    String NEULINK_BIZ_POSE_INFO = "poseinfo";

    String NEULINK_BIZ_BAG_INFO = "baginfo";

    String NEULINK_BIZ_HAT_INFO = "hatinfo";

    String NEULINK_BIZ_FACE_TEMPERATURE = "facetemperature";

    String NEULINK_BIZ_HAT$FACE_INFO = "hat2faceinfo";

    String NEULINK_HEADERS = "headers";

    String NEULINK_HEADERS_GROUP = "group";

    String NEULINK_HEADERS_REQ$RES = "res$req";

    String NEULINK_HEADERS_MODE = "mode";

    String NEULINK_HEADERS_CODE = "code";

    String NEULINK_HEADERS_MSG = "msg";

    String NEULINK_HEADERS_CUSTID = "custid";

    String NEULINK_HEADERS_STOREID = "storeid";

    String NEULINK_HEADERS_ZONEID = "zoneid";

    String NEULINK_HEADERS_TIME = "time";

    String NEULINK_HEADERS_DEVID = "devid";

    String NEULINK_HEADERS_BIZ = "biz";

    String NEULINK_HEADERS_VERSION = "version";

    String NEULINK_HEADERS_REQNO = "reqNo";

    String NEULINK_HEADERS_MD5 = "md5";

    String NEULINK_HEADERS_LZR = "lzt";
    /**
     * 请求方id
     */
    String NEULINK_HEADERS_CLIENT_ID = "clientId";

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
     * 参数不能为空
     */
    Integer STATUS_402 = 402;
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

    int CODE_200 = STATUS_200;
    String CODE_200_MESSAGE = "成功";

    int CODE_201 = STATUS_201;
    String CODE_201_MESSAGE = "成功，新增了资源";

    int CODE_202 = STATUS_202;
    String CODE_202_MESSAGE = "成功，请求被接受，正在处理中";

    int CODE_400 = STATUS_400;
    String CODE_400_MESSAGE = "请求错误";

    int CODE_401 = STATUS_401;
    String CODE_401_MESSAGE = "未认证";

    int CODE_403 = STATUS_403;
    String CODE_403_MESSAGE = "被禁止";

    int CODE_404 = STATUS_404;
    String CODE_404_MESSAGE = "资源不存在";

    int CODE_429 = STATUS_429;
    String CODE_429_MESSAGE = "请求过多";

    int CODE_500 = STATUS_500;
    String CODE_500_MESSAGE = "内部错误";

    int CODE_501 = STATUS_501;
    String CODE_501_MESSAGE = "功能未实现";

    int CODE_503 = STATUS_503;
    String CODE_503_MESSAGE = "服务不可用";

    int CODE_505 = STATUS_505;
    String CODE_505_MESSAGE = "协议不支持";

    int CODE_507 = STATUS_507;
    String CODE_507_MESSAGE = "存储空间不足";

    int CODE_40000 = STATUS_40000;
    String CODE_40000_MESSAGE = "同步包校验失败";

    int CODE_40001 = STATUS_40001;
    String CODE_40001_MESSAGE = "同步包格式错误";

    int CODE_50000 = STATUS_50000;
    String CODE_50000_MESSAGE = "同步包下载时连接服务器失败";

    int CODE_50001 = STATUS_50001;
    String CODE_50001_MESSAGE = "同步包下载过程中连接异常";

    int CODE_50002 = STATUS_50002;
    String CODE_50002_MESSAGE = "同步包处理过程中出错";
    
    String SYSTEM_DEBUG_KEY = "system.debug";

    String DEBUG_ON = "on";

    String DEBUG_OFF = "off";
    /**
     * 新增
     */
    String PROP_CHG_ACTION_ADD = "add";
    /**
     * 更新
     */
    String PROP_CHG_ACTION_UPD = "udp";
    /**
     * 删除
     */
    String PROP_CHG_ACTION_DEL = "del";

    String TimeZoneId = "TimeZoneId";

    String TimeZoneId_Asia$ShangHai = "Asia/Shanghai";

    String DateTimeFormat = "dateTimeFormat";

    String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    String DateFormat = "DateFormat";

    String YYYY_MM_DD = "yyyy-MM-dd";

    String TimeFormat = "TimeFormat";

    String HH_MM_SS = "HH:mm:ss";

    String HH_MM = "HH:mm";
}
