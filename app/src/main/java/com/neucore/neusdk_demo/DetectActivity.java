package com.neucore.neusdk_demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.neucore.NeuSDK.NeuFaceNode;
import com.neucore.NeuSDK.NeuFaceQuality;
import com.neucore.NeuSDK.NeuFaceRecgNode;
import com.neucore.NeuSDK.NeuFaceRegisterNode;
import com.neucore.NeuSDK.NeuHandClass;
import com.neucore.NeuSDK.NeuHandNode;
import com.neucore.NeuSDK.NeuHandSwipe;
import com.neucore.NeuSDK.NeuPoseNode;
import com.neucore.neulink.app.Const;
import com.neucore.neusdk_demo.app.MyApplication;
import com.neucore.neusdk_demo.camera2.NeuCameraUtil;
import com.neucore.neusdk_demo.db.RecordDaoUtils;
import com.neucore.neusdk_demo.db.UserDaoUtils;
import com.neucore.neusdk_demo.neucore.NeuFaceFactory;
import com.neucore.neusdk_demo.neucore.NeuHandFactory;
import com.neucore.neusdk_demo.neucore.NeuPoseFactory;
import com.neucore.neusdk_demo.neucore.NeuSegmentFactory;
import com.neucore.neusdk_demo.utility.Constants;
import com.neucore.neusdk_demo.utils.AppInfo;
import com.neucore.neusdk_demo.utils.NCModeSelectEvent;
import com.neucore.neusdk_demo.utils.FileAccess;
import com.neucore.neulink.util.LogUtils;
import com.neucore.neusdk_demo.utils.NeuHandInfo;
import com.neucore.neusdk_demo.utils.PermissionHelper;
import com.neucore.neusdk_demo.camera2.Camera2Util;
import com.neucore.neusdk_demo.camera2.OnImageAvailableListener;
import com.neucore.neusdk_demo.db.UserService;
import com.neucore.neusdk_demo.db.bean.Record;
import com.neucore.neusdk_demo.db.bean.User;
import com.neucore.neusdk_demo.neucore.FaceProcessing;
import com.neucore.neusdk_demo.neucore.OnFaceSuccessListener;
import com.neucore.neusdk_demo.receiver.PermissionInterface;
import com.neucore.neusdk_demo.utils.SPUtils;
import com.neucore.neusdk_demo.utils.SharePrefConstant;
import com.neucore.neusdk_demo.utils.Size;
import com.neucore.neusdk_demo.utils.Util;
import com.neucore.neusdk_demo.utils.WeiboDialogUtils;
import com.neucore.neusdk_demo.view.AutoFitTextureView;
import com.neucore.neusdk_demo.view.CustomHandSurfaceView;
import com.neucore.neusdk_demo.view.CustomSurfaceView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.opencv.core.Core.flip;
import static org.opencv.core.Core.transpose;


/**
 * 人脸识别
 */
