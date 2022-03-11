package com.neucore.neusdk_demo.neulink.extend.hello;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.NeulinkEvent;

public class HelloCmdListener implements ICmdListener<ActionResult<String>,HelloCmd> {
    @Override
    public ActionResult<String> doAction(NeulinkEvent<HelloCmd> event) {
        ActionResult<String> result = new ActionResult<>();
        result.setData("hello");
        return result;
    }
}
