package com.neucore.neulink.impl;

import android.content.Context;

import com.google.gson.JsonObject;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.IMessage;
import com.neucore.neulink.IProcessor;
import com.neucore.neulink.IClib$ObjtypeProcessor;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.impl.cmd.check.CheckCmd;
import com.neucore.neulink.impl.cmd.check.CheckCmdRes;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.impl.registry.ProcessRegistry;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.HeadersUtil;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.RequestContext;

import java.util.Map;

import cn.hutool.core.util.ObjectUtil;

public final class DefaultCLibProcessor extends GProcessor<CheckCmd, CheckCmdRes, QueryActionResult<Map<String,Object>>> implements IProcessor {

    private String libDir;
    public DefaultCLibProcessor() {
        super();
    }
    public DefaultCLibProcessor(Context context) {
        super(context);
        libDir = DeviceUtils.getTmpPath(context)+"/libDir";
    }

    final public void execute(boolean debug,int qos,NeulinkTopicParser.Topic topic, JsonObject headers, JsonObject payload) {

        CheckCmd req = parser(payload.toString());

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
                    msg = insert(req,headers.toString(), payload.toString());
                }
                if(ObjectUtil.isNotEmpty(msg)){
                    id = msg.getId();
                }

                /**
                 * 响应消息已到达
                 */
                resReceived2Cloud(debug,resTopic,biz,version,reqNo,req.getHeaders());
                try {
                    QueryActionResult result = process(topic,req);

                    CheckCmdRes res = responseWrapper(req,result);

                    if(ObjectUtil.isNotEmpty(res)){
                        if (res.getCode() == STATUS_200
                                ||res.getCode()==STATUS_202
                                ||res.getCode()==STATUS_403){//支撑多包批处理，所有包处理成功才叫做成功
                            update(msg.getId(),IMessage.STATUS_SUCCESS, res.getMsg());
                        }
                        else {
                            update(msg.getId(),IMessage.STATUS_FAIL, res.getMsg());//支撑多包批处理，当某个包处理失败的断点续传机制
                        }
                        mergeHeaders(req,res);
                        String jsonStr = JSonUtils.toString(res);
                        resLstRsl2Cloud(debug,qos,resTopic,version,reqNo, jsonStr);
                    }
                }
                catch(NeulinkException ex){
                    try {
                        NeuLogUtils.eTag(TAG, "execute", ex);
                        if(ObjectUtil.isNotEmpty(msg)){
                            update(id, IMessage.STATUS_FAIL, ex.getMessage());
                        }
                        CheckCmdRes res = fail(req, ex.getCode(), ex.getMsg());
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
                    NeuLogUtils.eTag(TAG,"process",ex);
                    if(ObjectUtil.isNotEmpty(msg)){
                        update(msg.getId(),IMessage.STATUS_FAIL, ex.getMessage());
                    }
                    CheckCmdRes res = fail(req, STATUS_500, ex.getMessage());
                    if(ObjectUtil.isNotEmpty(res)) {
                        mergeHeaders(req,res);
                        String jsonStr = JSonUtils.toString(res);
                        resLstRsl2Cloud(debug,qos,resTopic,version,reqNo, jsonStr);
                    }
                }
            }
            catch(NeulinkException ex){
                try {
                    NeuLogUtils.eTag(TAG, "execute", ex);
                    if(ObjectUtil.isNotEmpty(msg)) {
                        update(id, IMessage.STATUS_FAIL, ex.getMessage());
                    }
                    CheckCmdRes res = fail(req, ex.getCode(), ex.getMsg());
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
                    CheckCmdRes res = fail(req, ex.getMessage());
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
    protected QueryActionResult process(NeulinkTopicParser.Topic topic, CheckCmd cmd) {
        ICmdListener<QueryActionResult,CheckCmd> listener = getListener(cmd.getObjtype());
        if(listener==null){
            throw new NeulinkException(STATUS_404,cmd.getBiz()+ " Listener does not implemention");
        }
        CheckCmd checkCmd = buildPkg(cmd);
        QueryActionResult actionResult = listener.doAction(new NeulinkEvent<>(checkCmd));
        return actionResult;
    }

    /**
     *
     * @param objType
     * @return
     */
    protected ICmdListener<QueryActionResult,CheckCmd> getListener(String objType){
        return ListenerRegistry.getInstance().getExtendListener(NEULINK_BIZ_CLIB+"."+objType);
    }
    /**
     * 解析处理
     * @param payload
     * @return
     */
    protected CheckCmd parser(String payload){
        return JSonUtils.toObject(payload,CheckCmd.class);
    }
    /**
     *
     * @param cmd
     * @param actionResult
     * @return
     */
    @Override
    protected CheckCmdRes responseWrapper(CheckCmd cmd, QueryActionResult actionResult) {
        IClib$ObjtypeProcessor processor = ProcessRegistry.getClibProcessor(cmd.getObjtype());
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
    protected CheckCmdRes fail(CheckCmd cmd, String error){
        IClib$ObjtypeProcessor processor = ProcessRegistry.getClibProcessor(cmd.getObjtype());
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
    protected CheckCmdRes fail(CheckCmd cmd,int code, String error){
        IClib$ObjtypeProcessor processor = ProcessRegistry.getClibProcessor(cmd.getObjtype());
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
    protected CheckCmd buildPkg(CheckCmd cmd) throws NeulinkException{
        IClib$ObjtypeProcessor processor = ProcessRegistry.getClibProcessor(cmd.getObjtype());
        if(processor==null){
            throw new NeulinkException(STATUS_404,cmd.getObjtype()+ " Processor does not implemention");
        }
        return processor.buildPkg(cmd);
    }
}
