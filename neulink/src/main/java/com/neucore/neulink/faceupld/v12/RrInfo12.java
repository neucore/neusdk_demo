package com.neucore.neulink.faceupld.v12;

import com.google.gson.annotations.SerializedName;

public class RrInfo12 {
    @SerializedName("face_no")
    private int faceNo;

    @SerializedName("pose_score")
    private float poseScore;

    @SerializedName("clear_score")
    private float clearScore;

    public int getFaceNo() {
        return faceNo;
    }

    public void setFaceNo(int faceNo) {
        this.faceNo = faceNo;
    }

    public float getPoseScore() {
        return poseScore;
    }

    public void setPoseScore(float poseScore) {
        this.poseScore = poseScore;
    }

    public float getClearScore() {
        return clearScore;
    }

    public void setClearScore(float clearScore) {
        this.clearScore = clearScore;
    }
}
