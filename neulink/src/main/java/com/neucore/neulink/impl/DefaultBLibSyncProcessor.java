package com.neucore.neulink.impl;

import android.content.Context;

import com.google.gson.JsonObject;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.IBlib$ObjtypeProcessor;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.IMessage;
import com.neucore.neulink.IProcessor;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.impl.cmd.rrpc.PkgActionResult;
import com.neucore.neulink.impl.cmd.rrpc.PkgCmd;
import com.neucore.neulink.impl.cmd.rrpc.PkgRes;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.impl.registry.ProcessRegistry;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.HeadersUtil;
import com.neucore.neulink.util.JSonUtils;

import java.util.List;

import cn.hutool.core.util.ObjectUtil;

public final class DefaultBLibSyncProcessor extends GProcessor<PkgCmd, PkgRes, PkgActionResult> implements IProcessor {

    private String libDir;
    public DefaultBLibSyncProcessor() {
        super();
    }
    public DefaultBLibSyncProcessor(Context context) {
        super(context);
        libDir = DeviceUtils.getTmpPath(context)+"/libDir";
    }

    public void execute(boolean debug,int qos,NeulinkTopicParser.Topic topic, JsonObject headers, JsonObject payload) {

        PkgCmd req = parser(payload.toString());

        if(ObjectUtil.isEmpty(req.getReqtime())){
            req.setReqtime(System.currentTimeMillis());
        }

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
                    msg = insert(req,headers==null?null:headers.toString(), payload.toString());
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
                        PkgActionResult result = process(topic,req);
                        result.setTotal(req.getTotal());
                        result.setPages(pages);
                        result.setOffset(i);
                        PkgRes res = responseWrapper(req,result);

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
                            PkgRes res = fail(req, ex.getCode(), ex.getMsg());
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
                        PkgRes res = fail(req, STATUS_500, ex.getMessage());
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
                    PkgRes res = fail(req, ex.getCode(), ex.getMsg());
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
                    PkgRes res = fail(req, ex.getMessage());
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
    protected PkgActionResult process(NeulinkTopicParser.Topic topic, PkgCmd cmd) {
        ICmdListener<PkgActionResult,PkgCmd> listener = getListener(cmd.getObjtype());
        if(listener==null){
            throw new NeulinkException(STATUS_404,cmd.getBiz()+ " Listener does not implemention");
        }
        PkgCmd pkgCmd = buildPkg(cmd,cmd.getDataUrl(), cmd.getOffset());
        PkgActionResult actionResult = listener.doAction(new NeulinkEvent<>(pkgCmd));
        return actionResult;
    }

    /**
     *
     * @param objType
     * @return
     */
    protected ICmdListener<PkgActionResult,PkgCmd> getListener(String objType){
        return ListenerRegistry.getInstance().getExtendListener(NEULINK_BIZ_BLIB+"."+objType);
    }
    /**
     * 解析处理
     * @param payload
     * @return
     */
    protected PkgCmd parser(String payload){
        return JSonUtils.toObject(payload,PkgCmd.class);
    }
    /**
     *
     * @param cmd
     * @param actionResult
     * @return
     */
    @Override
    protected PkgRes responseWrapper(PkgCmd cmd, PkgActionResult actionResult) {
        IBlib$ObjtypeProcessor processor = ProcessRegistry.getBlibBatch(cmd.getObjtype());
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
    protected PkgRes fail(PkgCmd cmd, String error){
        IBlib$ObjtypeProcessor processor = ProcessRegistry.getBlibBatch(cmd.getObjtype());
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
    protected PkgRes fail(PkgCmd cmd,int code, String error){
        IBlib$ObjtypeProcessor processor = ProcessRegistry.getBlibBatch(cmd.getObjtype());
        if(processor==null){
            throw new NeulinkException(STATUS_404,cmd.getObjtype()+ " Processor does not implemention");
        }
        return processor.fail(cmd,code,error);
    }

    /**
     * 下载包数据并构建包请求
     * @param cmd
     * @param dataUrl
     * @param offset
     * @return
     * @throws NeulinkException
     */
    protected PkgCmd buildPkg(PkgCmd cmd, String dataUrl, long offset) throws NeulinkException{
        IBlib$ObjtypeProcessor processor = ProcessRegistry.getBlibBatch(cmd.getObjtype());
        if(processor==null){
            throw new NeulinkException(STATUS_404,cmd.getObjtype()+ " Processor does not implemention");
        }
        return processor.buildPkg(cmd);
    }
}
