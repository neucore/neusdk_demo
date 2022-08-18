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
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsicmVzMSJdLCJzY29wZUlkIjoyLCJyb2xlIjoxLCJ1c2VyX2lkIjoyMSwidXNlcl9uYW1lIjoie1wiaWRcIjoyMSxcInNjb3BlSWRcIjoyLFwidXNlcm5hbWVcIjpcIjEyOTk1ODY3ODhAcXEuY29tXCIsXCJwYXNzd29yZFwiOm51bGwsXCJmdWxsbmFtZVwiOlwi5bCR5LyfMVwiLFwiZW1haWxcIjpcIjEyOTk1ODY3ODhAcXEuY29tXCIsXCJwaG9uZU51bWJlclwiOm51bGwsXCJleHRlcm5hbElkXCI6bnVsbCxcImhlYWRQb3J0cmFpdFwiOlwiL2dmcmFtZS8yL3VzZXJzLzIxL2hwL2F2YXRhci5wbmdcIixcInR5cGVcIjo0LFwicm9sZVwiOjEsXCJkZXNjcmlwdGlvblwiOm51bGwsXCJpcFwiOm51bGwsXCJvcHRsb2NrXCI6MSxcImV4cGlyYXRpb25EYXRlXCI6MTY1MTg4NTYzNDAwMCxcInN0YXR1c1wiOjAsXCJpc0RlbFwiOjAsXCJjcmVhdGVkT25cIjoxNjUxODg1NjM0MDAwLFwibW9kaWZpZWRPblwiOjE2NjA3NDA0MzAwMDB9Iiwic2NvcGUiOlsiUk9MRV9BRE1JTiIsIlJPTEVfVVNFUiIsIlJPTEVfQVBJIl0sImV4cCI6MTY2MTAzNzA1NCwidHlwZSI6NCwiYXV0aG9yaXRpZXMiOlsiYWxsIl0sImp0aSI6Ijk0OTE0NGI0LTA3NTYtNDY3NS05MmE1LWE2MjExZjI0NzhkYyIsImNsaWVudF9pZCI6ImdlbWluaSJ9.9s9OWAXv3odeZARpGd_shk7kz2kay8mew2h3oqnBD-k";
    }
}
