package com.neucore.neulink.util;

import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.impl.service.NeulinkSecurity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;

public class HttpParamWrapper {
    public static Map<String,String> getParams(){

        IDeviceService deviceService = ServiceRegistry.getInstance().getDeviceService();

        boolean newVersion = deviceService.newVersion();
        String token = NeulinkSecurity.getInstance().getToken();
        final Map<String,String> headers = new HashMap<>();
        if(token!=null){
            int index = token.indexOf(" ");
            if(index!=-1){
                token = token.substring(index+1);
            }
            headers.put("Authorization","bearer "+token);
        }
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
            String sign = securetSign.getSign();
            headers.put("clientId",clientId);
            headers.put("sign",sign);
        }
        return headers;
    }
}
