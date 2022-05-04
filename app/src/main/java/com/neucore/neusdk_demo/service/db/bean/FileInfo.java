package com.neucore.neusdk_demo.service.db.bean;

import com.neucore.neulink.IFile;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "dldfile",indexes = {@Index(value = "url,thid",unique = true)})
public class FileInfo implements IFile {

    @Id(autoincrement = true)
    private Long id;
    private String url;
    private Integer thid;
    private long processed;
    private int status;

    @Generated(hash = 865454488)
    public FileInfo(Long id, String url, Integer thid, long processed, int status) {
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getProcessed() {
        return processed;
    }

    public void setProcessed(Long processed) {
        this.processed = processed;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getThid() {
        return this.thid;
    }

    public void setThid(Integer thid) {
        this.thid = thid;
    }

    public void setProcessed(long processed) {
        this.processed = processed;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
