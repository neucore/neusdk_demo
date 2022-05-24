package com.neucore.neusdk_demo.neulink.extend.hello.response;

import android.util.Log;

import com.neucore.neulink.IResCallback;
import com.neucore.neulink.impl.Result;

/**
 * 响应回调接口
 * 当HelloCmdListener处理结果返回给云端时返回云端处理结果
 */
public class HellResCallback implements IResCallback {
    private String TAG = HellResCallback.class.getSimpleName();
    @Override
    public void onFinished(Result result) {
        Log.i(TAG,result.getReqId());
    }
}
