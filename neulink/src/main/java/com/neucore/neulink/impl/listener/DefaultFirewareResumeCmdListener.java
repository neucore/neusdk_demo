package com.neucore.neulink.impl.listener;

import android.util.Log;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.IDownloadProgressListener;
import com.neucore.neulink.IDownloder;
import com.neucore.neulink.IFileService;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.impl.cmd.upd.UgrdeCmd;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.util.ContextHolder;

import java.io.File;
import java.text.DecimalFormat;

import cn.hutool.core.util.ObjectUtil;

public class DefaultFirewareResumeCmdListener implements ICmdListener<ActionResult, UgrdeCmd> {
    private String TAG = "DefaultFirewareResumeCmdListener";

    @Override
    public ActionResult doAction(NeulinkEvent<UgrdeCmd> event) {
        String[] cmds = null;
//        Map<String, String> result = null;
        File srcFile = null;
        try {
            final UgrdeCmd cmd = event.getSource();
            final String upgrade_url = cmd.getUrl();
            NeuLogUtils.iTag(TAG,"开始下载："+upgrade_url);
            final String resTopic = String.format("rrpc/res/%s",cmd.getBiz());
            String md5 = cmd.getMd5();

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

            NeuLogUtils.iTag(TAG,"存储位置: "+saveFile.getAbsolutePath());

            /**
             * TODO saveAs to dest for OTA
             */
//            saveFile.delete();

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
