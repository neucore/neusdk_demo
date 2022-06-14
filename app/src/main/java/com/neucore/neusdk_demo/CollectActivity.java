package com.neucore.neusdk_demo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.neucore.NeuSDK.NeuFaceRegisterNode;
import com.neucore.neusdk_demo.app.Const;
import com.neucore.neusdk_demo.neucore.FaceProcessing;
import com.neucore.neusdk_demo.service.db.bean.User;
import com.neucore.neusdk_demo.utils.FileAccess;
import com.neucore.neusdk_demo.utils.ImageUtil;
import com.neucore.neusdk_demo.view.AutoFitTextureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * 人脸采集
 */
public class CollectActivity extends AppCompatActivity {
    private FaceProcessing mFaceProcessor = null;
    TextView tv_back;
    ImageView iv_parent;
    Button btn_take_pic;
    Button btn_re_take_pic;
    LinearLayout ll_success;
    String TAG = "NEUCORE CollectActivity";

    private String path="";
//    private byte[] data=null;
    private Bitmap b=null;//保存头像
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_re_take_pic:
                    if(dataFace!=null) {
                        getFaceData(dataFace,imageWidth,imageHeight);
                    }else{
                        Toast.makeText(CollectActivity.this,"没有采集到人脸",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btn_take_pic:
                    if(b==null || TextUtils.isEmpty(face) || TextUtils.isEmpty(face_mask)){
                        Toast.makeText(CollectActivity.this,"请先采集人脸",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    path= Const.picPath+System.currentTimeMillis()+".jpg";
                    if(!new File(Const.picPath).exists())new File(Const.picPath).mkdirs();
//                    FileUtils.getFileFromBytes(data,path);
                    if(b!=null){
                        try {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data2 = baos.toByteArray();
                            FileAccess.writeFileSdcard(Const.picPath,path,data2);
                            b.recycle();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
//                        FileAccess.writeFileSdcard(,path, data);
                    Intent intent=new Intent();
                    intent.putExtra("path",path);
                    intent.putExtra("face",face);
                    intent.putExtra("face_mask",face_mask);
                    setResult(100,intent);
                    finish();
                    break;
                case R.id.tv_back:
                    finish();
                    break;
            }
        }
    };
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    ll_success.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
    private String face, face_mask;
    Gson gson = new Gson();
    private User user;
    private byte[] dataByte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
//        mPermissionHelper = new PermissionHelper(this, this);
//        mPermissionHelper.requestPermissions();
        mFaceProcessor = FaceProcessing.getInstance(this);
        initView();
        initListener();
    }

    private void initListener() {
        btn_re_take_pic.setOnClickListener(listener);
        btn_take_pic.setOnClickListener(listener);
    }

    private void initView(){
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        btn_take_pic = (Button) findViewById(R.id.btn_take_pic);
        btn_re_take_pic = (Button) findViewById(R.id.btn_re_take_pic);
        iv_parent = (ImageView) findViewById(R.id.iv_parent);
        ll_success = (LinearLayout) findViewById(R.id.ll_success);
        textureView = findViewById(R.id.texture_preview);
        textureView.setSurfaceTextureListener(textureListener);

        Intent intent=getIntent();
        if(intent.getExtras()!=null) {
            user = (User) intent.getExtras().get("user");
            if(user!=null){
                String path=user.getHeadPhoto();
                LogUtils.i("头像："+path);
                FileInputStream fis = null;
                Bitmap bitmap = null;
                try {
                    fis = new FileInputStream(path);
                    bitmap = BitmapFactory.decodeStream(fis);
                    iv_parent.setImageBitmap(bitmap);
                }catch (Exception e){
                    Log.e(TAG,"initView",e);
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        finish();

    }

    private static final String TAG_PREVIEW = "预览";
    private static final SparseIntArray ORIENTATION = new SparseIntArray();
    static {
        ORIENTATION.append(Surface.ROTATION_0, 90);
        ORIENTATION.append(Surface.ROTATION_90, 0);
        ORIENTATION.append(Surface.ROTATION_180, 270);
        ORIENTATION.append(Surface.ROTATION_270, 180);
    }

    private Size mPreviewSize;

    private ImageReader mImageReader;

    private CameraDevice mCameraDevice;

    private CameraCaptureSession mCaptureSession;

    private CaptureRequest mPreviewRequest;

    private CaptureRequest.Builder mPreviewRequestBuilder;

    private AutoFitTextureView textureView;

    private Surface mPreviewSurface;

    // Surface状态回调
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            setupCamera(width, height);
            configureTransform(width, height);
            openCamera();
        }


        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    // 摄像头状态回调
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            //开启预览
            startPreview();
        }


        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.i(TAG, "CameraDevice Disconnected");
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e(TAG, "CameraDevice Error");
        }
    };

    private CameraCaptureSession.CaptureCallback mPreviewCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {

        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {

        }
    };
    @Override
    protected void onPause() {
        closeCamera();
        super.onPause();
    }

    private void setupCamera(int width, int height) {
        // 获取摄像头的管理者CameraManager
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            // 遍历所有摄像头
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                // 默认打开后置摄像头 - 忽略前置摄像头
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) continue;
                // 获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                mPreviewSize = getOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height);
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    textureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    textureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }

