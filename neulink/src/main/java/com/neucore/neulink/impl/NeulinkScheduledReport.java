package com.neucore.neulink.impl;

import android.content.Context;

import com.neucore.neulink.IProcessor;
import com.neucore.neulink.app.CarshHandler;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.msg.CPUInfo;
import com.neucore.neulink.msg.DeviceInfo;
import com.neucore.neulink.msg.DiskInfo;
import com.neucore.neulink.msg.MemInfo;
import com.neucore.neulink.msg.MiscInfo;
import com.neucore.neulink.msg.SDInfo;
import com.neucore.neulink.msg.SoftVInfo;
import com.neucore.neulink.msg.Stat;
import com.neucore.neulink.msg.Status;
import com.neucore.neulink.rmsg.log.LogUpload;
import com.neucore.neulink.util.AppUtils;
import com.neucore.neulink.util.CpuStat;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.DoubleSerializer;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.MemoryUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 定期收集上报信息
 */
public class NeulinkScheduledReport {

    private  Context context;
    private  NeulinkService service;

    public NeulinkScheduledReport(Context context, NeulinkService service) {
        this.context = context;
        this.service = service;
    }

    void start(){
        regist();
        status();
        stat();
        lgUpld();
    }

    /**
     * 设备注册 msg/req/devinfo/v1.0/${req_no}[/${md5}], qos=0
     */
    private void regist() {
        /**
         * 确保有license的设备先激活，后注册
         */
        try {
            Thread.sleep(15);
        }
        catch (Exception ex){}

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId(DeviceUtils.getDeviceId(context)+"@@"+ ListenerFactory.getInstance().getDeviceService().getSN());
        deviceInfo.setMac(DeviceUtils.getMacAddress());

        deviceInfo.setTag(AppUtils.getVersionName(context));
        MiscInfo miscInfo = new MiscInfo();
        miscInfo.setLocalIp(DeviceUtils.getIpAddress(context));
        miscInfo.setDescription("Jeff@amlogic");
        deviceInfo.setMiscInfo(miscInfo);

        SoftVInfo vInfo = new SoftVInfo();

        vInfo.setOsName(android.os.Build.MANUFACTURER+"@"+android.os.Build.PRODUCT);
        vInfo.setOsVersion(android.os.Build.VERSION.RELEASE);
        vInfo.setReportName("NeuSDK");
        vInfo.setReportVersion(AppUtils.getVersionName(context));
        vInfo.setAlogVersion("1.0");
        deviceInfo.setSoftVInfo(vInfo);

        deviceInfo.setCpuMode(android.os.Build.CPU_ABI);

        String[] funList = {"face"};//人脸识别
        deviceInfo.setFunList(funList);

        String payload = JSonUtils.toString(deviceInfo);
        String devinfo_topic = "msg/req/devinfo";
        service.publishMessage(devinfo_topic, IProcessor.V1$0, payload, 0);
    }

    /**
     * 心跳包上报
     * msg/req/status/${req_no}/v1.0[/${md5}], qos=0
     */
    private void status() {

        new Thread("status") {
            public void run() {
                while (true) {

                    Status status = new Status();
                    status.setDeviceId(DeviceUtils.getDeviceId(context));

                    String payload = JSonUtils.toString(status);
                    String topic = "msg/req/status";
                    service.publishMessage(topic, IProcessor.V1$0, payload, 0);

                    try {
                        Thread.sleep(1000 * 30);
                    } catch (Exception e) {
                    }
                }
            }

        }.start();
    }


    /**
     * 硬件信息上报
     * msg/req/stat/v1.0/${req_no}[/${md5}], qos=0
     */
    private void stat(){
        new Thread("stat") {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000 * 60);
                    } catch (Exception e) {
                    }

                    Stat stat = new Stat();

                    stat.setDeviceId(DeviceUtils.getDeviceId(context));

                    CPUInfo cpuInfo = new CPUInfo();

                    cpuInfo.setUsed(CpuStat.getCpuUsed());
                    float temp = 0f;
                    try {
                        temp = Float.parseFloat(CpuStat.getCpuTemp());
                    }
                    catch (Exception ex){}
                    cpuInfo.setTemp(temp);

                    stat.setCpu(cpuInfo);

                    MemInfo memInfo = new MemInfo();

                    long total = MemoryUtils.getTotalMemory();
                    long free = MemoryUtils.getFreeMemorySize(context);
                    memInfo.setTotal(total);
                    memInfo.setUsed(total-free);
                    stat.setMem(memInfo);

                    DiskInfo diskInfo = DeviceUtils.readSystem();
                    stat.setDisk(diskInfo);

                    SDInfo sdInfo = DeviceUtils.readSD();

                    stat.setSdInfo(sdInfo);

                    String payload = JSonUtils.toString(stat,Double.class,new DoubleSerializer(2));
                    String topic = "msg/req/stat";
                    service.publishMessage(topic, IProcessor.V1$0, payload, 0);
                }
            }
        }.start();
    }

    /**
     * 错误日志上报
     * msg/req/rlog/v1.0/${req_no}/[/${md5}],qos=0
     */
    private void lgUpld(){

        new Thread("CarshLoggerReport"){
            public void run() {
                while (true) {

                    try{
                        Thread.sleep(1000*60);
                    }
                    catch (Exception ex){}
                    File[] logfiles = CarshHandler.getIntance().getFiles();
                    int len = logfiles==null?0:logfiles.length;
                    StringBuffer sb = new StringBuffer();
                    for(int i=0;i<len;i++){
                        File tmp = logfiles[i];
                        int readed = 0;
                        byte[] buffer = new byte[1024];
                        FileInputStream fileInputStream = null;
                        String name = null;
                        try {
                            fileInputStream = new FileInputStream(tmp);
                            while ((readed=fileInputStream.read(buffer))!=-1) {
                                sb.append(new String(buffer,0,readed));
                            }
                            name = tmp.getName();
                            tmp.delete();
                        }
                        catch (Throwable ex){
                        }
                        finally {
                            if(fileInputStream!=null){
                                try {
                                    fileInputStream.close();
                                } catch (IOException e) {
                                }
                            }
                        }
                        LogUpload req = new LogUpload();
                        req.setDeviceId(DeviceUtils.getDeviceId(context));
                        req.setReqId(UUID.randomUUID().toString());
                        req.setMsg(sb.toString());
                        int index = name.lastIndexOf(".");
                        req.setTime(name.substring(0,index));
                        String payload = JSonUtils.toString(req);
                        String topic = "upld/req/rlog";
                        service.publishMessage(topic, IProcessor.V1$0, payload, 0);
                    }
                }
            }
        }.start();
    }
}
