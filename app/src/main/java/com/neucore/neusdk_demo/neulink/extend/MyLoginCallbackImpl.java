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
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsicmVzMSJdLCJzY29wZUlkIjoxLCJyb2xlIjoxLCJ1c2VyX2lkIjoxLCJ1c2VyX25hbWUiOiJ7XCJpZFwiOjEsXCJzY29wZUlkXCI6MSxcInVzZXJuYW1lXCI6XCJhZG1pblwiLFwiZnVsbG5hbWVcIjpcIuW5s-WPsOeuoeeQhuWRmDFcIixcImVtYWlsXCI6XCJhb3FpLnN1bkBuZXVjb3JlLmNvbVwiLFwicGhvbmVOdW1iZXJcIjpcIjE1MjAxOTM2NTQxMlwiLFwiZXh0ZXJuYWxJZFwiOlwiXCIsXCJoZWFkUG9ydHJhaXRcIjpcIi9nZnJhbWUvMS91c2Vycy8xL2hwL2F2YXRhci5qcGdcIixcInR5cGVcIjoxLFwicm9sZVwiOjEsXCJvcHRsb2NrXCI6MSxcImV4cGlyYXRpb25EYXRlXCI6MTY1MTczOTk4MDAwMCxcInN0YXR1c1wiOjAsXCJpc0RlbFwiOjAsXCJjcmVhdGVkT25cIjoxNjQ2MDM5ODA1MDAwLFwibW9kaWZpZWRPblwiOjE2NTUxMTk4NzQwMDB9Iiwic2NvcGUiOlsiUk9MRV9BRE1JTiIsIlJPTEVfVVNFUiIsIlJPTEVfQVBJIl0sImV4cCI6MTY1NTM4MDM4MiwidHlwZSI6MSwiYXV0aG9yaXRpZXMiOlsiYWxsIl0sImp0aSI6ImNlMDBlOTQ1LTEyZjUtNGRkNy05YjVjLTMyOGNkZjI3YWY0MyIsImNsaWVudF9pZCI6ImdlbWluaSJ9.qR74OEfYyz1occcwpewoI_VDfQD_o3fervlS4lYRtyU";
    }
}
