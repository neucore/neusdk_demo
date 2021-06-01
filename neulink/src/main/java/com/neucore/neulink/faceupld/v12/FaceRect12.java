package com.neucore.neulink.faceupld.v12;

import com.google.gson.annotations.SerializedName;

public class FaceRect12 {
    @SerializedName("x")
    private int pointX;
    @SerializedName("y")
    private int pointY;
    @SerializedName("width")
    private int width;
    @SerializedName("height")
    private int height;

    public int getPointX() {
        return pointX;
    }

    public void setPointX(int pointX) {
        this.pointX = pointX;
    }

    public int getPointY() {
        return pointY;
    }

    public void setPointY(int pointY) {
        this.pointY = pointY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
