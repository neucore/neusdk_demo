package com.neucore.neusdk_demo;

import android.graphics.Rect;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.neucore.NeuSDK.NeuPose;
import com.neucore.NeuSDK.NeuPoseNode;
import com.neucore.neusdk_demo.camera2.Camera2Basic;
import com.neucore.neusdk_demo.camera2.OnImageAvailableListener;
import com.neucore.neusdk_demo.neucore.NeuPoseFactory;
import com.neucore.neusdk_demo.utility.Constants;
import com.neucore.neusdk_demo.utils.NeuHandInfo;
import com.neucore.neusdk_demo.utils.Util;
import com.neucore.neusdk_demo.view.AutoFitTextureView;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.transpose;


public class TestPoseActivity extends AppCompatActivity {

    boolean NeedRotate = false;

    private static final String TAG = "NEUCORE test_face";

    NeuPose mNeuPose = null;

    boolean mNeuFaceInitState = false;

    private AutoFitTextureView camera_view;

    private Camera2Basic mCamera  = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ########## 初始化需要的测试项 #########
		//OpenCVLoader.initDebug();
        mNeuPose = new NeuPose(this, null);
        mNeuFaceInitState = mNeuPose.getNeuSDKInitStatus();

        if(mNeuFaceInitState == false) {
            Toast toast = Toast.makeText(this,"NeuSDK 初始化失败，请检查网络连接和logcat", Toast.LENGTH_LONG);
            toast.show();
        }

        camera_view = (AutoFitTextureView) findViewById(R.id.camera_view);

