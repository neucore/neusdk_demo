package com.neucore.neusdk_demo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;

import com.neucore.NeuSDK.NeuFaceRecgNode;
import com.neucore.neusdk_demo.Camera2Activity;
import com.neucore.neusdk_demo.Camera2PortraitActivity;
import com.neucore.neusdk_demo.MenuActivity;
import com.neucore.neusdk_demo.app.MyApplication;
import com.neucore.neusdk_demo.neucore.NeuFaceFactory;
import com.neucore.neusdk_demo.utility.Constants;

import org.greenrobot.eventbus.EventBus;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.transpose;

public class Util {

    /**
     * 发送Integer类型EventBus消息
     * @param message
     */
    public static void sendIntEventMessge(int message){
        NCModeSelectEvent fvMode = new NCModeSelectEvent();
        fvMode.setMode(message);
        EventBus.getDefault().post(fvMode);
    }

    /**
     * 发送Integer类型EventBus消息
     * @param command,message
     */
    public static void sendIntEventMessge(int command, Bitmap bitmap){
        NCModeSelectEvent fvMode = new NCModeSelectEvent();
        fvMode.setMode(command);
        fvMode.setMessage(bitmap);
        EventBus.getDefault().post(fvMode);
    }

    /**
     * 发送MotionEvent类型EventBus消息
     * @param command,message
     */
    public static void sendIntEventMessge(int command, MotionEvent motionEvent){
        NCModeSelectEvent fvMode = new NCModeSelectEvent();
        fvMode.setMode(command);
        fvMode.setMessage(motionEvent);
        EventBus.getDefault().post(fvMode);
    }

    /**
     * 发送List类型EventBus消息
     * @param command,message
     */
    public static void sendIntEventMessge(int command, List list){
        NCModeSelectEvent fvMode = new NCModeSelectEvent();
        fvMode.setMode(command);
        fvMode.setList(list);
        EventBus.getDefault().post(fvMode);
    }

    public static void sendIntEventMessge(int command, String path){
        NCModeSelectEvent fvMode = new NCModeSelectEvent();
        fvMode.setMode(command);
        fvMode.setMessage(path);
        EventBus.getDefault().post(fvMode);
    }

    public static void sendIntEventMessge(int command, Rect rect){
        NCModeSelectEvent fvMode = new NCModeSelectEvent();
        fvMode.setMode(command);
        fvMode.setRect(rect);
        EventBus.getDefault().post(fvMode);
    }

    public static short[] toShortArray(byte[] src) {

        int count = src.length >> 1;
        short[] dest = new short[count];
        for (int i = 0; i < count; i++) {
            dest[i] = (short) ((src[i * 2] & 0xff) | ((src[2 * i + 1] & 0xff) << 8));
        }
        return dest;
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    public static void hideBottomUIMenu(Activity context) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT < 19) { // lower api
            View v = context.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = context.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public static float widthPointTrans(float x1){
        Size size = new Size(480,640);
        float widthSize = size.getWidth();
        float widthPingMu = AppInfo.getWidthPingMu();
        float mX1 = widthPingMu - ( (x1 * widthPingMu) / widthSize);
        return mX1;
    }

    public static float heightPointTrans(float y1){
        Size size = new Size(480,640);
        float heightSize = size.getHeight();
        float heightPingMu = AppInfo.getHeightPingMu();
        float mY1 = (y1 * heightPingMu) / heightSize;
        return mY1;
    }

    public static float widthPointTrans6421(float x1){
        Size size = new Size(640,480);
        float widthSize = size.getWidth();
        float widthPingMu = 1920;
        float mX1 = ( (x1 * widthPingMu) / widthSize);
        return mX1;
    }

    public static float heightPointTrans6421(float y1){
        Size size = new Size(640,480);
        float heightSize = size.getHeight();
        float heightPingMu = 1080;
        float mY1 = (y1 * heightPingMu) / heightSize;
        return mY1;
    }

    public static float widthPointTrans64010(float x1){
        Size size = new Size(480,640);
        float widthSize = size.getWidth();
        float widthPingMu = 800;
        float mX1 = widthPingMu - ( (x1 * widthPingMu) / widthSize);
        return mX1;
    }

    public static float heightPointTrans64010(float y1){
        Size size = new Size(480,640);
        float heightSize = size.getHeight();
        float heightPingMu = 1280;
        float mY1 = (y1 * heightPingMu) / heightSize;
        return mY1;
    }

    public static byte[] getBytesFromImageAsTypeRGB(Image image) {
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
                //上面这段代码,每次耗时3--4毫秒

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
        }
        return null;
    }

