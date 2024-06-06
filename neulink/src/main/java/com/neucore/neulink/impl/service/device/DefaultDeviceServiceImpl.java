package com.neucore.neulink.impl.service.device;

import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.impl.cmd.msg.CPUInfo;
import com.neucore.neulink.impl.cmd.msg.DeviceInfo;
import com.neucore.neulink.impl.cmd.msg.DiskInfo;
import com.neucore.neulink.impl.cmd.msg.HeatbeatInfo;
import com.neucore.neulink.impl.cmd.msg.MemInfo;
import com.neucore.neulink.impl.cmd.msg.RuntimeInfo;
import com.neucore.neulink.impl.cmd.msg.SDInfo;
import com.neucore.neulink.impl.service.LWTPayload;
import com.neucore.neulink.impl.service.LWTTopic;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.util.AESUtil;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.CpuStat;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.MemoryUtils;

import java.util.Locale;

import cn.hutool.core.util.ObjectUtil;

public class DefaultDeviceServiceImpl implements IDeviceService {



    @Override
    public String getExtSN() {
        /**
         * 默认实现
         * 每台设备固定不变【必须和设备出厂时的设备序列号一致，当不一致的时候设备将无法使用neucore云管理设备】
         * 这个主要时提供给中小企业不想建立云平台，想使用neucore云服务
         */
        if(ObjectUtil.isEmpty(getDeviceId())){
            return DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext());
        }
        else{
            return getDeviceId()+"@"+ DeviceUtils.getMacAddress();
        }
    }

    @Override
    public String getProductId(){
        return null;
    }
    @Override
    public String getDeviceId(){
        /**
         * 读取设备烧录的椰壳Id，即：设备Id
         */
        return DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext());
    }

    @Override
    public String getDeviceSecret(){
        /**
         * 读取设备烧录的设备密钥
         */
        return "";
    }

    @Override
    public String sign(String devId,String deviceSecret){
        if(ObjectUtil.isEmpty(devId)){
            throw new RuntimeException("设备授权Id不能为空");
        }
        if(ObjectUtil.isEmpty(deviceSecret)){
            throw new RuntimeException("设备密钥不能为空");
        }
        return AESUtil.v1V2Encrypt(deviceSecret,devId);
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
