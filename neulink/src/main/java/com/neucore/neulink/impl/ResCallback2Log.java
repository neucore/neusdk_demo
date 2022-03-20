package com.neucore.neulink.impl;

import android.util.Log;

import com.neucore.neulink.IResCallback;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.extend.Result;

public class ResCallback2Log implements IResCallback, NeulinkConst {
    private String TAG = TAG_PREFIX + "ResCallback2Log";
    @Override
    public void onFinished(Result result) {
        Log.i(TAG,"默认回调处理："+result.toString());
    }
}
