package com.neucore.neulink;

import android.content.Context;

import java.io.File;

public interface IResumeDownloader{
    void append(int size);
    void update(int threadId, long pos);
    File getSaveFile();
    long getFileSize();
    void start(Context context, String reqNo, String url, IDownloadProgressListener listener) throws Exception;
}
