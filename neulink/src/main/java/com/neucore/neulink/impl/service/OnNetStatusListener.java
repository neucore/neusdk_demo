package com.neucore.neulink.impl.service;

import android.util.Log;

import com.neucore.neulink.IOnNetStatusListener;
import com.neucore.neulink.app.NeulinkConst;

import java.lang.annotation.Target;

public class OnNetStatusListener implements IOnNetStatusListener ,NeulinkConst{
    private String TAG = TAG_PREFIX+"OnNetStatusListener";
    @Override
    public void onNetStatus(int netType, String netName) {
        Log.i(TAG,netName+",网络恢复");
    }
}
