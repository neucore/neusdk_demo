package com.neucore.neulink.util;

import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.LoginUser;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.impl.service.NeulinkSecurity;
import com.neucore.neulink.log.NeuLogUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;

public class HttpParamWrapper implements NeulinkConst {
    private static String TAG = TAG_PREFIX+"HttpParamWrapper";
    public static Map<String,String> getParams(){

        final Map<String,String> headers = new HashMap<>();

        IDeviceService deviceService = ServiceRegistry.getInstance().getDeviceService();
        boolean newVersion = deviceService.newVersion();

        if(ObjectUtil.isNotEmpty(deviceService)){
            Locale locale = deviceService.getLocale();
            if(ObjectUtil.isEmpty(locale)){
                locale = Locale.getDefault();
            }
            headers.put("Accept-Language",locale.getLanguage()+"-"+locale.getCountry());
        }

        if(newVersion){
            /**
             * 新版本无需登录
             */
            headers.remove("Authorization");
            SecuretSign securetSign = deviceService.sign();
            String clientId = securetSign.getClientId();
            String sign = securetSign.getPassword();
            headers.put("clientId",clientId);
            headers.put("sign",sign);
        }
        else{
            LoginUser loginUser = NeulinkSecurity.getInstance().getLoginUser();
            if(ObjectUtil.isEmpty(loginUser)){
                ILoginCallback loginCallback = ServiceRegistry.getInstance().getLoginCallback();
                if(loginCallback==null){
                    NeuLogUtils.iTag(TAG,"loginCallback没有找到");
                }
                else{
                    loginUser = loginCallback.login();
                    NeulinkSecurity.getInstance().setLoginUser(loginUser);
                }
            }

            if(LoginUser.isSuccessedLogin(loginUser)){
                boolean isLoginExpire = LoginUser.isLoginExpire(loginUser);
                if(isLoginExpire){
                    String refreshToken = loginUser.getRefreshToken();
                    ILoginCallback loginCallback = ServiceRegistry.getInstance().getLoginCallback();
                    if(loginCallback==null){
                        NeuLogUtils.iTag(TAG,"loginCallback没有找到");
                    }
                    else {
                        loginUser = loginCallback.refresh(refreshToken);
                        NeulinkSecurity.getInstance().setLoginUser(loginUser);
                    }
                }
                if(LoginUser.isSuccessedLogin(loginUser)){
                    headers.put("Authorization","bearer "+loginUser.getAccessToken());
                }
            }
            else{
                NeuLogUtils.iTag(TAG,"login 失败");
            }
        }
        NeuLogUtils.iTag(TAG,String.format("Http headers=%s",headers));
        return headers;
    }
}
