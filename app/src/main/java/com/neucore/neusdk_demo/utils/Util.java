package com.neucore.neusdk_demo.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.media.Image;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import java.nio.ByteBuffer;
import java.util.List;

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

    public static void sendIntEventMessge(int command,String path){
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
        float mX1 = (x1 * widthPingMu) / widthSize;
        return mX1;
    }

    public static float heightPointTrans(float y1){
        Size size = new Size(480,640);
        float heightSize = size.getHeight();
        float heightPingMu = AppInfo.getHeightPingMu();
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

}
