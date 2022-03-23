package com.neucore.neusdk_demo.service.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

@Entity
public class Record
{
    @Id(autoincrement = true)
    private Long _id;
    private String name;
    private String cardId;
    @NotNull
    private String userId;
    private String picture;
    private long time;
    private String org;
    private int isUp;//0未上传1已上传
    private long upTime;//上传时间
    private int failUp;//上传失败次数


    @Generated(hash = 81519750)
    public Record(Long _id, String name, String cardId, @NotNull String userId,
            String picture, long time, String org, int isUp, long upTime,
            int failUp) {
        this._id = _id;
        this.name = name;
        this.cardId = cardId;
        this.userId = userId;
        this.picture = picture;
        this.time = time;
        this.org = org;
        this.isUp = isUp;
        this.upTime = upTime;
        this.failUp = failUp;
    }
    @Generated(hash = 477726293)
    public Record() {
    }


    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCardId() {
        return this.cardId;
    }
    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getPicture() {
        return this.picture;
    }
    public void setPicture(String picture) {
        this.picture = picture;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public String getOrg() {
        return org;
    }
    public void setOrg(String org) {
        this.org = org;
    }
    public int getIsUp() {
        return this.isUp;
    }
    public void setIsUp(int isUp) {
        this.isUp = isUp;
    }
    public long getUpTime() {
        return this.upTime;
    }
    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }
    public int getFailUp() {
        return this.failUp;
    }
    public void setFailUp(int failUp) {
        this.failUp = failUp;
    }

}