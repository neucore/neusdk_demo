package com.neucore.neulink.impl.listener;

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

public class DefaultFirewareCmdListener implements ICmdListener<ActionResult, UgrdeCmd> {

    private String TAG = "DefaultFirewareCmdListener";

    @Override
    public ActionResult doAction(NeulinkEvent<UgrdeCmd> event) {
        String[] cmds = null;
        File srcFile = null;
        try {
            UgrdeCmd cmd = event.getSource();
            String upgrade_url = cmd.getUrl();
            String md5 = cmd.getMd5();
            NeuLogUtils.iTag(TAG,"开始下载："+upgrade_url);

            String storeDir = DeviceUtils.getExternalCacheDir(ContextHolder.getInstance().getContext());
            /**
             * 单线程
             */
            srcFile = NeuHttpHelper.dld2File(ContextHolder.getInstance().getContext(), RequestContext.getId(), upgrade_url+"&osooso=debug",new File(storeDir));
            /**
             * 新增上报下载进度
             */
            String resTopic = String.format("rrpc/res/%s",cmd.getBiz());
            NeulinkService.getInstance().getPublisherFacde().upldDownloadProgress(resTopic,cmd.getVersion(),cmd.getReqId(),"100");

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