                break;
            }
        } catch (CameraAccessException e) {
            Log.e(TAG,"setupCamera",e);
        }
    }


    private void openCamera() {
        //获取摄像头的管理者CameraManager
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        //检查权限
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //打开相机，第一个参数指示打开哪个摄像头，第二个参数stateCallback为相机的状态回调接口，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
            LogUtils.i("摄像头："+manager.getCameraIdList().toString());
            manager.openCamera(manager.getCameraIdList()[0], stateCallback, null);
        } catch (CameraAccessException e) {
            Log.e(TAG,"openCamera",e);
        }
    }

    private void closeCamera() {
        if (null != mCaptureSession) {
            mCaptureSession.close();
            mCaptureSession = null;
        }
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (null != mImageReader) {
            mImageReader.close();
            mImageReader = null;
        }
    }

    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == textureView || null == mPreviewSize) {
            return;
        }
        int rotation = Surface.ROTATION_90;//getWindowManager().getDefaultDisplay().getRotation();//
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    private void startPreview() {
//        setupImageReader();
        setImageFormat();
        SurfaceTexture mSurfaceTexture = textureView.getSurfaceTexture();
        //设置TextureView的缓冲区大小
        mSurfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        //获取Surface显示预览数据
        mPreviewSurface = new Surface(mSurfaceTexture);
        try {
            getPreviewRequestBuilder();
            //创建相机捕获会话，第一个参数是捕获数据的输出Surface列表，第二个参数是CameraCaptureSession的状态回调接口，当它创建好后会回调onConfigured方法，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
            mCameraDevice.createCaptureSession(Arrays.asList(mPreviewSurface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    mCaptureSession = session;
                    repeatPreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, null);
        } catch (CameraAccessException e) {
            Log.e(TAG,"startPreview",e);
        }
    }

    private void repeatPreview() {
        mPreviewRequestBuilder.setTag(TAG_PREVIEW);
        mPreviewRequest = mPreviewRequestBuilder.build();
        //设置反复捕获数据的请求，这样预览界面就会一直有数据显示
        try {
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mPreviewCaptureCallback, null);
        } catch (CameraAccessException e) {
            Log.e(TAG,"repeatPreview",e);
        }
    }
    // 选择sizeMap中大于并且最接近width和height的size
    private Size getOptimalSize(Size[] sizeMap, int width, int height) {
        List<Size> sizeList = new ArrayList<>();
        for (Size option : sizeMap) {
            if (width > height) {
                if (option.getWidth() > width && option.getHeight() > height) {
                    sizeList.add(option);
                }
            } else {
                if (option.getWidth() > height && option.getHeight() > width) {
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


    // 创建预览请求的Builder（TEMPLATE_PREVIEW表示预览请求）
    private void getPreviewRequestBuilder() {
        try {
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            Log.e(TAG,"getPreviewRequestBuilder",e);
        }
        //设置预览的显示界面
        mPreviewRequestBuilder.addTarget(mPreviewSurface);
        mPreviewRequestBuilder.addTarget(mImageReader.getSurface());
                    MeteringRectangle[] meteringRectangles = mPreviewRequestBuilder.get(CaptureRequest.CONTROL_AF_REGIONS);
        if (meteringRectangles != null && meteringRectangles.length > 0) {
            Log.d(TAG, "PreviewRequestBuilder: AF_REGIONS=" + meteringRectangles[0].getRect().toString());
        }
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
    }
    public void setImageFormat() {
        //前三个参数分别是需要的尺寸和格式，最后一个参数代表每次最多获取几帧数据
        LogUtils.i("setImageFormat宽："+mPreviewSize.getWidth()+"setImageFormat高："+mPreviewSize.getHeight());
        mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(), ImageFormat.YUV_420_888, 2);
        //监听ImageReader的事件，当有图像流数据可用时会回调onImageAvailable方法，它的参数就是预览帧数据，可以对这帧数据进行处理
        mImageReader.setOnImageAvailableListener(frontAvailableListener, null);
    }

    /**
     * 识别成功后
     * @param data
     */
    private void success(byte[] data){
        if(data!=null) {
            int rgb[] = ImageUtil.decodeYUV420SP(data, imageWidth, imageHeight);
             b = Bitmap.createBitmap(rgb, 0, imageWidth,
                    imageWidth, imageHeight,
                    Bitmap.Config.ARGB_8888);
            LogUtils.i("识别成功Data"+data.length);
        }
        if(b!=null) {
            LogUtils.i("TAG", "图片大小：" + b.getHeight() + "," + b.getWidth());
            b=ImageUtil.adjustPhotoRotation(b,90);
            iv_parent.setImageBitmap(b);
        }
            handler.sendEmptyMessage(0);
    }
    private byte[] dataFace;
    private int imageWidth;
    private int imageHeight;
    /**
     * ImageReader监听
     */
    private ImageReader.OnImageAvailableListener frontAvailableListener=new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            if(reader!=null) {
                Image image = reader.acquireLatestImage();//最后一帧
                if (image == null) return;
                 imageWidth = image.getWidth();
                 imageHeight = image.getHeight();
                if (mFaceProcessor != null) {
                    dataFace = ImageUtil.getBytesFromImageAsType(image,2);
                } else {
                    Log.e(TAG,"mfaceProcessor="+(mFaceProcessor != null));
                }
                image.close();
            }
        }
    };

    /**
     * 录入人脸获取人脸数据
     * @param dataByte
     * @param imageWidth
     * @param imageHeight
     */
    private void getFaceData(byte[] dataByte,int imageWidth,int imageHeight){
        ////通过创建对象时传入的cameraname设置数据，参数为byte类型数据，image的宽和高
        if(dataByte != null) {
            NeuFaceRegisterNode registerNode = mFaceProcessor.getFeature(dataByte, imageWidth, imageHeight);
            if(registerNode.getFeatureValid() == false)return;

            face=gson.toJson(registerNode.getFeature_v2());
            face_mask = gson.toJson(registerNode.getMaskFeature());
            LogUtils.i("face",face);
            LogUtils.i("face_mask",face_mask);
            success(dataFace);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK && event.getRepeatCount() == 0) {

            // 退出时，请求杀死进程
            System.exit(0);
        }
        return super.onKeyDown(keyCode, event);
    }
}