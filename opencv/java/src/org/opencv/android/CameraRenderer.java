package org.opencv.android;

import java.io.IOException;
import java.util.List;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;

@TargetApi(15)
@SuppressWarnings("deprecation")
public class CameraRenderer extends CameraGLRendererBase {

    public static final String LOGTAG = "CameraRenderer";

    private Camera mCamera;
    private boolean mPreviewStarted = false;

    CameraRenderer(CameraGLSurfaceView view) {
        super(view);
    }

    @Override
    protected synchronized void closeCamera() {
        LogUtils.i(LOGTAG, "closeCamera");
        if(mCamera != null) {
            mCamera.stopPreview();
            mPreviewStarted = false;
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected synchronized void openCamera(int id) {
        LogUtils.i(LOGTAG, "openCamera");
        closeCamera();
        if (id == CameraBridgeViewBase.CAMERA_ID_ANY) {
            LogUtils.dTag(LOGTAG, "Trying to open camera with old open()");
            try {
                mCamera = Camera.open();
            }
            catch (Exception e){
                LogUtils.eTag(LOGTAG, "Camera is not available (in use or does not exist): " + e.getLocalizedMessage());
            }

            if(mCamera == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                boolean connected = false;
                for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
                    LogUtils.dTag(LOGTAG, "Trying to open camera with new open(" + camIdx + ")");
                    try {
                        mCamera = Camera.open(camIdx);
                        connected = true;
                    } catch (RuntimeException e) {
                        LogUtils.eTag(LOGTAG, "Camera #" + camIdx + "failed to open: " + e.getLocalizedMessage());
                    }
                    if (connected) break;
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                int localCameraIndex = mCameraIndex;
                if (mCameraIndex == CameraBridgeViewBase.CAMERA_ID_BACK) {
                    LogUtils.i(LOGTAG, "Trying to open BACK camera");
                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                    for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
                        Camera.getCameraInfo( camIdx, cameraInfo );
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            localCameraIndex = camIdx;
                            break;
                        }
                    }
                } else if (mCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT) {
                    LogUtils.i(LOGTAG, "Trying to open FRONT camera");
                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                    for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
                        Camera.getCameraInfo( camIdx, cameraInfo );
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            localCameraIndex = camIdx;
                            break;
                        }
                    }
                }
                if (localCameraIndex == CameraBridgeViewBase.CAMERA_ID_BACK) {
                    LogUtils.eTag(LOGTAG, "Back camera not found!");
                } else if (localCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT) {
                    LogUtils.eTag(LOGTAG, "Front camera not found!");
                } else {
                    LogUtils.dTag(LOGTAG, "Trying to open camera with new open(" + localCameraIndex + ")");
                    try {
                        mCamera = Camera.open(localCameraIndex);
                    } catch (RuntimeException e) {
                        LogUtils.eTag(LOGTAG, "Camera #" + localCameraIndex + "failed to open: " + e.getLocalizedMessage());
                    }
                }
            }
        }
        if(mCamera == null) {
            LogUtils.eTag(LOGTAG, "Error: can't open camera");
            return;
        }
        Camera.Parameters params = mCamera.getParameters();
        List<String> FocusModes = params.getSupportedFocusModes();
        if (FocusModes != null && FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
        {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        mCamera.setParameters(params);

        try {
            mCamera.setPreviewTexture(mSTexture);
        } catch (IOException ioe) {
            LogUtils.eTag(LOGTAG, "setPreviewTexture() failed: " + ioe.getMessage());
        }
    }

    @Override
    public synchronized void setCameraPreviewSize(int width, int height) {
        LogUtils.i(LOGTAG, "setCameraPreviewSize: "+width+"x"+height);
        if(mCamera == null) {
            LogUtils.eTag(LOGTAG, "Camera isn't initialized!");
            return;
        }

        if(mMaxCameraWidth  > 0 && mMaxCameraWidth  < width)  width  = mMaxCameraWidth;
        if(mMaxCameraHeight > 0 && mMaxCameraHeight < height) height = mMaxCameraHeight;

        Camera.Parameters param = mCamera.getParameters();
        List<Size> psize = param.getSupportedPreviewSizes();
        int bestWidth = 0, bestHeight = 0;
        if (psize.size() > 0) {
            float aspect = (float)width / height;
            for (Size size : psize) {
                int w = size.width, h = size.height;
                LogUtils.dTag(LOGTAG, "checking camera preview size: "+w+"x"+h);
                if ( w <= width && h <= height &&
                     w >= bestWidth && h >= bestHeight &&
                     Math.abs(aspect - (float)w/h) < 0.2 ) {
                    bestWidth = w;
                    bestHeight = h;
                }
            }
            if(bestWidth <= 0 || bestHeight <= 0) {
                bestWidth  = psize.get(0).width;
                bestHeight = psize.get(0).height;
                LogUtils.eTag(LOGTAG, "Error: best size was not selected, using "+bestWidth+" x "+bestHeight);
            } else {
                LogUtils.i(LOGTAG, "Selected best size: "+bestWidth+" x "+bestHeight);
            }

            if(mPreviewStarted) {
                mCamera.stopPreview();
                mPreviewStarted = false;
            }
            mCameraWidth  = bestWidth;
            mCameraHeight = bestHeight;
            param.setPreviewSize(bestWidth, bestHeight);
        }
        param.set("orientation", "landscape");
        mCamera.setParameters(param);
        mCamera.startPreview();
        mPreviewStarted = true;
    }
}