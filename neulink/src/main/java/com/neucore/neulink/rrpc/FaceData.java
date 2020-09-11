package com.neucore.neulink.rrpc;

import com.google.gson.annotations.SerializedName;

public class FaceData {

    @SerializedName("ext_id")
    private String extId;

    @SerializedName("org")
    private String org;

    @SerializedName("name")
    private String name;

    @SerializedName("face")
    private String face;

    @SerializedName("face_mask")
    private String faceMask;


    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getFaceMask() {
        return faceMask;
    }

    public void setFaceMask(String faceMask) {
        this.faceMask = faceMask;
    }
}
