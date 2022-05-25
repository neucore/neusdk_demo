package com.neucore.neulink.impl.service;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.cmd.msg.DeviceInfo;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.NetworkHelper;

import cn.hutool.core.util.ObjectUtil;

public class Register implements NeulinkConst{

    private String TAG = TAG_PREFIX+"Register";
    private Context context;
    private NeulinkService service;
    private NeulinkScheduledReport autoReporter = null;
    private boolean networkReady = false;
    private boolean initRegistService = false;
    private boolean registed=false;
    private boolean registCalled = false;

    private String serviceUrl;
    private final NetworkHelper networkHelper = NetworkHelper.getInstance();

    public Register(final Context context, final NeulinkService service) {
        this.context = context;
        this.service = service;
        this.serviceUrl = serviceUrl;

        Log.i(TAG,String.format("from=%s,networkReady=%s,initRegistService=%s","startRegister",networkReady,initRegistService));

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

        Log.i(TAG,String.format("from=%s,networkReady=%s,initRegistService=%s",from,networkReady,initRegistService));

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
                Log.i(TAG,"do regist ...");
                while (!logined&&channel==1) {
                    ILoginCallback loginCallback = ServiceRegistry.getInstance().getLoginCallback();
                    if(loginCallback!=null) {
                        String token = loginCallback.login();
                        if(ObjectUtil.isEmpty(token)){
                            Log.i(TAG,"token非法。。。");
                        }
                        else{
                            logined = true;
                            NeulinkSecurity.getInstance().setToken(token);
                            Log.i(TAG,"success logined");
                            break;
                        }
                    }
                    else{
                        logined = true;
                        Log.i(TAG,"没有实现ILoginCallback，跳过登录授权");
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }

                Log.d(TAG,"start "+(channel==0?"mqtt":"http")+ " register");

                while(!registed){
                    try {
                        Thread.sleep(1000);
                        Log.i(TAG,"start regist");

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
                            autoReporter.start();
                            initRegistService = true;
                            Log.i(TAG,"success regist");
                        }
                    }
                    catch(Exception ex){
                        Log.e(TAG,"注册失败："+ex.getMessage());
                    }
                }

            }
        }.start();
    }
}
