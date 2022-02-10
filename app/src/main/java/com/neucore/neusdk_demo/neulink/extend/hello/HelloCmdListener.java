package com.neucore.neusdk_demo.neulink.extend.hello;

import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.extend.NeulinkEvent;

public class HelloCmdListener implements ICmdListener<String> {
    @Override
    public String doAction(NeulinkEvent event) {
        return "hello";
    }
}
