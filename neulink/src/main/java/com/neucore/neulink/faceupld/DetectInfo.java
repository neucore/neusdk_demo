package com.neucore.neulink.faceupld;

import com.google.gson.annotations.SerializedName;

@Deprecated
public class DetectInfo {

    @SerializedName("dir")
    private String dir;

    @SerializedName("face_image")
    private String faceImage;

    @SerializedName("head_image")
    private String headImage;

    @SerializedName("face_id")
    private String faceId;

    @SerializedName("face_background")
    private String faceBackgroud;

    @SerializedName("face_no")
    private long faceNo;

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFaceImage() {
        return faceImage;
    }

    public void setFaceImage(String faceImage) {
        this.faceImage = faceImage;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public String getFaceBackgroud() {
        return faceBackgroud;
    }

    public void setFaceBackgroud(String faceBackgroud) {
        this.faceBackgroud = faceBackgroud;
    }

    public long getFaceNo() {
        return faceNo;
    }

    public void setFaceNo(long faceNo) {
        this.faceNo = faceNo;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }
}
