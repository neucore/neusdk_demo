package com.neucore.neusdk_demo.neulink.extend.bind.listener;

import android.util.Log;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.NeulinkEvent;

import com.neucore.neusdk_demo.neulink.extend.bind.request.BindSyncCmd;

public class BindCmdListener implements ICmdListener<BindActionResult, BindSyncCmd> {
    private String TAG = BindCmdListener.class.getSimpleName();
    @Override
    public BindActionResult doAction(NeulinkEvent<BindSyncCmd> neulinkEvent) {
        BindSyncCmd cmd = neulinkEvent.getSource();

        //绑定事件
        toUserUpload(true);

        BindActionResult result = new BindActionResult();
        result.toLogcat();
        return result;
    }


    //--------------------------------------------------------------------
    public void toUserUpload(boolean flag)
    {
        Log.i(TAG,"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@hello@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    }
    //--------------------------------------------------------------------

}