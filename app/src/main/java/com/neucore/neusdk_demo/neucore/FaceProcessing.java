package com.neucore.neusdk_demo.neucore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.media.Image;
import android.util.Log;
import android.widget.Toast;

import com.neucore.NeuSDK.NeuFace;
import com.neucore.NeuSDK.NeuFaceNode;
import com.neucore.NeuSDK.NeuFaceRecgNode;
import com.neucore.NeuSDK.NeuFaceRegisterNode;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.opencv.core.Core.flip;
import static org.opencv.core.Core.transpose;

import com.neucore.neusdk_demo.app.MyApplication;
import com.neucore.neusdk_demo.db.UserService;
import com.neucore.neusdk_demo.utility.Constants;
import com.neucore.neusdk_demo.utils.AppInfo;
import com.neucore.neusdk_demo.utils.SPUtils;
import com.neucore.neusdk_demo.utils.SharePrefConstant;
import com.neucore.neusdk_demo.utils.Size;
import com.neucore.neusdk_demo.utils.Util;

public class FaceProcessing extends Thread {
    private static final String TAG = "NEUCORE FaceProcessing";

    private Context mContext;

    // This lock guards all of the member variables below.
    private static final Object mLock = new Object();
    private boolean mActive = true;

    private byte[] mPendingIRFrameData, mPendingRGBFrameData;

    int mIRimageWidth, mIRimageHeight;
    int mRGBimageWidth, mRGBimageHeight;
    private boolean mIRDataready = false;
    private boolean mRGBDataready = false;

    private static  NeuFace mNeucore_face = null;

    private volatile static FaceProcessing instance;

    //创建 FaceProcessing 静态变量
    public static FaceProcessing getInstance(Context context){
        if (instance == null) {
            synchronized (FaceProcessing.class) {
                if (instance == null){
                    instance = new FaceProcessing(context);
                    if (!mNeucore_face.getNeuSDKInitStatus()) {
                        mNeucore_face = null;
                        Toast.makeText(context,"NeuFace 初始化失败",Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "NeuFace 初始化失败");
                        instance = null;
                    }else {
                        Toast.makeText(context,"NeuFace 初始化成功",Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "NeuFace 初始化成功");
                    }
                }
            }
        }
        return instance;
    }

    private FaceProcessing(Context context)
    {
        this.mContext = context;
//        copyAssetResource2File(this.mContext);
//        OpenCVLoader.initDebug();
        //类的构造函数中，创建neucoreFace对象，并读取图片，获得特征值，存入链表中
        mNeucore_face = NeuFaceFactory.getInstance().create();
    }