public class DetectActivity extends AppCompatActivity implements PermissionInterface {
    String TAG = "NEUCORE DetectActivity";


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    String path = Const.photoPath + new Date().getTime() + ".jpg";
                    if (!new File(Const.photoPath).exists()) new File(Const.photoPath).mkdirs();
//                    FileUtils.getFileFromBytes(data,path);
                    if (data != null)
                        FileAccess.writeFileSdcard(Const.photoPath, path, data);
                    Record record = new Record(null, user.getName(), user.getCardId(),
                            user.getUserId(), path, new Date().getTime(), user.getOrg(), 0, 0, 0);
                    boolean b = mRecordDaoUtils.insertRecord(record);
                    if (b) {
                        Toast.makeText(DetectActivity.this, "刷卡成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DetectActivity.this, "刷卡失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1://清除刷脸显示
                    iv_parent.setImageBitmap(null);
                    tv_yanzheng.setText("请靠近屏幕");
                    break;
                case 2://验证通过
                    Bitmap bm=(Bitmap)msg.obj;
                    setSuccess(bm);
                    break;
                case 3://检测到人脸
                    Bitmap bm2=(Bitmap)msg.obj;
                    setSuccess2(bm2);
                    break;
            }
        }
    };




    ImageView iv_parent;
    private PermissionHelper mPermissionHelper;

    private AutoFitTextureView textureView_RGB;
    private AutoFitTextureView textureView_IR;

    private UserDaoUtils mUserDaoUtils;
    private RecordDaoUtils mRecordDaoUtils;
    private HashMap<String, User> user_hm = new HashMap<>();
    private User user = null;
    private byte[] data = null;
    private TextView tv_yanzheng;
    private TextView tv_name_gv;
    private int bide=0;
    private Camera2Util mCamera2Util;
    private FaceProcessing mFaceProcessor = null;
    private Dialog mWeiboDialog;

    private OnImageAvailableListener RGB_imageListener=new OnImageAvailableListener(){
        @Override
        public void onImageAvailable(ImageReader reader) {
            super.onImageAvailable(reader);
            if(reader!=null){
                Image image = reader.acquireLatestImage();//最后一帧
                if(image==null)return;
                //LogUtils.d(TAG,"rgb "+ reader.getHeight()+","+reader.getWidth());

                //更新画布UI
                //单目开启,双目不走下面的更新画布UI
                if (mFaceProcessor != null){
                    if (startPaintBoolean){
                        if (AppInfo.getStartKCF()){ //人脸检测框 UI初始化完成
                            String type = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.type,"");
                            if ("0".equals(type) || "1".equals(type)){
                                setPaintViewUI(image);
                            }else if ("3".equals(type)){ //手势
                                setPaintViewUIHand(image);
                            }else if ("4".equals(type)){ //Pose检测
                                setPaintViewUIPose(image);
                            }else if ("5".equals(type)){ //虚拟背景
                                setPaintViewUISegment(image);
                            }
                        }
                    }
                }

                if (mFaceProcessor != null && !mFaceProcessor.getRGBDataready()) {
                    //LogUtils.d(TAG,"rgb  Dataready"+ reader.getHeight()+","+reader.getWidth());
                    mFaceProcessor.setRGBFrameData(image);
                } else {
                    Log.e(TAG,"mfaceProcessor="+(mFaceProcessor != null)+ " getRGBDataready ="+ mFaceProcessor.getRGBDataready());
                }
                image.close();
            }
        }
    };

    private void setPaintViewUI(Image image) {
        //LogUtils.d(TAG,"rgb  0 0 0 0 " );
        int mRGBimageWidth = image.getWidth();
        int mRGBimageHeight = image.getHeight();

        //LogUtils.d(TAG,"rgb  1111" ); //下面这句最耗时  15毫秒
        byte[] mPendingRGBFrameData = getBytesFromImageAsTypeRGB(image);//将传入的 yuv buffer 转为 cv::mat, 并通过cvtcolor 转换为BGR 或 RGB 格式
        //byte[] mPendingRGBFrameData = getBytesFromImageAsType(image);//将传入的 yuv buffer 转为 cv::mat, 并通过cvtcolor 转换为BGR 或 RGB 格式
        //LogUtils.d(TAG,"rgb  2222" );
        Mat mat2 = new Mat((int)(mRGBimageHeight*1.5),mRGBimageWidth, CvType.CV_8UC1);
        //LogUtils.d(TAG,"rgb  3333" );
        mat2.put(0,0,mPendingRGBFrameData);
        //Mat rgb_mat = new Mat(mRGBimageHeight, mRGBimageWidth,CvType.CV_8UC3);
        //LogUtils.d(TAG,"rgb  4444" );
        Mat rgb_mat = Imgcodecs.imdecode(new MatOfByte(mPendingRGBFrameData), CvType.CV_8UC3);
        //LogUtils.d(TAG,"rgb  5555" );
        Imgproc.cvtColor(mat2 , rgb_mat, Imgproc.COLOR_YUV420sp2BGR);

        LogUtils.d(TAG,"rgb  6666" );
        transpose(rgb_mat, rgb_mat);    //耗时4毫秒
        //LogUtils.d(TAG,"rgb  7777" );
        flip(rgb_mat, rgb_mat, 1);  //耗时4毫秒
        //本人测试的camera获取到的帧数据是旋转270度的，所以需要手动再旋转90度，如果camera获取的原始数据方向是正确的，上面代码将不再需要
        LogUtils.d(TAG,"rgb  8888" );
//        NeuFaceNode[] resultRgb = NeuFaceFactory.getInstance().create().neu_iva_face_detect(rgb_mat);
        NeuFaceRecgNode[] resultRgb = NeuFaceFactory.getInstance().create().neu_iva_face_detect_recognize(rgb_mat);
        LogUtils.d(TAG,"rgb  9999" );

        List<Rect> rectList = new ArrayList<>();
        rectList.clear();
        for (int i = 0; i < resultRgb.length; i++) {
            //调用检测算法,得到人脸框,5点信息,特征值等信息
            //在 mat 中画人脸框
            Rect rect_event = new Rect(resultRgb[i].getLeft(),resultRgb[i].getTop(),
                    (resultRgb[i].getLeft() + resultRgb[i].getWidth()),resultRgb[i].getTop() + resultRgb[i].getHeight());

            Size size = new Size(480,640);
            int widthSize = size.getWidth();
            int heightSize = size.getHeight();
            int x1 = rect_event.left;
            int y1 = rect_event.top;
            int x2 = rect_event.right;
            int y2 = rect_event.bottom;

            int aaaX1 = (x1 * widthPingMu) / widthSize;
            int aaaX2 = (x2 * widthPingMu) / widthSize;
            int aaaY1 = (y1 * heightPingMu) / heightSize;
            int aaaY2 = (y2 * heightPingMu) / heightSize;

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
        if (Math.abs(faceTime - time) > 500){
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
                        String name = name_org.get(maxID) + " maxSum=" + String.format("%.2f", maxSum);
                        faceTime = System.currentTimeMillis();
                        System.out.println("eee     识别name: "+name);
                        //Imgproc.putText(rgb_mat, name, new Point(face_rect.x, face_rect.y), Imgproc.FONT_HERSHEY_SIMPLEX, 2, new Scalar(0, 0, 255), 4, 8);
                    }
                }
            }
        }

    }

    private int paintViewUIHandNum = 0;
    //手势识别
    private void setPaintViewUIHand(Image image) {
        //LogUtils.d(TAG,"rgb  0 0 0 0 " );
        int mRGBimageWidth = image.getWidth();
        int mRGBimageHeight = image.getHeight();

        //LogUtils.d(TAG,"rgb  1111" ); //下面这句最耗时  15毫秒
        byte[] mPendingRGBFrameData = getBytesFromImageAsTypeRGB(image);//将传入的 yuv buffer 转为 cv::mat, 并通过cvtcolor 转换为BGR 或 RGB 格式
        //byte[] mPendingRGBFrameData = getBytesFromImageAsType(image);//将传入的 yuv buffer 转为 cv::mat, 并通过cvtcolor 转换为BGR 或 RGB 格式
        //LogUtils.d(TAG,"rgb  2222" );
        Mat mat2 = new Mat((int)(mRGBimageHeight*1.5),mRGBimageWidth, CvType.CV_8UC1);
        //LogUtils.d(TAG,"rgb  3333" );
        mat2.put(0,0,mPendingRGBFrameData);
        //Mat rgb_mat = new Mat(mRGBimageHeight, mRGBimageWidth,CvType.CV_8UC3);
        //LogUtils.d(TAG,"rgb  4444" );
        Mat rgb_mat = Imgcodecs.imdecode(new MatOfByte(mPendingRGBFrameData), CvType.CV_8UC3);
        //LogUtils.d(TAG,"rgb  5555" );
        Imgproc.cvtColor(mat2 , rgb_mat, Imgproc.COLOR_YUV420sp2BGR);

        LogUtils.d(TAG,"rgb  6666" );
        transpose(rgb_mat, rgb_mat);    //耗时4毫秒
        //LogUtils.d(TAG,"rgb  7777" );
        flip(rgb_mat, rgb_mat, 1);  //耗时4毫秒
        //本人测试的camera获取到的帧数据是旋转270度的，所以需要手动再旋转90度，如果camera获取的原始数据方向是正确的，上面代码将不再需要
        LogUtils.d(TAG,"rgb  8888" );
        //获取手势数据
        NeuHandNode[] resultRgb = NeuHandFactory.getInstance().create().neu_iva_hand_detect(rgb_mat);
        LogUtils.d(TAG,"rgb  9999" );

        List<NeuHandInfo> rectList = new ArrayList<>();
        rectList.clear();
        for (int i = 0; i < resultRgb.length; i++) {
            NeuHandInfo neuHandInfo = new NeuHandInfo();
            //调用检测算法,得到手势框,5点信息,特征值等信息
            //在 mat 中画手势框
            Rect rect_event = new Rect(resultRgb[i].getLeft(),resultRgb[i].getTop(),
                    (resultRgb[i].getLeft() + resultRgb[i].getWidth()),resultRgb[i].getTop() + resultRgb[i].getHeight());

            Size size = new Size(480,640);
            int widthSize = size.getWidth();
            int heightSize = size.getHeight();
            int x1 = rect_event.left;
            int y1 = rect_event.top;
            int x2 = rect_event.right;
            int y2 = rect_event.bottom;

            int aaaX1 = (x1 * widthPingMu) / widthSize;
            int aaaX2 = (x2 * widthPingMu) / widthSize;
            int aaaY1 = (y1 * heightPingMu) / heightSize;
            int aaaY2 = (y2 * heightPingMu) / heightSize;

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
            int status = NeuHandFactory.getInstance().create().neu_iva_hand_class(rgb_mat, resultRgb[i]);
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

    private int paintViewUIPoseNum = 0;
    //Pose检测
    private void setPaintViewUIPose(Image image) {
        //LogUtils.d(TAG,"rgb  0 0 0 0 " );
        int mRGBimageWidth = image.getWidth();
        int mRGBimageHeight = image.getHeight();

        //LogUtils.d(TAG,"rgb  1111" ); //下面这句最耗时  15毫秒
        byte[] mPendingRGBFrameData = getBytesFromImageAsTypeRGB(image);//将传入的 yuv buffer 转为 cv::mat, 并通过cvtcolor 转换为BGR 或 RGB 格式
        //byte[] mPendingRGBFrameData = getBytesFromImageAsType(image);//将传入的 yuv buffer 转为 cv::mat, 并通过cvtcolor 转换为BGR 或 RGB 格式
        //LogUtils.d(TAG,"rgb  2222" );
        Mat mat2 = new Mat((int)(mRGBimageHeight*1.5),mRGBimageWidth, CvType.CV_8UC1);
        //LogUtils.d(TAG,"rgb  3333" );
        mat2.put(0,0,mPendingRGBFrameData);
        //Mat rgb_mat = new Mat(mRGBimageHeight, mRGBimageWidth,CvType.CV_8UC3);
        //LogUtils.d(TAG,"rgb  4444" );
        Mat rgb_mat = Imgcodecs.imdecode(new MatOfByte(mPendingRGBFrameData), CvType.CV_8UC3);
        //LogUtils.d(TAG,"rgb  5555" );
        Imgproc.cvtColor(mat2 , rgb_mat, Imgproc.COLOR_YUV420sp2BGR);

        LogUtils.d(TAG,"rgb  6666" );
        transpose(rgb_mat, rgb_mat);    //耗时4毫秒
        //LogUtils.d(TAG,"rgb  7777" );
        flip(rgb_mat, rgb_mat, 1);  //耗时4毫秒
        //本人测试的camera获取到的帧数据是旋转270度的，所以需要手动再旋转90度，如果camera获取的原始数据方向是正确的，上面代码将不再需要
        LogUtils.d(TAG,"rgb  8888" );
        //获取手势数据
        NeuPoseNode[] resultRgb = NeuPoseFactory.getInstance().create().neu_iva_pose_detect(rgb_mat);
        LogUtils.d(TAG,"rgb  9999" );

        List<NeuHandInfo> rectList = new ArrayList<>();
        rectList.clear();
        for (int i = 0; i < resultRgb.length; i++) {
            float[] pose_node = resultRgb[i].getOne_pose_keypoints();
            float[] pose_node_score = resultRgb[i].getOne_pose_keypoints_score();

            NeuHandInfo neuHandInfo = new NeuHandInfo();
            neuHandInfo.setPose_node(pose_node);
            neuHandInfo.setPose_node_score(pose_node_score);

            rectList.add(neuHandInfo);
        }
        if (rectList.size() > 0){
            paintViewUIPoseNum = 0;
            Util.sendIntEventMessge(Constants.HAND_START, rectList);
            //LogUtils.d(TAG,"rgb  10 10 10 10" );
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

    //虚拟背景
    private void setPaintViewUISegment(Image image) {
        //LogUtils.d(TAG,"rgb  0 0 0 0 " );
        int mRGBimageWidth = image.getWidth();
        int mRGBimageHeight = image.getHeight();

        //LogUtils.d(TAG,"rgb  1111" ); //下面这句最耗时  15毫秒
        byte[] mPendingRGBFrameData = getBytesFromImageAsTypeRGB(image);//将传入的 yuv buffer 转为 cv::mat, 并通过cvtcolor 转换为BGR 或 RGB 格式
        //byte[] mPendingRGBFrameData = getBytesFromImageAsType(image);//将传入的 yuv buffer 转为 cv::mat, 并通过cvtcolor 转换为BGR 或 RGB 格式
        //LogUtils.d(TAG,"rgb  2222" );
        Mat mat2 = new Mat((int)(mRGBimageHeight*1.5),mRGBimageWidth, CvType.CV_8UC1);
        //LogUtils.d(TAG,"rgb  3333" );
        mat2.put(0,0,mPendingRGBFrameData);
        //Mat rgb_mat = new Mat(mRGBimageHeight, mRGBimageWidth,CvType.CV_8UC3);
        //LogUtils.d(TAG,"rgb  4444" );
        Mat rgb_mat = Imgcodecs.imdecode(new MatOfByte(mPendingRGBFrameData), CvType.CV_8UC3);
        //LogUtils.d(TAG,"rgb  5555" );
        Imgproc.cvtColor(mat2 , rgb_mat, Imgproc.COLOR_YUV420sp2BGR);

        LogUtils.d(TAG,"rgb  6666" );
        transpose(rgb_mat, rgb_mat);    //耗时4毫秒
        //LogUtils.d(TAG,"rgb  7777" );
        flip(rgb_mat, rgb_mat, 1);  //耗时4毫秒
        //本人测试的camera获取到的帧数据是旋转270度的，所以需要手动再旋转90度，如果camera获取的原始数据方向是正确的，上面代码将不再需要
        LogUtils.d(TAG,"rgb  8888" );


        //获取虚拟背景int[]数据
        Imgproc.cvtColor(rgb_mat,rgb_mat,Imgproc.COLOR_RGBA2BGR);
        byte[] mask = new byte[rgb_mat.rows() * rgb_mat.cols()];
        NeuSegmentFactory.getInstance().create().neu_iva_segment_detect(rgb_mat,mask);
        LogUtils.d(TAG,"rgb  9999" );

        int[] image_value = {244, 67, 54};
        int size = rgb_mat.cols() * rgb_mat.rows() * 3;
        byte[] color_map = new byte[size];
        for(int i = 0; i < size; i++) {
            color_map[i] = (byte) (image_value[i%3] * mask[i/3]);
        }

        //System.out.println("31   所有点的坐标: " + Arrays.toString(color_map));

//        List<NeuHandInfo> rectList = new ArrayList<>();
//        rectList.clear();
//        for (int i = 0; i < resultRgb.length; i++) {
//
//            NeuHandInfo neuHandInfo = new NeuHandInfo();
//
//            rectList.add(neuHandInfo);
//        }
//        if (rectList.size() > 0){
//            paintViewUIPoseNum = 0;
//            Util.sendIntEventMessge(Constants.HAND_START, rectList);
//            //LogUtils.d(TAG,"rgb  10 10 10 10" );
//        }else {
//            if (paintViewUIPoseNum == 0){
//                paintViewUIPoseNum++;
//
//                rectList.clear();
//                NeuHandInfo neuHandInfo = new NeuHandInfo();
//                neuHandInfo.setRect(new Rect(0,0,0,0));
//
//                rectList.add(neuHandInfo);
//
//                Util.sendIntEventMessge(Constants.HAND_START, rectList);
//            }
//        }

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

    private int faceRecognNum = 0;
    private long faceTime = 0;

    private OnImageAvailableListener IR_imageListener=new OnImageAvailableListener(){
        @Override
        public void onImageAvailable(ImageReader reader) {
            super.onImageAvailable(reader);
            if (reader != null) {
                Image image = reader.acquireLatestImage();//最后一帧
                if(image==null)return;
                LogUtils.d(TAG,"ir "+ reader.getHeight()+","+reader.getWidth());
                if (mFaceProcessor != null && !mFaceProcessor.getIRDataready()) {
                    mFaceProcessor.setIRFrameData(image);
                } else {
                    Log.e(TAG,"mfaceProcessor="+(mFaceProcessor != null)+ " getIRDataready ="+ mFaceProcessor.getIRDataready());
                }
                image.close();
            }
        }
    };

    public byte[] getBytesFromImageAsTypeRGB(Image image) {
        try {
            //获取源数据，如果是YUV格式的数据planes.length = 3
            //plane[i]里面的实际数据可能存在byte[].length <= capacity (缓冲区总大小)
            //LogUtils.d(TAG,"rgb  1111  0000" );
            final Image.Plane[] planes = image.getPlanes();

            //数据有效宽度，一般的，图片width <= rowStride，这也是导致byte[].length <= capacity的原因
            // 所以我们只取width部分
            //LogUtils.d(TAG,"rgb  1111  1111" );
            int width = image.getWidth();
            int height = image.getHeight();

            //LogUtils.d(TAG,"rgb  1111  2222" );
            //此处用来装填最终的YUV数据，需要1.5倍的图片大小，因为Y U V 比例为 4:1:1
            byte[] yuvBytes = new byte[width * height * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8];
            //目标数组的装填到的位置
            int dstIndex = 0;
            //LogUtils.d(TAG,"rgb  1111  3333" );

            //临时存储uv数据的
            byte uBytes[] = new byte[width * height / 4];
            byte vBytes[] = new byte[width * height / 4];
            int uIndex = 0;
            int vIndex = 0;

            int pixelsStride, rowStride;
            for (int i = 0; i < planes.length; i++) {
                pixelsStride = planes[i].getPixelStride();
                rowStride = planes[i].getRowStride();

                ByteBuffer buffer = planes[i].getBuffer();

                //如果pixelsStride==2，一般的Y的buffer长度=800x600，UV的长度=800x600/2-1
                //源数据的索引，y的数据是byte中连续的，u的数据是v向左移以为生成的，两者都是偶数位为有效数据
                byte[] bytes = new byte[buffer.capacity()];
                buffer.get(bytes);
                //上面这段代码,每次耗时3--4毫秒

                int srcIndex = 0;
                if (i == 0) {
                    //LogUtils.d(TAG,"rgb  1111  4444  00" );
                    //直接取出来所有Y的有效区域，也可以存储成一个临时的bytes，到下一步再copy
                    for (int j = 0; j < height; j++) {
                        System.arraycopy(bytes, srcIndex, yuvBytes, dstIndex, width);
                        srcIndex += rowStride;
                        dstIndex += width;
                    }
                    //LogUtils.d(TAG,"rgb  1111  4444" );
                } else if (i == 1) {
                    //LogUtils.d(TAG,"rgb  1111  5555  00" );
                    //根据pixelsStride取相应的数据
                    for (int j = 0; j < height / 2; j++) {
                        for (int k = 0; k < width / 2; k++) {
                            uBytes[uIndex++] = bytes[srcIndex];
                            srcIndex += pixelsStride;
                        }
                        if (pixelsStride == 2) {
                            srcIndex += rowStride - width;
                        } else if (pixelsStride == 1) {
                            srcIndex += rowStride - width / 2;
                        }
                    }
                    //LogUtils.d(TAG,"rgb  1111  5555" );
                } else if (i == 2) {
                    //LogUtils.d(TAG,"rgb  1111  6666  00" );
                    //根据pixelsStride取相应的数据
                    for (int j = 0; j < height / 2; j++) {
                        for (int k = 0; k < width / 2; k++) {
                            vBytes[vIndex++] = bytes[srcIndex];
                            srcIndex += pixelsStride;
                        }
                        if (pixelsStride == 2) {
                            srcIndex += rowStride - width;
                        } else if (pixelsStride == 1) {
                            srcIndex += rowStride - width / 2;
                        }
                    }
                    //LogUtils.d(TAG,"rgb  1111  6666" );
                }
            }

            //根据要求的结果类型进行填充
            for (int i = 0; i < vBytes.length; i++) {
                yuvBytes[dstIndex++] = uBytes[i];
                yuvBytes[dstIndex++] = vBytes[i];
            }
            //LogUtils.d(TAG,"rgb  1111  7777" );

            return yuvBytes;
        } catch (final Exception e) {
            if (image != null) {
                image.close();
            }
            //LogUtils.d(TAG,"rgb  1111  8888" );
            Log.i(TAG, e.toString());
        }
        return null;
    }

    private int widthPingMu;
    private int heightPingMu;
    @BindView(R.id.ll_activity_detect_bottom)
    LinearLayout ll_activity_detect_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Util.hideBottomUIMenu(this);
        setContentView(R.layout.activity_detect);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        ButterKnife.bind(this);
        String type = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.type,"");
        if ("0".equals(type) || "1".equals(type) || "2".equals(type)){ //单目,双目,人脸识别,才注册
            //face_register();
            ll_activity_detect_bottom.setVisibility(View.VISIBLE);
        }else {
            ll_activity_detect_bottom.setVisibility(View.GONE);
        }

        //获取屏幕的绝对宽高
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        widthPingMu = dm.widthPixels;
        heightPingMu = dm.heightPixels;
        AppInfo.setWidthPingMu(widthPingMu);
        AppInfo.setHeightPingMu(heightPingMu);

        mPermissionHelper = new PermissionHelper(this, this);
        mPermissionHelper.requestPermissions();

        tv_name_gv = findViewById(R.id.tv_name_gv);
        tv_yanzheng = findViewById(R.id.tv_yanzheng);
        iv_parent = findViewById(R.id.iv_parent);

        if (faceProcessingThread != null) {
            if (faceProcessingThread.isAlive()) {
                faceProcessingThread.interrupt();
            }
        }
        faceProcessingThread = new FaceProcessingThread();
        faceProcessingThread.start();
    }

    private ArrayList<byte[]> feature_org = new ArrayList<byte[]>();
    private ArrayList<byte[]> feature_mask = new ArrayList<byte[]>();
    private ArrayList<String> name_org = new ArrayList<String>();

    //创建bitmapFactory对象，并设置config值
    BitmapFactory.Options options = new BitmapFactory.Options();
    public void face_register() {
        String registerPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/twocamera/photo/";
        File face_file = new File(registerPath);

        //下面注册后缀为 jpg 的文件
        File jpg_faces_name[] = face_file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("jpg");
            }
        });

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

    private static final int FACE_PROCESSING = 13;
    private Handler myHandler;
    private FaceProcessingThread faceProcessingThread;
    class FaceProcessingThread extends Thread {
        public void run() {
            Looper.prepare();

            myHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    switch (msg.what) {
                        case FACE_PROCESSING:
                            tHandler.sendEmptyMessage(333);

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

    private boolean startPaintBoolean = false;
    Handler tHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 333: //开启

                    faceProcessingInstance();
                    break;
                case 444:
                    mWeiboDialog = WeiboDialogUtils.createLoadingDialog(DetectActivity.this, "相机启动中...");
                    break;
                case 555:
                    WeiboDialogUtils.closeDialog(mWeiboDialog);

                    FrameLayout.LayoutParams linearParamsLand = (FrameLayout.LayoutParams) textureView_RGB.getLayoutParams(); //取控件当前的布局参数
                    linearParamsLand.height = dip2px(DetectActivity.this,600);  // screenWidth=800  screenHeight=1232
                    linearParamsLand.width = dip2px(DetectActivity.this,924);
                    textureView_RGB.setLayoutParams(linearParamsLand); //使设置好的布局参数应用到控件

                    String type = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.type,"");
                    if ("0".equals(type) || "1".equals(type) || "2".equals(type)){
                        Util.sendIntEventMessge(Constants.OPEN_KCF);
                    }else if ("3".equals(type) || "4".equals(type) || "5".equals(type)){ //手势检测  Pose检测  虚拟背景
                        Util.sendIntEventMessge(Constants.OPEN_HAND_KCF);
                    }


                    if ("2".equals(type)){  //双目的
                        startPaintBoolean = false;
                    }else { //单目 和 其他的(手势)
                        startPaintBoolean = true;
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    @BindView(R.id.fragment_content_two_ll)
    LinearLayout fragment_content_two_ll;
    private CustomSurfaceView customSurfaceView;
    private CustomHandSurfaceView customHandSurfaceView;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onModeSwitch(NCModeSelectEvent ncModeSelectEvent) {
        switch (ncModeSelectEvent.getMode()) {
            case Constants.OPEN_KCF://开启
                fragment_content_two_ll.setVisibility(View.VISIBLE);
                if (customSurfaceView != null) {
                    fragment_content_two_ll.removeView(customSurfaceView);
                }
                customSurfaceView = new CustomSurfaceView(DetectActivity.this);
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
                },1000);
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
                customHandSurfaceView = new CustomHandSurfaceView(DetectActivity.this);
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
        customSurfaceView.setUpStartDraw();

        if (customSurfaceView != null) {
            customSurfaceView.drawsTwo(rectList);
        }
    }

    private void getZuoBiaoXYContentHand(List<NeuHandInfo> rectList) {
        customHandSurfaceView.setUpStartDraw();
        if (customHandSurfaceView != null) {
            customHandSurfaceView.drawsTwo(rectList);
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void faceProcessingInstance() {
//        if(mCamera2Util==null)mCamera2Util=new Camera2Util();
//
//        mCamera2Util.setOnImageAvailableListener_RGB(RGB_imageListener);
//        mCamera2Util.setOnImageAvailableListener_IR(IR_imageListener);
//
//        mCamera2Util.init(DetectActivity.this,textureView_RGB,textureView_IR);

        NeuCameraUtil.getInstance().openCamera(DetectActivity.this,
                textureView_RGB,RGB_imageListener,
                textureView_IR,IR_imageListener);

        //创建FaceProcessing对象线程，并启动
        mFaceProcessor = FaceProcessing.getInstance(MyApplication.getContext());
        mFaceProcessor.setActive(true);//设置mFaceProcessor状态为true
        if (MyApplication.getThreadAlive() == 0){ //线程没有启动过
            mFaceProcessor.start();
            MyApplication.setThreadAlive(1);
        }
        mFaceProcessor.setOnFaceSuccessListener(new OnFaceSuccessListener(){

            @Override
            public void success(Bitmap face, String userid) {
                tHandler.sendEmptyMessage(555);
                LogUtils.i("success","=================================="+userid);
                user = UserService.getInstance(getApplicationContext()).getUser(userid);
                if (user != null) {
                    String path=user.getHeadPhoto();
                    if(path==null){
                        Log.d(TAG,"userId="+user.getUserId()+",没有头像");
                        return;
                    }
                    FileInputStream fis = null;
                    Bitmap bitmap = null;
                    try {
                        fis = new FileInputStream(path);
                    } catch (FileNotFoundException e) {
                        Log.e(TAG,"initCamera",e);
                    }
                    bitmap = BitmapFactory.decodeStream(fis);
                    Message msg = new Message();
                    msg.what = 2;
                    msg.obj = bitmap;
                    handler.sendMessage(msg);
                }else{
                    Message msg = new Message();
                    msg.what = 3;
                    msg.obj = face;
                    handler.sendMessage(msg);
                }
            }
        });

        handler_bide.postDelayed(runnable, HANDLER_BIDE_DELAYED);// 打开定时器，执行操作
    }

    private void initCamera(){
        tHandler.sendEmptyMessage(444);
        tHandler.sendEmptyMessageDelayed(555,6000);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        Log.e(TAG,"screenWidth="+screenWidth + "  screenHeight="+screenHeight);

        mUserDaoUtils = new UserDaoUtils(this);
        mRecordDaoUtils = new RecordDaoUtils(this);
        if(textureView_IR==null) textureView_IR=findViewById(R.id.textureView_IR);
        if(textureView_RGB==null) textureView_RGB=findViewById(R.id.textureView_RGB);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendToHandler(FACE_PROCESSING,"");
            }
        },100);

    }

    //发送handler通知
    public void sendToHandler(int what, Object obj) {
        Message me = new Message();
        me.what = what;
        me.obj = obj;
        myHandler.sendMessage(me);
    }

    @Override
    public int getPermissionsRequestCode() {
        //设置权限请求requestCode，只有不跟onRequestPermissionsResult方法中的其他请求码冲突即可。
        return 10000;
    }

    @Override
    public String[] getPermissions() {
        //设置该界面所需的全部权限
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE
        };
    }

    @Override
    public void requestPermissionsSuccess() {
        LogUtils.i(TAG,"获取权限成功");
        initCamera();
    }

    @Override
    public void requestPermissionsFail() {
//        initView();
        Toast.makeText(DetectActivity.this,"请先授权相机权限",Toast.LENGTH_SHORT).show();
        LogUtils.i(TAG,"获取权限失败");
        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)){
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            if (mFaceProcessor != null){
//                mFaceProcessor.release();
//            }
//            if (mFaceProcessor != null){
//                if (mFaceProcessor.isAlive()) {
//                    mFaceProcessor.interrupt();
//                }
//            }
            if (handler != null){
                handler.removeCallbacksAndMessages(null);
            }
            if (handler_bide != null){
                handler_bide.removeCallbacksAndMessages(null);
            }

//            if (mCamera2Util != null){
//                mCamera2Util.destroy();
//            }
            if (textureView_RGB != null){
                textureView_RGB.clearAnimation();
                textureView_RGB.clearFocus();
                textureView_RGB.destroyDrawingCache();
            }
            if (textureView_IR != null){
                textureView_IR.clearAnimation();
                textureView_IR.clearFocus();
                textureView_IR.destroyDrawingCache();
            }
            finish();

            // 退出时，请求杀死进程
            //System.exit(0);
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 识别成功后
     * @paramdata2 现场图
     * @param bitmap 头像
     */
//    private void setSuccess(byte[] data2,Bitmap bitmap){
    private void setSuccess(Bitmap bitmap){
        bide = 0;
        if(bitmap!=null) {
            iv_parent.setImageBitmap(bitmap);
            tv_yanzheng.setText("验证通过");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data2 = baos.toByteArray();
            data=data2;
            handler.sendEmptyMessage(0);
        }
    }
    private void setSuccess2(Bitmap bitmap){
        bide = 0;
        if(bitmap!=null) {
            iv_parent.setImageBitmap(bitmap);
            tv_yanzheng.setText("检测到人脸");
        }
    }

    private static int HANDLER_BIDE_DELAYED = 1000;
    //显示时间控制
    Handler handler_bide = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            bide++;
            if(bide==10){
                handler.sendEmptyMessage(1);
            }
            handler_bide.postDelayed(this, HANDLER_BIDE_DELAYED);// 1000是延时时长
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tHandler != null){
            tHandler.removeCallbacksAndMessages(null);
        }
        if (myHandler != null){
            myHandler.removeCallbacksAndMessages(null);
        }
        if (faceProcessingThread != null) {
            if (faceProcessingThread.isAlive()) {
                faceProcessingThread.interrupt();
            }
        }
//        if (mFaceProcessor != null){
//            mFaceProcessor.release();
//        }
//        if (mFaceProcessor != null){
//            if (mFaceProcessor.isAlive()) {
//                mFaceProcessor.interrupt();
//            }
//        }
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
        }
        if (handler_bide != null){
            handler_bide.removeCallbacksAndMessages(null);
        }

//        if (mCamera2Util != null){
//            mCamera2Util.destroy();
//        }
        if (textureView_RGB != null){
            textureView_RGB.clearAnimation();
            textureView_RGB.clearFocus();
            textureView_RGB.destroyDrawingCache();
        }
        if (textureView_IR != null){
            textureView_IR.clearAnimation();
            textureView_IR.clearFocus();
            textureView_IR.destroyDrawingCache();
        }

        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        String type = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.type,"");
        if ("0".equals(type) || "1".equals(type) || "2".equals(type)){ //单目,双目
            Util.sendIntEventMessge(Constants.CLOSE_KCF);
        }else if ("3".equals(type) || "4".equals(type) || "5".equals(type)){ //手势 , Pose , 虚拟背景
            Util.sendIntEventMessge(Constants.CLOSE_HAND_KCF);
        }

        finish();

        // 退出时，请求杀死进程
        //System.exit(0);
    }
}

