package com.neucore.neulink.impl;

import android.content.Context;

import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.cmd.msg.DeviceInfo;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.impl.service.NeulinkSecurity;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.NetworkHelper;

import cn.hutool.core.util.ObjectUtil;

class RegisterAdapter implements NeulinkConst{

    private String TAG = TAG_PREFIX+"RegisterAdapter";
    private boolean networkReady = false;
    private boolean initRegistService = false;
    private boolean registed=false;
    private boolean registCalled = false;
    private final NetworkHelper networkHelper = NetworkHelper.getInstance();
    private String last = null;
    public RegisterAdapter() {

        NeuLogUtils.iTag(TAG,String.format("from=%s,networkReady=%s,initRegistService=%s","startRegister",networkReady,initRegistService));

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
    }
    private Boolean logined = false;
    private void initRegistService(String from){
        String msg = String.format("from=%s,networkReady=%s,initRegistService=%s",from,networkReady,initRegistService);
        if(!msg.equals(last)){
            last = msg;
            NeuLogUtils.iTag(TAG,msg);
        }

        if(networkReady
            && !initRegistService){
            if(!registCalled){
                registCalled = true;
                regist();
            }
        }
    }

    /**
     * 设备注册 msg/req/devinfo/v1.0/${req_no}[/${md5}], qos=0
     */
    void regist() {
        new Thread(){
            public void run(){

                int channel = ConfigContext.getInstance().getConfig(ConfigContext.UPLOAD_CHANNEL,0);
                NeuLogUtils.iTag(TAG,"do regist ...");
                int count = 1;
                while (!logined&&channel==1) {
                    ILoginCallback loginCallback = ServiceRegistry.getInstance().getLoginCallback();
                    if(loginCallback!=null) {
                        String token = loginCallback.login();
                        if(ObjectUtil.isEmpty(token)){
                            NeuLogUtils.iTag(TAG,"token非法。。。");
                        }
                        else{
                            logined = true;
                            NeulinkSecurity.getInstance().setToken(token);
                            NeuLogUtils.iTag(TAG,"success logined");
                            break;
                        }
                    }
                    else{
                        logined = true;
                        NeuLogUtils.iTag(TAG,"没有实现ILoginCallback，跳过登录授权");
                    }
                    try {
                        Thread.sleep(1000*count);
                        if(count<30){
                            count++;
                        }
                    } catch (InterruptedException e) {
                    }
                }

                NeuLogUtils.dTag(TAG,"start "+(channel==0?"mqtt":"http")+ " register");

                while(!registed){
                    try {
                        Thread.sleep(1000);
                        NeuLogUtils.iTag(TAG,"start regist");

                        IDeviceService deviceService = ServiceRegistry.getInstance().getDeviceService();
                        DeviceInfo deviceInfo = deviceService.getInfo();
                        if (ObjectUtil.isEmpty(deviceInfo)) {
                            throw new RuntimeException("设备服务 getInfo没有实现。。。");
                        }
                        String extSN = deviceService.getExtSN();
                        if(ObjectUtil.isEmpty(extSN)){
                            extSN = DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext());
                        }
                        Context context = ContextHolder.getInstance().getContext();
                        ConfigContext configContext = ConfigContext.getInstance();
                        String devId = String.format("%s@@%s@@%s",DeviceUtils.getDeviceId(context),extSN,configContext.getConfig(ConfigContext.DEVICE_TYPE, 0));
                        deviceInfo.setDeviceId(devId);
                        boolean successed = deviceService.regist(deviceInfo);
                        if(successed){
                            registed = true;
                            initRegistService = true;
                            NeuLogUtils.iTag(TAG,"success call regist");
                        }
                    }
                    catch(Exception ex){
                        NeuLogUtils.eTag(TAG,"注册失败："+ex.getMessage());
                    }
                }
            }
        }.start();
    }
}
