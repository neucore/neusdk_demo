package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.IMessage;
import com.neucore.neulink.IProcessor;
import com.neucore.neulink.IQlib$ObjtypeProcessor;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.impl.cmd.rrpc.TLQueryRes;
import com.neucore.neulink.impl.cmd.rrpc.TLibQueryCmd;
import com.neucore.neulink.impl.cmd.rrpc.QResult;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.impl.registry.ProcessRegistry;
import com.neucore.neulink.util.DatesUtil;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;

import java.util.List;

import cn.hutool.core.util.ObjectUtil;

public class DefaultQLibProcessor extends GProcessor<TLibQueryCmd, TLQueryRes, QResult> implements IProcessor {

    private String libDir;
    public DefaultQLibProcessor() {
        super();
    }
    public DefaultQLibProcessor(Context context) {
        super(context);
        libDir = DeviceUtils.getTmpPath(context)+"/libDir";
    }

    final public void execute(NeulinkTopicParser.Topic topic, String payload) {
        this.topic = topic;

        payload = auth(topic,payload);

        /**
         * 发送响应消息给到服务端
         */
        String topicPrefix = String.format("%s/%s/%s",topic.getPrefix(),"res",topic.getBiz());

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
            TLibQueryCmd req = null;
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
                /**
                 * 响应消息已到达
                 */
                NeulinkService.getInstance().getPublisherFacde().response(topicPrefix,topic.getBiz(),topic.getVersion(),topic.getReqId(),NEULINK_MODE_RECEIVE,STATUS_201, NeulinkConst.MESSAGE_PROCESSING,req.getHeaders());

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
                            resLstRsl2Cloud(topic, jsonStr);
                            Log.d(TAG,"成功完成人脸offset:"+i+"下载");
                        }
                    }
                    catch(NeulinkException ex){
                        try {
                            Log.e(TAG, "execute", ex);
                            if(ObjectUtil.isNotEmpty(msg)){
                                update(id, IMessage.STATUS_FAIL, ex.getMessage());
                            }
                            TLQueryRes res = fail(req, ex.getCode(), ex.getMsg());
                            if(ObjectUtil.isNotEmpty(res)) {
                                mergeHeaders(req,res);
                                String jsonStr = JSonUtils.toString(res);
                                resLstRsl2Cloud(topic, jsonStr);
                            }
                        }
                        catch(Exception e){
                        }
                    }
                    catch (Exception ex){
                        Log.d(TAG,"人脸offset:"+i+"下载失败",ex);
                        Log.e(TAG,"process",ex);
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
                            resLstRsl2Cloud(topic, jsonStr);
                        }
                    }
                }
            }
            catch(NeulinkException ex){
                try {
                    Log.e(TAG, "execute", ex);
                    if(ObjectUtil.isNotEmpty(msg)) {
                        update(id, IMessage.STATUS_FAIL, ex.getMessage());
                    }
                    TLQueryRes res = fail(req, ex.getCode(), ex.getMsg());
                    if(ObjectUtil.isNotEmpty(res)){
                        mergeHeaders(req,res);
                        String jsonStr = JSonUtils.toString(res);
                        resLstRsl2Cloud(topic, jsonStr);
                    }

                }
                catch(Exception e){
                }
            }
            catch (Throwable ex) {
                try {
                    Log.e(TAG, "execute", ex);
                    if(ObjectUtil.isNotEmpty(msg)) {
                        update(id, IMessage.STATUS_FAIL, ex.getMessage());
                    }
                    TLQueryRes res = fail(req, ex.getMessage());
                    if(ObjectUtil.isNotEmpty(res)) {
                        mergeHeaders(req,res);
                        String jsonStr = JSonUtils.toString(res);
                        resLstRsl2Cloud(topic, jsonStr);
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
            throw new NeulinkException(STATUS_404,biz()+ " Listener does not implemention");
        }
        TLibQueryCmd pkgCmd = buildPkg(cmd,cmd.getDataUrl(), cmd.getOffset());
        QResult actionResult = listener.doAction(new NeulinkEvent<>(pkgCmd));
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
     * @param dataUrl
     * @param offset
     * @return
     * @throws NeulinkException
     */
    protected TLibQueryCmd buildPkg(TLibQueryCmd cmd, String dataUrl, long offset) throws NeulinkException{
        IQlib$ObjtypeProcessor processor = ProcessRegistry.getQlibBatch(cmd.getObjtype());
        if(processor==null){
            throw new NeulinkException(STATUS_404,cmd.getObjtype()+ " Processor does not implemention");
        }
        return processor.buildPkg(cmd.getCmdStr(),dataUrl,offset);
    }
}
