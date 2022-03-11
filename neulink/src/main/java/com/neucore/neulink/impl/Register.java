package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.IProcessor;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.cmd.msg.DeviceInfo;
import com.neucore.neulink.extend.NeulinkSecurity;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.impl.service.device.IDeviceService;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.NetworkHelper;

import cn.hutool.core.util.ObjectUtil;

public class Register implements NeulinkConst{

    private String TAG = TAG_PREFIX+"Register";
    private Context context;
    private  NeulinkService service;
    private NeulinkScheduledReport autoReporter = null;
    private boolean initMqttService = false;
    private boolean networkReady = false;
    private boolean initRegistService = false;
    private boolean registed=false;
    private boolean registCalled = false;

    private String serviceUrl;
    private final NetworkHelper networkHelper = NetworkHelper.getInstance();

    public Register(final Context context, final NeulinkService service, final String serviceUrl) {
        this.context = context;
        this.service = service;
        this.serviceUrl = serviceUrl;

        networkHelper.addListener(new NetworkHelper.Listener() {
            @Override
            public void onConnectivityChange(boolean connect) {
                if(connect){
                    networkReady = connect;
                    initRegistService("onConnectivityChange");
                }
            }
        });

        networkHelper.onStart();

        if(networkHelper.getNetworkConnected()){
            initRegistService("getNetworkConnected");
        }

        autoReporter = new NeulinkScheduledReport(context,service);
    }
    private Boolean logined = false;
    private void initRegistService(String from){
        Log.i(TAG,String.format("from=%s,networkReady=%s,initMqttService=%s,initRegistService=%s",from,networkReady,initMqttService,initRegistService));
        int channel = ConfigContext.getInstance().getConfig(ConfigContext.UPLOAD_CHANNEL,0);

        while (!logined&&channel==1) {
            ILoginCallback loginCallback = ServiceFactory.getInstance().getLoginCallback();
            if(loginCallback!=null) {
                String token = loginCallback.login();
                if(ObjectUtil.isEmpty(token)){
                    Log.i(TAG,"token非法。。。");
                }
                else{
                    logined = true;
                    NeulinkSecurity.getInstance().setToken(token);
                    continue;
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        }

        if(networkReady){
            if(!initMqttService ){//http注册方式&上报方式
                Log.d(TAG,"try mqtt register");
                service.init(serviceUrl,context);
                initMqttService = true;
            }
        }

        if(networkReady
            && initMqttService
            && !initRegistService){

            Log.d(TAG,"try "+(channel==0?"mqtt":"http")+ " register");
            if(!registCalled){
                registCalled = true;
                regist();
            }

            autoReporter.start();
            initRegistService = true;
        }
    }

    /**
     * 设备注册 msg/req/devinfo/v1.0/${req_no}[/${md5}], qos=0
     */
    void regist() {
        new Thread(){
            public void run(){
                while(!registed){
                    Log.i(TAG,"@Regist MQTT MqttConnSuccessed: "+service.getMqttConnSuccessed());
                    if(!service.getMqttConnSuccessed()){
                        service.connect();
                    }

                    if(!service.getMqttConnSuccessed()){
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                        }
                        continue;
                    }

                    IDeviceService deviceService = ServiceFactory.getInstance().getDeviceService();
                    DeviceInfo deviceInfo = deviceService.getInfo();
                    if(ObjectUtil.isEmpty(deviceInfo)){
                        throw new RuntimeException("设备服务 getInfo没有实现。。。");
                    }
                    String devId = DeviceUtils.getDeviceId(context)+"@@"+ deviceService.getExtSN()+"@@"+ ConfigContext.getInstance().getConfig(ConfigContext.DEVICE_TYPE,0);
                    deviceInfo.setDeviceId(devId);

                    String payload = JSonUtils.toString(deviceInfo);
                    String devinfo_topic = "msg/req/devinfo";
                    service.publishMessage(devinfo_topic, IProcessor.V1$0, payload, 0);
                    registed = true;
                }

            }
        }.start();

    }
}
