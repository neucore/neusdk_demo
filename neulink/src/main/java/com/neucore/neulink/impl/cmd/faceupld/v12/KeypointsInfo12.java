package com.neucore.neulink.impl.cmd.faceupld.v12;

import com.google.gson.annotations.SerializedName;

public class KeypointsInfo12 {

    @SerializedName("keypoints")
    private Keypoints keypoints;

    public Keypoints getKeypoints() {
        return keypoints;
    }

    public void setKeypoints(Keypoints keypoints) {
        this.keypoints = keypoints;
    }
}
