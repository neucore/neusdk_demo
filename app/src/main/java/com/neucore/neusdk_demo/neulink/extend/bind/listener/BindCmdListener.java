package com.neucore.neusdk_demo.neulink.extend.bind.listener;

import android.util.Log;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.extend.NeulinkEvent;

import com.neucore.neusdk_demo.neulink.extend.bind.request.BindSyncCmd;

public class BindCmdListener implements ICmdListener<BindActionResult, BindSyncCmd> {
    private String TAG = BindCmdListener.class.getSimpleName();
    @Override
    public BindActionResult doAction(NeulinkEvent<BindSyncCmd> neulinkEvent) {
        BindSyncCmd cmd = neulinkEvent.getSource();

        //绑定事件
        toUserUpload(true);
        /**
         * @TODO: 业务处理；eg：保存
         */
        BindActionResult result = new BindActionResult();
        result.setCode(NeulinkConst.STATUS_202);//需要审核确认
        result.setMessage(NeulinkConst.MESSAGE_SUCCESS);
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