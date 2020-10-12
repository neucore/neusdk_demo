package com.neucore.neusdk_demo;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.camera2.CameraDevice;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.luoye.bzcamera.BZCamera2View;
import com.neucore.NeuSDK.NeuPoseNode;
import com.neucore.neulink.util.LogUtils;
import com.neucore.neusdk_demo.app.MyApplication;
import com.neucore.neusdk_demo.camera2.YUVConvertUtil;
import com.neucore.neusdk_demo.neucore.NeuPoseFactory;
import com.neucore.neusdk_demo.utility.Constants;
import com.neucore.neusdk_demo.utils.AppInfo;
import com.neucore.neusdk_demo.utils.NCModeSelectEvent;
import com.neucore.neusdk_demo.utils.NeuHandInfo;
import com.neucore.neusdk_demo.utils.SPUtils;
import com.neucore.neusdk_demo.utils.SharePrefConstant;
import com.neucore.neusdk_demo.utils.Util;
import com.neucore.neusdk_demo.view.CustomPoseSurfaceView;
import com.neucore.neusdk_demo.view.CustomSurfaceView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.opencv.core.Core.flip;
import static org.opencv.core.Core.transpose;


public class Camera2Activity extends AppCompatActivity {
    private byte[] yBuffer = null;
    private byte[] uBuffer = null;
    private byte[] vBuffer = null;
    private YUVConvertUtil yuvConvertUtil;
    private Bitmap bitmap;
    String TAG = "NEUCORE Camera2Activity";

    private static final int FACE_PROCESSING = 18;
    private Handler camera2Handler;
    private camera2ProcessingThread camera2ProcessingThread;
    class camera2ProcessingThread extends Thread {
        public void run() {
            Looper.prepare();

            camera2Handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    switch (msg.what) {
                        case FACE_PROCESSING:
                            Image image = (Image) msg.obj;
                            //setPaintViewUIPose(image);

                            sendToMainHandler(UPDATE_IMAGE,image);
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });

