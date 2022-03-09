package com.neucore.neulink.extend.auth;

import com.neucore.neulink.impl.CmdRes;

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
