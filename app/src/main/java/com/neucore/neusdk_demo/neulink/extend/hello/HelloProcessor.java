package com.neucore.neusdk_demo.neulink.extend.hello;

import android.content.Context;

import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;

/**
 * 设备授权下发
 * HelloCmd:请求对象，
 * HelloCmdRes：响应对象
 * String:actionListener的返回类型
 */
public class HelloProcessor extends GProcessor<HelloCmd, HelloCmdRes,String> {

    public HelloProcessor(){
        this(ContextHolder.getInstance().getContext());
    }

    public HelloProcessor(Context context) {
        super(context);
    }

    @Override
    public HelloCmd parser(String payload) {
        return (HelloCmd) JSonUtils.toObject(payload, HelloCmd.class);
    }

    /**
     *
     * @param t 同步请求
     * @param result listener.doAction 的返回值
     * @return
     */
    @Override
    protected HelloCmdRes responseWrapper(HelloCmd t, String result) {
        HelloCmdRes res = new HelloCmdRes();
        res.setCmdStr(t.getCmdStr());
        res.setCode(200);
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getSN());
        res.setData(result);
        res.setMsg("成功");
        return res;
    }

    @Override
    protected HelloCmdRes fail(HelloCmd t, String error) {
        HelloCmdRes res = new HelloCmdRes();
        res.setCmdStr(t.getCmdStr());
        res.setCode(500);
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getSN());
        res.setData(error);
        res.setMsg("失败");
        return res;
    }

    @Override
    protected HelloCmdRes fail(HelloCmd t, int code, String error) {
        HelloCmdRes res = new HelloCmdRes();
        res.setCmdStr(t.getCmdStr());
        res.setCode(code);
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getSN());
        res.setData(error);
        res.setMsg("失败");
        return res;
    }
}
