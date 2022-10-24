package com.neucore.neulink.impl.down.http;

public class DownloadTaskContext {
    private long startPos;
    private long endPos;
    private long data;

    public DownloadTaskContext(long startPos, long endPos){
        this.startPos = startPos;
        this.endPos =endPos;
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
}
