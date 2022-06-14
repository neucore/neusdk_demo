package com.neucore.neusdk_demo.service.db.bean;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.IMessage;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity
public class Message implements IMessage {

    public final static String STATUS_PROCESS = "运行中";
    public final static String STATUS_SUCCESS = "成功";
    public final static String STATUS_FAIL = "失败";
    @SerializedName("id")
    @Id(autoincrement = true)
    private Long id;
    /**
     * 请求Id
     */
    @SerializedName("req_no")
    @Index(name="reqId")
    private String reqId;
    /**
     * 队列名
     */
    @SerializedName("topic")
    private String topic;

    @SerializedName("offset")
    private long offset=1;
    /**
     * 消息体
     */
    @SerializedName("payload")
    private String payload;

    @SerializedName("pkg_status")
    private String pkgStatus = STATUS_PROCESS;
    /**
     * 状态：运行中，成功，失败
     */
    @SerializedName("status")
    private String status=STATUS_PROCESS;
    /**
     * 响应包
     */
    @SerializedName("result")
    private String result;
    /**
     * 请求消息到达时间
     */
    @SerializedName("reqtime")
    @Index(name="reqtime")
    private Long reqtime ;
    /**
     * 开始响应时间
     */
    @SerializedName("restime")
    @Index(name="restime")
    private Long restime;

    @SerializedName("qos")
    private int qos;

    @Generated(hash = 379216382)
    public Message(Long id, String reqId, String topic, long offset, String payload,
            String pkgStatus, String status, String result, Long reqtime,
            Long restime, int qos) {
        this.id = id;
        this.reqId = reqId;
        this.topic = topic;
        this.offset = offset;
        this.payload = payload;
        this.pkgStatus = pkgStatus;
        this.status = status;
        this.result = result;
        this.reqtime = reqtime;
        this.restime = restime;
        this.qos = qos;
    }

    @Generated(hash = 637306882)
    public Message() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getPkgStatus() {
        return pkgStatus;
    }

    public void setPkgStatus(String pkgStatus) {
        this.pkgStatus = pkgStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getReqtime() {
        return reqtime;
    }

    public void setReqtime(Long reqtime) {
        this.reqtime = reqtime;
    }

    public Long getRestime() {
        return restime;
    }

    public void setRestime(Long restime) {
        this.restime = restime;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }
}
