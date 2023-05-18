package com.neucore.neusdk_demo.neucore;

import android.content.Context;

import com.neucore.NeuSDK.NeuFace;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neusdk_demo.app.MyApplication;

public class NeuFaceFactory {
    private String mModelPath = "/storage/emulated/0/neucore/nb/S905D3/";
    private volatile static NeuFaceFactory instance ;
    //初始化NeuFace对象
    //    jint recog_mode                    //常规为1； 开门为2
    //    jfloat recog_similar_thresh,       //识别相识度阈值(不戴口罩的)
    //    jfloat recog_similar_thresh_mask,  //识别相识度阈值(戴口罩的）
    //    jfloat recog_score_thresh,         //人脸质量分数阈值(不戴口罩的) ，   人脸质量分数低于这个阈值的人脸将被过滤掉
    //    jfloat recog_score_thresh_mask,    //人脸质量分数阈值(戴口罩的) ，     人脸质量分数低于这个阈值的人脸将被过滤掉
    //    jint with_att,                     //是否开人脸属性，包含口罩，眼镜  0,不开;  1,开启
    //    jint att_mask_only,                //只开人脸口罩  0,不开;  1,开启
    private NeuFace neuFace = new NeuFace(MyApplication.getContext(),2,0.6f,0.3f,0.55f,0.1f,1,1, mModelPath);

    public static NeuFaceFactory getInstance(){
        if (instance == null){
            synchronized (NeuFaceFactory.class){
                if (instance == null){
                    instance = new NeuFaceFactory();
                }
            }
        }
        return instance;
    }
    public NeuFace create(){
        return neuFace;
    }
}
