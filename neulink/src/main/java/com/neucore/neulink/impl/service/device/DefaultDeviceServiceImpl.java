package com.neucore.neulink.impl.service.device;

import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.IProcessor;
import com.neucore.neulink.impl.cmd.msg.CPUInfo;
import com.neucore.neulink.impl.cmd.msg.DeviceInfo;
import com.neucore.neulink.impl.cmd.msg.DiskInfo;
import com.neucore.neulink.impl.cmd.msg.HeatbeatInfo;
import com.neucore.neulink.impl.cmd.msg.MemInfo;
import com.neucore.neulink.impl.cmd.msg.RuntimeInfo;
import com.neucore.neulink.impl.cmd.msg.SDInfo;
import com.neucore.neulink.impl.LWTInfo;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.CpuStat;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.MemoryUtils;

import java.util.Locale;

public class DefaultDeviceServiceImpl implements IDeviceService {

    @Override
    public String getExtSN() {
        /**
         * 默认实现
         * 每台设备固定不变【必须和设备出厂时的设备序列号一致，当不一致的时候设备将无法使用neucore云管理设备】
         * 这个主要时提供给中小企业不想建立云平台，想使用neucore云服务
         */
        return DeviceUtils.getCPUSN(ContextHolder.getInstance().getContext());
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
    public boolean regist(DeviceInfo deviceInfo) {
        String payload = JSonUtils.toString(deviceInfo);
        String devinfo_topic = "msg/req/devinfo";
        NeulinkService.getInstance().publishMessage(devinfo_topic, IProcessor.V1$0, payload, 0);
        return true;
    }

    @Override
    public void connect() {
        NeulinkService.getInstance().publishConnect(1);
    }

    @Override
    public void disconnect() {
        NeulinkService.getInstance().publishDisConnect(1);
    }

    @Override
    public LWTInfo lwt() {
        LWTInfo info = new LWTInfo();
        info.setTopicPrefix("msg/req/lwt/v1.0");
        String payload = "{\"dev_id\":\""+getExtSN()+"\",\"status\":-1}";
        info.setPayload(payload);
        return info;
    }
}