        if (mCamera == null) {
            mCamera = new Camera2Basic(this, camera_view, imageListener);
            mCamera.start_camera();//开启摄像头并预览
        }
    }



    static Scalar[] scalar = {new Scalar(255,0,0), new Scalar(0,255,0), new Scalar(0,0,255), new Scalar(255,0,255)};

    static void draw_line(Mat img, float p1_x, float p1_y, float p2_x, float p2_y, float p1_score, float p2_score, Scalar scalar)
    {
        if (p1_score == 0.0f || p2_score == 0.0f) {
            return;
        } else {
            Imgproc.line(img, new Point(p1_x,p1_y), new Point(p2_x,p2_y), scalar, 4);
        }
    }

    static void draw_body(Mat image, float[] pose_node, float[] pose_node_score)
    {
        for(int j = 0; j < 18; ++j){
            Point p = new Point(pose_node[j * 2] , pose_node[j * 2 + 1]);

            Imgproc.circle(image, p, 5, new Scalar(255, 0,0),-1);

            // draw line of Nose and Neck
            for (int i = NeuPose.Nose; i <= NeuPose.Neck-1; i++) {
                draw_line(image, pose_node[i*2], pose_node[i*2 + 1], pose_node[(i+1)*2], pose_node[(i+1)*2 + 1],
                        pose_node_score[i], pose_node_score[i+1], scalar[0]);
            }

            for (int i = NeuPose.RShoulder; i <= NeuPose.RWrist-1; i++) {
                draw_line(image, pose_node[i*2], pose_node[i*2 + 1], pose_node[(i+1)*2], pose_node[(i+1)*2 + 1],
                        pose_node_score[i], pose_node_score[i+1], scalar[1]);
            }

            for (int i = NeuPose.LShoulder; i <= NeuPose.LWrist-1; i++) {
                draw_line(image, pose_node[i*2], pose_node[i*2 + 1], pose_node[(i+1)*2], pose_node[(i+1)*2 + 1],
                        pose_node_score[i], pose_node_score[i+1], scalar[1]);
            }

            for (int i = NeuPose.RHip; i <= NeuPose.RAnkle-1; i++) {
                draw_line(image, pose_node[i*2], pose_node[i*2 + 1], pose_node[(i+1)*2], pose_node[(i+1)*2 + 1],
                        pose_node_score[i], pose_node_score[i+1], scalar[2]);
            }

            for (int i = NeuPose.LHip; i <= NeuPose.LAnkle-1; i++) {
                draw_line(image, pose_node[i*2], pose_node[i*2 + 1], pose_node[(i+1)*2], pose_node[(i+1)*2 + 1],
                        pose_node_score[i], pose_node_score[i+1], scalar[2]);
            }

        }
    }


    /**
     * imagereader 的回调函数 OnImageAvailableListener() ,为重点关注函数
     * 通过 getBytesFromImageAsType() 函数将 image 转为 yuv 格式的byte[]
     * 通过 opencv 将 yuv 格式的byte[] 转换为 cv::Mat
     * 通过 Detect() 将 cv::Mat 进行算法处理
     * 通过 canvas.drawBitmap() 将图像绘制到 texterview 中
     */
    static byte[] dataByte = null;
    static Mat mat1 = null;
    static Mat rgb_mat = null;
    private OnImageAvailableListener imageListener=new OnImageAvailableListener(){
        @Override
        public void onImageAvailable(ImageReader reader) {
            super.onImageAvailable(reader);
            if(reader!=null){
                Image image = reader.acquireLatestImage();
                if(image==null)return;
                setPaintViewUIPose(image);


//                int imageWidth = image.getWidth();
//                int imageHeight = image.getHeight();
//
//                dataByte = mCamera.getBytesFromImageAsType(image);//从image中获取到byte格式的数据
//
//                //将传入的 yuv buffer 转为 cv::mat, 并通过cvtcolor 转换为BGR 或 RGB 格式
//                mat1 = new Mat((int)(imageHeight*1.5), imageWidth, CvType.CV_8UC1);
//                mat1.put(0,0,dataByte);
//
//                rgb_mat = new Mat(imageHeight, imageWidth, CvType.CV_8UC3);
//                Imgproc.cvtColor(mat1 , rgb_mat, Imgproc.COLOR_YUV420sp2RGBA);
//
//                if (NeedRotate) {
//                    transpose(rgb_mat, rgb_mat);
//                    flip(rgb_mat, rgb_mat, 1);//0: 沿X轴翻转； >0: 沿Y轴翻转； <0: 沿X轴和Y轴翻转
//                }
//                //############ 进行算法计算 ###########
//                pose_detect(rgb_mat);
//
//                Bitmap mDrawBitmap = Bitmap.createBitmap(rgb_mat.cols(), rgb_mat.rows(), Bitmap.Config.ARGB_8888);
//                Utils.matToBitmap(rgb_mat, mDrawBitmap);
//
//                if (mDrawBitmap != null) {
//                    Canvas canvas = camera_view.lockCanvas();
//                    if (canvas != null) {
//                        canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
//
//                        canvas.drawBitmap(mDrawBitmap, new Rect(0,0,mDrawBitmap.getWidth(), mDrawBitmap.getHeight()),
//                                new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null);
//                    }
//
//                    camera_view.unlockCanvasAndPost(canvas);
//                }
//                mDrawBitmap.recycle();
//
//                rgb_mat.release();
//                rgb_mat = null;
//                mat1.release();
//                mat1 = null;
//                dataByte = null;


                image.close();
            }
        }
    };


    private int width = 0;
    private int height = 0;
    private byte[] mPendingRGBFrameData;
    private int paintViewUIPoseNum = 0;
    //Pose检测
    private void setPaintViewUIPose(Image image) {
        LogUtils.dTag(TAG,"rgb  0 0 0 0 ImageToByte  start" );
        if (width == 0){
            width = image.getWidth();
        }
        if (height == 0){
            height = image.getHeight();
        }

        mPendingRGBFrameData = ImageToByte(image);
        LogUtils.dTag(TAG,"rgb  0 0 0 0  ImageToByte  end" );

        Mat yuvMat = new Mat(height + (height / 2), width, CvType.CV_8UC1);
        yuvMat.put(0, 0, mPendingRGBFrameData);
        Mat rgbMat = new Mat(height, width, CvType.CV_8UC3);
        Imgproc.cvtColor(yuvMat, rgbMat, Imgproc.COLOR_YUV2RGB_NV21, 3);
        yuvMat.release();
        LogUtils.dTag(TAG,"rgb  1111" ); //下面这句最耗时
        Imgcodecs.imwrite("/storage/emulated/0/neucore/111.jpg",rgbMat);

        //LogUtils.dTag(TAG,"rgb  6666" );
        transpose(rgbMat, rgbMat);    //耗时4毫秒  此处,只有我们项目中有需要
        LogUtils.dTag(TAG,"rgb  7777" );
        //flip(rgb_mat, rgb_mat, 1);  //耗时4毫秒  注释
        //本人测试的camera获取到的帧数据是旋转270度的，所以需要手动再旋转90度，如果camera获取的原始数据方向是正确的，上面代码将不再需要
        LogUtils.dTag(TAG,"rgb  8888" );
        //获取Pose数据
        NeuPoseNode[] resultRgb = NeuPoseFactory.getInstance().create().neu_iva_pose_detect(rgbMat,true); // withTracking 是否进行人脸追踪
        LogUtils.dTag(TAG,"rgb  9999" );



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
            //LogUtils.dTag(TAG,"rgb  10 10 10 10" );
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


    //按两次返回键退出程序
    private long mExitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 3000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                mCamera.stop_camera();
                // ############ 销毁需要的测试项 #########
                mNeuPose = null;

                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
