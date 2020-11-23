package com.luoye.bzcamera.model;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.luoye.bzcamera.listener.CameraPreviewListener;

/**
 * Created by jack_liu on 2019-08-29 13:54.
 * 说明:
 */
public class StartPreviewObj {
    private SurfaceTexture surfaceTexture = null;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int targetWidth = 720;
    private int targetHeight = 1280;
    private CameraPreviewListener cameraPreviewListener = null;
    private int displayOrientation = 0;
    private boolean needCallBackPreviewData = false;
    private int imageFormat = ImageFormat.YV12;

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    public void setSurfaceTexture(SurfaceTexture mSurfaceTexture) {
        this.surfaceTexture = mSurfaceTexture;
    }

    public int getTargetWidth() {
        return targetWidth;
    }

    public void setTargetWidth(int targetWidth) {
        this.targetWidth = targetWidth;
    }

    public int getTargetHeight() {
        return targetHeight;
    }

    public void setTargetHeight(int targetHeight) {
        this.targetHeight = targetHeight;
    }

    public CameraPreviewListener getCameraPreviewListener() {
        return cameraPreviewListener;
    }

    public void setCameraPreviewListener(CameraPreviewListener cameraPreviewListener) {
        this.cameraPreviewListener = cameraPreviewListener;
    }

    public int getCameraId() {
        return cameraId;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    public int getDisplayOrientation() {
        return displayOrientation;
    }

    public void setDisplayOrientation(int displayOrientation) {
        this.displayOrientation = displayOrientation;
    }

    public boolean isNeedCallBackPreviewData() {
        return needCallBackPreviewData;
    }

    public void setNeedCallBackPreviewData(boolean needCallBackPreviewData) {
        this.needCallBackPreviewData = needCallBackPreviewData;
    }

    public int getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(int imageFormat) {
        this.imageFormat = imageFormat;
    }
}
