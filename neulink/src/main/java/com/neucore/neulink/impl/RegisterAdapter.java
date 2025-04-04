package com.neucore.neulink.impl;

import android.content.Context;

import com.neucore.neulink.IResCallback;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.impl.cmd.msg.NeulinkZone;
import com.neucore.neulink.impl.cmd.msg.ResRegist;
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
import com.neucore.neulink.util.HttpParamWrapper;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.SecuretSign;
import com.neucore.neulink.util.NetworkHelper;
import com.neucore.neulink.util.NeuHttpHelper;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;

class RegisterAdapter implements NeulinkConst{

    private String TAG = TAG_PREFIX+"RegisterAdapter";
    private boolean networkReady = false;
    private boolean initRegistService = false;
    private boolean registed=false;
    private boolean registCalled = false;
    private final NetworkHelper networkHelper = NetworkHelper.getInstance();
    private IResCallback defaultResCallback = ConfigContext.getInstance().getDefaultResCallback();
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
    private void toSleep(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    /**
     * 设备注册 msg/req/devinfo/v1.0/${req_no}[/${md5}], qos=0
     */
    void regist() {
        new Thread("RegisterAdapter"){
            public void run(){
                boolean remoteConfig = ConfigContext.getInstance().getConfig(ConfigContext.ENABLE_REMOTE_CONFIG,false);

                IDeviceService deviceService = ServiceRegistry.getInstance().getDeviceService();
                boolean newVersion = deviceService.newVersion();
                if(!newVersion){
                    /**
                     * 老版本需要登录，获取token
                     */
                    NeuLogUtils.iTag(TAG,"do regist ...");

                    Integer channel = ConfigContext.getInstance().getConfig(ConfigContext.UPLOAD_CHANNEL,0);


                    NeuLogUtils.dTag(TAG,"start "+(channel==0?"mqtt":"http")+ " register");

                    while (!logined && (channel==1 || remoteConfig)) {
                        ILoginCallback loginCallback = ServiceRegistry.getInstance().getLoginCallback();
                        if(loginCallback!=null) {
                            String token = null;
                            try {
                                token = loginCallback.login();
                                if(ObjectUtil.isEmpty(token)){
                                    NeuLogUtils.iTag(TAG,"token非法。。。");
                                    toSleep();
                                    continue;
                                }
                                else{
                                    logined = true;
                                    NeulinkSecurity.getInstance().setToken(token);
                                    NeuLogUtils.iTag(TAG,"success logined");
                                    break;
                                }
                            }
                            catch (Exception e){
                                NeuLogUtils.eTag(TAG,"登陆失败",e);
                                toSleep();
                                continue;
                            }
                        }
                        else{
                            logined = true;
                            NeuLogUtils.iTag(TAG,"没有实现ILoginCallback，跳过登录授权");
                        }
                    }
                }
                /**
                 * 配置请求
                 */
                boolean configLoaded = false;
                NeuLogUtils.dTag(TAG, String.format("logined=%s,remoteConfig=%s,configLoaded=%s",logined,remoteConfig,configLoaded));

                Map<String,String> params = new HashMap<>();
                String extSn = ServiceRegistry.getInstance().getDeviceService().getExtSN();
                if(ObjectUtil.isNotEmpty(extSn)){
                    String licId = extSn.split("@")[0];
                    params.put("licId",licId);
                }
                String configsURL = ConfigContext.getInstance().getConfig(ConfigContext.CONDIG_SERVER_URL, "https://dev.neucore.com/api/user/v1/device/configs");

                Map<String, String> headers = HttpParamWrapper.getParams();
                if(newVersion){
                    logined = true;
                }
                while (logined && remoteConfig && !configLoaded){
                    String response = null;
                    try {

                        response = NeuHttpHelper.post(configsURL, params, headers, 10, 60, 1,null);
                        if(ObjectUtil.isEmpty(response)){
                            NeuLogUtils.iTag(TAG,"配置非法。。。");
                            toSleep();
                            continue;
                        }

                        NeuLogUtils.dTag(TAG, "设备configs响应：" + response);
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject zone = (JSONObject) jsonObject.get("zone");
                        NeulinkZone neulinkZone = null;
                        if(ObjectUtil.isNotEmpty(zone)){
                            neulinkZone = JSonUtils.toObject(zone.toString(), NeulinkZone.class);
                        }
                        else{
                            zone = (JSONObject) jsonObject.get("data");
                            neulinkZone = JSonUtils.toObject(zone.toString(), NeulinkZone.class);
                        }

                        syncConfig(neulinkZone);

                        configLoaded = true;
                    }
                    catch (NeulinkException e) {
                        NeuLogUtils.eTag(TAG,"配置加载失败",e);
                        Result result = new Result();
                        result.setReqId(UUID.fastUUID().toString());
                        result.setCode(e.getCode());
                        result.setMsg(e.getMsg());
                        defaultResCallback.onFinished(result);
                        toSleep();
                        continue;
                    }
                }

                NeuLogUtils.iTag(TAG,"start regist");

                while(!registed){
                    try {

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
                        toSleep();
                    }
                }
            }
        }.start();
    }

    private void syncConfig(NeulinkZone zone){

        String storeid = zone.getStoreid();
        if(ObjectUtil.isEmpty(storeid)){
            storeid = ConfigContext.getInstance().getConfig(ConfigContext.STOREID);
        }
        String zoneid = zone.getId();
        if(ObjectUtil.isEmpty(storeid)){
            zoneid = ConfigContext.getInstance().getConfig(ConfigContext.ZONEID);
        }
        String mqttServer = zone.getMqttServer();
        if(ObjectUtil.isEmpty(mqttServer)){
            mqttServer = ConfigContext.getInstance().getConfig(ConfigContext.MQTT_SERVER);
        }
        String mqttUserName = zone.getMqttUserName();
        if(ObjectUtil.isEmpty(mqttUserName)){
            mqttUserName = ConfigContext.getInstance().getConfig(ConfigContext.MQTT_USERNAME);
        }
        String mqttPassword = zone.getMqttPassword();
        if(ObjectUtil.isEmpty(mqttPassword)){
            mqttPassword = ConfigContext.getInstance().getConfig(ConfigContext.MQTT_PASSWORD);
        }
        String upldServer = zone.getUploadServer();
        if(ObjectUtil.isEmpty(upldServer)){
            upldServer = ConfigContext.getInstance().getConfig(ConfigContext.HTTP_UPLOAD_SERVER);
        }
        String reqIp = zone.getReqIp();
        if(ObjectUtil.isEmpty(reqIp)){
            reqIp = DeviceUtils.getIpAddress(ContextHolder.getInstance().getContext());
        }

        String ftpServer = zone.getFtpServer();
        if(ObjectUtil.isEmpty(ftpServer)){
            ftpServer = ConfigContext.getInstance().getConfig(ConfigContext.FTP_SERVER);
        }

        String ftpUsername = zone.getFtpUsername();
        if(ObjectUtil.isEmpty(ftpUsername)){
            ftpUsername = ConfigContext.getInstance().getConfig(ConfigContext.FTP_USER_NAME);
        }

        String ftpPassword = zone.getFtpPassword();
        if(ObjectUtil.isEmpty(ftpPassword)){
            ftpPassword = ConfigContext.getInstance().getConfig(ConfigContext.FTP_PASSWORD);
        }

        ConfigContext.getInstance().update(ConfigContext.SCOPEID, zone.getCustid());
        ConfigContext.getInstance().update(ConfigContext.STOREID,storeid );
        ConfigContext.getInstance().update(ConfigContext.ZONEID, zoneid);
        ConfigContext.getInstance().update(ConfigContext.MQTT_SERVER, mqttServer);
        ConfigContext.getInstance().update(ConfigContext.FTP_SERVER, ftpServer);
        ConfigContext.getInstance().update(ConfigContext.FTP_USER_NAME, ftpUsername);
        ConfigContext.getInstance().update(ConfigContext.FTP_PASSWORD, ftpPassword);

        IDeviceService deviceService = ServiceRegistry.getInstance().getDeviceService();

        String productKey = deviceService.getProductKey();
        String deviceName = deviceService.getDeviceName();
        String deviceSecret = deviceService.getDeviceSecret();
        if(ObjectUtil.isNotEmpty(productKey)
                && ObjectUtil.isNotEmpty(deviceSecret)
                && ObjectUtil.isNotEmpty(deviceName)
        ){
            mqttServer = deviceService.getMqttServer();
            ConfigContext.getInstance().update(ConfigContext.MQTT_SERVER, mqttServer);
            SecuretSign securetSign = deviceService.sign();
            ConfigContext.getInstance().update(ConfigContext.MQTT_CLIENT_ID, securetSign.getClientId());
            ConfigContext.getInstance().update(ConfigContext.MQTT_USERNAME, securetSign.getUsername());
            ConfigContext.getInstance().update(ConfigContext.MQTT_PASSWORD, securetSign.getPassword());
            NeuLogUtils.iTag(TAG,String.format("一机一密 host=%,productKey=%s,deviceName=%s,sign=%s,clientId=%s",mqttServer,deviceService.getProductKey(),deviceService.getDeviceName(),securetSign.getPassword(),securetSign.getClientId()));
        }
        else{
            ConfigContext.getInstance().update(ConfigContext.MQTT_USERNAME, mqttUserName);
            ConfigContext.getInstance().update(ConfigContext.MQTT_PASSWORD, mqttPassword);
            ConfigContext.getInstance().update(ConfigContext.MQTT_CLIENT_ID, deviceService.getExtSN());
            NeuLogUtils.iTag(TAG,String.format("老版本 mqtt 连接,clientId=%s",deviceService.getExtSN()));
        }

        ConfigContext.getInstance().update(ConfigContext.HTTP_UPLOAD_SERVER,upldServer);
        ConfigContext.getInstance().update(ConfigContext.HTTP_REQ_IP,reqIp);
    }
}
