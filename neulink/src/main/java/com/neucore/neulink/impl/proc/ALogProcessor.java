package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.cmd.rmsg.app.AlogUpgrCmd;
import com.neucore.neulink.cmd.rmsg.app.AlogUpgrRes;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.NeuHttpHelper;
import com.neucore.neulink.util.RequestContext;

import java.io.File;

public class ALogProcessor extends GProcessor<AlogUpgrCmd,AlogUpgrRes,String>{

    public ALogProcessor(Context context){
        super(context);
    }

    @Override
    public String process(NeulinkTopicParser.Topic topic, AlogUpgrCmd alog) {
        File srcFile = null;
        try {

            srcFile = NeuHttpHelper.dld2File(this.getContext(), RequestContext.getId(), alog.getUrl());
            ICmdListener listener = getListener();
            if(listener==null){
                throw new NeulinkException(404,"alog Listener does not implemention");
            }
            listener.doAction(new NeulinkEvent(srcFile));
            return "success";
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
        return (AlogUpgrCmd) JSonUtils.toObject(payload, AlogUpgrCmd.class);
    }

    public AlogUpgrRes responseWrapper(AlogUpgrCmd cmd, String result) {
        AlogUpgrRes res = new AlogUpgrRes();
        res.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        res.setCode(200);
        res.setMsg(result);
        res.setVinfo(cmd.getVinfo());
        return res;
    }

    public AlogUpgrRes fail(AlogUpgrCmd cmd, String message) {
        AlogUpgrRes res = new AlogUpgrRes();
        res.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        res.setCode(500);
        res.setMsg(message);
        res.setVinfo(cmd.getVinfo());
        return res;
    }
    public AlogUpgrRes fail(AlogUpgrCmd cmd, int code, String message) {
        AlogUpgrRes res = new AlogUpgrRes();
        res.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        res.setCode(code);
        res.setMsg(message);
        res.setVinfo(cmd.getVinfo());
        return res;
    }

    @Override
    protected String biz() {
        return "alog";
    }

    @Override
    protected ICmdListener getListener() {
        return ListenerFactory.getInstance().getAlogListener();
    }
}
