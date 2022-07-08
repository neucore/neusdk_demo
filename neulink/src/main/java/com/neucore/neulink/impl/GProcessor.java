package com.neucore.neulink.impl;

import android.content.Context;

import com.google.gson.JsonObject;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.IMessage;
import com.neucore.neulink.IMessageService;
import com.neucore.neulink.IProcessor;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.IActionResult;
import com.neucore.neulink.util.NeulinkUtils;
import com.neucore.neulink.util.JSonUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;

public abstract class GProcessor<Req extends Cmd, Res extends CmdRes, ActionResult extends IActionResult> implements IProcessor {

    protected String TAG = TAG_PREFIX+this.getClass().getSimpleName();
    protected Context context;
    protected Object lock = new Object();

    protected IMessage msg;
    public GProcessor() {
        this.context = ContextHolder.getInstance().getContext();
    }
    public GProcessor(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void execute(boolean debug,int qos,NeulinkTopicParser.Topic topic,JsonObject headers, JsonObject payload) {

        Req req = parser(payload.toString());

        NeulinkUtils.binding(req,topic,headers);

        String reqNo = req.getHeaders().get(NeulinkConst.NEULINK_HEADERS_REQNO);
        String version = req.getHeaders().get(NeulinkConst.NEULINK_HEADERS_VERSION);
        String biz = req.getHeaders().get(NeulinkConst.NEULINK_HEADERS_BIZ);

        payload = auth(headers,payload);

        /**
         * 发送响应消息给到服务端
         */
        String resTopic = String.format("%s/%s/%s",topic.getPrefix(),"res",biz);

        //检查当前请求是否已经已经到达过
        synchronized (lock){
            msg = query(biz);
            if (msg != null &&
                    (!"blib".equalsIgnoreCase(biz) ||//非目标库批量同步
                            ("blib".equalsIgnoreCase(biz) &&
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
                    String headersStr = "";
                    if(ObjectUtil.isNotEmpty(headers)){
                        headersStr = headers.toString();
                    }
                    String payloadStr = "";
                    if(ObjectUtil.isNotEmpty(payload)){
                        payloadStr = payload.toString();
                    }
                    msg = insert(req,topic,headersStr, payloadStr);
                }
                if(ObjectUtil.isNotEmpty(msg)){
                    id = msg.getId();
                }
                /**
                 * 响应消息已到达
                 */
                resReceived2Cloud(debug,resTopic,biz,version,reqNo,req.getHeaders());

                ActionResult actionResult = process(topic, req);
                if(ObjectUtil.isNotEmpty(actionResult)){
                    Res res = responseWrapper(req, actionResult);

                    if(ObjectUtil.isNotEmpty(res)){
                        if (res.getCode() == STATUS_200
                                ||res.getCode()==STATUS_202
                                ||res.getCode()==STATUS_403){//支撑多包批处理，所有包处理成功才叫做成功
                            update(id, IMessage.STATUS_SUCCESS, res.getMsg());
                        }
                        else {
                            update(id, IMessage.STATUS_FAIL, res.getMsg());//支撑多包批处理，当某个包处理失败的断点续传机制
                        }
                        mergeHeaders(req,res);
                        String jsonStr = JSonUtils.toString(res);
                        resLstRsl2Cloud(debug,qos,resTopic, version,reqNo,jsonStr);
                    }
                }
            }
            catch(NeulinkException ex){
                try {
                    NeuLogUtils.eTag(TAG, "execute", ex);
                    update(id, IMessage.STATUS_FAIL, ex.getMessage());
                    Res res = fail(req, ex.getCode(), ex.getMsg());
                    if(ObjectUtil.isNotEmpty(res)){
                        mergeHeaders(req,res);
                        String jsonStr = JSonUtils.toString(res);
                        resLstRsl2Cloud(debug,qos,resTopic, version,reqNo,jsonStr);
                    }
                }
                catch(Exception e){
                }
            }
            catch (Throwable ex) {
                try {
                    NeuLogUtils.eTag(TAG, "execute", ex);
                    update(id, IMessage.STATUS_FAIL, ex.getMessage());
                    Res res = fail(req, ex.getMessage());
                    if(ObjectUtil.isNotEmpty(res)){
                        mergeHeaders(req,res);
                        String jsonStr = JSonUtils.toString(res);
                        resLstRsl2Cloud(debug,qos,resTopic, version,reqNo,jsonStr);
                    }
                }
                catch(Exception e){}
            }
        }
    }

    protected JsonObject auth(JsonObject headers, JsonObject payload){

        /**
         * 解密
         */
        //TODO 解密
        payload = payload;
        /**
         * 计算md5
         */
        //TODO 计算md5
        /**
         * 验证
         */
        //TODO 验证
        return payload;
    }

    protected void mergeHeaders(Req req,Res res){
        if(ObjectUtil.isNotEmpty(req)
                && ObjectUtil.isNotEmpty(res)){
            /**
             * 请求Head
             */
            Map<String,String> reqHeaders = req.getHeaders();
            /**
             * 返回Head
             */
            Map<String,String> resHeaders = res.getHeaders();
            if(ObjectUtil.isNotEmpty(resHeaders)){//返回Head不为空
                /**
                 * res返回了header
                 */
                if(ObjectUtil.isNotEmpty(reqHeaders)){
                    /**
                     * req也带了head
                     */
                    reqHeaders.putAll(resHeaders);//resHeader覆盖reqHeaders
                    res.setHeaders(reqHeaders);
                }
            }
            else{
                res.setHeaders(reqHeaders);
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
        try {
            ICmdListener<ActionResult,Req> listener = getListener(cmd);
            if(listener==null){
                throw new NeulinkException(STATUS_404,cmd.getBiz()+ " Listener does not implemention");
            }
            ActionResult actionResult = listener.doAction(new NeulinkEvent<>(cmd));
            return actionResult;
        }
        catch (NeulinkException ex){
            throw ex;
        }
        catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    protected static String stack2Str(Throwable ex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ex.printStackTrace(ps);
        ps.close();
        return new String(baos.toByteArray());
    }

    protected IMessage insert(Req cmd,NeulinkTopicParser.Topic topic, String headers,String payload) {
        IMessageService messageService = ServiceRegistry.getInstance().getMessageService();
        if(ObjectUtil.isNotEmpty(messageService)){
            return messageService.save(cmd.getReqNo(), topic,headers,payload);
        }
        return null;
    }

    /**
     *
     * @param id
     * @param status
     * @param msg
     */
    protected void update(long id, String status, String msg) {
        IMessageService messageService = ServiceRegistry.getInstance().getMessageService();
        if(ObjectUtil.isNotEmpty(messageService)){
            messageService.update(id,status,msg);
        }
    }

    /**
     *
     * @param id
     * @param offset
     * @param status
     * @param msg
     */
    protected void updatePkg(long id, long offset,String status,String msg) {
        IMessageService messageService = ServiceRegistry.getInstance().getMessageService();
        if(ObjectUtil.isNotEmpty(messageService)){
            messageService.updatePkg(id,offset,status,msg);
        }
    }

    protected IMessage query(String reqId) {
        IMessageService messageService = ServiceRegistry.getInstance().getMessageService();
        if(ObjectUtil.isNotEmpty(messageService)){
            IMessage message = messageService.queryByReqNo(reqId);
            return message;
        }
        return null;
    }

    /**
     *
     * @param debug
     * @param resTopic
     * @param biz
     * @param version
     * @param reqNo
     * @param headers
     */
    protected void resReceived2Cloud(boolean debug,String resTopic,String biz,String version,String reqNo,Map<String,String> headers){
        NeulinkService.getInstance().publishMessage(debug,resTopic,biz,version,reqNo,NEULINK_MODE_RECEIVE,STATUS_201, NeulinkConst.MESSAGE_PROCESSING,headers);
    }
    /**
     * 根据requestId查询历史记录，如果已经执行完成了则直接返回执行结果
     * 把最后执行结果返回给到云端
     * @param message
     */
    protected void resLstRsl2Cloud(boolean debug,String resTopic,String version,String reqNo, IMessage message){
        NeulinkService.getInstance().publishMessage(debug,resTopic,version,reqNo,message.getPayload(),message.getQos());
    }

    /**
     * 根据requestId查询历史记录，如果已经执行完成了则直接返回执行结果
     * 把最后执行结果返回给到云端
     * @param resTopic
     * @param result
     */
    protected void resLstRsl2Cloud(boolean debug,int qos,String resTopic,String version,String reqNo,String result){
        NeulinkService.getInstance().publishMessage(debug,resTopic,version,reqNo,result,qos);
    }

    /**
     * 命令消息到达是日志状态默认为处理中，只有无法响应的请求【eg:reboot】消息到达是日志记录的状态是成功
     *
     * @return
     */
    protected String getStatus() {
        return IMessage.STATUS_PROCESS;
    }

    protected ICmdListener<ActionResult,Req> getListener(Req req){
        return ListenerRegistry.getInstance().getExtendListener(req.getBiz());
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
}

