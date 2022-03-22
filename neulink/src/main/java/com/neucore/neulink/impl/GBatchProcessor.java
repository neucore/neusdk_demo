package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.IProcessor;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.cmd.rrpc.PkgActionResult;
import com.neucore.neulink.cmd.rrpc.PkgCmd;
import com.neucore.neulink.cmd.rrpc.PkgRes;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.impl.listener.DefaultFaceSyncListener;
import com.neucore.neulink.util.DatesUtil;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;

import java.util.List;

import cn.hutool.core.util.ObjectUtil;

public abstract class GBatchProcessor<Req extends PkgCmd, Res extends PkgRes, ActionResult extends PkgActionResult> extends GProcessor<Req, Res, ActionResult> implements IProcessor {

    final protected String ADD = "add",DEL = "del",UPDATE = "update",SYNC = "sync",PUSH = "push";
    private String libDir;
    public GBatchProcessor() {
        super();
    }
    public GBatchProcessor(Context context) {
        super(context);
        libDir = DeviceUtils.getTmpPath(context)+"/libDir";
        ListenerRegistrator.getInstance().setExtendListener("blib",new DefaultFaceSyncListener());
    }

    final public void execute(NeulinkTopicParser.Topic topic, String payload) {
        this.topic = topic;

        payload = auth(topic,payload);

        /**
         * 发送响应消息给到服务端
         */
        String topicPrefix = String.format("%s/%s/%s",topic.getPrefix(),"res",topic.getBiz());
        NeulinkService.getInstance().getPublisherFacde().response(topicPrefix,topic.getBiz(),topic.getVersion(),topic.getReqId(),NEULINK_MODE_RECEIVE,STATUS_201, NeulinkConst.MESSAGE_PROCESSING,null);
        //检查当前请求是否已经已经到达过
        synchronized (lock){
            msg = query(topic.getReqId());
            if (msg != null &&
                    (!"blib".equalsIgnoreCase(topic.getBiz()) ||//非目标库批量同步
                            ("blib".equalsIgnoreCase(topic.getBiz()) &&
                                    IMessage.STATUS_SUCCESS.equalsIgnoreCase(msg.getStatus())//成功的目标库同步操作直接返回
                            )
                    )
            ) {
                resLstRsl2Cloud(msg);
                return;
            }

            long id = 0;
            Req req = null;
            try {
                if (msg == null) {
                    msg = insert(topic, payload);
                }
                if(ObjectUtil.isNotEmpty(msg)){
                    id = msg.getId();
                }
                long reqTime = DatesUtil.getNowTimeStamp();//msg.getReqtime();
                req = parser(payload);

                req.setBiz(topic.getBiz());
                req.setReqId(topic.getReqId());
                req.setReqtime(reqTime);
                req.setVersion(topic.getVersion());

                long pages = req.getPages();

                long offset = req.getOffset();

                Long lastOffset = null;

                if(ObjectUtil.isNotEmpty(msg)){
                    Log.i(TAG,"pages="+pages+",offset="+offset+",MsgOffset="+lastOffset+",PkgStatus="+msg.getPkgStatus());

                    lastOffset = msg.getOffset();

                    if(IMessage.STATUS_FAIL.equalsIgnoreCase(msg.getPkgStatus())){
                        offset = req.getOffset();
                    }
                    else if(IMessage.STATUS_SUCCESS.equalsIgnoreCase(msg.getStatus()) ){
                        offset = lastOffset+1;
                    }
                }
                String rsl =null;
                long i=offset;
                List failed = null;
                for(;i<pages+1;i++){
                    Log.d(TAG,"开始进入人脸offset:"+i+"下载");
                    try {
                        req.setOffset(i);
                        ActionResult result = process(topic,req);
                        result.setTotal(req.getTotal());
                        result.setPages(pages);
                        result.setOffset(i);
                        Res res = responseWrapper(req,result);
                        res.setFailed(failed);
                        rsl = JSonUtils.toString(res);
                        resLstRsl2Cloud(topic, rsl);
                        Log.d(TAG,"成功完成人脸offset:"+i+"下载");
                        if(ObjectUtil.isNotEmpty(msg)){
                            updatePkg(msg.getId(),i, IMessage.STATUS_SUCCESS, MESSAGE_SUCCESS);
                        }
                    }
                    catch(NeulinkException ex){
                        try {
                            Log.e(TAG, "execute", ex);
                            if(ObjectUtil.isNotEmpty(msg)){
                                update(id, IMessage.STATUS_FAIL, ex.getMessage());
                            }
                            Res res = fail(req, ex.getCode(), ex.getMsg());
                            String jsonStr = JSonUtils.toString(res);
                            resLstRsl2Cloud(topic, jsonStr);
                        }
                        catch(Exception e){
                        }
                    }
                    catch (Exception ex){
                        Log.d(TAG,"人脸offset:"+i+"下载失败",ex);
                        Log.e(TAG,"process",ex);
                        Res result = fail(req, STATUS_500, ex.getMessage());
                        result.setTotal(req.getTotal());
                        result.setPages(pages);
                        result.setOffset(i);
                        if(ObjectUtil.isNotEmpty(msg)){
                            updatePkg(msg.getId(),i, IMessage.STATUS_FAIL, ex.getMessage());
                        }
                    }
                }
            }
            catch(NeulinkException ex){
                try {
                    Log.e(TAG, "execute", ex);
                    update(id, IMessage.STATUS_FAIL, ex.getMessage());
                    Res res = fail(req, ex.getCode(), ex.getMsg());
                    String jsonStr = JSonUtils.toString(res);
                    resLstRsl2Cloud(topic, jsonStr);
                }
                catch(Exception e){
                }
            }
            catch (Throwable ex) {
                try {
                    Log.e(TAG, "execute", ex);
                    update(id, IMessage.STATUS_FAIL, ex.getMessage());
                    Res res = fail(req, ex.getMessage());
                    String jsonStr = JSonUtils.toString(res);
                    resLstRsl2Cloud(topic, jsonStr);
                }
                catch(Exception e){}
            }
        }
    }


    /**
     * 默认实现
     * @param topic
     * @param cmd
     * @return
     */
    protected ActionResult process(NeulinkTopicParser.Topic topic, Req cmd) {
        ICmdListener<ActionResult,Req> listener = getListener();
        if(listener==null){
            throw new NeulinkException(STATUS_404,biz()+ " Listener does not implemention");
        }
        buildCmd(cmd.getCmdStr(),cmd.getDataUrl(), cmd.getOffset());
        ActionResult actionResult = listener.doAction(new NeulinkEvent<>(cmd));
        return actionResult;
    }

    protected ICmdListener<ActionResult,Req> getListener(){
        return ListenerRegistrator.getInstance().getExtendListener(biz());
    }
    /**
     * 解析处理
     * @param payload
     * @return
     */
    protected abstract Req parser(String payload);

    protected abstract Res responseWrapper(Req t, ActionResult actionResult);

    protected abstract Res fail(Req t, String error);

    protected abstract Res fail(Req t,int code, String error);

    protected abstract Req buildCmd(String cmdStr, String dataUrl, long offset) throws NeulinkException;
}
