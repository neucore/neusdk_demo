package com.neucore.neulink;

public interface IResumeDownloader extends IDownloder{
    void append(int size);
    void update(int threadId, long pos);
}
