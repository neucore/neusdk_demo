package com.neucore.neulink.impl.cmd.faceupld.v12;

import com.google.gson.annotations.SerializedName;

public class Keypoints {

    @SerializedName("key_x")
    private int[] keyX;

    @SerializedName("key_y")
    private int[] keyY;

    public int[] getKeyX() {
        return keyX;
    }

    public void setKeyX(int[] keyX) {
        this.keyX = keyX;
    }

    public int[] getKeyY() {
        return keyY;
    }

    public void setKeyY(int[] keyY) {
        this.keyY = keyY;
    }
}
