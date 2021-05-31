package com.neucore.neulink.faceupld;

import com.google.gson.annotations.SerializedName;

@Deprecated
public class AIData {
    @SerializedName("dir")
    private String dir;
    @SerializedName("background")
    private String background;

    @SerializedName("face_list")
    private Face[] faces;

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public Face[] getFaces() {
        return faces;
    }

    public void setFaces(Face[] faces) {
        this.faces = faces;
    }
}
