package com.neucore.neulink.cmd.msg;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.util.DatesUtil;

/**
 * 硬件监控信息
 */
public class Stat {
    @SerializedName("dev_id")
    private String deviceId;

    @SerializedName("cpu")
    private CPUInfo cpu;

    @SerializedName("mem")
    private MemInfo mem;

    @SerializedName("disk")
    private DiskInfo disk;

    @SerializedName("sd")
    private SDInfo sdInfo;

    @SerializedName("timestamp")
    private long timestamp = DatesUtil.getNowTimeStamp();

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public CPUInfo getCpu() {
        return cpu;
    }

    public void setCpu(CPUInfo cpu) {
        this.cpu = cpu;
    }

    public MemInfo getMem() {
        return mem;
    }

    public void setMem(MemInfo mem) {
        this.mem = mem;
    }

    public DiskInfo getDisk() {
        return disk;
    }

    public void setDisk(DiskInfo disk) {
        this.disk = disk;
    }

    public SDInfo getSdInfo() {
        return sdInfo;
    }

    public void setSdInfo(SDInfo sdInfo) {
        this.sdInfo = sdInfo;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
