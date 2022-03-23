package com.neucore.neusdk_demo.service.db.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.io.Serializable;

@Entity
public class User implements Serializable,Comparable<User>
{
    @Expose()
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    @Expose()
    private Long _id;

    @SerializedName("name")
    private String name;

    @Expose()
    private String cardId;

    @NotNull
    @SerializedName("ext_id")
    private String userId;

    @Expose()
    private String photoType;

    @Expose()
    private String headPhoto;

    @Expose()
    private long time;//操作时间【插入/更新时间】

    @SerializedName("org")
    private String org;

    @SerializedName("face")
    private String face;

    @SerializedName("face_mask")
    private String faceMask;

    @Expose()
    private String userType;

    @Expose()
    private String checkType;



    @Generated(hash = 1569872252)
    public User(Long _id, String name, String cardId, @NotNull String userId,
            String photoType, String headPhoto, long time, String org, String face,
            String faceMask, String userType, String checkType) {
        this._id = _id;
        this.name = name;
        this.cardId = cardId;
        this.userId = userId;
        this.photoType = photoType;
        this.headPhoto = headPhoto;
        this.time = time;
        this.org = org;
        this.face = face;
        this.faceMask = faceMask;
        this.userType = userType;
        this.checkType = checkType;
    }
    @Generated(hash = 586692638)
    public User() {
    }


    
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }

    /**
     * 用户姓名
     * @return
     */
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 用户卡号
     * @return
     */
    public String getCardId() {
        return this.cardId;
    }
    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    /**
     *
     * 获取用户卡号 用 getCardId（）替换
     * @return
     */
    @Deprecated
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 图片类型
     * @return
     */
    public String getPhotoType() {
        return this.photoType;
    }
    public void setPhotoType(String photoType) {
        this.photoType = photoType;
    }

    /**
     * 获取头像
     * @return
     */
    @Deprecated
    public String getHeadPhoto() {
        return this.headPhoto;
    }
    public void setHeadPhoto(String headPhoto) {
        this.headPhoto = headPhoto;
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

    public String getFace() {
        return this.face;
    }
    public void setFace(String face) {
        this.face = face;
    }

    public String getFaceMask() {
        return faceMask;
    }

    public void setFaceMask(String faceMask) {
        this.faceMask = faceMask;
    }

    public String getUserType() {
        return this.userType;
    }
    public void setUserType(String userType) {
        this.userType = userType;
    }
    public String getCheckType() {
        return this.checkType;
    }
    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    @Override
    public int compareTo(User user) {
        String cardId = user.getCardId();
        return this.getCardId().compareTo(cardId);
    }
}