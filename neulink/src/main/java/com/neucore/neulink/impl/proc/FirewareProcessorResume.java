package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.cmd.upd.UgrdeCmd;
import com.neucore.neulink.cmd.upd.UgrdeCmdRes;
import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.impl.service.resume.DownloadProgressListener;
import com.neucore.neulink.impl.service.resume.FileDownloader;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.FileUtils;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.RequestContext;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * NeuSDK升级/或者固件升级
 */
public class FirewareProcessorResume extends GProcessor<UgrdeCmd, UgrdeCmdRes,String> {

    public FirewareProcessorResume(Context context){
        super(context);
    }
    @Override
    public String process(final NeulinkTopicParser.Topic topic, UgrdeCmd cmd) {
        String[] cmds = null;
        Map<String, String> result = null;
        File srcFile = null;
        try {
            final String upgrade_url = cmd.getUrl();
            String md5 = cmd.getMd5();
            String storeDir = DeviceUtils.getExternalCacheDir(ContextHolder.getInstance().getContext())+File.separator+RequestContext.getId();
            final FileDownloader downloader = new FileDownloader(ContextHolder.getInstance().getContext(), upgrade_url, new File(storeDir), 3);
            downloader.download(new DownloadProgressListener() {
                @Override
                public void onDownloadSize(int size) {
                    long total = downloader.getFileSize();
                    DecimalFormat formater = new DecimalFormat("##.0");
                    String progress = formater.format(size*1.0/total*1.0*100);
                    String resTopic = String.format("rrpc/res/",topic.getBiz());
                    NeulinkService.getInstance().getPublisherFacde().upldDownloadProgress(resTopic,topic.getReqId(),progress);
                }
            });
            ICmdListener listener = getListener();
            if(listener==null){
                throw new NeulinkException(404,"apk Listener does not implemention");
            }
            cmd.setLocalFile(srcFile);
            listener.doAction(new NeulinkEvent(cmd));
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
                FileUtils.delete(srcFile.getAbsolutePath());
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
        return ListenerFactory.getInstance().getFireware$ApkListener();
    }
}
