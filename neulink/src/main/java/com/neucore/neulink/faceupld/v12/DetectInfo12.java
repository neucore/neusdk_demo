package com.neucore.neulink.faceupld.v12;

import com.google.gson.annotations.SerializedName;

public class DetectInfo12 {

    @SerializedName("is_live")
    private int isAlive;

    @SerializedName("track_id")
    private int trackId;

    @SerializedName("face_detect_info")
    private FaceRect12 faceRect12;

    public int getIsAlive() {
        return isAlive;
    }

    public void setIsAlive(int isAlive) {
        this.isAlive = isAlive;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public FaceRect12 getFaceRect12() {
        return faceRect12;
    }

    public void setFaceRect12(FaceRect12 faceRect12) {
        this.faceRect12 = faceRect12;
    }
}
