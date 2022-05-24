package com.neucore.neusdk_demo.neulink.extend.bind.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neusdk_demo.neulink.extend.bind.request.BindSyncCmd;
/**
 * 云端下发至设备端的命令侦听器
 * 所有业务处理都在这地方处理
 */
public class BindCmdListener implements ICmdListener<BindActionResult, BindSyncCmd> {
    private String TAG = BindCmdListener.class.getSimpleName();
    @Override
    public BindActionResult doAction(NeulinkEvent<BindSyncCmd> neulinkEvent) {
        BindSyncCmd cmd = neulinkEvent.getSource();
        /**
         * @TODO: 业务处理；异步弹框显示请求
         */
        BindActionResult result = new BindActionResult();
        result.setCode(NeulinkConst.STATUS_200);
        result.setMessage(NeulinkConst.MESSAGE_SUCCESS);
        return result;
    }
}