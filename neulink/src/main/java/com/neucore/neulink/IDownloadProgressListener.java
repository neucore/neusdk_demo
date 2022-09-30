package com.neucore.neulink;

import java.io.File;

public interface IDownloadProgressListener {
    void onDownload(long size);
    void onFinished(File file);
}
