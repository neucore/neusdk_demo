package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.cmd.rrpc.RsvctrlSynCmd;
import com.neucore.neulink.cmd.rrpc.RsvctrlSynCmdRes;
import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.JSonUtils;

/**
 * 预约控制下发
 */
public class RsvctrlProcessor extends GProcessor<RsvctrlSynCmd, RsvctrlSynCmdRes,String> {
    public RsvctrlProcessor(Context context) {
        super(context);
    }

    @Override
    public String process(NeulinkTopicParser.Topic topic, RsvctrlSynCmd payload) {
        return "待实现";
    }

    @Override
    public RsvctrlSynCmd parser(String payload) {
        return (RsvctrlSynCmd) JSonUtils.toObject(payload, RsvctrlSynCmd.class);
    }

    @Override
    protected RsvctrlSynCmdRes responseWrapper(RsvctrlSynCmd t, String result) {
        return null;
    }

    @Override
    protected RsvctrlSynCmdRes fail(RsvctrlSynCmd t, String error) {
        return null;
    }

    @Override
    protected RsvctrlSynCmdRes fail(RsvctrlSynCmd t, int code, String error) {
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
