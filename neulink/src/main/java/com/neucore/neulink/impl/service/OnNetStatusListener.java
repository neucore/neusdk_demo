package com.neucore.neulink.impl.service;

import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.IOnNetStatusListener;
import com.neucore.neulink.NeulinkConst;

public class OnNetStatusListener implements IOnNetStatusListener ,NeulinkConst{
    private String TAG = TAG_PREFIX+"OnNetStatusListener";
    @Override
    public void onNetStatus(int netType, String netName) {
        NeuLogUtils.iTag(TAG,netName+",网络恢复");
    }
}
