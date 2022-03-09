package com.neucore.neulink.extend.auth;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.NeulinkEvent;

public class AuthCmdListener implements ICmdListener<AuthActionResult,AuthSyncCmd> {
    @Override
    public AuthActionResult doAction(NeulinkEvent<AuthSyncCmd> event) {
        AuthSyncCmd cmd = event.getSource();
        return null;
    }
}
