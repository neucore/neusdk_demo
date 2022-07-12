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
        if(STATUS_200==result.getCode()){
            int initCnt = ConfigContext.getInstance().getConfig(INIT_CNT,1);
            initCnt++;
            CfgItem item = new CfgItem();
            item.setKeyName(INIT_CNT);
            item.setValue(String.valueOf(initCnt));
            ConfigContext.getInstance().add(item);
            NeuLogUtils.iTag(TAG,String.format("initCnt=%s",initCnt));
        }
        NeuLogUtils.iTag(TAG,String.format("%s",result.getMsg()));
    }
}
