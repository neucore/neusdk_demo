package com.neucore.neulink.rrpc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaceCmd {

    private String cmd;
    private long reqtime;
    private long offset;
    private long pages;

    private List<FaceData> payload;
    private Map imageDatas = new HashMap();

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public long getReqtime() {
        return reqtime;
    }

    public void setReqtime(long reqtime) {
        this.reqtime = reqtime;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public List<FaceData> getPayload() {
        return payload;
    }

    public void setPayload(List<FaceData> payload) {
        this.payload = payload;
    }

    public Map getImageDatas() {
        return imageDatas;
    }

    public void setImageDatas(Map datas) {
        this.imageDatas = datas;
    }
}
