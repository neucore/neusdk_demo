package com.neucore.neulink;

import java.io.File;

public interface IDownloadProgressListener {
    void onDownload(Double percent);
    void onFinished(File file);
}
