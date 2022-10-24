package com.neucore.neulink.impl.adapter;

import com.neucore.neulink.IResCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.Result;
import com.neucore.neulink.impl.cmd.cfg.CfgItem;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.log.NeuLogUtils;

public class RegistCallback implements IResCallback, NeulinkConst {
    private String TAG = TAG_PREFIX+"RegistCallback";
    @Override
    public void onFinished(Result result) {
        NeuLogUtils.iTag(TAG,String.format("%s",result.getMsg()));
    }
}
