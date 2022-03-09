package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.Result;
import com.neucore.neulink.impl.ArgCmd;

public class HibrateActionListener implements ICmdListener<Result,ArgCmd> {
    @Override
    public Result doAction(NeulinkEvent<ArgCmd> event) {
        ArgCmd cmd = event.getSource();
        /**
         * 此处实现系统休眠操作
         */
        //@TODO
        Result result = new Result();
        /**
         * 200表示成功 500：表示错误
         */
        result.setCode(200);
        /**
         * 错误信息
         */
        result.setMessage("success");
        return result;
    }
}
