package com.neucore.neusdk_demo.neucore;

import com.neucore.NeuSDK.NeuHand;
import com.neucore.neusdk_demo.app.MyApplication;

public class NeuHandFactory {
    private volatile static NeuHandFactory instance ;
    private NeuHand neuHand = new NeuHand(MyApplication.getContext(),null,512);
    public static NeuHandFactory getInstance(){
        if (instance == null){
            synchronized (NeuHandFactory.class){
                if (instance == null){
                    instance = new NeuHandFactory();
                }
            }
        }
        return instance;
    }
    public NeuHand create(){
        return neuHand;
    }
}
