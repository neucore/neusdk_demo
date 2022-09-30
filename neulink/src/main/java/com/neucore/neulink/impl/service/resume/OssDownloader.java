package com.neucore.neulink.impl.service.resume;

import android.content.Context;

import com.neucore.neulink.IDownloadProgressListener;
import com.neucore.neulink.IDownloder;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.NeuHttpHelper;
import com.neucore.neulink.util.RequestContext;

import java.io.File;
import java.io.IOException;

import cn.hutool.core.util.ObjectUtil;

public class OssDownloader implements IDownloder, NeulinkConst {

    @Override
    public File start(Context context, String reqNo, String url, IDownloadProgressListener listener) throws IOException {
        String tmpPath = DeviceUtils.getTmpPath(context);
        String reqdir = tmpPath+File.separator+ RequestContext.getId();
        File toDir = new File(reqdir);
        /**
         * TODO
         */
        File dest = NeuHttpHelper.dld2File(context,reqNo,url, toDir);
        if(ObjectUtil.isNotEmpty(listener)){
            listener.onDownload(100d);
            listener.onFinished(dest);
        }
        return dest;
    }
}
