package com.neucore.neulink.msg;

import com.google.gson.annotations.SerializedName;

public class NeulinkZone {
    @SerializedName("zoneid")
    private int id;
    @SerializedName("custid")
    private String custid;
    @SerializedName("storeid")
    private String storeid;
    @SerializedName("mqtt.server")
    private String mqttServer;
    @SerializedName("mqtt.port")
    private int mqttPort;
    @SerializedName("upload.server")
    private String uploadServer="https://data.neuapi.com/neulink/upload2cloud";//默认值

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustid() {
        return custid;
    }

    public void setCustid(String custid) {
        this.custid = custid;
    }

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public String getMqttServer() {
        return mqttServer;
    }

    public void setMqttServer(String mqttServer) {
        this.mqttServer = mqttServer;
    }

    public int getMqttPort() {
        return mqttPort;
    }

    public void setMqttPort(int mqttPort) {
        this.mqttPort = mqttPort;
    }

    public String getUploadServer() {
        return uploadServer;
    }

    public void setUploadServer(String uploadServer) {
        this.uploadServer = uploadServer;
    }
}