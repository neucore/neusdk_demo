package com.neucore.neulink.impl.service.resume;

import android.content.Context;

import com.neucore.neulink.IDownloadProgressListener;
import com.neucore.neulink.IDownloder;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.impl.service.NeulinkSecurity;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.NeuHttpHelper;
import com.neucore.neulink.util.RequestContext;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import cn.hutool.core.util.ObjectUtil;

public class HttpDownloader implements IDownloder, NeulinkConst {

    @Override
    public File start(Context context, String reqNo, String url, IDownloadProgressListener listener) throws IOException {
        ILoginCallback loginCallback = ServiceRegistry.getInstance().getLoginCallback();
        String token = loginCallback.login();
        if(token!=null){
            int index = token.indexOf(" ");
            if(index!=-1){
                token = token.substring(index+1);
            }
        }
        if(ObjectUtil.isNotEmpty(token)){
            NeulinkSecurity.getInstance().setToken(token);
        }

        HashMap<String,String> headers = new HashMap<>();
        headers.put("Authorization","bearer "+token);
        String tmpPath = DeviceUtils.getTmpPath(context);
        String reqdir = tmpPath+File.separator+ RequestContext.getId();
        File toDir = new File(reqdir);
        File dest = NeuHttpHelper.dld2File(context,headers,reqNo,url, toDir);
        if(ObjectUtil.isNotEmpty(listener)){
            listener.onDownload(100d);
            listener.onFinished(dest);
        }
        return dest;
    }
}
