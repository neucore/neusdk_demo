package com.neucore.neulink.extend;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.app.NeulinkConst;

import java.io.Serializable;

public class PublishResult<T> implements Serializable {

    /**
     * 记录总条数
     */
    @SerializedName("total")
    private Long total;

    /**
     * 状态码
     */
    @SerializedName("code")
    private int code;

    /**
     * 响应消息内容以
     */
    @SerializedName("msg")
    private String msg;


    /**
     * 响应数据体[VO对象，所有VO对象必须继承AbstractVO]
     */
    @SerializedName("data")
    private T data;

    @SerializedName("time")
    private Long time;

    private PublishResult() {
        this.time = System.currentTimeMillis();
    }


    private PublishResult(int resultCode, String msg) {
        this(resultCode, null, msg);
    }

    public PublishResult(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.time = System.currentTimeMillis();
    }

    public PublishResult(Long total, int code, T data, String msg) {
        this.total = total;
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.time = System.currentTimeMillis();
    }

    public static <T> PublishResult<T> ok() {
        return ok(NeulinkConst.STATUS_200, null, NeulinkConst.MESSAGE_SUCCESS);
    }


    public static <T> PublishResult<T> ok(T data) {
        return ok(NeulinkConst.STATUS_200, data, NeulinkConst.MESSAGE_SUCCESS);
    }

    public static <T> PublishResult<T> ok(T data, Long count) {
        return ok(NeulinkConst.STATUS_200, data, NeulinkConst.MESSAGE_SUCCESS,count);
    }

    public static <T> PublishResult<T> ok(int code, T data, String msg) {
        return new PublishResult<>(code, data, msg == null ? null : msg);
    }


    public static <T> PublishResult<T> ok(int code, T data, String msg, Long count) {
        return new PublishResult(count, code, data,msg);
    }


    public static <T> PublishResult<T> fail() {
        return new PublishResult<>(NeulinkConst.STATUS_500, NeulinkConst.MESSAGE_FAILED);
    }

    public static <T> PublishResult<T> fail(String msg) {
        return new PublishResult<>(NeulinkConst.STATUS_500, msg);
    }

    public static <T> PublishResult<T> fail(int code, String msg) {
        return new PublishResult<>(code, null, msg);
    }

    public static <T> PublishResult<T> fail(int code, T data, String msg) {
        return new PublishResult<>(code, data, msg);
    }

    public static <T> PublishResult<T> fail(int resultCode) {
        return new PublishResult<>(resultCode,NeulinkConst.MESSAGE_FAILED);
    }

    public static <T> PublishResult<T> fail(int resultCode, T date) {
        return new PublishResult<>(resultCode, date,NeulinkConst.MESSAGE_FAILED);
    }


    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}