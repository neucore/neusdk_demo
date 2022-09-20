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
        String token = NeulinkSecurity.getInstance().getToken();
        final Map<String,String> params = new HashMap<>();
        if(token!=null){
            int index = token.indexOf(" ");
            if(index!=-1){
                token = token.substring(index+1);
            }
            params.put("Authorization","bearer "+token);
        }
        IDeviceService deviceService = ServiceRegistry.getInstance().getDeviceService();
        if(ObjectUtil.isNotEmpty(deviceService)){
            Locale locale = deviceService.getLocale();
            if(ObjectUtil.isEmpty(locale)){
                locale = Locale.getDefault();
            }
            params.put("Accept-Language",locale.getLanguage()+"-"+locale.getCountry());
        }
        return params;
    }
}
