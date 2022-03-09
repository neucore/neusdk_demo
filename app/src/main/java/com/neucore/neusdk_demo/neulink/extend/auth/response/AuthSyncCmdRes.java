package com.neucore.neusdk_demo.neulink.extend.auth.response;

import com.neucore.neulink.impl.CmdRes;
import com.neucore.neusdk_demo.neulink.extend.auth.response.AuthActionResult;

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
