package com.neucore.neulink.extend.impl;

import android.content.Context;

import com.neucore.neulink.cmd.rrpc.SceneSyncCmd;
import com.neucore.neulink.cmd.rrpc.SceneSyncCmdRes;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.util.JSonUtils;

/**
 * 场景感应下发
 */
public class SceneProcessor extends GProcessor<SceneSyncCmd, SceneSyncCmdRes,String> {
    public SceneProcessor(Context context) {
        super(context);
    }

    @Override
    public SceneSyncCmd parser(String payload) {
        return (SceneSyncCmd) JSonUtils.toObject(payload, SceneSyncCmd.class);
    }

    @Override
    protected SceneSyncCmdRes responseWrapper(SceneSyncCmd t, String result) {
        return null;
    }

    @Override
    protected SceneSyncCmdRes fail(SceneSyncCmd t, String error) {
        return null;
    }

    @Override
    protected SceneSyncCmdRes fail(SceneSyncCmd t, int code, String error) {
        return null;
    }
}
