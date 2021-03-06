package com.neucore.neulink;

public class NeulinkException extends RuntimeException {

    public final static int CODE_200 = 200;
    public final static String CODE_200_MESSAGE = "成功";

    public final static int CODE_201 = 201;
    public final static String CODE_201_MESSAGE = "成功，新增了资源";

    public final static int CODE_202 = 202;
    public final static String CODE_202_MESSAGE = "成功，请求被接受，正在处理中";

    public final static int CODE_400 = 400;
    public final static String CODE_400_MESSAGE = "请求错误";

    public final static int CODE_401 = 401;
    public final static String CODE_401_MESSAGE = "未认证";

    public final static int CODE_403 = 403;
    public final static String CODE_403_MESSAGE = "被禁止";

    public final static int CODE_404 = 404;
    public final static String CODE_404_MESSAGE = "资源不存在";

    public final static int CODE_429 = 429;
    public final static String CODE_429_MESSAGE = "请求过多";

    public final static int CODE_500 = 500;
    public final static String CODE_500_MESSAGE = "内部错误";

    public final static int CODE_501 = 501;
    public final static String CODE_501_MESSAGE = "功能未实现";

    public final static int CODE_503 = 503;
    public final static String CODE_503_MESSAGE = "服务不可用";

    public final static int CODE_505 = 505;
    public final static String CODE_505_MESSAGE = "协议不支持";

    public final static int CODE_507 = 507;
    public final static String CODE_507_MESSAGE = "存储空间不足";

    public final static int CODE_40000 = 40000;
    public final static String CODE_40000_MESSAGE = "同步包校验失败";

    public final static int CODE_40001 = 40001;
    public final static String CODE_40001_MESSAGE = "同步包格式错误";

    public final static int CODE_50000 = 50000;
    public final static String CODE_50000_MESSAGE = "同步包下载时连接服务器失败";

    public final static int CODE_50001 = 50001;
    public final static String CODE_50001_MESSAGE = "同步包下载过程中连接异常";

    public final static int CODE_50002 = 50002;
    public final static String CODE_50002_MESSAGE = "同步包处理过程中出错";

    private int code;
    private String msg;
    public NeulinkException(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
    public NeulinkException(int code, String msg, Throwable throwable){
        super(throwable);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
