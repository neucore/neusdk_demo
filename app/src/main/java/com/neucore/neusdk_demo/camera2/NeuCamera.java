package com.neucore.neusdk_demo.camera2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.blankj.utilcode.util.LogUtils;
import com.neucore.neusdk_demo.R;
import com.neucore.neusdk_demo.app.MyApplication;
import com.neucore.neusdk_demo.utility.Constants;
import com.neucore.neusdk_demo.utils.SPUtils;
import com.neucore.neusdk_demo.utils.SharePrefConstant;
import com.neucore.neusdk_demo.view.AutoFitTextureView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class NeuCamera {

    private final String TAG="NEUCORE NeuCamera";

    private int camera_idx;

    private HandlerThread mBackgroundThread;

    private Handler mHandler;

    private Size mPreviewSize;

    private AutoFitTextureView mTextureview;

    private Context mContext;

    private ImageReader mImageReader;

    private Size mImagerReaderSize;

    private Integer mSensorOrientation;

    private CameraDevice mCameraDevice;

    private CameraCaptureSession mPreviewSession;

    private CaptureRequest.Builder mPreviewBuilder;

    private Semaphore mCameraOpenCloseLock = new Semaphore(1);//使用信号量 Semaphore 进行多线程任务调度

    private OnImageAvailableListener onImageAvailableListener = new OnImageAvailableListener();

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static
    {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    public NeuCamera(final int camera_idx,Context context, AutoFitTextureView textureview){
        this.camera_idx = camera_idx;
        this.mContext = context;
        this.mTextureview = textureview;
    }

    public void init()
    {
        TextureView.SurfaceTextureListener mIRTextureListener=new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                openCamera(camera_idx, mTextureview.getWidth(), mTextureview.getHeight());
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) { }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                Log.d(TAG,"onSurfaceTextureUpdated");
            }
        };

        if (mTextureview.isAvailable()) {
            Log.d(TAG,"open Camera IR");
            openCamera(camera_idx, mTextureview.getWidth(), mTextureview.getHeight());
        } else {
            mTextureview.setSurfaceTextureListener(mIRTextureListener);
        }
    }

    private void openCamera(final int camera_idx, int width, int height) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String CameraId = "0";

        CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }

            if(manager.getCameraIdList().length < 2) {
                Log.e(TAG,"required two camera");
                return;
            }

            CameraId = manager.getCameraIdList()[camera_idx];

            CameraCharacteristics characteristics = manager.getCameraCharacteristics(CameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            Log.i(TAG,"mSensorOrientation_IR:"+ mSensorOrientation);
            if (map == null) {
                throw new RuntimeException("Cannot get available preview/video sizes");
            }

            mPreviewSize = getOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height);

            String equip_type2 = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.EQUIPMENT_TYPE, Constants.TYPE_64010);
            if (Constants.TYPE_64010.equals(equip_type2)){
                //竖屏64010板子专属
                mImagerReaderSize = getOptimalSize(map.getOutputSizes(SurfaceTexture.class), 480, 640);
            }else if (Constants.TYPE_6421_VER.equals(equip_type2)){
                //竖屏6421板子专属
                mImagerReaderSize = getOptimalSize(map.getOutputSizes(SurfaceTexture.class), 300, 533);  //300x480    360x576
            }

            int orientation = mContext.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureview.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                mTextureview.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }
            configureTransform(camera_idx,width, height);

        } catch (CameraAccessException | InterruptedException e) {
            Log.e(TAG,"open camera failed");
        }

        try {
            manager.openCamera(CameraId, new CameraDevice.StateCallback() {//通过cameraid 打开camera，并中callback函数中显示预览
                @Override
                public void onOpened(CameraDevice camera) {
                    mCameraDevice = camera;
                    startPreview(camera_idx);
                    mCameraOpenCloseLock.release();
                }
                @Override
                public void onDisconnected(CameraDevice camera) {
                    mCameraOpenCloseLock.release();
                    camera.close();
                    mCameraDevice = null;
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    camera.close();
                    mCameraDevice = null;
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.e(TAG,e.getMessage());
        }
    }

    private void configureTransform(int camera_name, int viewWidth, int viewHeight){

        int rotation = Surface.ROTATION_0;

        if (null == mTextureview || null == mPreviewSize) {
            return;
        }

        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (camera_name == NeuCameraUtil.RGB_CAMERA){
            if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
                matrix.postScale(1, -1); // 镜像水平翻转
                bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
                matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
                float scale = Math.max(
                        (float) viewHeight / mPreviewSize.getHeight(),
                        (float) viewWidth / mPreviewSize.getWidth());
                matrix.postScale(scale, scale, centerX, centerY);
                matrix.postRotate(90 * (rotation - 2), centerX, centerY);
            } else if (Surface.ROTATION_180 == rotation || Surface.ROTATION_0 ==rotation) {
//                matrix.postScale(-1, 0); // 镜像水平翻转
//                bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
//                matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
//                float scale = Math.max(
//                        (float) viewHeight / mPreviewSize.getHeight(),
//                        (float) viewWidth / mPreviewSize.getWidth());
//                matrix.postScale(scale, scale, centerX, centerY);
                //matrix.postRotate(90 * (rotation - 2), centerX, centerY);
            }
            LogUtils.i("TAG","rotation:"+rotation);
            mTextureview.setTransform(matrix);
        }else {
            System.out.println("    rotation: "+rotation);
            if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
                bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
                matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
                float scale = Math.max(
                        (float) viewHeight / mPreviewSize.getHeight(),
                        (float) viewWidth / mPreviewSize.getWidth());
                matrix.postScale(scale, scale, centerX, centerY);
                matrix.postRotate(90 * (rotation - 2), centerX, centerY);
            } else if (Surface.ROTATION_180 == rotation || Surface.ROTATION_0 == rotation) {
                matrix.postRotate(90, centerX, centerY);

//                bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
//                matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
//                float scale = Math.max(
//                        (float) viewHeight / mPreviewSize.getHeight(),
//                        (float) viewWidth / mPreviewSize.getWidth());
//                matrix.postScale(scale, scale, centerX, centerY);
//                matrix.postRotate(270, centerX, centerY);
            }
            LogUtils.i("TAG","rotation:"+rotation);
            mTextureview.setTransform(matrix);
        }

    }

    private void startPreview(final int camera_name) {
        final String threadName;

        if (null == mCameraDevice || !mTextureview.isAvailable() || null == mPreviewSize) {
            return;
        }
        setImageReader();
        try {
            SurfaceTexture texture = mTextureview.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            Surface previewSurface = new Surface(texture);
            mPreviewBuilder.addTarget(previewSurface);
            mPreviewBuilder.addTarget(mImageReader.getSurface());
            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            mPreviewSession = session;

                            if (null == mCameraDevice) {
                                return;
                            }
                            try {
                                setUpCaptureRequestBuilder(mPreviewBuilder);
                                //HandlerThread thread = new HandlerThread(threadName);
                                //thread.start();
                                session.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler);
                            } catch (CameraAccessException e) {
                                Log.e(TAG,"onConfigured",e);
                            }
                        }
                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                            Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }, mHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG,"createCaptureSession",e);
        }
    }

    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        // 获取设备方向
        //int rotation = Surface.ROTATION_90 ;//getWindowManager().getDefaultDisplay().getRotation();//
        int rotation = Surface.ROTATION_0 ;//getWindowManager().getDefaultDisplay().getRotation();//
        //rotation=1;
        // 根据设备方向计算设置照片的方向
        builder.set(CaptureRequest.JPEG_ORIENTATION
                , ORIENTATIONS.get(rotation));
    }
    private void setImageReader() {
        if(onImageAvailableListener!=null){
            mImageReader = ImageReader.newInstance(mImagerReaderSize.getWidth(), mImagerReaderSize.getHeight(), ImageFormat.YUV_420_888, 2);
            //监听ImageReader的事件，当有图像流数据可用时会回调onImageAvailable方法，它的参数就是预览帧数据，可以对这帧数据进行处理
            mImageReader.setOnImageAvailableListener(onImageAvailableListener, mHandler);
        }
    }

    public void setOnImageAvailableListener(OnImageAvailableListener listener) {
        this.onImageAvailableListener = listener;
    }
    public void start() {
        mBackgroundThread = new HandlerThread("Background_"+camera_idx);
        mBackgroundThread.start();
        mHandler = new Handler(mBackgroundThread.getLooper());
    }

    // 选择sizeMap中大于并且最接近width和height的size
    private Size getOptimalSize(Size[] sizeMap, int width, int height) {
        List<Size> sizeList = new ArrayList<>();
        for (Size option : sizeMap) {
            if (width > height) {
                if (option.getWidth() >= width && option.getHeight() >= height) {
                    sizeList.add(option);
                }
            } else {
                if (option.getWidth() >= height && option.getHeight() >= width) {
                    sizeList.add(option);
                }
            }
        }
        if (sizeList.size() > 0) {
            return Collections.min(sizeList, new Comparator<Size>() {
                @Override
                public int compare(Size lhs, Size rhs) {
                    return Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getWidth() * rhs.getHeight());
                }
            });
        }
        return sizeMap[0];
    }
}