    //将assets目录下的nb 文件夹中的xx.nb 文件放入 /storage/emulated/0/neucore/nb/S905D3/
    public void copyAssetResource2File(Context context)
    {
        String ModelFileName = "nb/";
        String OrgFilepath = "/storage/emulated/0/neucore/";
        String ModelFilepath0 = "/storage/emulated/0/neucore/nb/";
        String ModelFilepath = "/storage/emulated/0/neucore/nb/S905D3/";

        try {
            //判断 /storage/emulated/0/neucore/ 路径是否存在
            File orgpath = new File(OrgFilepath);
            if(! orgpath.exists()){
                orgpath.mkdirs();
            }

            //判断 /storage/emulated/0/neucore/nb/ 路径是否存在
            File despath0 = new File(ModelFilepath0);
            if(! despath0.exists()){
                despath0.mkdirs();
            }

            //判断 /storage/emulated/0/neucore/nb/S90503/ 路径是否存在
            File despath = new File(ModelFilepath);
            if(! despath.exists()){
                despath.mkdirs();
            }

            /**
             * 下面代码将assets目录下 nb 文件夹下所有的文件拷贝到定义的 /storage/emulated/0/neucore/nb/ 路径下
             * 可在此添加限制,只在apk首次启动或者 /storage/emulated/0/neucore/nb/路径被删除 时做此操作,
             * 达到节约时间目的
             */

            String[] filenames = context.getAssets().list(ModelFileName);
            for (String file : filenames) {
                InputStream is = context.getAssets().open(ModelFileName + file);

                File outF = new File(ModelFilepath+file);
                FileOutputStream fos = new FileOutputStream(outF);

                int byteCount;
                byte[] buffer = new byte[1024];
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
                outF.setReadable(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public NeuFaceRegisterNode getFeature(byte[] data, int imageWidth, int imageHeight)
    {
        //将传入的 yuv buffer 转为 cv::mat, 并通过cvtcolor 转换为BGR 或 RGB 格式
        Mat mat1 = new Mat((int)(imageHeight*1.5), imageWidth, CvType.CV_8UC1);
        mat1.put(0,0,data);
        Mat image_mat = new Mat(imageHeight, imageWidth,CvType.CV_8UC3);
        Imgproc.cvtColor(mat1 , image_mat, Imgproc.COLOR_YUV420sp2BGR);

        //图片旋转
        transpose(image_mat, image_mat);
        flip(image_mat, image_mat, 1);//0: 沿X轴翻转； >0: 沿Y轴翻转； <0: 沿X轴和Y轴翻转

        NeuFaceRegisterNode registerNode = mNeucore_face.neu_iva_get_picture_face_feature(image_mat);

        return registerNode;
    }

    /**
     * Sets the frame data received from the IR camera.
     */
    public void setIRFrameData(Image paramImage) {
        synchronized (mLock) {
            if (mPendingIRFrameData != null) {
                mPendingIRFrameData = null;
            }

            mIRimageWidth = paramImage.getWidth();
            mIRimageHeight = paramImage.getHeight();

            //mPendingIRFrameData = this.getBytesFromImageAsTypeIR(paramImage);
            mPendingIRFrameData = this.getBytesFromImageAsTypeIRFast(paramImage);
            mIRDataready = true;

            // Notify the processor thread if the rgb data is ready
            if (mRGBDataready) {
                mLock.notifyAll();
            }
        }
    }

    /**
     * Sets the frame data received from the RGB camera.
     */
    public void setRGBFrameData(Image paramImage,byte[] rgbData) {

        synchronized (mLock) {
//            if (mPendingRGBFrameData != null) {
//                mPendingRGBFrameData = null;
//            }

            mRGBimageWidth = paramImage.getWidth();
            mRGBimageHeight = paramImage.getHeight();

            String type = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.type,"");
            if ("2".equals(type)){ //双目专用
                mPendingRGBFrameData = this.getBytesFromImageAsTypeRGBFast(paramImage);
            }else {
                mPendingRGBFrameData = rgbData;
            }
            mRGBDataready = true;

            // Notify the processor thread if the ir data is ready
            if (mIRDataready) {
                mLock.notifyAll();
            }
        }
    }

    public boolean startProcess = true;
    private int rectNum = 0;
    public void setThreadStop(){
        startProcess = false;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (startProcess) {
                synchronized (mLock) {
                    if (!mActive) {
                        return;
                    }

                    while (mActive && (mRGBDataready == false || mIRDataready == false)) {
                        try {
                            // Wait for the next frame to be received from the camera, since we
                            // don't have it yet.
                            mLock.wait();
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Frame processing loop terminated.", e);
                            return;
                        }
                    }

                    //将传入的 yuv buffer 转为 cv::mat, 并通过cvtcolor 转换为BGR 或 RGB 格式
                    Mat mat1 = new Mat((int)(mIRimageHeight*1.5), mIRimageWidth, CvType.CV_8UC1);
                    mat1.put(0,0,mPendingIRFrameData);
                    //Mat ir_mat = new Mat(mIRimageHeight, mIRimageWidth,CvType.CV_8UC3);
                    Mat ir_mat = Imgcodecs.imdecode(new MatOfByte(mPendingIRFrameData), CvType.CV_8UC3);
                    Imgproc.cvtColor(mat1 , ir_mat, Imgproc.COLOR_YUV420sp2BGR);

                    //将传入的 yuv buffer 转为 cv::mat, 并通过cvtcolor 转换为BGR 或 RGB 格式
                    Mat mat2 = new Mat((int)(mRGBimageHeight*1.5),mRGBimageWidth, CvType.CV_8UC1);
                    mat2.put(0,0,mPendingRGBFrameData);
                    //Mat rgb_mat = new Mat(mRGBimageHeight, mRGBimageWidth,CvType.CV_8UC3);
                    Mat rgb_mat = Imgcodecs.imdecode(new MatOfByte(mPendingRGBFrameData), CvType.CV_8UC3);
                    Imgproc.cvtColor(mat2 , rgb_mat, Imgproc.COLOR_YUV420sp2BGR);

                    //本人测试的camera获取到的帧数据是旋转270度的，所以需要手动再旋转90度，如果camera获取的原始数据方向是正确的，下面代码将不再需要
                    transpose(ir_mat, ir_mat);
                    flip(ir_mat, ir_mat, 1);//0: 沿X轴翻转； >0: 沿Y轴翻转； <0: 沿X轴和Y轴翻转

                    transpose(rgb_mat, rgb_mat);
                    flip(rgb_mat, rgb_mat, 1);
                    //本人测试的camera获取到的帧数据是旋转270度的，所以需要手动再旋转90度，如果camera获取的原始数据方向是正确的，上面代码将不再需要


                    NeuFaceNode[] resultRgb = mNeucore_face.neu_iva_face_detect(rgb_mat);
                    System.out.println("      点的坐标 byte: "+ resultRgb.length );

                    List<Rect> rectList = new ArrayList<>();
                    rectList.clear();
                    for (int i = 0; i < resultRgb.length; i++) {
                        //调用检测算法,得到人脸框,5点信息,特征值等信息
                        //在 mat 中画人脸框
                        System.out.println("     点的坐标:    开始点:   x: "+resultRgb[i].getLeft() + "    y: "+resultRgb[i].getTop()
                                + "        结束点:    x: "+resultRgb[i].getLeft() + resultRgb[i].getWidth()
                                + "        结束点:    y: "+resultRgb[i].getTop() + resultRgb[i].getHeight() );
                        Rect rect_event = new Rect(resultRgb[i].getLeft(),resultRgb[i].getTop(),
                                (resultRgb[i].getLeft() + resultRgb[i].getWidth()),resultRgb[i].getTop() + resultRgb[i].getHeight());

                        Size size = new Size(480,640);
                        int widthSize = size.getWidth();
                        int heightSize = size.getHeight();
                        int x1 = rect_event.left;
                        int y1 = rect_event.top;
                        int x2 = rect_event.right;
                        int y2 = rect_event.bottom;

                        int widthPingMu = AppInfo.getWidthPingMu();
                        int heightPingMu = AppInfo.getHeightPingMu();
                        int aaaX1 = (x1 * widthPingMu) / widthSize;
                        int aaaX2 = (x2 * widthPingMu) / widthSize;
                        int aaaY1 = (y1 * heightPingMu) / heightSize;
                        int aaaY2 = (y2 * heightPingMu) / heightSize;

                        Rect rect = new Rect(aaaX1, aaaY1, aaaX2, aaaY2);
                        rectList.add(rect);
                        Util.sendIntEventMessge(Constants.FACE_START, rectList);

                        rectNum = 0;
                    }

                    //没有识别框数据时,识别框不显示
                    if (resultRgb.length == 0){
                        if (rectNum == 0){
                            rectNum++;
                            rectList.clear();
                            rectList.add(new Rect(0,0,0,0));
                            Util.sendIntEventMessge(Constants.FACE_START, rectList);
                        }
                    }

                    NeuFaceRecgNode[] result = mNeucore_face.neu_iva_face_detect_live(rgb_mat, ir_mat);
                    for (int i = 0; i < result.length; i++) {

                        if(result[i].getIslive() == false) {
                            Log.e(TAG,"result[i].getIslive()==false");
                            continue;
                        }

                        org.opencv.core.Rect rect = new org.opencv.core.Rect(result[i].getLeft(), result[i].getTop(), result[i].getWidth(), result[i].getHeight());
                        Mat sub = new Mat(rgb_mat, rect);
                        //Imgproc.cvtColor(sub , sub, Imgproc.COLOR_RGB2BGR); //Bitmap 需要bgr的图像显示的才是正确的，确保转换成bitmap的cv::mat 是bgr格式
                        Bitmap face = Bitmap.createBitmap(sub.cols(), sub.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(sub, face);//这个是发现的人脸，转换成了bitmap，当然也可以是其他的格式

                        ArrayList<byte[]> feature_org = UserService.getInstance(mContext).getFeatures();
                        ArrayList<byte[]> feature_mask = UserService.getInstance(mContext).getMaskFeatures();
                        ArrayList<String> name_org = UserService.getInstance(mContext).getNames();

                        Log.e(TAG,"feature_valid="+result[i].getFeatureValid()+" ismask="+result[i].getIsmask()+" lightnumber="+result[i].getLightNumber()
                                +" isglass="+result[i].getIsglass()+" angle="+result[i].getFaceAngle()[0]+" "+result[i].getFaceAngle()[1]+" "+result[i].getFaceAngle()[2]);

                        //如果特征值有效,进行人脸识别
                        if (result[i].getFeatureValid() == true) {

                            float maxSum = 0;
                            int maxID = 0;

                            if (result[i].getIsmask() == true) {
                                // 从注册图中找到相似度最大的ID 和 最大相似度
                                for (int org_size = 0; org_size < feature_mask.size(); org_size++) {
                                    float sum = mNeucore_face.neu_iva_face_similarity(feature_mask.get(org_size), result[i].getFeature());

                                    if (sum > maxSum) {
                                        maxSum = sum;
                                        maxID = org_size;
                                    }
                                }
                            } else {
                                // 从注册图中找到相似度最大的ID 和 最大相似度
                                for (int org_size = 0; org_size < feature_org.size(); org_size++) {
                                    float sum = mNeucore_face.neu_iva_face_similarity(feature_org.get(org_size), result[i].getFeature());

                                    if (sum > maxSum) {
                                        maxSum = sum;
                                        maxID = org_size;
                                    }
                                }
                            }

                            Log.e(TAG,"maxSum="+maxSum);
                            if (maxSum > 0.8) {
                                onFaceSuccessListener.success(face,name_org.get(maxID));
                                Log.d(TAG, "###### found one registered person, name is " + name_org.get(maxID));
                            }else {
                                onFaceSuccessListener.success(face,"检测到人脸");
                            }
                        }
                    }

                    mPendingIRFrameData = null;
                    mPendingRGBFrameData = null;
                    mRGBDataready = false;
                    mIRDataready = false;

                }
            }
        }
    }

    public boolean getRGBDataready()
    {
        return mRGBDataready;
    }

    public boolean getIRDataready()
    {
        return mIRDataready;
    }

    /**
     * Marks the runnable as active/not active.  Signals any blocked threads to continue.
     */
    public void setActive(boolean active) {
        synchronized (mLock) {
            mActive = active;
            mLock.notifyAll();
        }
    }

    /**
     * 释放资源，java 中 创建对象，释放的方式是将对象置为null
     */
    public void release() {
        if (mNeucore_face != null) {
            mNeucore_face = null;
        }
    }

    //原 getBytesFromImageAsTypeRGB 和 getBytesFromImageAsTypeIR 共用的方法
    public byte[] getBytesFromImageAsType(Image image) {
        try {
            //获取源数据，如果是YUV格式的数据planes.length = 3
            //plane[i]里面的实际数据可能存在byte[].length <= capacity (缓冲区总大小)
            final Image.Plane[] planes = image.getPlanes();

            //数据有效宽度，一般的，图片width <= rowStride，这也是导致byte[].length <= capacity的原因
            // 所以我们只取width部分
            int width = image.getWidth();
            int height = image.getHeight();

            //此处用来装填最终的YUV数据，需要1.5倍的图片大小，因为Y U V 比例为 4:1:1
            byte[] yuvBytes = new byte[width * height * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8];
            //目标数组的装填到的位置
            int dstIndex = 0;

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

                int srcIndex = 0;
                if (i == 0) {
                    //直接取出来所有Y的有效区域，也可以存储成一个临时的bytes，到下一步再copy
                    for (int j = 0; j < height; j++) {
                        System.arraycopy(bytes, srcIndex, yuvBytes, dstIndex, width);
                        srcIndex += rowStride;
                        dstIndex += width;
                    }
                } else if (i == 1) {
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
                } else if (i == 2) {
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
                }
            }

            //根据要求的结果类型进行填充
            for (int i = 0; i < vBytes.length; i++) {
                yuvBytes[dstIndex++] = uBytes[i];
                yuvBytes[dstIndex++] = vBytes[i];
            }

            return yuvBytes;
        } catch (final Exception e) {
            if (image != null) {
                image.close();
            }
            Log.i(TAG, e.toString());
        }
        return null;
    }

    public byte[] getBytesFromImageAsTypeRGB(Image image) {
        try {
            //获取源数据，如果是YUV格式的数据planes.length = 3
            //plane[i]里面的实际数据可能存在byte[].length <= capacity (缓冲区总大小)
            final Image.Plane[] planes = image.getPlanes();

            //数据有效宽度，一般的，图片width <= rowStride，这也是导致byte[].length <= capacity的原因
            // 所以我们只取width部分
            int width = image.getWidth();
            int height = image.getHeight();

            //此处用来装填最终的YUV数据，需要1.5倍的图片大小，因为Y U V 比例为 4:1:1
            byte[] yuvBytes = new byte[width * height * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8];
            //目标数组的装填到的位置
            int dstIndex = 0;

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

                int srcIndex = 0;
                if (i == 0) {
                    //直接取出来所有Y的有效区域，也可以存储成一个临时的bytes，到下一步再copy
                    for (int j = 0; j < height; j++) {
                        System.arraycopy(bytes, srcIndex, yuvBytes, dstIndex, width);
                        srcIndex += rowStride;
                        dstIndex += width;
                    }
                } else if (i == 1) {
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
                } else if (i == 2) {
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
                }
            }

            //根据要求的结果类型进行填充
            for (int i = 0; i < vBytes.length; i++) {
                yuvBytes[dstIndex++] = uBytes[i];
                yuvBytes[dstIndex++] = vBytes[i];
            }

            return yuvBytes;
        } catch (final Exception e) {
            if (image != null) {
                image.close();
            }
            Log.i(TAG, e.toString());
        }
        return null;
    }

    //imagereader 获取的image 从yuv_420_888 转到 yuv 的byte[]
    public byte[] getBytesFromImageAsTypeRGBFast(Image image) {  //总共耗时3毫秒
        Image.Plane Y = image.getPlanes()[0];
        Image.Plane U = image.getPlanes()[1];  //耗时1毫秒
        Image.Plane V = image.getPlanes()[2];

        int Yb = Y.getBuffer().remaining();
        int Ub = U.getBuffer().remaining();
        int Vb = V.getBuffer().remaining();

        byte[] data = new byte[Yb + Ub + Vb];

        for (int s = 0; s < 3; s += 3){
            if (s == 0){
                Y.getBuffer().get(data, 0, Yb);  //耗时2毫秒
            }else if (s == 1){
                U.getBuffer().get(data, Yb, Ub);    //耗时2毫秒
            }else if (s == 2){
                V.getBuffer().get(data, Yb+ Ub, Vb);  //耗时5毫秒
            }
        }
        //上面的for循环,总共耗时 (5-2) = 3 毫秒
        return data;
    }

    public byte[] getBytesFromImageAsTypeIR(Image image) {
        try {
            //获取源数据，如果是YUV格式的数据planes.length = 3
            //plane[i]里面的实际数据可能存在byte[].length <= capacity (缓冲区总大小)
            final Image.Plane[] planes = image.getPlanes();

            //数据有效宽度，一般的，图片width <= rowStride，这也是导致byte[].length <= capacity的原因
            // 所以我们只取width部分
            int width = image.getWidth();
            int height = image.getHeight();

            //此处用来装填最终的YUV数据，需要1.5倍的图片大小，因为Y U V 比例为 4:1:1
            byte[] yuvBytes = new byte[width * height * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8];
            //目标数组的装填到的位置
            int dstIndex = 0;

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

                int srcIndex = 0;
                if (i == 0) {
                    //直接取出来所有Y的有效区域，也可以存储成一个临时的bytes，到下一步再copy
                    for (int j = 0; j < height; j++) {
                        System.arraycopy(bytes, srcIndex, yuvBytes, dstIndex, width);
                        srcIndex += rowStride;
                        dstIndex += width;
                    }
                } else if (i == 1) {
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
                } else if (i == 2) {
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
                }
            }

            //根据要求的结果类型进行填充
            for (int i = 0; i < vBytes.length; i++) {
                yuvBytes[dstIndex++] = uBytes[i];
                yuvBytes[dstIndex++] = vBytes[i];
            }

            return yuvBytes;
        } catch (final Exception e) {
            if (image != null) {
                image.close();
            }
            Log.i(TAG, e.toString());
        }
        return null;
    }

