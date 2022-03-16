package com.neucore.neusdk_demo.neulink.extend.hello.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neusdk_demo.neulink.extend.hello.request.HelloCmd;

/**
 * 云端下发至设备端的命令侦听器
 * 所有业务处理都在这地方处理
 */
public class HelloCmdListener implements ICmdListener<ActionResult<String>, HelloCmd> {
    private String TAG = HelloCmdListener.class.getSimpleName();
    @Override
    public ActionResult<String> doAction(NeulinkEvent<HelloCmd> event) {
        ActionResult<String> result = new ActionResult<>();
        result.setData("hello");
        return result;
    }
}
