package com.neucore.neusdk_demo.neucore;

import com.neucore.NeuSDK.NeuPose;
import com.neucore.neusdk_demo.app.MyApplication;

public class NeuPoseFactory {
    private volatile static NeuPoseFactory instance ;
    private NeuPose neuPose = new NeuPose(MyApplication.getContext(),null);
    public static NeuPoseFactory getInstance(){
        if (instance == null){
            synchronized (NeuPoseFactory.class){
                if (instance == null){
                    instance = new NeuPoseFactory();
                }
            }
        }
        return instance;
    }
    public NeuPose create(){
        return neuPose;
    }
}
