package com.neucore.neulink.impl;

import android.content.Context;

import com.google.gson.JsonObject;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.IMessage;
import com.neucore.neulink.IProcessor;
import com.neucore.neulink.IQlib$ObjtypeProcessor;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.impl.cmd.rrpc.TLQueryRes;
import com.neucore.neulink.impl.cmd.rrpc.TLibQueryCmd;
import com.neucore.neulink.impl.cmd.rrpc.QResult;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.impl.registry.ProcessRegistry;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.HeadersUtil;
import com.neucore.neulink.util.JSonUtils;

import java.util.List;

import cn.hutool.core.util.ObjectUtil;

public final class DefaultQLibProcessor extends GProcessor<TLibQueryCmd, TLQueryRes, QResult> implements IProcessor {

    private String libDir;
    public DefaultQLibProcessor() {
        super();
    }
    public DefaultQLibProcessor(Context context) {
        super(context);
        libDir = DeviceUtils.getTmpPath(context)+"/libDir";
    }

    public void execute(boolean debug,int qos,NeulinkTopicParser.Topic topic, JsonObject headers, JsonObject payload) {

        TLibQueryCmd req = parser(payload.toString());

        req.setDebug(debug);

        HeadersUtil.binding(req,topic,headers);

        String group = req.getGroup();
        String biz = req.getBiz();
        String reqNo = req.getReqNo();
        String version = req.getVersion();

        payload = auth(headers,payload);

        /**
         * 发送响应消息给到服务端
         */
        String resTopic = String.format("%s/%s/%s",group,"res",biz);

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
                resLstRsl2Cloud(debug,resTopic,version,reqNo,msg);
                return;
            }

            long id = 0;

