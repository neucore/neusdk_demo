package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IProcessor;
import com.neucore.neulink.app.CarshHandler;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.cmd.msg.CPUInfo;
import com.neucore.neulink.cmd.msg.DiskInfo;
import com.neucore.neulink.cmd.msg.MemInfo;
import com.neucore.neulink.cmd.msg.SDInfo;
import com.neucore.neulink.cmd.msg.Stat;
import com.neucore.neulink.cmd.msg.Status;
import com.neucore.neulink.cmd.rmsg.log.LogUploadCmd;
import com.neucore.neulink.extend.ServiceFactory;
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
    private Boolean started = false;
    private String TAG = NeulinkConst.TAG_PREFIX+"ScheduledReport";

    public NeulinkScheduledReport(Context context, NeulinkService service) {
        this.context = context;
        this.service = service;
    }

    void start(){
        synchronized (started){
            if(!started){

                status();
                stat();
                lgUpld();
            }
        }
    }
    /**
     * 心跳包上报
     * msg/req/status/${req_no}/v1.0[/${md5}], qos=0
     */
    private void status() {


        new Thread("status") {
            public void run() {

                while (!service.getDestroy() && true) {
                    try {
                        Thread.sleep(1000 * 30);
                    }
                    catch (Exception ex){}
                    if("true".equalsIgnoreCase(ConfigContext.getInstance().getConfig("enable.status","false"))){
                        try {
                            Status status = new Status();
                            status.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());

                            String payload = JSonUtils.toString(status);
                            String topic = "msg/req/status";
                            service.publishMessage(topic, IProcessor.V1$0, payload, 0);
                        }
                        catch(Exception ex){
                            Log.e(TAG,ex.getMessage());
                        }
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
                while (!service.getDestroy() &&true) {
                    try {
                        Thread.sleep(1000 * 30);
                    }
                    catch (Exception ex){}
                    if("true".equalsIgnoreCase(ConfigContext.getInstance().getConfig("enable.stat","false"))){
                        try {
                            Stat stat = new Stat();

                            stat.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());

                            CPUInfo cpuInfo = new CPUInfo();

                            cpuInfo.setUsed(CpuStat.getCpuUsed());
                            float temp = 0f;
                            try {
                                temp = Float.parseFloat(CpuStat.getCpuTemp());
                            } catch (Exception ex) {
                            }
                            cpuInfo.setTemp(temp);

                            stat.setCpu(cpuInfo);

                            MemInfo memInfo = new MemInfo();

                            long total = MemoryUtils.getTotalMemory();
                            long free = MemoryUtils.getFreeMemorySize(context);
                            memInfo.setTotal(total);
                            memInfo.setUsed(total - free);
                            stat.setMem(memInfo);

                            DiskInfo diskInfo = DeviceUtils.readSystem();
                            stat.setDisk(diskInfo);

                            SDInfo sdInfo = DeviceUtils.readSD();

                            stat.setSdInfo(sdInfo);

                            String payload = JSonUtils.toString(stat, Double.class, new DoubleSerializer(2));
                            String topic = "msg/req/stat";
                            service.publishMessage(topic, IProcessor.V1$0, payload, 0);
                        }catch (Exception ex){
                            Log.e(TAG,ex.getMessage());
                        }
                    }
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
                while (!service.getDestroy() && true) {

                    try{
                        Thread.sleep(1000*60);
                    }
                    catch (Exception ex){}
                    try {
                        File[] logfiles = CarshHandler.getIntance().getFiles();
                        int len = logfiles == null ? 0 : logfiles.length;
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < len; i++) {
                            File tmp = logfiles[i];
                            int readed = 0;
                            byte[] buffer = new byte[1024];
                            FileInputStream fileInputStream = null;
                            String name = null;
                            try {
                                fileInputStream = new FileInputStream(tmp);
                                while ((readed = fileInputStream.read(buffer)) != -1) {
                                    sb.append(new String(buffer, 0, readed));
                                }
                                name = tmp.getName();
                                tmp.delete();
                            } catch (Throwable ex) {
                                Log.e(TAG, ex.getMessage());
                            } finally {
                                if (fileInputStream != null) {
                                    try {
                                        fileInputStream.close();
                                    } catch (IOException e) {
                                    }
                                }
                            }
                            LogUploadCmd req = new LogUploadCmd();
                            req.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
                            req.setReqId(UUID.randomUUID().toString());
                            req.setMsg(sb.toString());
                            int index = name.lastIndexOf(".");
                            req.setTime(name.substring(0, index));
                            String payload = JSonUtils.toString(req);
                            String topic = "upld/req/rlog";
                            service.publishMessage(topic, IProcessor.V1$0, payload, 0);
                        }
                    }
                    catch (Exception ex){
                        Log.e(TAG,ex.getMessage());
                    }
                }
            }
        }.start();
    }
}