            Looper.loop();  //looper开始处理消息。
        }
    }

    private static final int UPDATE_IMAGE = 23;
    Handler mainHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_IMAGE:
                    Image image = (Image) msg.obj;
                    setPaintViewUIPose(image);
                    break;
                default:
                    break;
            }
            return true;
        }
    });
    private int widthPingMu;
    private int heightPingMu;
    private boolean onResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        onResume = true;
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        ButterKnife.bind(this);
        yuvConvertUtil = new YUVConvertUtil(this);
        BZCamera2View bz_camera2_view = findViewById(R.id.bz_camera2_view);
        bz_camera2_view.setCheckCameraCapacity(false);
        bz_camera2_view.setPreviewTargetSize(640, 480);
        bz_camera2_view.setOnStatusChangeListener(new BZCamera2View.OnStatusChangeListener() {
            @Override
            public void onPreviewSuccess(CameraDevice mCameraDevice, int width, int height) {

            }

            @Override
            public void onImageAvailable(Image image, int displayOrientation, float fps) {
                System.out.println("     开始时间  1111");

                Image.Plane[] planes = image.getPlanes();

                //sendToCamera2Handler(FACE_PROCESSING,image);

                if (onResume){
                    setPaintViewUIPose(image);
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Util.sendIntEventMessge(Constants.OPEN_HAND_KCF);
            }
        },1000);

//        if (camera2ProcessingThread != null) {
//            if (camera2ProcessingThread.isAlive()) {
//                camera2ProcessingThread.interrupt();
//            }
//        }
//        camera2ProcessingThread = new camera2ProcessingThread();
//        camera2ProcessingThread.start();
    }

    //发送handler通知
    public void sendToCamera2Handler(int what, Object obj) {
        Message me = new Message();
        me.what = what;
        me.obj = obj;
        camera2Handler.sendMessage(me);
    }

    //发送handler通知
    public void sendToMainHandler(int what, Object obj) {
        Message me = new Message();
        me.what = what;
        me.obj = obj;
        mainHandler.sendMessage(me);
    }


    private int width = 0;
    private int height = 0;
    private byte[] mPendingRGBFrameData;
    private int paintViewUIPoseNum = 0;
    //Pose检测
    private void setPaintViewUIPose(Image image) {
        LogUtils.d(TAG,"rgb  0 0 0 0 ImageToByte  start" );
        if (width == 0){
            width = image.getWidth();
        }
        if (height == 0){
            height = image.getHeight();
        }

        mPendingRGBFrameData = ImageToByte(image);
        LogUtils.d(TAG,"rgb  0 0 0 0  ImageToByte  end" );

        Mat yuvMat = new Mat(height + (height / 2), width, CvType.CV_8UC1);
        yuvMat.put(0, 0, mPendingRGBFrameData);
        Mat rgb_mat = new Mat(height, width, CvType.CV_8UC3);
        Imgproc.cvtColor(yuvMat, rgb_mat, Imgproc.COLOR_YUV2RGB_NV21, 3);
        yuvMat.release();
        LogUtils.d(TAG,"rgb  1111" );
        //这里,查看图片,要求图片人是朝上的
        //Imgcodecs.imwrite("/storage/emulated/0/neucore/111.jpg",rgb_mat);


//
//        int mRGBimageWidth = image.getWidth();
//        int mRGBimageHeight = image.getHeight();
//
//        mPendingRGBFrameData = getBytesFromImageAsType(image);//将传入的 yuv buffer 转为 cv::mat, 并通过cvtcolor 转换为BGR 或 RGB 格式
//        //LogUtils.d(TAG,"rgb  2222" );
//        Mat mat2 = new Mat((int)(mRGBimageHeight*1.5),mRGBimageWidth, CvType.CV_8UC1);
//        //LogUtils.d(TAG,"rgb  3333" );
//        mat2.put(0,0,mPendingRGBFrameData);
//        //Mat rgb_mat = new Mat(mRGBimageHeight, mRGBimageWidth,CvType.CV_8UC3);
//        //LogUtils.d(TAG,"rgb  4444" );
//        Mat rgb_mat = Imgcodecs.imdecode(new MatOfByte(mPendingRGBFrameData), CvType.CV_8UC3);
//        //LogUtils.d(TAG,"rgb  5555" );
//        Imgproc.cvtColor(mat2 , rgb_mat, Imgproc.COLOR_YUV420sp2BGR);
//
//        Imgcodecs.imwrite("/storage/emulated/0/neucore/111.jpg",rgb_mat);


        //LogUtils.d(TAG,"rgb  6666" );
        //transpose(rgbMat, rgbMat);    //耗时4毫秒(旋转图片)  此处,只有我们项目中有需要
        LogUtils.d(TAG,"rgb  7777" );
        //flip(rgb_mat, rgb_mat, 1);  //耗时4毫秒  注释
        //本人测试的camera获取到的帧数据是旋转270度的，所以需要手动再旋转90度，如果camera获取的原始数据方向是正确的，上面代码将不再需要
        LogUtils.d(TAG,"rgb  8888" );
        //获取Pose数据
        NeuPoseNode[] resultRgb = NeuPoseFactory.getInstance().create().neu_iva_pose_detect(rgb_mat,false); // withTracking 是否进行人脸追踪
        LogUtils.d(TAG,"rgb  9999  " + resultRgb.length);



        List<NeuHandInfo> rectList = new ArrayList<>();
        rectList.clear();
        for (int i = 0; i < resultRgb.length; i++) {
            float[] pose_node = resultRgb[i].getOne_pose_keypoints();
            float[] pose_node_score = resultRgb[i].getOne_pose_keypoints_score();
            LogUtils.d(TAG,"rgb  9999   pose_node_score: " + pose_node_score +"   pose_node: "+pose_node );

            NeuHandInfo neuHandInfo = new NeuHandInfo();
            neuHandInfo.setPose_node(pose_node);
            neuHandInfo.setPose_node_score(pose_node_score);

            rectList.add(neuHandInfo);
        }
        if (rectList.size() > 0){
            paintViewUIPoseNum = 0;
            Util.sendIntEventMessge(Constants.HAND_START, rectList);
            LogUtils.d(TAG,"rgb  10 10 10 10" + rectList.size() );
        }else {
            if (paintViewUIPoseNum == 0){
                paintViewUIPoseNum++;

                rectList.clear();
                NeuHandInfo neuHandInfo = new NeuHandInfo();
                neuHandInfo.setRect(new Rect(0,0,0,0));

                rectList.add(neuHandInfo);

                Util.sendIntEventMessge(Constants.HAND_START, rectList);
            }
        }

    }


    private byte[] dataI = null;
    private int ySize = 0;
    private byte[] ImageToByte(Image image ) {

        Image.Plane yPlane = image.getPlanes()[0];
        if (ySize == 0){
            ySize = yPlane.getBuffer().remaining();
        }

        Image.Plane uPlane = image.getPlanes()[1];
        Image.Plane vPlane = image.getPlanes()[2];

        int uSize = uPlane.getBuffer().remaining();
        int vSize = vPlane.getBuffer().remaining();

        if (dataI == null){
            dataI = new byte[ySize + (ySize / 2)];
        }

        yPlane.getBuffer().get(dataI, 0, ySize);

        ByteBuffer ub = uPlane.getBuffer();
        ByteBuffer vb = vPlane.getBuffer();

        int uvPixelStride = uPlane.getPixelStride();
        if (uvPixelStride == 1) {
            ub.get(dataI, ySize, uSize);
            vb.get(dataI, ySize + uSize, vSize);
            return dataI;
        }
        vb.get(dataI, ySize, vSize);
        return dataI;

    }

    //imagereader 获取的image 从yuv_420_888 转到 yuv 的byte[]
    public byte[] getBytesFromImageAsType(Image image) {
        LogUtils.d(TAG,"rgb  1111   asType  1111" );
        Image.Plane Y = image.getPlanes()[0];
        //LogUtils.d(TAG,"rgb  1111   asType  2222" );
        Image.Plane U = image.getPlanes()[1];  //耗时1毫秒
        //LogUtils.d(TAG,"rgb  1111   asType  3333" );
        Image.Plane V = image.getPlanes()[2];
        //LogUtils.d(TAG,"rgb  1111   asType  4444" );

        int Yb = Y.getBuffer().remaining();
        //LogUtils.d(TAG,"rgb  1111   asType  5555" );
        int Ub = U.getBuffer().remaining();
        //LogUtils.d(TAG,"rgb  1111   asType  6666" );
        int Vb = V.getBuffer().remaining();
        //LogUtils.d(TAG,"rgb  1111   asType  7777" );

        byte[] data = new byte[Yb + Ub + Vb];
        //LogUtils.d(TAG,"rgb  1111   asType  8888" );

        for(int a=1,b=2,c=3;a<b;c++,a++,b--){
            LogUtils.d(TAG,"rgb  1111   asType  9999  3个变量:  a="+a+"  b="+b+"   c="+c );
            if (c == 3){
                V.getBuffer().get(data, Yb+ Ub, Vb);  //耗时5毫秒
                LogUtils.d(TAG,"rgb  1111   asType  9999  2222" );
            }
            if (b == 2){
                U.getBuffer().get(data, Yb, Ub);    //耗时2毫秒
                LogUtils.d(TAG,"rgb  1111   asType  9999  1111" );
            }
            if (a == 1){
                Y.getBuffer().get(data, 0, Yb);  //耗时2毫秒
                LogUtils.d(TAG,"rgb  1111   asType  9999 0000" );
            }
        }

//        Y.getBuffer().get(data, 0, Yb);  //耗时2毫秒
//        U.getBuffer().get(data, Yb, Ub);    //耗时2毫秒
//        V.getBuffer().get(data, Yb+ Ub, Vb);  //耗时5毫秒

        LogUtils.d(TAG,"rgb  1111   asType  9999  end" );
        return data;
    }


    @BindView(R.id.fragment_content_two_ll)
    LinearLayout fragment_content_two_ll;
    private CustomPoseSurfaceView customHandSurfaceView;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onModeSwitch(NCModeSelectEvent ncModeSelectEvent) {
        switch (ncModeSelectEvent.getMode()) {
            case Constants.OPEN_HAND_KCF://开启
                fragment_content_two_ll.setVisibility(View.VISIBLE);
                if (customHandSurfaceView != null) {
                    fragment_content_two_ll.removeView(customHandSurfaceView);
                }
                customHandSurfaceView = new CustomPoseSurfaceView(Camera2Activity.this);
                fragment_content_two_ll.addView(customHandSurfaceView);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AppInfo.setStartKCF(true);
                    }
                },1000);
                break;

            case Constants.CLOSE_HAND_KCF://关闭
                customHandSurfaceView.setUpStartDrawTwo();     //画布可以画
                if (customHandSurfaceView != null) {
                    fragment_content_two_ll.removeView(customHandSurfaceView);
                }
                fragment_content_two_ll.setVisibility(View.GONE);
                AppInfo.setStartKCF(false);
                break;

            case Constants.HAND_START: //收到坐标消息
                if (AppInfo.getStartKCF()) { //UI初始化完成
                    if (fragment_content_two_ll.getVisibility() == View.GONE){
                        fragment_content_two_ll.setVisibility(View.VISIBLE);
                    }
                    List<NeuHandInfo> rectList = ncModeSelectEvent.getList();
                    getZuoBiaoXYContentHand(rectList);
                }
                break;
            default:
                break;
        }
    }

    private void getZuoBiaoXYContentHand(List<NeuHandInfo> rectList) {
        if (customHandSurfaceView != null) {
            customHandSurfaceView.drawsTwo(rectList);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        onResume = false;
        Util.clearAllCache(MyApplication.getContext());
        //Pose
        Util.sendIntEventMessge(Constants.CLOSE_HAND_KCF);
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        if (camera2ProcessingThread != null) {
            if (camera2ProcessingThread.isAlive()) {
                camera2ProcessingThread.interrupt();
            }
        }
        if (camera2Handler != null){
            camera2Handler.removeCallbacksAndMessages(null);
        }
        if (mainHandler != null){
            mainHandler.removeCallbacksAndMessages(null);
        }
        finish();
    }
}
