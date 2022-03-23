package com.neucore.neusdk_demo.service.db.bean;

import com.google.gson.annotations.Expose;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class LicNumber {
    @Id(autoincrement = true)
    @Expose()
    private Long _id;

    private String licNum;

    private long time;//操作时间【插入/更新时间】

    @Generated(hash = 1291046809)
    public LicNumber(Long _id, String licNum, long time) {
        this._id = _id;
        this.licNum = licNum;
        this.time = time;
    }

    @Generated(hash = 1388838975)
    public LicNumber() {
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getLicNum() {
        return licNum;
    }

    public void setLicNum(String licNum) {
        this.licNum = licNum;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
