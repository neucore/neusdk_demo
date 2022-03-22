package com.neucore.neusdk_demo.neulink.extend.other;

import android.util.Log;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.cmd.upd.UgrdeCmd;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.impl.service.resume.DownloadProgressListener;
import com.neucore.neulink.impl.service.resume.FileDownloader;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.RequestContext;

import java.io.File;
import java.text.DecimalFormat;

public class SampleFirewareResumeCmdListener implements ICmdListener<ActionResult, UgrdeCmd> {
    private String TAG = "DefaultFirewareResumeCmdListener";
    @Override
    public ActionResult doAction(NeulinkEvent<UgrdeCmd> event) {
        /**
         * @TODO: 业务实现
         */
        String[] cmds = null;
        File srcFile = null;
        try {
            final UgrdeCmd cmd = event.getSource();
            final String upgrade_url = cmd.getUrl();
            Log.i(TAG,"开始下载："+upgrade_url);
            final String resTopic = String.format("rrpc/res/%s",cmd.getBiz());
            String md5 = cmd.getMd5();
            /**
             * 发送响应消息给到服务端
             */
            String storeDir = DeviceUtils.getExternalCacheDir(ContextHolder.getInstance().getContext())+File.separator+ RequestContext.getId();
            final FileDownloader downloader = new FileDownloader(ContextHolder.getInstance().getContext(), upgrade_url, new File(storeDir), 6);
            downloader.download(new DownloadProgressListener() {
                @Override
                public void onDownloadSize(long size) {
                    long total = downloader.getFileSize();
                    DecimalFormat formater = new DecimalFormat("##.0");
                    String progress = formater.format(size*1.0/total*1.0*100);

                    Log.i(TAG,cmd.getReqId()+ " progress: "+progress);
                    NeulinkService.getInstance().getPublisherFacde().upldDownloadProgress(resTopic,cmd.getReqId(),progress);
                }
            });
            Log.i(TAG,"成功下载完成："+downloader.getFileSize());
            Log.i(TAG,"存储位置: "+downloader.getSaveFile().getAbsolutePath());


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
