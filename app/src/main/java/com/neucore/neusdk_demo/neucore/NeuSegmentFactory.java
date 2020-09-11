package com.neucore.neusdk_demo.neucore;

import com.neucore.NeuSDK.NeuSegment;
import com.neucore.neusdk_demo.app.MyApplication;

public class NeuSegmentFactory {
    private volatile static NeuSegmentFactory instance ;
    private NeuSegment neuSegment = new NeuSegment(MyApplication.getContext(),null);
    public static NeuSegmentFactory getInstance(){
        if (instance == null){
            synchronized (NeuSegmentFactory.class){
                if (instance == null){
                    instance = new NeuSegmentFactory();
                }
            }
        }
        return instance;
    }
    public NeuSegment create(){
        return neuSegment;
    }
}
