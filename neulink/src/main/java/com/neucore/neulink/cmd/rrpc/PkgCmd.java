package com.neucore.neulink.cmd.rrpc;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.Cmd;

import java.util.List;
import java.util.Map;

public class PkgCmd<T extends PkgData,K,V> extends Cmd {
    @SerializedName("objtype")
    private String objtype;
    @SerializedName("total")
    private long total;
    @SerializedName("pages")
    private long pages;
    @SerializedName("offset")
    private Long offset;
    @SerializedName("data_url")
    private String dataUrl;
    @SerializedName("md5")
    private String md5;
    private String cmd;
    private long reqtime;
    private List<T> data;
    private Map<String,Map<K,V>> stringKVMap;

    public String getObjtype() {
        return objtype;
    }

    public void setObjtype(String objtype) {
        this.objtype = objtype;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public long getReqtime() {
        return reqtime;
    }

    @Override
    public void setReqtime(long reqtime) {
        this.reqtime = reqtime;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Map<String, Map<K, V>> getStringKVMap() {
        return stringKVMap;
    }

    public void setStringKVMap(Map<String, Map<K, V>> stringKVMap) {
        this.stringKVMap = stringKVMap;
    }
}
