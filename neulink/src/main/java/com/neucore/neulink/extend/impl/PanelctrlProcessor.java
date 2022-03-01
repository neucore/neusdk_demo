package com.neucore.neulink.extend.impl;

import android.content.Context;

import com.neucore.neulink.cmd.rrpc.PanelCtrlSyncCmd;
import com.neucore.neulink.cmd.rrpc.PanelCtrlSyncCmdRes;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.util.JSonUtils;

/**
 * 面板控制下发
 */
public class PanelctrlProcessor extends GProcessor<PanelCtrlSyncCmd, PanelCtrlSyncCmdRes,String> {
    public PanelctrlProcessor(Context context) {
        super(context);
    }

    @Override
    public PanelCtrlSyncCmd parser(String payload) {
        return (PanelCtrlSyncCmd) JSonUtils.toObject(payload, PanelCtrlSyncCmd.class);
    }

    @Override
    protected PanelCtrlSyncCmdRes responseWrapper(PanelCtrlSyncCmd t, String result) {
        return null;
    }

    @Override
    protected PanelCtrlSyncCmdRes fail(PanelCtrlSyncCmd t, String error) {
        return null;
    }

    @Override
    protected PanelCtrlSyncCmdRes fail(PanelCtrlSyncCmd t, int code, String error) {
        return null;
    }

}
