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
        //NeuLogUtils.iTag(TAG,"login...");
        //return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsicmVzMSJdLCJzY29wZUlkIjoyLCJyb2xlIjoxLCJ1c2VyX2lkIjoyMSwidXNlcl9uYW1lIjoie1wiaWRcIjoyMSxcInNjb3BlSWRcIjoyLFwidXNlcm5hbWVcIjpcIjEyOTk1ODY3ODhAcXEuY29tXCIsXCJwYXNzd29yZFwiOm51bGwsXCJmdWxsbmFtZVwiOlwi5bCR5LyfMVwiLFwiZW1haWxcIjpcIjEyOTk1ODY3ODhAcXEuY29tXCIsXCJwaG9uZU51bWJlclwiOm51bGwsXCJleHRlcm5hbElkXCI6bnVsbCxcImhlYWRQb3J0cmFpdFwiOlwiaHR0cHM6Ly9uZXVkZXZpY2Uub3NzLWNuLXNoYW5naGFpLmFsaXl1bmNzLmNvbS9nZnJhbWUvMi91c2Vycy8yMS9ocC9hdmF0YXIucG5nXCIsXCJ0eXBlXCI6NCxcInJvbGVcIjoxLFwiZGVzY3JpcHRpb25cIjpudWxsLFwiaXBcIjpudWxsLFwib3B0bG9ja1wiOjEsXCJleHBpcmF0aW9uRGF0ZVwiOjE2NTE4ODU2MzQwMDAsXCJzdGF0dXNcIjowLFwiaXNEZWxcIjowLFwiY3JlYXRlZE9uXCI6MTY1MTg4NTYzNDAwMCxcIm1vZGlmaWVkT25cIjoxNjYxMTU2NDM3MDAwfSIsInNjb3BlIjpbIlJPTEVfQURNSU4iLCJST0xFX1VTRVIiLCJST0xFX0FQSSJdLCJleHAiOjE2NjE0NzE2MjYsInR5cGUiOjQsImF1dGhvcml0aWVzIjpbImFsbCJdLCJqdGkiOiI2M2JlZTk1Ni1jNWQ1LTQ0ZGMtODU4Mi1jMTMxMTJiMTNlZDMiLCJjbGllbnRfaWQiOiJnZW1pbmkifQ.hdfzRxMKM9k8jsZ_qHvkpcq-__VJ9hIOXUujXZLKWFM";
        /**
         * 实现登录返回token
         */
        NeuLogUtils.iTag(TAG,"login...");

        Map<String,String> headers = new HashMap<>();
        headers.put("accept-language","zh-Hans-CN");
        headers.put("mate-scope","Mg");
        headers.put("from","2");

        Map<String,String> params = new HashMap<>();
        params.put("client_id","client-smrtlib");
        params.put("client_secret","client-smrtlib-secret");
        params.put("grant_type","password");
        params.put("username","15800860806");
        params.put("password","123456");


        String response = NeuHttpHelper.post("http://47.118.59.46:18093/v1/oauth/token",params,headers,3);
//        String response = NeuHttpHelper.post(Util.getRetrofitBaseUrl() + "/v1/oauth/token",params,headers,3);

        JSONObject jsonObject = new JSONObject(response);
        String accessToken = ((JSONObject)jsonObject.get("data")).getStr("access_token");
        return accessToken;
    }
}
