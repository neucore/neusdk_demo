package com.neucore.neusdk_demo.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

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

}
