package com.neucore.neulink.impl.cmd.faceupld.v12;

import com.google.gson.annotations.SerializedName;

public class Face12 {

    @SerializedName("head_img")
    private String headImg;

    @SerializedName("face_detect_info")
    private DetectInfo12 detectInfo;

    @SerializedName("face_recog_info")
    private RecogInfo12 recogInfo;

    @SerializedName("face_keypoints_info")
    private KeypointsInfo12 keypointsInfo;

    @SerializedName("face_attr_info")
    private AtrrInfo12 atrrInfo;

    @SerializedName("face_rr_info")
    private RrInfo12 rrInfo;

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public DetectInfo12 getDetectInfo() {
        return detectInfo;
    }

    public void setDetectInfo(DetectInfo12 detectInfo) {
        this.detectInfo = detectInfo;
    }

    public RecogInfo12 getRecogInfo() {
        return recogInfo;
    }

    public void setRecogInfo(RecogInfo12 recogInfo) {
        this.recogInfo = recogInfo;
    }

    public KeypointsInfo12 getKeypointsInfo() {
        return keypointsInfo;
    }

    public void setKeypointsInfo(KeypointsInfo12 keypointsInfo) {
        this.keypointsInfo = keypointsInfo;
    }

    public AtrrInfo12 getAtrrInfo() {
        return atrrInfo;
    }

    public void setAtrrInfo(AtrrInfo12 atrrInfo) {
        this.atrrInfo = atrrInfo;
    }

    public RrInfo12 getRrInfo() {
        return rrInfo;
    }

    public void setRrInfo(RrInfo12 rrInfo) {
        this.rrInfo = rrInfo;
    }
}
