package com.neucore.neulink.impl;

import com.blankj.utilcode.util.LogUtils;
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

    public RegisterAdapter() {

        LogUtils.iTag(TAG,String.format("from=%s,networkReady=%s,initRegistService=%s","startRegister",networkReady,initRegistService));

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

        LogUtils.iTag(TAG,String.format("from=%s,networkReady=%s,initRegistService=%s",from,networkReady,initRegistService));

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
                LogUtils.iTag(TAG,"do regist ...");
                while (!logined&&channel==1) {
                    ILoginCallback loginCallback = ServiceRegistry.getInstance().getLoginCallback();
                    if(loginCallback!=null) {
                        String token = loginCallback.login();
                        if(ObjectUtil.isEmpty(token)){
                            LogUtils.iTag(TAG,"token非法。。。");
                        }
                        else{
                            logined = true;
                            NeulinkSecurity.getInstance().setToken(token);
                            LogUtils.iTag(TAG,"success logined");
                            break;
                        }
                    }
                    else{
                        logined = true;
                        LogUtils.iTag(TAG,"没有实现ILoginCallback，跳过登录授权");
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }

                LogUtils.dTag(TAG,"start "+(channel==0?"mqtt":"http")+ " register");

                while(!registed){
                    try {
                        Thread.sleep(1000);
                        LogUtils.iTag(TAG,"start regist");

                        IDeviceService deviceService = ServiceRegistry.getInstance().getDeviceService();
                        DeviceInfo deviceInfo = deviceService.getInfo();
                        if (ObjectUtil.isEmpty(deviceInfo)) {
                            throw new RuntimeException("设备服务 getInfo没有实现。。。");
                        }
                        String extSN = deviceService.getExtSN();
                        if(ObjectUtil.isEmpty(extSN)){
                            extSN = DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext());
                        }
                        String devId = DeviceUtils.getDeviceId(ContextHolder.getInstance().getContext()) + "@@" + extSN + "@@" + ConfigContext.getInstance().getConfig(ConfigContext.DEVICE_TYPE, 0);
                        deviceInfo.setDeviceId(devId);
                        boolean successed = deviceService.regist(deviceInfo);
                        if(successed){
                            registed = true;
                            initRegistService = true;
                            LogUtils.iTag(TAG,"success regist");
                        }
                    }
                    catch(Exception ex){
                        LogUtils.eTag(TAG,"注册失败："+ex.getMessage());
                    }
                }

            }
        }.start();
    }
}