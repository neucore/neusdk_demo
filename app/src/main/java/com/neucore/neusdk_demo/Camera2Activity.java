package com.neucore.neusdk_demo;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.camera2.CameraDevice;
import android.media.ExifInterface;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.luoye.bzcamera.BZCamera2View;
import com.neucore.NeuSDK.NeuFaceQuality;
import com.neucore.NeuSDK.NeuFaceRecgNode;
import com.neucore.NeuSDK.NeuFaceRegisterNode;
import com.neucore.NeuSDK.NeuHandClass;
import com.neucore.NeuSDK.NeuHandNode;
import com.neucore.NeuSDK.NeuHandSwipe;
import com.neucore.NeuSDK.NeuPoseNode;
import com.neucore.neulink.util.LogUtils;
import com.neucore.neusdk_demo.app.MyApplication;
import com.neucore.neusdk_demo.camera2.YUVConvertUtil;
import com.neucore.neusdk_demo.neucore.NeuFaceFactory;
import com.neucore.neusdk_demo.neucore.NeuHandFactory;
import com.neucore.neusdk_demo.neucore.NeuPoseFactory;
import com.neucore.neusdk_demo.utility.Constants;
import com.neucore.neusdk_demo.utils.AppInfo;
import com.neucore.neusdk_demo.utils.NCModeSelectEvent;
import com.neucore.neusdk_demo.utils.NeuHandInfo;
import com.neucore.neusdk_demo.utils.SPUtils;
import com.neucore.neusdk_demo.utils.SharePrefConstant;
import com.neucore.neusdk_demo.utils.Size;
import com.neucore.neusdk_demo.utils.Util;
import com.neucore.neusdk_demo.view.CustomPoseSurfaceView;
import com.neucore.neusdk_demo.view.CustomSurfaceView;
import com.neucore.neusdk_demo.view.SeekAttentionView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
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
    private static final int IMAGE_PLANE = 19;
    private Handler camera2Handler;
    private camera2ProcessingThread camera2ProcessingThread;
    private ImageView image_view;
    private TextView tv_yanzheng;
    private ImageView iv_parent;

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
                        case IMAGE_PLANE:
                            Image.Plane[] planes = (Image.Plane[]) msg.obj;
                            System.out.println("     开始时间  2222 + width: "+width);

                            //sendToMainHandler(IMAGE_PLANE_UPDATE,planes);

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
    private static final int IMAGE_PLANE_UPDATE = 24;
    Handler mainHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 4:
                    String photo = (String)msg.obj;
                    String url = registerPath + photo + ".jpg";
                    if (iv_parent != null && tv_yanzheng != null){
                        Glide.with(Camera2Activity.this).load(url).asBitmap().centerCrop().into(iv_parent);
                        tv_yanzheng.setText("检测到人脸");
                        ObjectAnimator animator = SeekAttentionView.tada(iv_parent);
                        animator.setRepeatCount(1);
                        animator.start();
                    }

                    break;
                case UPDATE_IMAGE:
                    Image image = (Image) msg.obj;
                    setPaintViewUIPose(image);
                    break;
                case IMAGE_PLANE_UPDATE:
                    System.out.println("     开始时间  3333 " );
                    Image.Plane[] planes = (Image.Plane[]) msg.obj;

                    System.out.println("     开始时间  3333 " );
//                    if (null == yBuffer) {
//                        yBuffer = new byte[width * height];
//                    }
//                    ByteBuffer byU = planes[1].getBuffer();
//                    ByteBuffer byV = planes[2].getBuffer();
//                    if (null == uBuffer) {
//                        uBuffer = new byte[byU.capacity()];
//                    }
//                    if (null == vBuffer) {
//                        vBuffer = new byte[byV.capacity()];
//                    }
//                    System.out.println("     开始时间  3333 " );
//                    planes[0].getBuffer().get(yBuffer);
//                    byU.get(vBuffer);
//                    byV.get(uBuffer);
//                    System.out.println("     开始时间  4444");


