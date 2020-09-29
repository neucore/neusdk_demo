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
import android.media.Image;
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

import com.neucore.neulink.util.LogUtils;
import com.neucore.neusdk_demo.utils.Util;
import com.neucore.neusdk_demo.view.AutoFitTextureView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Deprecated
public class Camera2Util {
    private final String TAG="NEUCORE Camera2Uil";

    static final int IR_CAMERA = 1;
    static final int RGB_CAMERA = 0;

    private HandlerThread mBackgroundThread_RGB, mBackgroundThread_IR;

    private Handler mHandler_RGB, mHandler_IR;

    private Size mPreviewSize_RGB, mPreviewSize_IR;

    private AutoFitTextureView mTextureview_RGB, mTextureview_IR;

    private Context mContext;

    private ImageReader mImageReader_RGB, mImageReader_IR;

    private Size mImagerReaderSize_IR, mImagerReaderSize_RGB;//监听数据流Size

    private Integer mSensorOrientation;

    private CameraDevice mCameraDevice;

    private CameraCaptureSession mPreviewSession;

    private CaptureRequest.Builder mPreviewBuilder;

    private Semaphore mCameraOpenCloseLock_IR = new Semaphore(1);//使用信号量 Semaphore 进行多线程任务调度

    private Semaphore mCameraOpenCloseLock_RGB = new Semaphore(1);

