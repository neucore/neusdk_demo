package com.neucore.neulink.proc;

import android.content.Context;

import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.upd.UgrdeCmd;
import com.neucore.neulink.upd.UgrdeCmdRes;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.NeuHttpHelper;
import com.neucore.neulink.util.RequestContext;

import java.io.File;
import java.util.Map;

/**
 * NeuSDK升级/或者固件升级
 */
public class FirewareProcessor extends GProcessor<UgrdeCmd, UgrdeCmdRes,String> {

    public FirewareProcessor(Context context){
        super(context);
    }
    @Override
    public String process(NeulinkTopicParser.Topic topic, UgrdeCmd cmd) {
        String[] cmds = null;
        Map<String, String> result = null;
        File srcFile = null;
        try {
            String upgrade_url = cmd.getUrl();
            String md5 = cmd.getMd5();
            srcFile = NeuHttpHelper.dld2File(this.getContext(), RequestContext.getId(), upgrade_url);
            ICmdListener listener = getListener();
            if(listener==null){
                throw new NeulinkException(404,"apk Listener does not implemention");
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
            if(srcFile==null){
                srcFile.delete();
            }
        }
    }

//    protected String getStatus(){
//        return Message.STATUS_SUCCESS;
//    }

    @Override
    public UgrdeCmd parser(String payload) {
        return (UgrdeCmd) JSonUtils.toObject(payload, UgrdeCmd.class);
    }

    @Override
    protected UgrdeCmdRes responseWrapper(UgrdeCmd cmd, String result) {
        UgrdeCmdRes res = new UgrdeCmdRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        res.setCode(200);
        res.setMsg("success");
        return res;
    }

    @Override
    protected UgrdeCmdRes fail(UgrdeCmd cmd, String error) {
        UgrdeCmdRes res = new UgrdeCmdRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        res.setCode(500);
        res.setMsg(error);
        return res;
    }

    @Override
    protected UgrdeCmdRes fail(UgrdeCmd cmd,int code, String error) {
        UgrdeCmdRes res = new UgrdeCmdRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        res.setCode(code);
        res.setMsg(error);
        return res;
    }

    @Override
    protected String resTopic(){
        return "rrpc/res/firmware";
    }

    @Override
    protected ICmdListener getListener() {
        return ListenerFactory.getInstance().getAPkListener();
    }
}