            try {
                if (msg == null) {
                    msg = insert(req,headers.toString(), payload.toString());
                }
                if(ObjectUtil.isNotEmpty(msg)){
                    id = msg.getId();
                }

                /**
                 * 响应消息已到达
                 */
                resReceived2Cloud(debug,resTopic,biz,version,reqNo,req.getHeaders());

                long pages = req.getPages();

                long offset = req.getOffset();

                Long lastOffset = null;

                if(ObjectUtil.isNotEmpty(msg)){
                    NeuLogUtils.iTag(TAG,"pages="+pages+",offset="+offset+",MsgOffset="+lastOffset+",PkgStatus="+msg.getPkgStatus());

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
                    NeuLogUtils.dTag(TAG,"开始进入人脸offset:"+i+"下载");
                    try {
                        req.setOffset(i);
                        QResult result = process(topic,req);
                        result.setCount(req.getTotal());
                        result.setPage(pages);
                        result.setOffset(i);
                        TLQueryRes res = responseWrapper(req,result);

                        if(ObjectUtil.isNotEmpty(res)){
                            if (res.getCode() == STATUS_200
                                    ||res.getCode()==STATUS_202
                                    ||res.getCode()==STATUS_403){//支撑多包批处理，所有包处理成功才叫做成功
                                updatePkg(msg.getId(),i, IMessage.STATUS_SUCCESS, res.getMsg());
                            }
                            else {
                                updatePkg(msg.getId(),i, IMessage.STATUS_FAIL, res.getMsg());//支撑多包批处理，当某个包处理失败的断点续传机制
                            }
                            mergeHeaders(req,res);
                            String jsonStr = JSonUtils.toString(res);
                            resLstRsl2Cloud(debug,qos,resTopic,version,reqNo, jsonStr);
                            NeuLogUtils.dTag(TAG,"成功完成人脸offset:"+i+"下载");
                        }
                    }
                    catch(NeulinkException ex){
                        try {
                            NeuLogUtils.eTag(TAG, "execute", ex);
                            if(ObjectUtil.isNotEmpty(msg)){
                                update(id, IMessage.STATUS_FAIL, ex.getMessage());
                            }
                            TLQueryRes res = fail(req, ex.getCode(), ex.getMsg());
                            if(ObjectUtil.isNotEmpty(res)) {
                                mergeHeaders(req,res);
                                String jsonStr = JSonUtils.toString(res);
                                resLstRsl2Cloud(debug,qos,resTopic,version,reqNo, jsonStr);
                            }
                        }
                        catch(Exception e){
                        }
                    }
                    catch (Exception ex){
                        NeuLogUtils.eTag(TAG,"人脸offset:"+i+"下载失败",ex);
                        NeuLogUtils.eTag(TAG,"process",ex);
                        if(ObjectUtil.isNotEmpty(msg)){
                            updatePkg(msg.getId(),i, IMessage.STATUS_FAIL, ex.getMessage());
                        }
                        TLQueryRes res = fail(req, STATUS_500, ex.getMessage());
                        if(ObjectUtil.isNotEmpty(res)) {
                            mergeHeaders(req,res);
                            res.setTotal(req.getTotal());
                            res.setPages(pages);
                            res.setOffset(i);
                            String jsonStr = JSonUtils.toString(res);
                            resLstRsl2Cloud(debug,qos,resTopic,version,reqNo, jsonStr);
                        }
                    }
                }
            }
            catch(NeulinkException ex){
                try {
                    NeuLogUtils.eTag(TAG, "execute", ex);
                    if(ObjectUtil.isNotEmpty(msg)) {
                        update(id, IMessage.STATUS_FAIL, ex.getMessage());
                    }
                    TLQueryRes res = fail(req, ex.getCode(), ex.getMsg());
                    if(ObjectUtil.isNotEmpty(res)){
                        mergeHeaders(req,res);
                        String jsonStr = JSonUtils.toString(res);
                        resLstRsl2Cloud(debug,qos,resTopic,version,reqNo, jsonStr);
                    }

                }
                catch(Exception e){
                }
            }
            catch (Throwable ex) {
                try {
                    NeuLogUtils.eTag(TAG, "execute", ex);
                    if(ObjectUtil.isNotEmpty(msg)) {
                        update(id, IMessage.STATUS_FAIL, ex.getMessage());
                    }
                    TLQueryRes res = fail(req, ex.getMessage());
                    if(ObjectUtil.isNotEmpty(res)) {
                        mergeHeaders(req,res);
                        String jsonStr = JSonUtils.toString(res);
                        resLstRsl2Cloud(debug,qos,resTopic,version,reqNo, jsonStr);
                    }
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
    protected QResult process(NeulinkTopicParser.Topic topic, TLibQueryCmd cmd) {
        ICmdListener<QResult,TLibQueryCmd> listener = getListener(cmd.getObjtype());
        if(listener==null){
            throw new NeulinkException(STATUS_404,cmd.getBiz()+ " Listener does not implemention");
        }
        TLibQueryCmd queryCmd = buildPkg(cmd);
        QResult actionResult = listener.doAction(new NeulinkEvent<>(queryCmd));
        return actionResult;
    }

    /**
     *
     * @param objType
     * @return
     */
    protected ICmdListener<QResult,TLibQueryCmd> getListener(String objType){
        return ListenerRegistry.getInstance().getExtendListener(NEULINK_BIZ_QLIB+"."+objType);
    }
    /**
     * 解析处理
     * @param payload
     * @return
     */
    protected TLibQueryCmd parser(String payload){
        return JSonUtils.toObject(payload,TLibQueryCmd.class);
    }
    /**
     *
     * @param cmd
     * @param actionResult
     * @return
     */
    @Override
    protected TLQueryRes responseWrapper(TLibQueryCmd cmd, QResult actionResult) {
        IQlib$ObjtypeProcessor processor = ProcessRegistry.getQlibBatch(cmd.getObjtype());
        if(processor==null){
            throw new NeulinkException(STATUS_404,cmd.getObjtype()+ " Processor does not implemention");
        }
        return processor.responseWrapper(cmd,actionResult);
    }

    /**
     *
     * @param cmd
     * @param error
     * @return
     */
    protected TLQueryRes fail(TLibQueryCmd cmd, String error){
        IQlib$ObjtypeProcessor processor = ProcessRegistry.getQlibBatch(cmd.getObjtype());
        if(processor==null){
            throw new NeulinkException(STATUS_404,cmd.getObjtype()+ " Processor does not implemention");
        }
        return processor.fail(cmd,error);
    }

    /**
     *
     * @param cmd
     * @param code
     * @param error
     * @return
     */
    protected TLQueryRes fail(TLibQueryCmd cmd,int code, String error){
        IQlib$ObjtypeProcessor processor = ProcessRegistry.getQlibBatch(cmd.getObjtype());
        if(processor==null){
            throw new NeulinkException(STATUS_404,cmd.getObjtype()+ " Processor does not implemention");
        }
        return processor.fail(cmd,code,error);
    }

    /**
     * 下载包数据并构建包请求
     * @param cmd
     * @return
     * @throws NeulinkException
     */
    protected TLibQueryCmd buildPkg(TLibQueryCmd cmd) throws NeulinkException{
        IQlib$ObjtypeProcessor processor = ProcessRegistry.getQlibBatch(cmd.getObjtype());
        if(processor==null){
            throw new NeulinkException(STATUS_404,cmd.getObjtype()+ " Processor does not implemention");
        }
        return processor.buildPkg(cmd);
    }
}
