package com.neucore.neulink.impl.service.resume;

import android.content.Context;

import com.neucore.neulink.IDownloder;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.NeuHttpHelper;
import com.neucore.neulink.util.RequestContext;

import java.io.File;
import java.io.IOException;

public class HttpDownloader implements IDownloder, NeulinkConst {
    @Override
    public File execute(Context context, String reqNo, String url) throws IOException {

        String libDir = DeviceUtils.getTmpPath(context)+"/faceDir";
        String reqdir = libDir+File.separator+ RequestContext.getId();
        File toDir = new File(reqdir);
        return NeuHttpHelper.dld2File(context,reqNo,url, toDir);
    }
}
