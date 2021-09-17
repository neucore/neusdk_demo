package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.cmd.rrpc.QResult;
import com.neucore.neulink.cmd.rrpc.TLQueryRes;
import com.neucore.neulink.cmd.rrpc.TLibQueryCmd;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;

/**
 * 目标库处理器
 */
public class QLibProcessor extends GProcessor<TLibQueryCmd, TLQueryRes,QResult> {

    final private String ADD = "add",DEL = "del",UPDATE = "update",SYNC = "sync";
    private String facelibDir;

    private String infoFileDir = null;
    private String imagesFileDir = null;

    public QLibProcessor(Context context){
        super(context);
        facelibDir = context.getFilesDir().getAbsolutePath()+"/facelib";
        infoFileDir = facelibDir+"/info";
        imagesFileDir = facelibDir + "/img";
    }

    public QResult process(NeulinkTopicParser.Topic topic, TLibQueryCmd cmd) {
        try {
            String objtype = cmd.getObjtype();
            QResult result = new QResult();
            if("face".equalsIgnoreCase(objtype)){
                //QueryResult queryResult = ListenerFactory.getInstance().getFaceQueryListener().doAction(new NeulinkEvent(cmd));
                //result.setCount(count);
                throw new RuntimeException("人脸目标库查询还在建设中");
            }
            else if("body".equalsIgnoreCase(objtype)){
                throw new RuntimeException("人体目标库还在建设中");
            }
            else if("car".equalsIgnoreCase(objtype)){
                throw new RuntimeException("车辆目标库还在建设中");
            }
            else if("lic".equalsIgnoreCase(objtype)){
                throw new RuntimeException("车牌目标库还在建设中");
            }
            return result;
        }
        catch(Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public TLibQueryCmd parser(String payload) {
        return (TLibQueryCmd) JSonUtils.toObject(payload, TLibQueryCmd.class);
    }

    @Override
    protected TLQueryRes responseWrapper(TLibQueryCmd t, QResult result) {
        TLQueryRes res = new TLQueryRes();
        res.setDeviceId(DeviceUtils.getDeviceId(getContext()));
        res.setObjtype(t.getObjtype());
        res.setCode(200);
        res.setMsg("success");
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
        res.setDeviceId(DeviceUtils.getDeviceId(getContext()));
        res.setObjtype(t.getObjtype());
        res.setCode(500);
        res.setMsg(error);
        return res;
    }

    @Override
    protected TLQueryRes fail(TLibQueryCmd t,int code, String error) {
        TLQueryRes res = new TLQueryRes();
        res.setDeviceId(DeviceUtils.getDeviceId(getContext()));
        res.setObjtype(t.getObjtype());
        res.setCode(code);
        res.setMsg(error);
        return res;
    }

    @Override
    protected ICmdListener getListener() {
        return ListenerFactory.getInstance().getFaceQueryListener();
    }
}
