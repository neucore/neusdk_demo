package com.luoye.bzcamera;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.bzcommon.utils.BZLogUtil;
import com.luoye.bzcamera.listener.CameraStateListener;
import com.luoye.bzcamera.model.FocusObj;
import com.luoye.bzcamera.model.StartPreviewObj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jack_liu on 2019-08-29 14:00.
 * 说明:
 */
public class CameraHandler extends Handler implements Camera.PreviewCallback {
    private static final String TAG = "bz_CameraHandler";
    public static final int MSG_START_PREVIEW = 0x300;
    public static final int MSG_STOP_PREVIEW = 0x301;
    public static final int MSG_SET_FLASH_MODE = 0x302;
    public static final int MSG_SET_FOCUS_POINT = 0x303;
    public static final int MSG_SET_WHITE_BALANCE = 0x304;
    public static final int MSG_SET_EXPOSURE_COMPENSATION = 0x305;
    public static final int MSG_SET_LOCK_EWB = 0x306;
    public static final int MSG_SET_UN_LOCK_EWB = 0x307;
    public static final int MSG_SET_LOCK_FOCUS = 0x308;
    public static final int MSG_SET_UN_LOCK_FOCUS = 0x309;

    public static final int FOCUS_RADIUS_DP = 20;

    private Camera mCamera = null;
    private int mDisplayOrientation = 90;
    private boolean mBooleanMirror = false;
    private CameraStateListener mCameraStateListener = null;
    private Camera.Size mPreviewSize = null;
    private boolean mUseOneShot = false;
    private StartPreviewObj startPreviewObj;

    CameraHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MSG_START_PREVIEW:
                removeMessages(MSG_START_PREVIEW);
                if (!(msg.obj instanceof StartPreviewObj)) {
                    break;
                }
                startPreview((StartPreviewObj) msg.obj);
                break;
            case MSG_STOP_PREVIEW:
                removeMessages(MSG_STOP_PREVIEW);
                stopPreview();
                break;
            case MSG_SET_FLASH_MODE:
                removeMessages(MSG_SET_FLASH_MODE);
                if (msg.obj instanceof String) {
                    setFlashMode((String) msg.obj);
                }
                break;
            case MSG_SET_FOCUS_POINT:
                removeMessages(MSG_SET_FOCUS_POINT);
                if (msg.obj instanceof FocusObj) {
                    setFocusPoint((FocusObj) msg.obj);
                }
                break;
            case MSG_SET_WHITE_BALANCE:
                removeMessages(MSG_SET_WHITE_BALANCE);
                if (msg.obj instanceof String) {
                    setWhiteBalance((String) msg.obj);
                }
                break;
            case MSG_SET_EXPOSURE_COMPENSATION:
                removeMessages(MSG_SET_EXPOSURE_COMPENSATION);
                if (msg.obj instanceof Float) {
                    setExposureCompensation((Float) msg.obj);
                }
                break;
            case MSG_SET_LOCK_EWB:
                removeMessages(MSG_SET_LOCK_EWB);
                lockEWB();
                break;

