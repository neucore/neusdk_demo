package com.neucore.neulink.faceupld;

import com.google.gson.annotations.SerializedName;

@Deprecated
public class Face {

    @SerializedName("face_no")
    private String faceNo;
    @SerializedName("face")
    private String faceImg;
    @SerializedName("head")
    private String head;
    @SerializedName("feature_id")
    private String featureId;
    @SerializedName("recog_info")
    private RecogInfo recogInfo;
    @SerializedName("atrr_info")
    private AtrrInfo atrrInfo;

    public String getFaceNo() {
        return faceNo;
    }

    public void setFaceNo(String faceNo) {
        this.faceNo = faceNo;
    }

    public String getFaceImg() {
        return faceImg;
    }

    public void setFaceImg(String faceImg) {
        this.faceImg = faceImg;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    public RecogInfo getRecogInfo() {
        return recogInfo;
    }

    public void setRecogInfo(RecogInfo recogInfo) {
        this.recogInfo = recogInfo;
    }

    public AtrrInfo getAtrrInfo() {
        return atrrInfo;
    }

    public void setAtrrInfo(AtrrInfo atrrInfo) {
        this.atrrInfo = atrrInfo;
    }
}
