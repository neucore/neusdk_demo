package com.neucore.neulink;

import android.content.Context;

import java.io.File;

/**
 * 简单文件下载
 */
public interface IDownloder {

    File start(Context context,String reqNo, String url,IDownloadProgressListener listener) throws Exception;
}
