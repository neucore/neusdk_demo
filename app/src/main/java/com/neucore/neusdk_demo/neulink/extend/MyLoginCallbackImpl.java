package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.log.NeuLogUtils;

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
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsicmVzMSJdLCJzY29wZUlkIjoyLCJyb2xlIjoxLCJ1c2VyX2lkIjoyMSwidXNlcl9uYW1lIjoie1wiaWRcIjoyMSxcInNjb3BlSWRcIjoyLFwidXNlcm5hbWVcIjpcIjEyOTk1ODY3ODhAcXEuY29tXCIsXCJwYXNzd29yZFwiOm51bGwsXCJmdWxsbmFtZVwiOlwi5bCR5LyfMVwiLFwiZW1haWxcIjpcIjEyOTk1ODY3ODhAcXEuY29tXCIsXCJwaG9uZU51bWJlclwiOm51bGwsXCJleHRlcm5hbElkXCI6bnVsbCxcImhlYWRQb3J0cmFpdFwiOlwiZ2ZyYW1lLzIvdXNlcnMvMjEvaHAvYXZhdGFyLnBuZ1wiLFwidHlwZVwiOjQsXCJyb2xlXCI6MSxcImRlc2NyaXB0aW9uXCI6bnVsbCxcImlwXCI6bnVsbCxcIm9wdGxvY2tcIjoxLFwiZXhwaXJhdGlvbkRhdGVcIjoxNjUxODg1NjM0MDAwLFwic3RhdHVzXCI6MCxcImlzRGVsXCI6MCxcImNyZWF0ZWRPblwiOjE2NTE4ODU2MzQwMDAsXCJtb2RpZmllZE9uXCI6MTY2MTc1ODkzMjAwMH0iLCJzY29wZSI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIiwiUk9MRV9BUEkiXSwiZXhwIjoxNjYyMDE4Njk2LCJ0eXBlIjo0LCJhdXRob3JpdGllcyI6WyJhbGwiXSwianRpIjoiY2ExMjdlYjYtYjFhNi00OGJkLThjYmMtYjZjMTQ5MzFjYzI0IiwiY2xpZW50X2lkIjoiZ2VtaW5pIn0.-FckKn-yMnN6DrkA4lIj0W4_4x3GAjWGob31C5_AXu8";
    }
}
