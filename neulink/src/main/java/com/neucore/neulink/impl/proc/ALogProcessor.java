package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.ListenerRegistrator;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.ServiceRegistrator;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.cmd.rmsg.app.AlogUpgrCmd;
import com.neucore.neulink.cmd.rmsg.app.AlogUpgrRes;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.NeuHttpHelper;
import com.neucore.neulink.util.RequestContext;

import java.io.File;

public class ALogProcessor extends GProcessor<AlogUpgrCmd,AlogUpgrRes, ActionResult<String>>{

    public ALogProcessor(Context context){
        super(context);
    }

    @Override
    public ActionResult<String> process(NeulinkTopicParser.Topic topic, AlogUpgrCmd alog) {
        File srcFile = null;
        try {

            srcFile = NeuHttpHelper.dld2File(this.getContext(), RequestContext.getId(), alog.getUrl());
            ICmdListener listener = getListener();
            if(listener==null){
                throw new NeulinkException(STATUS_404,"alog Listener does not implemention");
            }
            listener.doAction(new NeulinkEvent(srcFile));
            ActionResult<String> actionResult = new ActionResult<>();
            actionResult.setData(MESSAGE_SUCCESS);
            return actionResult;
        }
        catch (NeulinkException ex){
            throw ex;
        }
        catch (Throwable ex){
            throw new RuntimeException(ex);
        }
        finally {
            if(srcFile!=null){
                srcFile.delete();
            }
        }
    }
    public AlogUpgrCmd parser(String payload){
        return JSonUtils.toObject(payload, AlogUpgrCmd.class);
    }

    public AlogUpgrRes responseWrapper(AlogUpgrCmd cmd, ActionResult<String> actionResult) {
        AlogUpgrRes res = new AlogUpgrRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        res.setVinfo(cmd.getVinfo());
        res.setData(actionResult.getData());
        return res;
    }

    public AlogUpgrRes fail(AlogUpgrCmd cmd, String message) {
        AlogUpgrRes res = new AlogUpgrRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCode(STATUS_500);
        res.setMsg(MESSAGE_FAILED);
        res.setVinfo(cmd.getVinfo());
        res.setData(message);
        return res;
    }
    public AlogUpgrRes fail(AlogUpgrCmd cmd, int code, String message) {
        AlogUpgrRes res = new AlogUpgrRes();
        res.setDeviceId(ServiceRegistrator.getInstance().getDeviceService().getExtSN());
        res.setCode(code);
        res.setMsg(MESSAGE_FAILED);
        res.setVinfo(cmd.getVinfo());
        res.setData(message);
        return res;
    }

    @Override
    protected String biz() {
        return "alog";
    }

    @Override
    protected ICmdListener getListener() {
        return ListenerRegistrator.getInstance().getAlogListener();
    }
}
