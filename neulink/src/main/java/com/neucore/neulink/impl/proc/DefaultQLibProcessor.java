package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.impl.cmd.rrpc.QResult;
import com.neucore.neulink.impl.cmd.rrpc.TLQueryRes;
import com.neucore.neulink.impl.cmd.rrpc.TLibQueryCmd;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.impl.listener.DefaultFaceQueryListener;
import com.neucore.neulink.util.JSonUtils;

/**
 * 目标库处理器
 */
public class DefaultQLibProcessor extends GProcessor<TLibQueryCmd, TLQueryRes,QResult> {

    final private String ADD = "add",DEL = "del",UPDATE = "update",SYNC = "sync";
    private String facelibDir;

    private String infoFileDir = null;
    private String imagesFileDir = null;

    public DefaultQLibProcessor(Context context){
        super(context);
        facelibDir = context.getFilesDir().getAbsolutePath()+"/facelib";
        infoFileDir = facelibDir+"/info";
        imagesFileDir = facelibDir + "/img";
        ListenerRegistry.getInstance().setExtendListener("qlib",new DefaultFaceQueryListener());
    }

    @Override
    public TLibQueryCmd parser(String payload) {
        return JSonUtils.toObject(payload, TLibQueryCmd.class);
    }

    @Override
    protected TLQueryRes responseWrapper(TLibQueryCmd t, QResult result) {
        TLQueryRes res = new TLQueryRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setObjtype(t.getObjtype());
        res.setCode(STATUS_200);
        res.setMsg(MESSAGE_SUCCESS);
        res.setTotal(result.getCount());
        res.setPages(result.getPage());
        res.setOffset(result.getOffset());
        res.setUrl(result.getUrl());
        res.setMd5(result.getMd5());
        return res;
    }

    @Override
    protected TLQueryRes fail(TLibQueryCmd t, String error) {
        TLQueryRes res = new TLQueryRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setObjtype(t.getObjtype());
        res.setCode(STATUS_500);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }

    @Override
    protected TLQueryRes fail(TLibQueryCmd t,int code, String error) {
        TLQueryRes res = new TLQueryRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setObjtype(t.getObjtype());
        res.setCode(code);
        res.setMsg(MESSAGE_FAILED);
        res.setData(error);
        return res;
    }
}
