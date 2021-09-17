package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.cmd.rrpc.AuthSyncCmd;
import com.neucore.neulink.cmd.rrpc.AuthSyncCmdRes;
import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;

public class AuthProcessor  extends GProcessor<AuthSyncCmd, AuthSyncCmdRes,String> {
    public AuthProcessor(Context context) {
        super(context);
    }

    @Override
    public String process(NeulinkTopicParser.Topic topic, AuthSyncCmd payload) {

        return null;
    }

    @Override
    public AuthSyncCmd parser(String payload) {
        return null;
    }

    @Override
    protected AuthSyncCmdRes responseWrapper(AuthSyncCmd t, String result) {
        return null;
    }

    @Override
    protected AuthSyncCmdRes fail(AuthSyncCmd t, String error) {
        return null;
    }

    @Override
    protected AuthSyncCmdRes fail(AuthSyncCmd t, int code, String error) {
        return null;
    }

    @Override
    protected String resTopic() {
        return null;
    }

    @Override
    protected ICmdListener getListener() {
        return null;
    }
}