            case MSG_SET_UN_LOCK_EWB:
                removeMessages(MSG_SET_UN_LOCK_EWB);
                unlockEWB();
                break;
            case MSG_SET_LOCK_FOCUS:
                removeMessages(MSG_SET_LOCK_FOCUS);
                lockFocus();
                break;
            case MSG_SET_UN_LOCK_FOCUS:
                removeMessages(MSG_SET_UN_LOCK_FOCUS);
                unlockFocus();
                break;

        }
    }

    private void lockFocus() {
        if (null == mCamera) {
            BZLogUtil.e(TAG, "lockFocus null == mCamera");
            return;
        }
        BZLogUtil.d(TAG, "lockFocus");
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();

        for (String supportedFocusMode : supportedFocusModes) {
            if (Camera.Parameters.FOCUS_MODE_FIXED.equals(supportedFocusMode)) {
                parameters.setFocusMode(supportedFocusMode);
                try {
                    mCamera.setParameters(parameters);
                } catch (Throwable e) {
                    BZLogUtil.e(TAG, e);
                }
                BZLogUtil.d(TAG, "lockFocus supportedFocusMode:" + supportedFocusMode);
                break;
            }
        }
    }

    private void unlockFocus() {
        if (null == mCamera) {
            BZLogUtil.e(TAG, "unlockFocus null == mCamera");
            return;
        }
        BZLogUtil.d(TAG, "unlockFocus");
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        for (String supportedFocusMode : supportedFocusModes) {
            if (Camera.Parameters.FOCUS_MODE_AUTO.equals(supportedFocusMode)) {
                parameters.setFocusMode(supportedFocusMode);
                mCamera.setParameters(parameters);
                BZLogUtil.d(TAG, "unlockFocus supportedFocusMode:" + supportedFocusMode);
                break;
            }
        }
    }

    private void lockEWB() {
        if (null == mCamera) {
            BZLogUtil.e(TAG, "lockEWB null == mCamera");
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        boolean autoExposureLockSupported = parameters.isAutoExposureLockSupported();
        boolean autoWhiteBalanceLockSupported = parameters.isAutoWhiteBalanceLockSupported();
        BZLogUtil.d(TAG, "lockEWB autoExposureLockSupported=" + autoExposureLockSupported + " autoWhiteBalanceLockSupported=" + autoWhiteBalanceLockSupported);

        if (autoExposureLockSupported) {
            parameters.setAutoWhiteBalanceLock(true);
        }
        if (autoWhiteBalanceLockSupported) {
            parameters.setAutoExposureLock(true);
        }
        try {
            mCamera.setParameters(parameters);
        } catch (Throwable e) {
            BZLogUtil.e(TAG, e);
        }
    }

    private void unlockEWB() {
        if (null == mCamera) {
            BZLogUtil.e(TAG, "unlockEWB null == mCamera");
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        boolean autoExposureLockSupported = parameters.isAutoExposureLockSupported();
        boolean autoWhiteBalanceLockSupported = parameters.isAutoWhiteBalanceLockSupported();
        BZLogUtil.d(TAG, "unlockEWB autoExposureLockSupported=" + autoExposureLockSupported + " autoWhiteBalanceLockSupported=" + autoWhiteBalanceLockSupported);

        if (autoExposureLockSupported) {
            parameters.setAutoWhiteBalanceLock(false);
        }
        if (autoWhiteBalanceLockSupported) {
            parameters.setAutoExposureLock(false);
        }
        try {
            mCamera.setParameters(parameters);
        } catch (Throwable e) {
            BZLogUtil.e(TAG, e);
        }
    }

    private void setExposureCompensation(float progress) {
        if (null == mCamera) {
            return;
        }
        if (progress < 0) progress = 0;
        if (progress > 1) progress = 1;
        Camera.Parameters parameters = mCamera.getParameters();
        int maxExposureCompensation = parameters.getMaxExposureCompensation();
        int minExposureCompensation = parameters.getMinExposureCompensation();
        float exposureCompensationStep = parameters.getExposureCompensationStep();
        int exposureLevel = (int) (maxExposureCompensation + (minExposureCompensation - maxExposureCompensation) / (1.0f - 0) * (progress - 0) + 0.5);
        BZLogUtil.d(TAG, "exposureLevel=" + exposureLevel + " minExposureCompensation=" + minExposureCompensation + " maxExposureCompensation=" + maxExposureCompensation + " exposureCompensationStep=" + exposureCompensationStep);
        parameters.setExposureCompensation(exposureLevel);
        try {
            mCamera.setParameters(parameters);
        } catch (Throwable e) {
            BZLogUtil.e(TAG, e);
        }
    }

    private void setFlashMode(String flashMode) {
        if (null == flashMode || null == mCamera) {
            return;
        }
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            if (isFlashModeSupport(parameters, flashMode) && !flashMode.equals(parameters.getFlashMode())) {
                parameters.setFlashMode(flashMode);
                mCamera.setParameters(parameters);
            } else {
                BZLogUtil.e(TAG, "setFlashMode fail flashMode=" + flashMode);
            }
        } catch (Exception e) {
            BZLogUtil.e(TAG, e);
        }
    }

    private boolean isFlashModeSupport(Camera.Parameters parameters, String flashMode) {
        if (parameters != null && flashMode != null) {
            List<String> supportedFlashModes = parameters.getSupportedFlashModes();
            if (supportedFlashModes != null) {
                for (int i = 0; i < supportedFlashModes.size(); ++i) {
                    if (flashMode.equals(supportedFlashModes.get(i))) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return false;
        }
    }

    private void startPreview(StartPreviewObj startPreviewObj) {
        if (null == startPreviewObj.getSurfaceTexture() || startPreviewObj.getTargetWidth() <= 0 || startPreviewObj.getTargetHeight() <= 0) {
            BZLogUtil.e(TAG, "start_preview params error");
            return;
        }
        if (null != mCamera) {
            stopPreview();
        }
        this.startPreviewObj = startPreviewObj;

        BZLogUtil.d(TAG, "startPreview");
        try {
            long startTime = System.currentTimeMillis();
            mCamera = Camera.open(startPreviewObj.getCameraId());
            mCamera.setPreviewTexture(startPreviewObj.getSurfaceTexture());

            int cameraOrientation = getCameraOrientation(startPreviewObj.getCameraId());
            mDisplayOrientation = computeSensorToViewOffset(startPreviewObj.getCameraId(), cameraOrientation, startPreviewObj.getDisplayOrientation());
            Camera.Parameters parameters = mCamera.getParameters();
            setPreviewSize(parameters, startPreviewObj.getTargetHeight(), startPreviewObj.getTargetWidth());
            applyDefaultFocus(parameters);
            List<int[]> previewFpsRange = parameters.getSupportedPreviewFpsRange();
            Collections.sort(previewFpsRange, new Comparator<int[]>() {
                @Override
                public int compare(int[] o1, int[] o2) {
                    if (o1.length < 2 || o2.length < 2) {
                        return 0;
                    }
                    int w = o1[0] - o2[0];
                    if (w == 0)
                        return o1[1] - o2[1];
                    return w;
                }
            });
            int[] fpsRange = null;
            for (int[] ints : previewFpsRange) {
                if (ints[0] > 30000 && ints[1] > 30000) {
                    break;
                }
                fpsRange = ints;
            }
            if (null != fpsRange) {
                BZLogUtil.d(TAG, "setPreviewFpsRange " + fpsRange[0] + "x" + fpsRange[1]);
                parameters.setPreviewFpsRange(fpsRange[0], fpsRange[1]);
            }
            List<Integer> previewFormats = parameters.getSupportedPreviewFormats();
            if (null != previewFormats && !previewFormats.isEmpty()) {
                for (Integer previewFormat : previewFormats) {
                    if (previewFormat == startPreviewObj.getImageFormat()) {
                        parameters.setPreviewFormat(startPreviewObj.getImageFormat());
                        break;
                    }
                }
            }
            List<String> supportedSceneModes = parameters.getSupportedSceneModes();
            if (null != supportedSceneModes && supportedSceneModes.size() > 0) {
                for (String supportedSceneMode : supportedSceneModes) {
                    BZLogUtil.d(TAG, "supportedSceneMode=" + supportedSceneMode);
                    if (Camera.Parameters.SCENE_MODE_PORTRAIT.equals(supportedSceneMode)) {
                        parameters.setSceneMode(supportedSceneMode);
                    }
                }
            }

            String valueFlatten = parameters.flatten();
            if (null != valueFlatten) {
                BZLogUtil.d(TAG, "valueFlatten=" + valueFlatten);
            }
            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(mDisplayOrientation);

            mPreviewSize = parameters.getPreviewSize();
            if (startPreviewObj.isNeedCallBackPreviewData()) {
                if (useOneShot()) {
                    mUseOneShot = true;
                    mCamera.setOneShotPreviewCallback(this);
                } else {
                    mCamera.addCallbackBuffer(new byte[mPreviewSize.width * mPreviewSize.height * 3 / 2]);
                    mCamera.addCallbackBuffer(new byte[mPreviewSize.width * mPreviewSize.height * 3 / 2]);
                    mCamera.addCallbackBuffer(new byte[mPreviewSize.width * mPreviewSize.height * 3 / 2]);
                    mCamera.setPreviewCallbackWithBuffer(this);
                    mUseOneShot = false;
                }
            }

            mCamera.startPreview();
            if (null != startPreviewObj.getCameraPreviewListener()) {
                if (mDisplayOrientation == 90 || mDisplayOrientation == 270) {
                    startPreviewObj.getCameraPreviewListener().onPreviewSuccess(mCamera, mPreviewSize.height, mPreviewSize.width);
                } else {
                    startPreviewObj.getCameraPreviewListener().onPreviewSuccess(mCamera, mPreviewSize.width, mPreviewSize.height);
                }
            }
            if (null != mCameraStateListener) {
                if (mDisplayOrientation == 90 || mDisplayOrientation == 270) {
                    mCameraStateListener.onPreviewSuccess(mCamera, mPreviewSize.height, mPreviewSize.width);
                } else {
                    mCameraStateListener.onPreviewSuccess(mCamera, mPreviewSize.width, mPreviewSize.height);
                }
            }
            BZLogUtil.d(TAG, "startPreview time consuming=" + (System.currentTimeMillis() - startTime));
        } catch (Throwable e) {
            BZLogUtil.e(TAG, e);
            if (null != mCameraStateListener) {
                mCameraStateListener.onPreviewFail("Camera.open fail");
            }
        }
    }

    private void stopPreview() {
        if (null == mCamera) {
            return;
        }
        BZLogUtil.d(TAG, "stopPreview");
        long startTime = System.currentTimeMillis();
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            BZLogUtil.e(TAG, e);
        }
        try {
            mCamera.release();
        } catch (Exception e) {
            BZLogUtil.e(TAG, e);
        }
        mCamera = null;
        if (null != mCameraStateListener) {
            mCameraStateListener.onCameraClose();
        }
        BZLogUtil.d(TAG, "stopPreview time consuming=" + (System.currentTimeMillis() - startTime));
    }


    private void setPreviewSize(Camera.Parameters parameters, int targetWidth, int targetHeight) {
        if (null == parameters || targetWidth <= 0 || targetHeight <= 0) {
            return;
        }
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        Collections.sort(supportedPreviewSizes, comparatorBigger);
        for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
            BZLogUtil.v(TAG, "supportedPreviewSize w=" + supportedPreviewSize.width + " supportedPreviewSize h=" + supportedPreviewSize.height);
        }

        Camera.Size targetSize = null;
        for (Camera.Size previewSize : supportedPreviewSizes) {
            if (previewSize.width == targetWidth && previewSize.height == targetHeight) {
                targetSize = previewSize;
                break;
            }
        }
        if (null == targetSize) {
            for (Camera.Size previewSize : supportedPreviewSizes) {
                if (targetSize == null || (previewSize.width >= targetWidth && previewSize.height >= targetHeight)) {
                    targetSize = previewSize;
                }
            }
        }
        if (null != targetSize) {
            //targetSize.width=640;
            //targetSize.height=480;
            parameters.setPreviewSize(targetSize.width, targetSize.height);
        } else {
            parameters.setPreviewSize(targetWidth, targetHeight);
        }
    }

    //Make sure to arrange from big to small
    private Comparator<Camera.Size> comparatorBigger = new Comparator<Camera.Size>() {
        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            int w = rhs.width - lhs.width;
            if (w == 0)
                return rhs.height - lhs.height;
            return w;
        }
    };

    private int getCameraOrientation(int cameraID) {
        int cameraOrientation = 0;
        try {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int i = 0;

            for (int count = Camera.getNumberOfCameras(); i < count; ++i) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == cameraID) {
                    cameraOrientation = cameraInfo.orientation;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cameraOrientation;
    }

    private static int computeSensorToViewOffset(int cameraId, int cameraOrientation, int displayOrientation) {
        return cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT ? (360 - (cameraOrientation + displayOrientation) % 360) % 360 : (cameraOrientation - displayOrientation + 360) % 360;
    }

    private void applyDefaultFocus(Camera.Parameters parameters) {
        if (null == parameters) {
            return;
        }
        List<String> list = parameters.getSupportedFocusModes();
        if (null == list || list.isEmpty()) {
            return;
        }
        boolean supportCAF = false;
        boolean supportAF = false;
        for (String mode : list) {
            if (Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE.equals(mode)) {
                supportCAF = true;
            } else if (Camera.Parameters.FOCUS_MODE_AUTO.equals(mode)) {
                supportAF = true;
            }
        }
        if (supportCAF) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else if (supportAF) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
    }

    private void setFocusPoint(FocusObj focusObj) {
        if (null == mCamera || null == focusObj || null == focusObj.getFocusPointF() || focusObj.getFocusRadius() <= 0) {
            return;
        }
        PointF pointF = focusObj.getFocusPointF();
        //match Camera coord
        Matrix matrix = new Matrix();
        //front camera logic
        if (mDisplayOrientation % 180 == 0) {
            matrix.setScale(mBooleanMirror ? -1.0f : 1.0f, 1.0f);
        } else {
            matrix.setScale(1.0f, mBooleanMirror ? -1.0f : 1.0f);
        }
        float contentWidth = focusObj.getContentWidth();
        float contentHeight = focusObj.getContentHeight();

        matrix.postRotate(mDisplayOrientation);
        matrix.postScale(contentWidth / 2000.0f, contentHeight / 2000.0f);
        matrix.postTranslate(contentWidth / 2.0f, contentHeight / 2.0f);
        Matrix finalMatrix = new Matrix();
        matrix.invert(finalMatrix);

        float focusRadius = focusObj.getFocusRadius();
        RectF focusRectF = new RectF(pointF.x - focusRadius, pointF.y - focusRadius, pointF.x + focusRadius, pointF.y + focusRadius);
        finalMatrix.mapRect(focusRectF);
        Rect rect = new Rect((int) clamp(focusRectF.left), (int) clamp(focusRectF.top), (int) clamp(focusRectF.right), (int) clamp(focusRectF.bottom));

        try {
            mCamera.cancelAutoFocus();
            setFocusArea(rect);
            mCamera.autoFocus(null);
        } catch (Exception e) {
            BZLogUtil.e(TAG, e);
        }
        BZLogUtil.d(TAG, "pointF=" + pointF.toString() + " rect=" + rect);
    }

    private float clamp(float value) {
        return (value < -1000.0f) ? -1000.0f :
                (value > 1000.0f) ? 1000.0f :
                        value;
    }

    private void setFocusArea(Rect rect) {
        if (null == mCamera) {
            return;
        }
        Camera.Parameters parameters = this.mCamera.getParameters();
        if (parameters == null) {
            return;
        }
        try {
            ArrayList<Camera.Area> areaArrayList = new ArrayList<>();
            areaArrayList.add(new Camera.Area(rect, 1));
            if (supportFocusArea()) {
                parameters.setFocusMode(Camera.Parameters.FLASH_MODE_AUTO);
                parameters.setFocusAreas(areaArrayList);
            }
            if (supportFocusMeteringArea()) {
                parameters.setMeteringAreas(areaArrayList);
            }
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            BZLogUtil.e(TAG, e);
        }
    }

    public boolean cameraIsOpen() {
        return null != mCamera;
    }

    public boolean supportFocus() {
        return supportFocusArea();
    }

    private void setWhiteBalance(String whiteBalance) {
        if (null == mCamera || null == whiteBalance) {
            return;
        }
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            List<String> supportedWhiteBalance = parameters.getSupportedWhiteBalance();
            boolean supportCurrentWhiteBalance = false;
            if (null != supportedWhiteBalance && !supportedWhiteBalance.isEmpty()) {
                for (String balance : supportedWhiteBalance) {
                    if (whiteBalance.equals(balance)) {
                        supportCurrentWhiteBalance = true;
                        break;
                    }
                }
            }
            if (supportCurrentWhiteBalance) {
                parameters.setWhiteBalance(whiteBalance);
                mCamera.setParameters(parameters);
            } else {
                BZLogUtil.e(TAG, "not supported whiteBalance:" + whiteBalance);
            }
        } catch (Exception e) {
            BZLogUtil.e(TAG, e);
        }
    }

    private boolean supportFocusArea() {
        if (null == mCamera) {
            return false;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> list = parameters.getSupportedFocusModes();
        if (null == list || list.isEmpty()) {
            return false;
        }
        boolean supportAF = false;
        for (String mode : list) {
            if (Camera.Parameters.FOCUS_MODE_AUTO.equals(mode)) {
                supportAF = true;
                break;
            }
        }
        return supportAF && parameters.getMaxNumFocusAreas() > 0;
    }

    private boolean supportFocusMeteringArea() {
        if (null == mCamera) {
            return false;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> list = parameters.getSupportedFocusModes();
        if (null == list || list.isEmpty()) {
            return false;
        }
        return parameters.getMaxNumMeteringAreas() > 0;
    }

    public static boolean useOneShot() {
        if (!TextUtils.isEmpty(Build.BRAND) && Build.BRAND.toLowerCase().contains("huawei")) {
            BZLogUtil.d(TAG, "brand is huawei");
            return true;
        } else {
            return false;
        }
    }

    public void setCameraStateListener(CameraStateListener cameraStateListener) {
        this.mCameraStateListener = cameraStateListener;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (null != mCameraStateListener) {
            int cameraId = 0;
            if (null != startPreviewObj) {
                cameraId = startPreviewObj.getCameraId();
            }
            if (null != mPreviewSize) {
                mCameraStateListener.onPreviewDataUpdate(data, mPreviewSize.width, mPreviewSize.height, mDisplayOrientation, cameraId);
            } else {
                mCameraStateListener.onPreviewDataUpdate(data, -1, -1, mDisplayOrientation, cameraId);
            }
        }
        if (null == mCamera) {
            return;
        }
        if (!mUseOneShot) {
            mCamera.addCallbackBuffer(data);
        } else {
            mCamera.setOneShotPreviewCallback(this);
        }
    }
}
