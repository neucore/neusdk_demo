package com.neucore.neulink.impl.proc;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.cmd.upd.UgrdeCmd;
import com.neucore.neulink.cmd.upd.UgrdeCmdRes;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.NeuHttpHelper;
import com.neucore.neulink.util.RequestContext;

import java.io.File;
import java.util.Map;

/**
 * NeuSDK升级/或者固件升级
 * @deprecated
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
            Log.i(TAG,"开始下载："+upgrade_url);

            String storeDir = DeviceUtils.getExternalCacheDir(ContextHolder.getInstance().getContext());
            /**
             * 单线程
             */
            srcFile = NeuHttpHelper.dld2File(this.getContext(), RequestContext.getId(), upgrade_url+"&osooso=debug",new File(storeDir));
            /**
             * 新增上报下载进度
             */
            String resTopic = String.format("rrpc/res/%s",topic.getBiz());
            NeulinkService.getInstance().getPublisherFacde().upldDownloadProgress(resTopic,topic.getReqId(),"100");

            ICmdListener listener = getListener();
            if(listener==null){
                throw new NeulinkException(STATUS_404,"apk Listener does not implemention");
            }

            cmd.setLocalFile(srcFile);
            listener.doAction(new NeulinkEvent(cmd));
            return MESSAGE_SUCCESS;
        }
        catch (NeulinkException ex){
            throw ex;
        }
        catch (Throwable ex){
            throw new RuntimeException(ex);
        }
        finally {
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
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        return res;
    }

    @Override
    protected UgrdeCmdRes fail(UgrdeCmd cmd, String error) {
        UgrdeCmdRes res = new UgrdeCmdRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCode(STATUS_500);
        res.setMsg(error);
        return res;
    }

    @Override
    protected UgrdeCmdRes fail(UgrdeCmd cmd,int code, String error) {
        UgrdeCmdRes res = new UgrdeCmdRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCode(code);
        res.setMsg(error);
        return res;
    }

    @Override
    protected ICmdListener getListener() {
        return ListenerFactory.getInstance().getFireware$ApkListener();
    }
}
