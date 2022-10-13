package com.neucore.neulink.impl;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.NeulinkConst;

import java.io.Serializable;

public class Result<T> implements Serializable {

    private String reqId;
    /**
     * 记录总条数
     */
    @SerializedName("total")
    private Long total;

    /**
     * 状态码
     */
    @SerializedName("code")
    private Integer code;

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

    public Result() {
        this.time = System.currentTimeMillis();
    }


    private Result(Integer resultCode, String msg) {
        this(resultCode, null, msg);
    }

    public Result(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.time = System.currentTimeMillis();
    }

    public Result(Long total, Integer code, T data, String msg) {
        this.total = total;
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.time = System.currentTimeMillis();
    }

    public static <T> Result<T> ok() {
        return ok(NeulinkConst.STATUS_200, null, NeulinkConst.MESSAGE_SUCCESS);
    }


    public static <T> Result<T> ok(T data) {
        return ok(NeulinkConst.STATUS_200, data, NeulinkConst.MESSAGE_SUCCESS);
    }

    public static <T> Result<T> ok(T data, Long count) {
        return ok(NeulinkConst.STATUS_200, data, NeulinkConst.MESSAGE_SUCCESS,count);
    }

    public static <T> Result<T> ok(Integer code, T data, String msg) {
        return new Result<>(code, data, msg == null ? null : msg);
    }


    public static <T> Result<T> ok(Integer code, T data, String msg, Long count) {
        return new Result(count, code, data,msg);
    }


    public static <T> Result<T> fail() {
        return new Result<>(NeulinkConst.STATUS_500, NeulinkConst.MESSAGE_FAILED);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<>(NeulinkConst.STATUS_500, msg);
    }

    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code, null, msg);
    }

    public static <T> Result<T> fail(Integer code, T data, String msg) {
        return new Result<>(code, data, msg);
    }

    public static <T> Result<T> fail(Integer resultCode) {
        return new Result<>(resultCode,NeulinkConst.MESSAGE_FAILED);
    }

    public static <T> Result<T> fail(Integer resultCode, T date) {
        return new Result<>(resultCode, date,NeulinkConst.MESSAGE_FAILED);
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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

    @Override
    public String toString() {
        return "Result{" +
                "reqId='" + reqId + '\'' +
                ", time=" + time +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                ", total=" + total +
                ", data=" + data +
                '}';
    }
}