package com.neucore.neulink.impl.down.http;

public class HttpResumeDownloadTaskContext {
    private long startPos;
    private long endPos;
    private long downloaded;

    public HttpResumeDownloadTaskContext(long startPos, long endPos, long downloaded){
        this.startPos = startPos;
        this.endPos =endPos;
        this.downloaded = downloaded;
    }

    public long getStartPos() {
        return startPos;
    }

    public void setStartPos(long startPos) {
        this.startPos = startPos;
    }

    public long getEndPos() {
        return endPos;
    }

    public void setEndPos(long endPos) {
        this.endPos = endPos;
    }

    public long getData(){
        return endPos-startPos;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(long downloaded) {
        this.downloaded = downloaded;
    }
}
