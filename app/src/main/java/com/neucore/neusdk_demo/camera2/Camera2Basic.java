package com.neucore.neusdk_demo.camera2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;

import androidx.core.app.ActivityCompat;

import com.neucore.neusdk_demo.view.AutoFitTextureView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 这个类完全是按照 google 标准 camera2 API 流程而来,没有任何算法相关的内容
 * 算法只用到 imagereader 获取到的数据,通过回调函数 OnImageAvailableListener() 返回到MainActivity.java
 * 请关注MainActivity 中的 OnImageAvailableListener() 流程
 */

public class Camera2Basic {

    private static final String TAG = "NEUCORE Camera2Basic";

    private Context mcontext;
    private AutoFitTextureView mTextureView;
    private OnImageAvailableListener onImageAvailableListener = new OnImageAvailableListener();
    CameraDevice mCameraDevice;

    private CaptureRequest.Builder mCaptureRequestBuilder;

    private ImageReader mImageReader;

    private Size mImageReader_size;

    private CameraCaptureSession mCameraCaptureSession;

    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;


    public Camera2Basic(Context context, AutoFitTextureView textureView, OnImageAvailableListener listener) {
        this.mcontext = context;
        this.mTextureView = textureView;
        this.onImageAvailableListener = listener;
    }

    public void start_camera() {
        startBackgroundThread();

        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    public void stop_camera(){
        if (mCameraCaptureSession != null) {
            try {
                mCameraCaptureSession.stopRepeating();
                mCameraCaptureSession.abortCaptures();
                mCameraCaptureSession.close();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        if(mCameraDevice!=null){
            mCameraDevice.close();
            mCameraDevice=null;
        }
        if (null != mImageReader) {
            mImageReader.close();
            mImageReader = null;
        }

        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage());
        }
    }

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            openCamera(width, height);//在textureview的listener函数中打开camera
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) { }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }
    };

    private void openCamera(int width, int height)
    {
        CameraManager cameraManager = (CameraManager)mcontext.getSystemService(Context.CAMERA_SERVICE);

        String CameraId = null;
        try {
            CameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        if (CameraId == null) {
            return;
        }

        CameraCharacteristics cameraCharacteristics = null;
        try {
            cameraCharacteristics = cameraManager.getCameraCharacteristics(CameraId);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if(map != null) {

            mImageReader_size = getOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height);

            Log.d(TAG, "mImageReader_size="+mImageReader_size.getWidth()+" "+mImageReader_size.getHeight());
            int orientation = mcontext.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio(mImageReader_size.getWidth(), mImageReader_size.getHeight());
            } else {
                mTextureView.setAspectRatio(mImageReader_size.getHeight(), mImageReader_size.getWidth());
            }
        } else{
            Log.e(TAG,"Could not get configuration map.");
            return;
        }

        if (ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG,"You don't have the required permissions.");
            return;
        }

        try {
            cameraManager.openCamera(CameraId, new CameraDevice.StateCallback() {//通过cameraid 打开camera，并中callback函数中显示预览
                @Override
                public void onOpened(CameraDevice camera) {
                    mCameraDevice = camera;
                    setup_Preview();
                }
                @Override
                public void onDisconnected(CameraDevice camera) { }

                @Override
                public void onError(CameraDevice camera, int error) {
                    camera.close();
                    mCameraDevice = null;
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG,e.getMessage());
        }
    }

    // 开始预览
    private void start_Preview(){
        try {
            // Auto focus should be continuous for camera preview.
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_ANTIBANDING_MODE_AUTO);

            mCameraCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG,e.getMessage());
        }
    }

    // 为预览做设置工作
    private void setup_Preview(){
//        SurfaceTexture texture = camera_view.getSurfaceTexture();
//        texture.setDefaultBufferSize(camera_view.getWidth(), camera_view.getHeight());
//        Surface surface = new Surface(texture);


        //创建imagereader对象，大小为 mPreviewSize ，格式为yuv_420_888, 一帧数据
        mImageReader = ImageReader.newInstance(mImageReader_size.getWidth(), mImageReader_size.getHeight(), ImageFormat.YUV_420_888, 2);
        mImageReader.setOnImageAvailableListener(onImageAvailableListener, mBackgroundHandler);//imagereader设置回调函数onImageAvailable，获取到数据后会触发

        try {
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //mCaptureRequestBuilder.addTarget(surface);

            mCaptureRequestBuilder.addTarget(mImageReader.getSurface());//
           // mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
            mCameraDevice.createCaptureSession(Arrays.asList(mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    // The camera is already closed
                    if (null == mCameraDevice) {
                        return;
                    }

                    // When the session is ready, we start displaying the preview.
                    mCameraCaptureSession = session;
                    start_Preview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    Log.e(TAG,"Could not configure capture session.");
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
    }

    //为camera预览和取数据开启一个线程
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CAMERA");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
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

    //imagereader 获取的image 从yuv_420_888 转到 yuv 的byte[]
    public byte[] getBytesFromImageAsType(Image image) {
        Image.Plane Y = image.getPlanes()[0];
        Image.Plane U = image.getPlanes()[1];
        Image.Plane V = image.getPlanes()[2];

        int Yb = Y.getBuffer().remaining();
        int Ub = U.getBuffer().remaining();
        int Vb = V.getBuffer().remaining();

        byte[] data = new byte[Yb + Ub + Vb];

        Y.getBuffer().get(data, 0, Yb);
        U.getBuffer().get(data, Yb, Ub);
        V.getBuffer().get(data, Yb+ Ub, Vb);
        return data;
    }
}
