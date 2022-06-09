package com.neucore.neulink.impl;

import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.IResCallback;
import com.neucore.neulink.NeulinkConst;

public class ResCallback2Log implements IResCallback, NeulinkConst {
    private String TAG = TAG_PREFIX + "ResCallback2Log";
    @Override
    public void onFinished(Result result) {
        NeuLogUtils.iTag(TAG,"默认回调处理："+result.toString());
    }
}
