package com.neucore.neulink.impl.service;

import android.util.Log;

import com.neucore.neulink.IOnNetStatusListener;

import java.lang.annotation.Target;

public class OnNetStatusListener implements IOnNetStatusListener {
    private String TAG = "OnNetStatusListener";
    @Override
    public void onNetStatus(int netType, String netName) {
        Log.i(TAG,netName+",网络恢复");
    }
}
