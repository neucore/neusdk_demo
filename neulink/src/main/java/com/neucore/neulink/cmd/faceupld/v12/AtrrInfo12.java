package com.neucore.neulink.cmd.faceupld.v12;

import com.google.gson.annotations.SerializedName;

public class AtrrInfo12 {

    @SerializedName("is_valid")
    private int isValid;

    @SerializedName("gender")
    private int gender;

    @SerializedName("has_hat")
    private int hasHat;
    @SerializedName("slide_face")
    private int slideFace;
    @SerializedName("lower_head")
    private int lowerHead;
    @SerializedName("sunglasses")
    private int sunglasses;
    @SerializedName("mask")
    private int mask;
    @SerializedName("age")
    private int age;
    @SerializedName("emotion")
    private int emotion;

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getHasHat() {
        return hasHat;
    }

    public void setHasHat(int hasHat) {
        this.hasHat = hasHat;
    }

    public int getSlideFace() {
        return slideFace;
    }

    public void setSlideFace(int slideFace) {
        this.slideFace = slideFace;
    }

    public int getLowerHead() {
        return lowerHead;
    }

    public void setLowerHead(int lowerHead) {
        this.lowerHead = lowerHead;
    }

    public int getSunglasses() {
        return sunglasses;
    }

    public void setSunglasses(int sunglasses) {
        this.sunglasses = sunglasses;
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getEmotion() {
        return emotion;
    }

    public void setEmotion(int emotion) {
        this.emotion = emotion;
    }
}
