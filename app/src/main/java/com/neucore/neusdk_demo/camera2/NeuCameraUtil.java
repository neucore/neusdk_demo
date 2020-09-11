package com.neucore.neusdk_demo.camera2;

import android.content.Context;

import com.neucore.neusdk_demo.app.MyApplication;
import com.neucore.neusdk_demo.utils.SPUtils;
import com.neucore.neusdk_demo.utils.SharePrefConstant;
import com.neucore.neusdk_demo.view.AutoFitTextureView;

public class NeuCameraUtil {

    static final int IR_CAMERA = 1;
    static final int RGB_CAMERA = 0;

//    private static NeuCameraUtil instance = new NeuCameraUtil();
//
//    public static NeuCameraUtil getInstance(){
//        return instance;
//    }

    private volatile static NeuCameraUtil instance;

    private NeuCameraUtil (){

    }

    public static NeuCameraUtil getInstance(){
        if(instance == null){
            synchronized(NeuCameraUtil.class){
                if(instance == null){
                    instance = new NeuCameraUtil();
                }
            }
        }

        return instance;
    }

    public void openCamera(Context context, AutoFitTextureView textureview_RGB,
                        OnImageAvailableListener onImageAvailableListener_RGB,
                        AutoFitTextureView textureview_IR,
                        OnImageAvailableListener onImageAvailableListener_IR){

        NeuCamera rgbCamera = new NeuCamera(RGB_CAMERA,context,textureview_RGB);
        rgbCamera.setOnImageAvailableListener(onImageAvailableListener_RGB);
        rgbCamera.init();

        String type = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.type,"");
        if ("2".equals(type)) { //双目的
            NeuCamera irCamera = new NeuCamera(IR_CAMERA,context,textureview_IR);
            irCamera.setOnImageAvailableListener(onImageAvailableListener_IR);
            irCamera.init();
            irCamera.start();
        }

        rgbCamera.start();

    }
}
