package com.neucore.neulink.impl;

import android.content.Context;

import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.IProcessor;
import com.neucore.neulink.app.CarshHandler;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.cmd.msg.RuntimeInfo;
import com.neucore.neulink.impl.cmd.msg.HeatbeatInfo;
import com.neucore.neulink.impl.cmd.rmsg.log.LogUploadCmd;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.util.DoubleSerializer;
import com.neucore.neulink.util.JSonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import cn.hutool.core.util.ObjectUtil;

/**
 * 定期收集上报信息
 */
public class NeulinkScheduledReport implements NeulinkConst{

    private  Context context;
    private NeulinkService service;
    private Boolean started = false;
    private String TAG = TAG_PREFIX+"ScheduledReport";
    private IDeviceService deviceService;

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

                while (!service.getDestroyed() && true) {
                    try {
                        Thread.sleep(1000 * 30);
                    }
                    catch (Exception ex){}
                    if("true".equalsIgnoreCase(ConfigContext.getInstance().getConfig("enable.heartbeat","false"))){
                        try {
                            HeatbeatInfo heatbeatInfo = service.getDeviceService().heatbeat();
                            if(ObjectUtil.isNotEmpty(heatbeatInfo)){
                                heatbeatInfo.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
                                String payload = JSonUtils.toString(heatbeatInfo);
                                String topic = "msg/req/status";
                                service.publishMessage(topic, IProcessor.V1$0, payload, ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,0));
                            }
                            else{
                                NeuLogUtils.eTag(TAG,"deviceService的heatbeat没有实现");
                            }
                        }
                        catch(Exception ex){
                            NeuLogUtils.eTag(TAG,ex.getMessage());
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
                while (!service.getDestroyed() &&true) {
                    try {
                        Thread.sleep(1000 * 30);
                    }
                    catch (Exception ex){}
                    if("true".equalsIgnoreCase(ConfigContext.getInstance().getConfig("enable.runtime","false"))){
                        try {
                            RuntimeInfo runtimeInfo = service.getDeviceService().runtime();
                            if(ObjectUtil.isNotEmpty(runtimeInfo)){
                                runtimeInfo.setDeviceId(service.getDeviceService().getExtSN());
                                String payload = JSonUtils.toString(runtimeInfo, Double.class, new DoubleSerializer(2));
                                String topic = "msg/req/stat";
                                service.publishMessage(topic, IProcessor.V1$0, payload, ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,0));
                            }
                            else{
                                NeuLogUtils.eTag(TAG,"deviceService的runtime没有实现");
                            }

                        }catch (Exception ex){
                            NeuLogUtils.eTag(TAG,ex.getMessage());
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
                while (!service.getDestroyed() && true) {

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
                                NeuLogUtils.eTag(TAG, ex.getMessage());
                            } finally {
                                if (fileInputStream != null) {
                                    try {
                                        fileInputStream.close();
                                    } catch (IOException e) {
                                    }
                                }
                            }
                            LogUploadCmd req = new LogUploadCmd();
                            req.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
                            req.setReqId(UUID.randomUUID().toString());
                            req.setMsg(sb.toString());
                            int index = name.lastIndexOf(".");
                            req.setTime(name.substring(0, index));
                            String payload = JSonUtils.toString(req);
                            String topic = "upld/req/rlog";
                            service.publishMessage(topic, IProcessor.V1$0, payload, ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,0));
                        }
                    }
                    catch (Exception ex){
                        NeuLogUtils.eTag(TAG,ex.getMessage());
                    }
                }
            }
        }.start();
    }
}
