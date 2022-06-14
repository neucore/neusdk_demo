package com.luoye.bzcamera;

import android.Manifest;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;

import com.bzcommon.utils.BZLogUtil;
import com.luoye.bzcamera.listener.CameraPreviewListener;
import com.luoye.bzcamera.listener.CameraStateListener;
import com.luoye.bzcamera.listener.OnTransformChangeListener;
import com.luoye.bzcamera.model.FocusObj;
import com.luoye.bzcamera.model.StartPreviewObj;
import com.luoye.bzcamera.utils.PermissionUtil;

/**
 * Created by jack_liu on 2019-08-29 11:03.
 * 说明:
 */
public class BZCameraView extends TextureView implements TextureView.SurfaceTextureListener {
    private static final String TAG = "bz_BZCameraView";
    private float dp_1;

    private CameraHandler mCameraHandler = null;
    private HandlerThread mCameraHandlerThread;
    private SurfaceTexture mSurfaceTexture;
    private int mCurrentCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private CameraStateListener mCameraStateListener = null;
    private String mFlashMode = null;
    private float mExposureProgress = 0.5f;
    private String mWhiteBalance = null;
    private boolean needCallBackData = false;
    private OnTransformChangeListener onTransformChangeListener;
    private int previewTargetSizeWidth = 720;
    private int previewTargetSizeHeight = 1280;
    private int imageFormat = ImageFormat.YV12;
    private boolean enableTouchFocus = true;


    public BZCameraView(Context context) {
        this(context, null);
    }

