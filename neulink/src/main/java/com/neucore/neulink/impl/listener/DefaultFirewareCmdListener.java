package com.neucore.neulink.impl.listener;

import android.util.Log;

import com.neucore.neulink.IDownloadProgressListener;
import com.neucore.neulink.IDownloder;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.impl.cmd.upd.UgrdeCmd;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.NeuHttpHelper;
import com.neucore.neulink.util.RequestContext;

import java.io.File;
import java.text.DecimalFormat;

public class DefaultFirewareCmdListener implements ICmdListener<ActionResult, UgrdeCmd> {

    private String TAG = "DefaultFirewareCmdListener";

    @Override
    public ActionResult doAction(NeulinkEvent<UgrdeCmd> event) {
        String[] cmds = null;
        try {
            UgrdeCmd cmd = event.getSource();
            String upgrade_url = cmd.getUrl();
            String md5 = cmd.getMd5();
            NeuLogUtils.iTag(TAG,"开始下载："+upgrade_url);

            /**
             * 新增上报下载进度
             */
            String resTopic = String.format("rrpc/res/%s",cmd.getBiz());

            IDownloder downloader = ServiceRegistry.getInstance().getDownloder();;
            File saveFile = downloader.start(ContextHolder.getInstance().getContext(),cmd.getReqNo(),cmd.getUrl(),new IDownloadProgressListener() {
                @Override
                public void onDownload(Double percent) {
                    DecimalFormat formater = new DecimalFormat("##.0");
                    String progress = formater.format(percent);
                    NeuLogUtils.iTag(TAG,cmd.getReqNo()+ " progress: "+progress);
                    NeulinkService.getInstance().getPublisherFacde().upldDownloadProgress(resTopic,cmd.getVersion(),cmd.getReqNo(),progress);
                }
                @Override
                public void onFinished(File file){
                    Log.i(TAG,"成功下载完成");
                }
            } );

            NeulinkService.getInstance().getPublisherFacde().upldDownloadProgress(resTopic,cmd.getVersion(),cmd.getReqNo(),"100");

            /**
             * @TODO: 业务实现
             */
            ActionResult<String> result = new ActionResult<>();
            result.setData(MESSAGE_SUCCESS);
            return result;
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
}
