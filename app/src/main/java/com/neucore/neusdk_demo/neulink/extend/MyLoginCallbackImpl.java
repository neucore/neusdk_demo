package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.util.NeuHttpHelper;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.json.JSONObject;

/**
 * 登录服务回调实现
 */
public class MyLoginCallbackImpl implements ILoginCallback {
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
        headers.put("from","2");

        Map<String,String> params = new HashMap<>();
        params.put("client_id","gemini");
        params.put("client_secret","secret");
        params.put("grant_type","password");
        params.put("username","changwei.yao@neucore.com");
        params.put("password","123456");


        String response = NeuHttpHelper.post("https://dev.neucore.com/api/uaa/oauth/token",params,headers,3);

        JSONObject jsonObject = new JSONObject(response);
        String accessToken = ((JSONObject)jsonObject.get("data")).getStr("access_token");
        return accessToken;
    }
}
