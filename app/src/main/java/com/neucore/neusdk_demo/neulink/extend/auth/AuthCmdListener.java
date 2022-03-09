package com.neucore.neusdk_demo.neulink.extend.auth;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neusdk_demo.neulink.extend.auth.request.AuthSyncCmd;
import com.neucore.neusdk_demo.neulink.extend.auth.response.AuthActionResult;

/**
 *
 */
public class AuthCmdListener implements ICmdListener<AuthActionResult, AuthSyncCmd> {
    @Override
    public AuthActionResult doAction(NeulinkEvent<AuthSyncCmd> event) {
        AuthSyncCmd cmd = event.getSource();
        /**
         * @TODO: 实现业务。。。构造返回结果
         */
        return null;
    }
}
