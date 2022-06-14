package com.luoye.bzcamera.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;

/**
 * Created by jack_liu on 2019-09-04 11:44.
 * Function:
 */
public class FastYUVtoRGB {
    private RenderScript rs;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    private Type.Builder yuvType, rgbaType;
    private Allocation in, out;
    private long count = 0;
    private long spaceTime = 0;

    public FastYUVtoRGB(Context context) {
        rs = RenderScript.create(context);
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
    }


    public Bitmap convertYUVtoRGB(byte[] yuvData, int width, int height) {
        long startTime = System.currentTimeMillis();

        if (yuvType == null) {
            yuvType = new Type.Builder(rs, Element.U8(rs)).setX(yuvData.length);
            in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

            rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
            out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
        }
        in.copyFrom(yuvData);
        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);
        count++;
        spaceTime += (System.currentTimeMillis() - startTime);
        long time = spaceTime / count;
        Log.d("convertYUVtoRGB", "convert 耗时=" + time);

        Bitmap bmpout = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        startTime = System.currentTimeMillis();
        out.copyTo(bmpout);
//        Log.d("convertYUVtoRGB", "Bitmap 耗时=" + (System.currentTimeMillis() - startTime));


        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        matrix.postScale(-1, 1);
        // 创建新的图片
        Bitmap result = Bitmap.createBitmap(bmpout, 0, 0,
                bmpout.getWidth(), bmpout.getHeight(), matrix, false);
//
        bmpout.recycle();

        return result;
    }
}
