package com.neucore.neulink.proc;

import android.content.Context;

import com.neucore.neulink.check.CheckCmd;
import com.neucore.neulink.check.CheckCmdRes;
import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.QueryResult;
import com.neucore.neulink.extend.Result;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.JSonUtils;

import java.util.Map;

public class CheckProcessor extends GProcessor<CheckCmd, CheckCmdRes,String> {

    public CheckProcessor(Context context) {
        super(context);
    }

    @Override
    public String process(NeulinkTopicParser.Topic topic, CheckCmd payload) {
        NeulinkEvent event = new NeulinkEvent(payload);
        ICmdListener<QueryResult<Map<String,Object>>> listener = getListener();
        QueryResult<Map<String,Object>> result = listener.doAction(event);
        return (String)result.getData().get("card_ids");
    }

    @Override
    public CheckCmd parser(String payload) {
        return (CheckCmd) JSonUtils.toObject(payload, CheckCmd.class);
    }

    @Override
    protected CheckCmdRes responseWrapper(CheckCmd t, String result) {
        CheckCmdRes cmdRes = new CheckCmdRes();
        cmdRes.setCmdStr(t.getCmdStr());
        cmdRes.setCode(200);
        cmdRes.setObjtype(t.getObjtype());
        cmdRes.setMsg("success");
        cmdRes.setDatas(result);
        return cmdRes;
    }

    @Override
    protected CheckCmdRes fail(CheckCmd t, String error) {
        CheckCmdRes cmdRes = new CheckCmdRes();
        cmdRes.setCmdStr(t.getCmdStr());
        cmdRes.setCode(500);
        cmdRes.setObjtype(t.getObjtype());
        cmdRes.setMsg(error);
        return cmdRes;
    }

    @Override
    protected CheckCmdRes fail(CheckCmd t, int code, String error) {
        CheckCmdRes cmdRes = new CheckCmdRes();
        cmdRes.setCmdStr(t.getCmdStr());
        cmdRes.setCode(code);
        cmdRes.setObjtype(t.getObjtype());
        cmdRes.setMsg(error);
        return cmdRes;
    }

    @Override
    protected String resTopic(){
        return "rrpc/res/check";
    }

    @Override
    protected ICmdListener getListener() {
        return ListenerFactory.getInstance().getFaceCheckListener();
    }
}
