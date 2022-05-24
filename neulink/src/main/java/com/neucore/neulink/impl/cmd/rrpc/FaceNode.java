package com.neucore.neulink.impl.cmd.rrpc;

public class FaceNode {

    private String faceSid;
    private String faceSidMask;
    private boolean featureValid;
    private String failedReason;

    public boolean isFeatureValid() {
        return featureValid;
    }

    public void setFeatureValid(boolean featureValid) {
        this.featureValid = featureValid;
    }

    public String getFaceSid() {
        return faceSid;
    }

    public void setFaceSid(String faceSid) {
        this.faceSid = faceSid;
    }

    public String getFaceSidMask() {
        return faceSidMask;
    }

    public void setFaceSidMask(String faceSidMask) {
        this.faceSidMask = faceSidMask;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }
}