//                    final Bitmap bitmap = yuvConvertUtil.yuv420_2_Bitmap(yBuffer, uBuffer, vBuffer, planes[1].getPixelStride(), width , height, 0, true);
//                    image_view.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            image_view.setImageBitmap(bitmap);
//                            System.out.println("     开始时间  5555");
//                        }
//                    });
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
    private boolean onThread = false;
    private int width = 0;
    private int height = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        onResume = true;
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        ButterKnife.bind(this);
        String type = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.type,"");
        if ("0".equals(type) || "1".equals(type)) { //单目 人脸识别 才注册
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    face_register();
                }
            },4000);
            LinearLayout ll_activity_detect_bottom = (LinearLayout) findViewById(R.id.ll_activity_detect_bottom);
            ll_activity_detect_bottom.setVisibility(View.VISIBLE);
            tv_yanzheng = findViewById(R.id.tv_yanzheng);
            iv_parent = findViewById(R.id.iv_parent);
        }
        yuvConvertUtil = new YUVConvertUtil(this);
        image_view = findViewById(R.id.image_view);
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

                if (onResume){
                    if (AppInfo.getStartKCF()) { //View图层 UI初始化完成
                        Image.Plane[] planes = image.getPlanes();

//                        if (onThread){
//                            if (width != 0){
//                                width = image.getWidth();
//                            }
//                            if (height != 0){
//                                height = image.getHeight();
//                            }
//                            System.out.println("     开始时间  1111   3333");
//                            //sendToCamera2Handler(FACE_PROCESSING,image);
//                            sendToCamera2HandlerDelayed(IMAGE_PLANE,planes); //延迟50ms
//                            System.out.println("     开始时间  1111   4444");
//                        }

                        String type = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.type,"");
                        if ("0".equals(type) || "1".equals(type)){
                            setPaintViewUI(image);
                        }else if ("3".equals(type)){ //手势
                            setPaintViewUIHand(image);
                        }else if ("4".equals(type)){ //Pose检测
                            setPaintViewUIPose(image);
                        }else if ("7".equals(type)){ //人脸关键点
                            setPaintViewUIFacePoint(image);
                        }

                    }

                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String type = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.type,"");
                if ("0".equals(type) || "1".equals(type)){ //单目活体, 单目非活体
                    Util.sendIntEventMessge(Constants.OPEN_KCF);
                }else if ("3".equals(type) || "4".equals(type) || "7".equals(type)){ //手势 , Pose检测 , 人脸关键点
                    Util.sendIntEventMessge(Constants.OPEN_HAND_KCF);
                }
            }
        },1000);

