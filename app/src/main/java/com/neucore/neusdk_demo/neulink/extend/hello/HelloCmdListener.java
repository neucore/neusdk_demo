package com.neucore.neusdk_demo.neulink.extend.hello;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.NeulinkEvent;

public class HelloCmdListener implements ICmdListener<String,HelloCmd> {
    @Override
    public String doAction(NeulinkEvent<HelloCmd> event) {
        return "hello";
    }
}