    private OnImageAvailableListener onImageAvailableListener_IR = new OnImageAvailableListener();
    private OnImageAvailableListener onImageAvailableListener_RGB = new OnImageAvailableListener();

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static
    {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    public void init(Context context, AutoFitTextureView textureview_RGB, AutoFitTextureView textureview_IR)
    {
        this.mContext = context;
        this.mTextureview_RGB = textureview_RGB;
        this.mTextureview_IR = textureview_IR;

        if (mTextureview_IR.isAvailable()) {
            Log.d(TAG,"open Camera IR");
            openCamera(IR_CAMERA, mTextureview_IR.getWidth(), mTextureview_IR.getHeight());
        } else {
            mTextureview_IR.setSurfaceTextureListener(mIRTextureListener);
        }

        if (mTextureview_RGB.isAvailable()) {
            Log.d(TAG,"open Camera RGB");
            openCamera(RGB_CAMERA, mTextureview_RGB.getWidth(), mTextureview_RGB.getHeight());
        } else {
            mTextureview_RGB.setSurfaceTextureListener(mRGBTextureListener);
        }

        startBackgroundThread();
    }


    public void setOnImageAvailableListener_IR(OnImageAvailableListener listener) {
        this.onImageAvailableListener_IR = listener;
    }

    public void setOnImageAvailableListener_RGB(OnImageAvailableListener listener) {
        this.onImageAvailableListener_RGB = listener;
    }


    private void startBackgroundThread() {
        mBackgroundThread_RGB = new HandlerThread("Background_RGB");
        mBackgroundThread_RGB.start();
        mHandler_RGB = new Handler(mBackgroundThread_RGB.getLooper());

        mBackgroundThread_IR = new HandlerThread("Background_IR");
        mBackgroundThread_IR.start();
        mHandler_IR = new Handler(mBackgroundThread_IR.getLooper());
    }

    private TextureView.SurfaceTextureListener mRGBTextureListener=new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera(RGB_CAMERA, mTextureview_RGB.getWidth(), mTextureview_RGB.getHeight());
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) { }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) { }
    };

    private TextureView.SurfaceTextureListener mIRTextureListener=new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera(IR_CAMERA, mTextureview_IR.getWidth(), mTextureview_IR.getHeight());
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) { }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) { }
    };

    private void openCamera(final int camera_name, int width, int height) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String CameraId = "0";

        CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!getSemaphore(camera_name).tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }

            if(manager.getCameraIdList().length < 2) {
                Log.e(TAG,"required two camera");
                return;
            }

            if (camera_name == IR_CAMERA){
                CameraId = manager.getCameraIdList()[IR_CAMERA];

                CameraCharacteristics characteristics = manager.getCameraCharacteristics(CameraId);
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                Log.i(TAG,"mSensorOrientation_IR:"+ mSensorOrientation);
                if (map == null) {
                    throw new RuntimeException("Cannot get available preview/video sizes");
                }

                mPreviewSize_IR = getOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height);
                mImagerReaderSize_IR = getOptimalSize(map.getOutputSizes(SurfaceTexture.class), 480, 640);

                int orientation = mContext.getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureview_IR.setAspectRatio(mPreviewSize_IR.getWidth(), mPreviewSize_IR.getHeight());
                } else {
                    mTextureview_IR.setAspectRatio(mPreviewSize_IR.getHeight(), mPreviewSize_IR.getWidth());
                }
                configureTransform(IR_CAMERA,width, height);
            }else {
                CameraId = manager.getCameraIdList()[RGB_CAMERA];

                CameraCharacteristics characteristics = manager.getCameraCharacteristics(CameraId);
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                Log.i(TAG,"mSensorOrientation_RGB:"+ mSensorOrientation);
                if (map == null) {
                    throw new RuntimeException("Cannot get available preview/video sizes");
                }

                mPreviewSize_RGB = getOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height);
                mImagerReaderSize_RGB = getOptimalSize(map.getOutputSizes(SurfaceTexture.class), 480, 640);

                int orientation = mContext.getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureview_RGB.setAspectRatio(mPreviewSize_RGB.getWidth(), mPreviewSize_RGB.getHeight());
                } else {
                    mTextureview_RGB.setAspectRatio(mPreviewSize_RGB.getHeight(), mPreviewSize_RGB.getWidth());
                }
                configureTransform(RGB_CAMERA,width, height);
            }
        } catch (CameraAccessException | InterruptedException e) {
                Log.e(TAG,"open camera failed");
        }

        try {
            manager.openCamera(CameraId, new CameraDevice.StateCallback() {//通过cameraid 打开camera，并中callback函数中显示预览
                @Override
                public void onOpened(CameraDevice camera) {
                    mCameraDevice = camera;
                    startPreview(camera_name);
                    getSemaphore(camera_name).release();
                }
                @Override
                public void onDisconnected(CameraDevice camera) {
                    getSemaphore(camera_name).release();
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

    private Semaphore getSemaphore(int camera_name) {
        switch (camera_name){
            case RGB_CAMERA:
                return mCameraOpenCloseLock_RGB;
            case IR_CAMERA:
                return mCameraOpenCloseLock_IR;
            default:
                return null;
        }
    }

    private ImageReader mImageReader;
    private Handler mHandler;
    private void startPreview(final int camera_name) {
        final String threadName;

        if (camera_name == RGB_CAMERA){
            size = mPreviewSize_RGB;
            mHandler = mHandler_RGB;
            threadName="RGB CAMERA";
            setImageReader(camera_name);
            mImageReader=mImageReader_RGB;
        }else if (camera_name == IR_CAMERA){
            size = mPreviewSize_IR;
            mHandler = mHandler_IR;
            threadName="IR CAMERA";
            setImageReader(camera_name);
            mImageReader=mImageReader_IR;
        }

        if (null == mCameraDevice || !getTexture(camera_name).isAvailable() || null == size) {
            return;
        }
        try {
            SurfaceTexture texture = getTexture(camera_name).getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(size.getWidth(), size.getHeight());
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

    private AutoFitTextureView getTexture(int camera_name){
        switch (camera_name){
            case RGB_CAMERA:
                return mTextureview_RGB;
            case IR_CAMERA:
                return mTextureview_IR;
            default:
                return null;
        }
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
        System.out.println("    getOptimalSize: "+ sizeMap[0].getWidth() +"  "+sizeMap[0].getHeight());
        return sizeMap[0];
    }

    private int rotation;
    private TextureView textureView;
    private Size size;
    private void configureTransform(int camera_name, int viewWidth, int viewHeight){

        if(camera_name == RGB_CAMERA) {
            textureView = mTextureview_RGB;
            size = mPreviewSize_RGB;
            rotation = Surface.ROTATION_0;//getWindowManager().getDefaultDisplay().getRotation();
        } else if (camera_name == IR_CAMERA){
            textureView = mTextureview_IR;
            size = mPreviewSize_IR;
            rotation = Surface.ROTATION_0;//getWindowManager().getDefaultDisplay().getRotation();
        }

        if (null == textureView || null == size) {
            return;
        }

        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, size.getHeight(), size.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / size.getHeight(),
                    (float) viewWidth / size.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        LogUtils.i("TAG","rotation:"+rotation);
        textureView.setTransform(matrix);
    }

    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        // 获取设备方向
        int rotation = Surface.ROTATION_90 ;//getWindowManager().getDefaultDisplay().getRotation();//
//        rotation=1;
        // 根据设备方向计算设置照片的方向
        builder.set(CaptureRequest.JPEG_ORIENTATION
                , ORIENTATIONS.get(rotation));
    }

    public void destroy(){
        if(mCameraDevice!=null){
            mCameraDevice.close();
            mCameraDevice=null;
        }
        if (null != mImageReader_IR) {
            mImageReader_IR.close();
            mImageReader_IR = null;
        }
        if (null != mImageReader_RGB) {
            mImageReader_RGB.close();
            mImageReader_RGB = null;
        }
        if (null != mImageReader_IR) {
            mImageReader_IR.close();
            mImageReader_IR = null;
        }
        if (null != mImageReader_RGB) {
            mImageReader_RGB.close();
            mImageReader_RGB = null;
        }
        if (textureView != null){
            textureView.clearAnimation();
            textureView.clearFocus();
        }
    }


    private void setImageReader(int camera_name) {

        if (camera_name == RGB_CAMERA) {
            mImageReader_RGB = ImageReader.newInstance(mImagerReaderSize_RGB.getWidth(), mImagerReaderSize_RGB.getHeight(), ImageFormat.YUV_420_888, 2);

            //监听ImageReader的事件，当有图像流数据可用时会回调onImageAvailable方法，它的参数就是预览帧数据，可以对这帧数据进行处理
            mImageReader_RGB.setOnImageAvailableListener(ImagereaderListener_RGB, mHandler_RGB);
        }else if (camera_name == IR_CAMERA){
            mImageReader_IR = ImageReader.newInstance(mImagerReaderSize_IR.getWidth(), mImagerReaderSize_IR.getHeight(), ImageFormat.YUV_420_888, 2);

            //监听ImageReader的事件，当有图像流数据可用时会回调onImageAvailable方法，它的参数就是预览帧数据，可以对这帧数据进行处理
            mImageReader_IR.setOnImageAvailableListener(ImagereaderListener_IR, mHandler_IR);
        }
    }


    //预览处理
   private OnImageAvailableListener ImagereaderListener_IR = new OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            if (onImageAvailableListener_IR != null) {
                onImageAvailableListener_IR.onImageAvailable(reader);
            }
        }
    };

    private OnImageAvailableListener ImagereaderListener_RGB = new OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            if (onImageAvailableListener_RGB != null) {
                onImageAvailableListener_RGB.onImageAvailable(reader);
            }
        }
    };

}
