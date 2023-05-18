package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.util.NeuHttpHelper;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.json.JSONObject;

/**
 * 登录服务回调实现
 */
public class MyLoginCallbackImpl implements ILoginCallback, NeulinkConst {
    private String TAG = "MyLoginCallbackImpl";
    @Override
    public String login() {
        /**
         * 实现登录返回token
         */
        NeuLogUtils.iTag(TAG,"login...");

        Map<String,String> headers = new HashMap<>();
        headers.put("accept-language","zh-Hans-CN");
        headers.put("mate-scope","Mg");
        headers.put("from","3");
        /**
         * 新增时区字段
         */
        headers.put("ltz", ConfigContext.getInstance().getConfig(TimeZoneId,TimeZoneId_Asia$ShangHai));

        Map<String,String> params = new HashMap<>();
        params.put("client_id","gemini");//client-smrtlib,gemini
        params.put("client_secret","secret");//client-smrtlib-secret,secret
        params.put("grant_type","password");//password
        params.put("username","frame2");//15800860806,frame2
        params.put("password","123456");//123456

        String url = "http://10.18.9.35:18091/oauth/token";
//        String url = "https://dev.magm.com.cn/api/uaa/oauth/token";

        String response = NeuHttpHelper.post(url,params,headers,3);

        JSONObject jsonObject = new JSONObject(response);
        String accessToken = ((JSONObject)jsonObject.get("data")).getStr("access_token");
        return accessToken;
    }
}
