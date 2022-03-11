package com.neucore.neulink.cmd.msg;

import com.google.gson.annotations.SerializedName;

public class NeulinkZone {
    @SerializedName("zoneid")
    private String id;
    @SerializedName("custid")
    private String custid;
    @SerializedName("storeid")
    private String storeid;
    @SerializedName("mqtt_server")
    private String mqttServer;
    @SerializedName("mqtt_port")
    private int mqttPort;
    @SerializedName("upload_server")
    private String uploadServer="https://dev.neucore.com/api/v1/neulink/upload2cloud";

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