    //imagereader 获取的image 从yuv_420_888 转到 yuv 的byte[]
    public byte[] getBytesFromImageAsTypeIRFast(Image image) {  //总共耗时3毫秒
        Image.Plane Y = image.getPlanes()[0];
        Image.Plane U = image.getPlanes()[1];  //耗时1毫秒
        Image.Plane V = image.getPlanes()[2];

        int Yb = Y.getBuffer().remaining();
        int Ub = U.getBuffer().remaining();
        int Vb = V.getBuffer().remaining();

        byte[] data = new byte[Yb + Ub + Vb];

        for (int s = 0; s < 3; s += 3){
            if (s == 0){
                Y.getBuffer().get(data, 0, Yb);  //耗时2毫秒
            }else if (s == 1){
                U.getBuffer().get(data, Yb, Ub);    //耗时2毫秒
            }else if (s == 2){
                V.getBuffer().get(data, Yb+ Ub, Vb);  //耗时5毫秒
            }
        }
        //上面的for循环,总共耗时 (5-2) = 3 毫秒
        return data;
    }

    private OnFaceSuccessListener onFaceSuccessListener;
    public void setOnFaceSuccessListener(OnFaceSuccessListener onFaceSuccessListener) {
        this.onFaceSuccessListener=onFaceSuccessListener;
    }
}
