package com.neucore.neulink.impl.cmd.faceupld.v12;

import com.google.gson.annotations.SerializedName;

public class AIData12 {
    @SerializedName("dir")
    private String dir;

    @SerializedName("background")
    private String background;

    @SerializedName("face_detect_flag")
    private int detectFlag;

    @SerializedName("face_atrr_flag")
    private int atrrFlag;

    @SerializedName("face_keypoints_flag")
    private int keypointsFlag;

    @SerializedName("face_rr_flag")
    private int rrFlag;

    @SerializedName("face_recog_flag")
    private int recogFlag;

    @SerializedName("face_live_flag")
    private int live_flag;

    @SerializedName("face_num")
    private int faceNum;

    @SerializedName("face_list")
    private Face12[] faces;

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

    public int getDetectFlag() {
        return detectFlag;
    }

    public void setDetectFlag(int detectFlag) {
        this.detectFlag = detectFlag;
    }

    public int getAtrrFlag() {
        return atrrFlag;
    }

    public void setAtrrFlag(int atrrFlag) {
        this.atrrFlag = atrrFlag;
    }

    public int getKeypointsFlag() {
        return keypointsFlag;
    }

    public void setKeypointsFlag(int keypointsFlag) {
        this.keypointsFlag = keypointsFlag;
    }

    public int getRrFlag() {
        return rrFlag;
    }

    public void setRrFlag(int rrFlag) {
        this.rrFlag = rrFlag;
    }

    public int getRecogFlag() {
        return recogFlag;
    }

    public void setRecogFlag(int recogFlag) {
        this.recogFlag = recogFlag;
    }

    public int getLive_flag() {
        return live_flag;
    }

    public void setLive_flag(int live_flag) {
        this.live_flag = live_flag;
    }

    public int getFaceNum() {
        return faceNum;
    }

    public void setFaceNum(int faceNum) {
        this.faceNum = faceNum;
    }

    public Face12[] getFaces() {
        return faces;
    }

    public void setFaces(Face12[] faces) {
        this.faces = faces;
    }
}
