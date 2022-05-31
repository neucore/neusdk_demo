package com.neucore.neulink.impl;

import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.neucore.neulink.IResCallback;
import com.neucore.neulink.NeulinkConst;

public class ResCallback2Log implements IResCallback, NeulinkConst {
    private String TAG = TAG_PREFIX + "ResCallback2Log";
    @Override
    public void onFinished(Result result) {
        LogUtils.iTag(TAG,"默认回调处理："+result.toString());
    }
}
