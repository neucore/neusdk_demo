package com.neucore.neulink.impl.cmd.tmptr;

import com.google.gson.annotations.SerializedName;

public class FaceTemp {

    @SerializedName("face_url")
    private String faceUrl;
    @SerializedName("temprature")
    private String temprature;

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public String getTemprature() {
        return temprature;
    }

    public void setTemprature(String temprature) {
        this.temprature = temprature;
    }
}
