package com.neucore.neulink.impl.service.device;

import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.impl.cmd.msg.CPUInfo;
import com.neucore.neulink.impl.cmd.msg.DeviceInfo;
import com.neucore.neulink.impl.cmd.msg.DiskInfo;
import com.neucore.neulink.impl.cmd.msg.HeatbeatInfo;
import com.neucore.neulink.impl.cmd.msg.MemInfo;
import com.neucore.neulink.impl.cmd.msg.RuntimeInfo;
import com.neucore.neulink.impl.cmd.msg.SDInfo;
import com.neucore.neulink.impl.service.LWTPayload;
import com.neucore.neulink.impl.service.LWTTopic;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.CpuStat;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.MemoryUtils;

import java.util.Locale;

public class DefaultDeviceServiceImpl implements IDeviceService {

    @Override
    public String getMqttServer(){
        return "";
    }

    @Override
    public String getProductKey(){
        return "";
    }

    @Override
    public String getDeviceName(){
        return "";
    }

    @Override
    public String getDeviceSecret(){
        return "";
    }

    @Override
    public DeviceInfo getInfo() {
        DeviceInfo deviceInfo = DeviceInfoDefaultBuilder.getInstance().build();
        return deviceInfo;
    }

    @Override
    public HeatbeatInfo heatbeat() {
        return new HeatbeatInfo();
    }

    @Override
    public RuntimeInfo runtime() {
        RuntimeInfo runtimeInfo = new RuntimeInfo();

        CPUInfo cpuInfo = new CPUInfo();

        cpuInfo.setUsed(CpuStat.getCpuUsed());
        float temp = 0f;
        try {
            temp = Float.parseFloat(CpuStat.getCpuTemp());
        } catch (Exception ex) {
        }
        cpuInfo.setTemp(temp);

        runtimeInfo.setCpu(cpuInfo);

        MemInfo memInfo = new MemInfo();

        long total = MemoryUtils.getTotalMemory();
        long free = MemoryUtils.getFreeMemorySize(ContextHolder.getInstance().getContext());
        memInfo.setTotal(total);
        memInfo.setUsed(total - free);
        runtimeInfo.setMem(memInfo);

        DiskInfo diskInfo = DeviceUtils.readSystem();
        runtimeInfo.setDisk(diskInfo);

        SDInfo sdInfo = DeviceUtils.readSD();

        runtimeInfo.setSdInfo(sdInfo);
        return runtimeInfo;
    }

    @Override
    public Locale getLocale(){
        return Locale.getDefault();
    }


    @Override
    public LocalTimezone getTimezone(){
        return new LocalTimezone();
    }


    @Override
    public boolean regist(DeviceInfo deviceInfo) {
        return NeulinkService.getInstance().regist(deviceInfo);
    }

    @Override
    public void connect() {
        NeulinkService.getInstance().connect(1);
    }

    @Override
    public void disconnect() {
        NeulinkService.getInstance().disconnect(1);
    }

    @Override
    public LWTTopic lwtTopic() {
        return NeulinkService.getInstance().lwtTopic();
    }

    @Override
    public LWTPayload lwtPayload() {
        return NeulinkService.getInstance().lwtPayload();
    }
}
