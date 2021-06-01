package com.neucore.neulink.faceupld.v12;

import com.google.gson.annotations.SerializedName;

public class FaceUpload12 {
    @SerializedName("dev_id")
    private String deviceId;

    @SerializedName("position")
    private String position;

    @SerializedName("channel")
    private int channel = 0;

    @SerializedName("time_stamp")
    private long timestamp;

    @SerializedName("module")
    private String module;

    @SerializedName("type")
    private int type;

    @SerializedName("ai_data")
    private AIData12 aiData;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public AIData12 getAiData() {
        return aiData;
    }

    public void setAiData(AIData12 aiData) {
        this.aiData = aiData;
    }
}
