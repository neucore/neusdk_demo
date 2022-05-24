package com.neucore.neulink.impl.cmd.faceupld;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.ArgCmd;

@Deprecated
public class FaceUpload extends ArgCmd {
    @SerializedName("dev_id")
    private String deviceId;

    @SerializedName("channel")
    private int channel = 0;

    @SerializedName("time_stamp")
    private long timestamp;

    @SerializedName("type")
    private int type;

    @SerializedName("attach_info")
    private String attachInfo;

    @SerializedName("ai_data")
    private AIData aiData;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAttachInfo() {
        return attachInfo;
    }

    public void setAttachInfo(String attachInfo) {
        this.attachInfo = attachInfo;
    }

    public AIData getAiData() {
        return aiData;
    }

    public void setAiData(AIData aiData) {
        this.aiData = aiData;
    }
}
