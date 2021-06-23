package com.neucore.neulink.impl.service.resume.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "dldfile",indexes = {@Index(value = "url,thid",unique = true)})
public class FileInfo {

    @Id
    private long id;
    private String url;
    private Integer thid;
    private long processed;
    private int status;

    @Generated(hash = 602957643)
    public FileInfo(long id, String url, Integer thid, long processed, int status) {
        this.id = id;
        this.url = url;
        this.thid = thid;
        this.processed = processed;
        this.status = status;
    }

    @Generated(hash = 1367591352)
    public FileInfo() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getProcessed() {
        return processed;
    }

    public void setProcessed(long processed) {
        this.processed = processed;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getThid() {
        return this.thid;
    }

    public void setThid(Integer thid) {
        this.thid = thid;
    }
}
