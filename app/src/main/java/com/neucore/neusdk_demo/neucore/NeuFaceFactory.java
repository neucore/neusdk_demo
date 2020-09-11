package com.neucore.neusdk_demo.neucore;

import android.content.Context;

import com.neucore.NeuSDK.NeuFace;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neusdk_demo.app.MyApplication;

public class NeuFaceFactory {
    private volatile static NeuFaceFactory instance ;
    private NeuFace neuFace = new NeuFace(MyApplication.getContext(),null);
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