//        if (camera2ProcessingThread != null) {
//            if (camera2ProcessingThread.isAlive()) {
//                camera2ProcessingThread.interrupt();
//            }
//        }
//        camera2ProcessingThread = new camera2ProcessingThread();
//        camera2ProcessingThread.start();
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                onThread = true;
//            }
//        },2000);

    }

    //发送handler通知
    public void sendToCamera2Handler(int what, Object obj) {
        Message me = new Message();
        me.what = what;
        me.obj = obj;
        camera2Handler.sendMessage(me);
    }
    //发送handler通知
    public void sendToCamera2HandlerDelayed(int what, Object obj) {
        Message me = new Message();
        me.what = what;
        me.obj = obj;
        camera2Handler.sendMessageDelayed(me,70);
    }

    //发送handler通知
    public void sendToMainHandler(int what, Object obj) {
        Message me = new Message();
        me.what = what;
        me.obj = obj;
        mainHandler.sendMessage(me);
    }

    private long faceTime = 0;
    //人脸框
    private void setPaintViewUI(Image image) {
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
        if (rgbMat == null){
            rgbMat = new Mat(height, width, CvType.CV_8UC3);
        }
        Imgproc.cvtColor(yuvMat, rgbMat, Imgproc.COLOR_YUV2RGB_NV21, 3);
        yuvMat.release();
        LogUtils.d(TAG,"rgb  1111" ); //下面这句最耗时  15毫秒
        //Imgcodecs.imwrite("/storage/emulated/0/neucore/111.jpg",rgbMat);

        //LogUtils.d(TAG,"rgb  6666" );
        //transpose(rgbMat, rgbMat);    //耗时4毫秒  此处,只有我们项目中有需要
        LogUtils.d(TAG,"rgb  7777" );
        //flip(rgb_mat, rgb_mat, 1);  //耗时4毫秒  注释
        //本人测试的camera获取到的帧数据是旋转270度的，所以需要手动再旋转90度，如果camera获取的原始数据方向是正确的，上面代码将不再需要
        LogUtils.d(TAG,"rgb  8888" );
        //获取人脸数据
        NeuFaceRecgNode[] resultRgb = NeuFaceFactory.getInstance().create().neu_iva_face_detect_recognize(rgbMat,true); //withTracking 是否进行人脸追踪
        LogUtils.d(TAG,"rgb  9999" );



        List<Rect> rectList = new ArrayList<>();
        rectList.clear();
        for (int i = 0; i < resultRgb.length; i++) {
            //调用检测算法,得到人脸框,5点信息,特征值等信息
            //在 mat 中画人脸框
            Rect rect_event = new Rect(resultRgb[i].getLeft(),resultRgb[i].getTop(),
                    (resultRgb[i].getLeft() + resultRgb[i].getWidth()),resultRgb[i].getTop() + resultRgb[i].getHeight());

            int x1 = rect_event.left;
            int y1 = rect_event.top;
            int x2 = rect_event.right;
            int y2 = rect_event.bottom;

            int aaaX1 = (int) Util.widthPointTrans6421(x1);
            int aaaX2 = (int) Util.widthPointTrans6421(x2);
            int aaaY1 = (int) Util.heightPointTrans6421(y1);
            int aaaY2 = (int) Util.heightPointTrans6421(y2);

            Rect rect = new Rect(aaaX1, aaaY1, aaaX2, aaaY2);
            rectList.add(rect);
        }
        if (rectList.size() > 0){
            Util.sendIntEventMessge(Constants.FACE_START, rectList);
            //LogUtils.d(TAG,"rgb  10 10 10 10" );
        }else {
            rectList.clear();
            rectList.add(new Rect(0,0,0,0));
            Util.sendIntEventMessge(Constants.FACE_START, rectList);
        }

        long time = System.currentTimeMillis();
        if (Math.abs(faceTime - time) > 1000){
            for (int i = 0; i < resultRgb.length; i++) {
                //如果特征值有效,进行人脸识别
                if (resultRgb[i].getFeatureValid() == true) {

                    float maxSum = 0;
                    int maxID = 0;

                    if (resultRgb[i].getIsmask() == true) {
                        // 从注册图中找到相似度最大的ID 和 最大相似度
                        for (int org_size = 0; org_size < feature_mask.size(); org_size++) {
                            float sum = NeuFaceFactory.getInstance().create().neu_iva_face_similarity(feature_mask.get(org_size), resultRgb[i].getFeature());
                            if (sum > maxSum) {
                                maxSum = sum;
                                maxID = org_size;
                            }
                        }
                    } else {
                        // 从注册图中找到相似度最大的ID 和 最大相似度
                        for (int org_size = 0; org_size < feature_org.size(); org_size++) {
                            float sum = NeuFaceFactory.getInstance().create().neu_iva_face_similarity(feature_org.get(org_size), resultRgb[i].getFeature());
                            if (sum > maxSum) {
                                maxSum = sum;
                                maxID = org_size;
                            }
                        }
                    }

                    if (name_org.size() != 0) {
                        Log.d(TAG, "max sum name=" + name_org.get(maxID) + "  maxSum=" + maxSum);
                    }

                    if (maxSum > 0.8) {
                        //如果大于阈值,识别结果绘制到 mat 中
                        //String name = name_org.get(maxID) + " maxSum=" + String.format("%.2f", maxSum);
                        String name = name_org.get(maxID);
                        faceTime = System.currentTimeMillis();
                        System.out.println("eee     识别name: " + name);

                        Message msg = new Message();
                        msg.what = 4;
                        msg.obj = name;
                        mainHandler.sendMessage(msg);
                    }
                }else {
                    //清除记录
                    //dHandler.sendEmptyMessage(1);
                }
            }
        }

    }

    private int paintViewUIHandNum = 0;
    private Mat rgbMat;
    //手势识别
    private void setPaintViewUIHand(Image image) {
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
        if (rgbMat == null){
            rgbMat = new Mat(height, width, CvType.CV_8UC3);
        }
        Imgproc.cvtColor(yuvMat, rgbMat, Imgproc.COLOR_YUV2RGB_NV21, 3);
        yuvMat.release();
        LogUtils.d(TAG,"rgb  1111" ); //下面这句最耗时
        //Imgcodecs.imwrite("/storage/emulated/0/neucore/111.jpg",rgbMat);

        //LogUtils.d(TAG,"rgb  6666" );
        //transpose(rgbMat, rgbMat);    //耗时4毫秒  此处,只有我们项目中有需要
        LogUtils.d(TAG,"rgb  7777" );
        //flip(rgb_mat, rgb_mat, 1);  //耗时4毫秒  注释
        //本人测试的camera获取到的帧数据是旋转270度的，所以需要手动再旋转90度，如果camera获取的原始数据方向是正确的，上面代码将不再需要
        LogUtils.d(TAG,"rgb  8888" );
        //获取手势数据
        NeuHandNode[] resultRgb = NeuHandFactory.getInstance().create().neu_iva_hand_detect(rgbMat);
        LogUtils.d(TAG,"rgb  9999" );

        List<NeuHandInfo> rectList = new ArrayList<>();
        rectList.clear();
        for (int i = 0; i < resultRgb.length; i++) {
            NeuHandInfo neuHandInfo = new NeuHandInfo();
            //调用检测算法,得到手势框,5点信息,特征值等信息
            //在 mat 中画手势框
            Rect rect_event = new Rect(resultRgb[i].getLeft(),resultRgb[i].getTop(),
                    (resultRgb[i].getLeft() + resultRgb[i].getWidth()),resultRgb[i].getTop() + resultRgb[i].getHeight());

            int x1 = rect_event.left;
            int y1 = rect_event.top;
            int x2 = rect_event.right;
            int y2 = rect_event.bottom;

            int aaaX1 = (int) Util.widthPointTrans6421(x1);
            int aaaX2 = (int) Util.widthPointTrans6421(x2);
            int aaaY1 = (int) Util.heightPointTrans6421(y1);
            int aaaY2 = (int) Util.heightPointTrans6421(y2);

            Rect rect = new Rect(aaaX1, aaaY1, aaaX2, aaaY2);
            neuHandInfo.setRect(rect);

            // Mat 中标注滑动手势
            String swipe = "";
            switch (resultRgb[i].getHandSwipe()) {
                case NeuHandSwipe.NEU_IVA_STATE_UNKNOW:
                    swipe = "unknow";
                    break;
                case NeuHandSwipe.NEU_IVA_STATE_LEFT:
                    swipe = "left";
                    break;
                case NeuHandSwipe.NEU_IVA_STATE_RIGHT:
                    swipe = "right";
                    break;
                case NeuHandSwipe.NEU_IVA_STATE_UP:
                    swipe = "up";
                    break;
                case NeuHandSwipe.NEU_IVA_STATE_DOWN:
                    swipe = "down";
                    break;
                default:
                    swipe = "default";
            }
            System.out.println("1    swipe: "+swipe);
            neuHandInfo.setSwipe(swipe);

            //调用分类网络,手势分类
            int status = NeuHandFactory.getInstance().create().neu_iva_hand_class(rgbMat, resultRgb[i]);
            if (status != 0) {
                Log.e(TAG,"error at mNeuHand.neu_iva_hand_class()");
                rectList.add(neuHandInfo);
                continue;
            }

            String text = "";
            switch (resultRgb[i].getHandClass()) {
                case NeuHandClass.NEU_IVA_HAND_FIRST:
                    text = "first";
                    break;
                case NeuHandClass.NEU_IVA_HAND_ONE:
                    text = "one";
                    break;
                case NeuHandClass.NEU_IVA_HAND_TWO:
                    text = "two";
                    break;
                case NeuHandClass.NEU_IVA_HAND_THREE:
                    text = "three";
                    break;
                case NeuHandClass.NEU_IVA_HAND_FOUR:
                    text = "four";
                    break;
                case NeuHandClass.NEU_IVA_HAND_FIVE:
                    text = "five";
                    break;
                case NeuHandClass.NEU_IVA_HAND_SIX:
                    text = "six";
                    break;
                case NeuHandClass.NEU_IVA_HAND_SEVEN:
                    text = "seven";
                    break;
                case NeuHandClass.NEU_IVA_HAND_EIGHT:
                    text = "eight";
                    break;
                case NeuHandClass.NEU_IVA_HAND_NINE:
                    text = "nine";
                    break;
                case NeuHandClass.NEU_IVA_HAND_TEN:
                    text = "ten";
                    break;
                case NeuHandClass.NEU_IVA_HAND_HANDHEART:
                    text = "handheart";
                    break;
                case NeuHandClass.NEU_IVA_HAND_OK:
                    text = "ok";
                    break;
                case NeuHandClass.NEU_IVA_HAND_ROCK:
                    text = "rock";
                    break;
                case NeuHandClass.NEU_IVA_HAND_NO:
                    text = "no";
                    break;
                case NeuHandClass.NEU_IVA_HAND_STOP:
                    text = "stop";
                    break;
                case NeuHandClass.NEU_IVA_HAND_OTHER:
                    text = "other";
                    break;
                default:
                    text = "default";
            }
            System.out.println("1    text: "+text);
            neuHandInfo.setText(text);
            rectList.add(neuHandInfo);
        }
        if (rectList.size() > 0){
            paintViewUIHandNum = 0;
            Util.sendIntEventMessge(Constants.HAND_START, rectList);
            //LogUtils.d(TAG,"rgb  10 10 10 10" );
        }else {
            if (paintViewUIHandNum == 0){
                paintViewUIHandNum++;

                rectList.clear();
                NeuHandInfo neuHandInfo = new NeuHandInfo();
                neuHandInfo.setRect(new Rect(0,0,0,0));
                neuHandInfo.setSwipe("");
                neuHandInfo.setText("");

                rectList.add(neuHandInfo);

                Util.sendIntEventMessge(Constants.HAND_START, rectList);
            }
        }

    }

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
        if (rgbMat == null){
            rgbMat = new Mat(height, width, CvType.CV_8UC3);
        }
        Imgproc.cvtColor(yuvMat, rgbMat, Imgproc.COLOR_YUV2RGB_NV21, 3);
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
        NeuPoseNode[] resultRgb = NeuPoseFactory.getInstance().create().neu_iva_pose_detect(rgbMat,false); // withTracking 是否进行人脸追踪
        LogUtils.d(TAG,"rgb  9999  ");



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


    private int paintViewFacePointNum = 0;
    //人脸关键点
    private void setPaintViewUIFacePoint(Image image) {
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
        if (rgbMat == null){
            rgbMat = new Mat(height, width, CvType.CV_8UC3);
        }
        Imgproc.cvtColor(yuvMat, rgbMat, Imgproc.COLOR_YUV2RGB_NV21, 3);
        yuvMat.release();
        LogUtils.d(TAG,"rgb  1111" ); //下面这句最耗时  15毫秒
        //Imgcodecs.imwrite("/storage/emulated/0/neucore/111.jpg",rgbMat);

        //LogUtils.d(TAG,"rgb  6666" );
        //transpose(rgbMat, rgbMat);    //耗时4毫秒  此处,只有我们项目中有需要
        LogUtils.d(TAG,"rgb  7777" );
        //flip(rgb_mat, rgb_mat, 1);  //耗时4毫秒  注释
        //本人测试的camera获取到的帧数据是旋转270度的，所以需要手动再旋转90度，如果camera获取的原始数据方向是正确的，上面代码将不再需要
        LogUtils.d(TAG,"rgb  8888" );
        //获取人脸关键点数据
        NeuFaceRecgNode[] resultRgb = NeuFaceFactory.getInstance().create().neu_iva_face_detect_recognize(rgbMat,false); // withTracking 是否进行人脸追踪
        LogUtils.d(TAG,"rgb  9999" );



        List<NeuHandInfo> rectList = new ArrayList<>();
        rectList.clear();
        for (int i = 0; i < resultRgb.length; i++) {
            float[] mLandMarkPoints = resultRgb[i].getLandMarkPoints();
            float[] mKeyPoints = resultRgb[i].getKeyPoints();

            NeuHandInfo neuHandInfo = new NeuHandInfo();
            neuHandInfo.setmLandMarkPoints(mLandMarkPoints);
            neuHandInfo.setmKeyPoints(mKeyPoints);

            rectList.add(neuHandInfo);
        }
        if (rectList.size() > 0){
            paintViewFacePointNum = 0;
            Util.sendIntEventMessge(Constants.HAND_START, rectList);
            //LogUtils.d(TAG,"rgb  10 10 10 10" );
        }else {
            if (paintViewFacePointNum == 0){
                paintViewFacePointNum++;

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
    private CustomSurfaceView customSurfaceView;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onModeSwitch(NCModeSelectEvent ncModeSelectEvent) {
        switch (ncModeSelectEvent.getMode()) {
            case Constants.OPEN_KCF://开启
                fragment_content_two_ll.setVisibility(View.VISIBLE);
                if (customSurfaceView != null) {
                    fragment_content_two_ll.removeView(customSurfaceView);
                }
                customSurfaceView = new CustomSurfaceView(Camera2Activity.this);
                fragment_content_two_ll.addView(customSurfaceView);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AppInfo.setStartKCF(true);
                        if (customSurfaceView != null) {
                            List<Rect> rectList = new ArrayList<>();
                            rectList.add(new Rect(0,0,0,0));
                            customSurfaceView.drawsTwo(rectList);
                        }
                    }
                },1500);
                break;

            case Constants.CLOSE_KCF://关闭
                customSurfaceView.setUpStartDrawTwo();     //画布可以画
                if (customSurfaceView != null) {
                    fragment_content_two_ll.removeView(customHandSurfaceView);
                }
                fragment_content_two_ll.setVisibility(View.GONE);
                AppInfo.setStartKCF(false);
                break;

            case Constants.FACE_START: //收到坐标消息
                if (AppInfo.getStartKCF()) { //UI初始化完成
                    if (fragment_content_two_ll.getVisibility() == View.GONE){
                        fragment_content_two_ll.setVisibility(View.VISIBLE);
                    }
                    List<Rect> rectList = ncModeSelectEvent.getList();
                    //Rect rect = ncModeSelectEvent.getRect();
                    getZuoBiaoXYContent(rectList);
                }
                break;
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

    private void getZuoBiaoXYContent(List<Rect> rectList) {
        if (customSurfaceView != null) {
            customSurfaceView.drawsTwo(rectList);
        }
    }

    private void getZuoBiaoXYContentHand(List<NeuHandInfo> rectList) {
        if (customHandSurfaceView != null) {
            customHandSurfaceView.drawsTwo(rectList);
        }
    }

    private ArrayList<byte[]> feature_org = new ArrayList<byte[]>();
    private ArrayList<byte[]> feature_mask = new ArrayList<byte[]>();
    private ArrayList<String> name_org = new ArrayList<String>();
    public String registerPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/twocamera/photo/";

    //创建bitmapFactory对象，并设置config值
    BitmapFactory.Options options = new BitmapFactory.Options();
    public void face_register() {
        File face_file = new File(registerPath);

        //下面注册后缀为 jpg 的文件
        File jpg_faces_name[] = face_file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("jpg");
            }
        });

        if(jpg_faces_name!=null){
            /**
             * 不能为空！
             */
            //通过循环的方式，将 registerPath 目录下所有的后缀为 .jpg 图像进行注册并将文件名对应为人名
            for (File face : jpg_faces_name) {
                Bitmap bitmap = BitmapFactory.decodeFile(registerPath + face.getName(), options);
                //通过图片中的旋转信息旋转图片,目的是为了让图像正确方向
                bitmap = rotateBitmap(bitmap, getBitmapDegree(registerPath + face.getName()));

                NeuFaceRegisterNode register_face = NeuFaceFactory.getInstance().create().neu_iva_get_picture_face_feature_bitmap(bitmap);

                if (register_face.getQuality() == NeuFaceQuality.NEU_IVA_FACE_OK) {
                    if (register_face.getFeatureValid() == true) {
                        feature_org.add(register_face.getFeature());
                        feature_mask.add(register_face.getMaskFeature());
                        name_org.add(face.getName().split("\\.")[0]);
                        Log.d(TAG, "add one feature to feature_org, name = " + face.getName().split("\\.")[0]);
                    }
                }else{
                    Log.e(TAG,face.getName().split("\\.")[0] +" register failed quality="+NeuFaceQuality.typeToString(register_face.getQuality()));
                }
            }

            //下面注册后缀为 png 的文件
            File png_faces_name[] = face_file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith("png");
                }
            });

            //通过循环的方式，将 registerPath 目录下所有的后缀为 .png 图像进行注册并将文件名对应为人名
            for (File face : png_faces_name) {
                Bitmap bitmap = BitmapFactory.decodeFile(registerPath + face.getName(), options);
                bitmap = rotateBitmap(bitmap, getBitmapDegree(registerPath + face.getName()));

                NeuFaceRegisterNode register_face = NeuFaceFactory.getInstance().create().neu_iva_get_picture_face_feature_bitmap(bitmap);

                if (register_face.getQuality() == NeuFaceQuality.NEU_IVA_FACE_OK) {
                    if (register_face.getFeatureValid() == true) {
                        feature_org.add(register_face.getFeature());
                        feature_mask.add(register_face.getMaskFeature());
                        name_org.add(face.getName().split("\\.")[0]);
                        Log.d(TAG, "add one feature to feature_org, name = " + face.getName().split("\\.")[0]);
                    }
                }else{
                    Log.e(TAG,face.getName().split("\\.")[0] +" register failed quality="+NeuFaceQuality.typeToString(register_face.getQuality()));
                }
            }
        }
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return bmp;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        onResume = false;
        onThread = false;
        Util.clearAllCache(MyApplication.getContext());
        //Pose
        Util.sendIntEventMessge(Constants.CLOSE_HAND_KCF);
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        String type = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.type,"");
        if ("0".equals(type) || "1".equals(type) ){ //单目活体, 单目非活体
            Util.sendIntEventMessge(Constants.CLOSE_KCF);
        }else if ("3".equals(type) || "4".equals(type) || "7".equals(type)){ //手势 , Pose , 人脸关键点
            Util.sendIntEventMessge(Constants.CLOSE_HAND_KCF);
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