    public BZCameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BZCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSurfaceTextureListener(this);
        dp_1 = getResources().getDisplayMetrics().density;
    }

    public void onResume() {
        BZLogUtil.d(TAG, "onResume");
        startPreview();
    }

    public void startPreview() {
        if (null != mSurfaceTexture && isAvailable()) {
            startPreview(mSurfaceTexture);
        }
    }

    public void onPause() {
        BZLogUtil.d(TAG, "onPause");
        stopPreview();
    }

    public synchronized void setNeedCallBackData(boolean needCallBackData) {
        this.needCallBackData = needCallBackData;
    }

    public void setPreviewTargetSize(int previewTargetSizeWidth, int previewTargetSizeHeight) {
        if (previewTargetSizeWidth <= 0 || previewTargetSizeHeight <= 0) {
            BZLogUtil.e(TAG, "setPreviewTargetSize previewTargetSizeWidth <= 0 || previewTargetSizeHeight <= 0");
            return;
        }
        this.previewTargetSizeWidth = previewTargetSizeWidth;
        this.previewTargetSizeHeight = previewTargetSizeHeight;
    }

    private synchronized void startPreview(final SurfaceTexture surfaceTexture) {
        BZLogUtil.d(TAG, "startPreview");
        if (null == surfaceTexture) {
            BZLogUtil.w(TAG, "null == surfaceTexture");
            return;
        }
        //Granted Permission
        if (!PermissionUtil.isPermissionGranted(getContext(), Manifest.permission.CAMERA)) {
            BZLogUtil.e(TAG, "no camera permission");
            return;
        }

        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) {
            BZLogUtil.e(TAG, "width<=0||height<=0");
            return;
        }
        stopPreview();
        mCameraHandlerThread = new HandlerThread("CameraHandlerThread", Thread.MAX_PRIORITY);
        mCameraHandlerThread.start();
        mCameraHandler = new CameraHandler(mCameraHandlerThread.getLooper());
        mCameraHandler.setCameraStateListener(mCameraStateListener);
        StartPreviewObj startPreviewObj = new StartPreviewObj();
        startPreviewObj.setCameraId(getFitCameraID(mCurrentCameraID));
        startPreviewObj.setSurfaceTexture(surfaceTexture);
        startPreviewObj.setCameraPreviewListener(cameraPreviewListener);
        startPreviewObj.setTargetHeight(previewTargetSizeHeight);
        startPreviewObj.setTargetWidth(previewTargetSizeWidth);
        startPreviewObj.setDisplayOrientation(getDisplayOrientation(getContext()));
        startPreviewObj.setNeedCallBackPreviewData(needCallBackData);
        startPreviewObj.setImageFormat(imageFormat);
        Message message = new Message();
        message.what = CameraHandler.MSG_START_PREVIEW;
        message.obj = startPreviewObj;
        mCameraHandler.sendMessage(message);
    }

    public synchronized void stopPreview() {
        BZLogUtil.d(TAG, "stopPreview");
        if (null != mCameraHandler) {
            mCameraHandler.removeMessages(CameraHandler.MSG_START_PREVIEW);
            mCameraHandler.removeMessages(CameraHandler.MSG_STOP_PREVIEW);
            mCameraHandler.sendEmptyMessage(CameraHandler.MSG_STOP_PREVIEW);
            mCameraHandler = null;
        }
        if (null != mCameraHandlerThread) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    mCameraHandlerThread.quitSafely();
                } else {
                    mCameraHandlerThread.quit();
                }
                long startTime = System.currentTimeMillis();
                mCameraHandlerThread.join();
                BZLogUtil.d(TAG, "mCameraHandlerThread.join() time consuming=" + (System.currentTimeMillis() - startTime));
            } catch (Exception e) {
                BZLogUtil.e(TAG, e);
            }
            mCameraHandlerThread = null;
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurfaceTexture = surface;
        BZLogUtil.d(TAG, "onSurfaceTextureAvailable");
        startPreview(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        BZLogUtil.d(TAG, "onSurfaceTextureSizeChanged width=" + width + " height=" + height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private CameraPreviewListener cameraPreviewListener = new CameraPreviewListener() {
        @Override
        public void onPreviewSuccess(Camera camera, final int width, final int height) {
            BZLogUtil.d(TAG, "onPreviewSuccess width=" + width + " height=" + height);
            post(new Runnable() {
                @Override
                public void run() {
                    Matrix matrix = new Matrix();
                    float finalWidth = getWidth();
                    float finalHeight = finalWidth * height / width;
                    if (finalHeight < getHeight()) {
                        finalHeight = getHeight();
                        finalWidth = finalHeight * width / height;
                    }
                    matrix.postScale(finalWidth / getWidth(), finalHeight / getHeight(), getWidth() / 2, getHeight() / 2);
                    setTransform(matrix);
                    if (null != onTransformChangeListener) {
                        onTransformChangeListener.onTransformChange(matrix);
                    }
                }
            });
            if (null != mFlashMode) {
                setFlashMode(mFlashMode);
            }
            setExposureCompensation(mExposureProgress);
            if (null != mWhiteBalance) {
                setWhiteBalance(mWhiteBalance);
            }
        }
    };

    public void setCameraStateListener(CameraStateListener cameraStateListener) {
        this.mCameraStateListener = cameraStateListener;
        if (null != mCameraHandler) {
            mCameraHandler.setCameraStateListener(mCameraStateListener);
        }
    }

    public static int getDisplayOrientation(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        short degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
        }

        return degrees;
    }

    public static int getFitCameraID(int targetCameraID) {
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras <= 0) {
            return -1;
        }
        int finalCameraID = 0;
        for (int i = 0; i < numberOfCameras; i++) {
            if (i == targetCameraID) {
                finalCameraID = i;
                break;
            }
        }
        return finalCameraID;
    }

    public synchronized void lockFocus() {
        if (null == mCameraHandler) {
            return;
        }
        Message message = new Message();
        message.what = CameraHandler.MSG_SET_LOCK_FOCUS;
        mCameraHandler.sendMessage(message);
    }

    public synchronized void unLockFocus() {
        if (null == mCameraHandler) {
            return;
        }
        Message message = new Message();
        message.what = CameraHandler.MSG_SET_UN_LOCK_FOCUS;
        mCameraHandler.sendMessage(message);
    }


    public synchronized void lockEWB() {
        if (null == mCameraHandler) {
            return;
        }
        Message message = new Message();
        message.what = CameraHandler.MSG_SET_LOCK_EWB;
        mCameraHandler.sendMessage(message);
    }

    public synchronized void unlockEWB() {
        if (null == mCameraHandler) {
            return;
        }
        Message message = new Message();
        message.what = CameraHandler.MSG_SET_UN_LOCK_EWB;
        mCameraHandler.sendMessage(message);
    }

    public synchronized void setFlashMode(String flashMode) {
        if (null == mCameraHandler || null == flashMode) {
            return;
        }
        mFlashMode = flashMode;
        Message message = new Message();
        message.obj = flashMode;
        message.what = CameraHandler.MSG_SET_FLASH_MODE;
        mCameraHandler.sendMessage(message);
    }

    public synchronized void setExposureCompensation(float progress) {
        if (null == mCameraHandler) {
            return;
        }
        mExposureProgress = progress;
        Message message = new Message();
        message.obj = progress;
        message.what = CameraHandler.MSG_SET_EXPOSURE_COMPENSATION;
        mCameraHandler.sendMessage(message);
    }

    public synchronized void setWhiteBalance(String whiteBalance) {
        if (null == mCameraHandler || null == whiteBalance) {
            return;
        }
        mWhiteBalance = whiteBalance;
        Message message = new Message();
        message.obj = whiteBalance;
        message.what = CameraHandler.MSG_SET_WHITE_BALANCE;
        mCameraHandler.sendMessage(message);
    }

    public synchronized void switchCamera() {
        if (mCurrentCameraID == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mCurrentCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
        } else if (mCurrentCameraID == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCurrentCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        switchCamera2ID(mCurrentCameraID);
    }

    public synchronized void switchCamera2ID(int cameraId) {
        mCurrentCameraID = cameraId;
        startPreview();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setFocusPoint(event.getX(), event.getY());
        }
        return super.dispatchTouchEvent(event);
    }

    private void setFocusPoint(float x, float y) {
        if (null == mCameraHandler || !enableTouchFocus) {
            return;
        }
        RectF rectSrc = new RectF(0, 0, getWidth(), getHeight());
        RectF rectDst = new RectF();
        Matrix matrix = new Matrix();
        getTransform(matrix);
        matrix.mapRect(rectDst, rectSrc);
        if (!rectDst.contains(x, y)) {//Touch not camera area
            return;
        }
        x -= rectDst.left;
        y -= rectDst.top;
        BZLogUtil.d(TAG, "setFocusPoint dis x=" + x + " y=" + y);

        FocusObj focusObj = new FocusObj();
        focusObj.setFocusPointF(new PointF(x, y));
        focusObj.setFocusRadius(dp_1 * CameraHandler.FOCUS_RADIUS_DP);
        focusObj.setContentWidth(rectDst.width());
        focusObj.setContentHeight(rectDst.height());

        Message message = new Message();
        message.what = CameraHandler.MSG_SET_FOCUS_POINT;
        message.obj = focusObj;
        mCameraHandler.sendMessage(message);
    }

    public boolean isEnableTouchFocus() {
        return enableTouchFocus;
    }

    public void setEnableTouchFocus(boolean enableTouchFocus) {
        this.enableTouchFocus = enableTouchFocus;
    }

    public void setPreviewFormat(int imageFormat) {
        this.imageFormat = imageFormat;
    }

    public void setOnTransformChangeListener(OnTransformChangeListener onTransformChangeListener) {
        this.onTransformChangeListener = onTransformChangeListener;
    }

    public double getCurrentISO() {
        return 30;
    }

    public double getCurrentExposureTime() {
        return 1000;
    }
}
