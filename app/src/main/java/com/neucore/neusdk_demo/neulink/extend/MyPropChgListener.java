package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.IPropChgListener;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.impl.SysPropAction;
import com.neucore.neulink.log.NeuLogUtils;

import java.util.List;

public class MyPropChgListener implements IPropChgListener {
    private String TAG = TAG_PREFIX+"MyPropChgListener";
    @Override
    public void doAction(NeulinkEvent<List<SysPropAction>> event) {
        List<SysPropAction> actions = event.getSource();
        for (SysPropAction action:actions){
            NeuLogUtils.iTag(TAG,String.format("key=%s,val=%s,act=%s",action.getPropKey(),action.getPropValue(),action.getName()));
        }
    }
}
