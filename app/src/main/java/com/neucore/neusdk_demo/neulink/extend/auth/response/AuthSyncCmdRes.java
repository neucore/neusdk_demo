package com.neucore.neusdk_demo.neulink.extend.auth.response;

import com.neucore.neulink.impl.CmdRes;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.AuthActionResult;

/**
 * 授权下发 响应
 */
public class AuthSyncCmdRes extends CmdRes {

    private AuthActionResult data;

    @Override
    public AuthActionResult getData() {
        return data;
    }

    public void setData(AuthActionResult data) {
        this.data = data;
    }
}
