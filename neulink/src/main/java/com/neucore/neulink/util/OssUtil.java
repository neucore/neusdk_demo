package com.neucore.neulink.util;

import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.json.JSONObject;

public class OssUtil {

    private static String TAG = NeulinkConst.TAG_PREFIX+"OssUtil";

    public static JSONObject getOssConfig(){

        Map<String,String> headers = HttpParamWrapper.getParams();
        /**
         * 获取OSS临时授权
         */
        String ossStsAuthUrl = ConfigContext.getInstance().getConfig(ConfigContext.OSS_STS_AUTH_URL,String.format("https://dev.neucore.com/api/storage/v1/%s/authorization",ConfigContext.getInstance().getConfig(ConfigContext.SCOPEID)));
        Map<String,String> params = new HashMap<>();
        params.put("action","7");
        String response = NeuHttpHelper.post(ossStsAuthUrl,params,headers,10,60,3,null);
        JSONObject jsonObject = new JSONObject(response);
        JSONObject data = jsonObject.getJSONObject("data");
        return data;
    }
}