    public static String TAG = "NEUCORE DetectActivity";

    public static Mat imageToMat(Image image) {
        ByteBuffer buffer;
        int rowStride;
        int pixelStride;
        int width = image.getWidth();
        int height = image.getHeight();
        int offset = 0;

        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[image.getWidth() * image.getHeight() * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];

        for (int i = 0; i < planes.length; i++) {
            buffer = planes[i].getBuffer();
            rowStride = planes[i].getRowStride();
            pixelStride = planes[i].getPixelStride();
            int w = (i == 0) ? width : width / 2;
            int h = (i == 0) ? height : height / 2;
            for (int row = 0; row < h; row++) {
                int bytesPerPixel = ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8;
                if (pixelStride == bytesPerPixel) {
                    int length = w * bytesPerPixel;
                    buffer.get(data, offset, length);

                    if (h - row != 1) {
                        buffer.position(buffer.position() + rowStride - length);
                    }
                    offset += length;
                } else {
                    if (h - row == 1) {
                        buffer.get(rowData, 0, width - pixelStride + 1);
                    } else {
                        buffer.get(rowData, 0, rowStride);
                    }
                    for (int col = 0; col < w; col++) {
                        data[offset++] = rowData[col * pixelStride];
                    }
                }
            }
        }

        Mat mat = new Mat(height + height / 2, width, CvType.CV_8UC3);
        mat.put(0, 0, data);

//        double[][] array = {{1.164 , 0.000,  1.596, -0.87075},
//                            {1.164, -0.391, -0.813, 0.529250},
//                            {1.164, 2.0180 , 0.000, -1.08175},
//                            {0.000, 0.0000 , 0.000, 1.000000}};
        double[][] array = {{1.1643828125 , 0.000,  1.59602734375, -0.87078515625},
                {1.1643828125, -0.39176171875, -0.81296875, 0.52959375},
                {1.1643828125, 2.017234375 , 0.000, -1.081390625},
                {0.000, 0.0000 , 0.000, 1.000000}};
//        Jama.Matrix A = new Jama.Matrix(array);
//        Jama.Matrix b = Jama.Matrix.random(3,1);
//        Jama.Matrix x = A.solve(b);
//        Jama.Matrix Residual = A.times(x).minus(b);
//        double rnorm = Residual.normInf();

        return mat;
    }

    public static byte[] ImageToByte(Image image ) {

        Image.Plane yPlane = image.getPlanes()[0];
        int ySize = yPlane.getBuffer().remaining();

        Image.Plane uPlane = image.getPlanes()[1];
        Image.Plane vPlane = image.getPlanes()[2];

        int uSize = uPlane.getBuffer().remaining();
        int vSize = vPlane.getBuffer().remaining();

        byte[] data = new byte[ySize + (ySize / 2)];

        yPlane.getBuffer().get(data, 0, ySize);

        ByteBuffer ub = uPlane.getBuffer();
        ByteBuffer vb = vPlane.getBuffer();

        int uvPixelStride = uPlane.getPixelStride();
        if (uvPixelStride == 1) {
            ub.get(data, ySize, uSize);
            vb.get(data, ySize + uSize, vSize);
            return data;
        }
        vb.get(data, ySize, vSize);
        return data;

    }


    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

}
