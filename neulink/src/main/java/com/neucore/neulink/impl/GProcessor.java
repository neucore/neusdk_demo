package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IMessageService;
import com.neucore.neulink.IProcessor;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DatesUtil;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.MD5Utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import cn.hutool.core.util.ObjectUtil;

public abstract class GProcessor<Req extends Cmd, Res extends CmdRes, T> implements IProcessor {

    protected String TAG = NeulinkConst.TAG_PREFIX+this.getClass().getSimpleName();
    private Context context;
    protected Object lock = new Object();
    protected NeulinkTopicParser.Topic topic;
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

    final public void execute(NeulinkTopicParser.Topic topic, String payload) {

        this.topic = topic;

        payload = auth(topic,payload);

        String resTopic = resTopic();
        /**
         * 发送响应消息给到服务端
         */
        NeulinkService.getInstance().getPublisherFacde().upldResponse(resTopic,topic.getReqId(),"receive");
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
                req.setReqId(topic.getReqId());
                req.setReqtime(reqTime);
                T result = process(topic, req);
                Res res = responseWrapper(req, result);
                String jsonStr = JSonUtils.toString(res);
                if (res.getCode() == 200){//支撑多包批处理，所有包处理成功才叫做成功
                    update(id, IMessage.STATUS_SUCCESS, res.getMsg());
                }
                else {
                    update(id, IMessage.STATUS_FAIL, res.getMsg());//支撑多包批处理，当某个包处理失败的断点续传机制
                }
                resLstRsl2Cloud(topic, jsonStr);
            }
            catch(NeulinkException ex){
                update(id,IMessage.STATUS_FAIL, ex.getMessage());
                Res res = fail(req, ex.getCode(),ex.getMsg());
                String jsonStr = JSonUtils.toString(res);
                resLstRsl2Cloud(topic, jsonStr);
            }
            catch (Throwable ex) {
                Log.e(TAG,"execute",ex);
                update(id,IMessage.STATUS_FAIL, ex.getMessage());
                Res res = fail(req, ex.getMessage());
                String jsonStr = JSonUtils.toString(res);
                resLstRsl2Cloud(topic, jsonStr);
            }
        }
    }

    /**
     * 默认实现
     * @param topic
     * @param cmd
     * @return
     */
    public T process(NeulinkTopicParser.Topic topic, Req cmd) {
        try {

            ICmdListener<T,Req> listener = getListener();
            if(listener==null){
                throw new NeulinkException(404,"awaken Listener does not implemention");
            }
            T result = listener.doAction(new NeulinkEvent<Req>(cmd));
            return result;
        }
        catch (NeulinkException ex){
            throw ex;
        }
        catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    public String auth(NeulinkTopicParser.Topic topic, String payload){
        String version = topic.getVersion();
        if("v1.0".equalsIgnoreCase(version)){
            return payload;
        }
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
        String md5 = topic.getMd5();
        MD5Utils.getInstance().getMD5String(payload);
        return payload;
    }

    public static String stack2Str(Throwable ex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ex.printStackTrace(ps);
        ps.close();
        return new String(baos.toByteArray());
    }

    public IMessage insert(NeulinkTopicParser.Topic topic, String payload) {
        IMessageService messageService = ServiceFactory.getInstance().getMessageService();
        if(ObjectUtil.isNotEmpty(messageService)){
            return messageService.save(topic,payload);
        }
        return null;
    }

    /**
     *
     * @param id
     * @param status
     * @param msg
     */
    public void update(long id, String status, String msg) {
        IMessageService messageService = ServiceFactory.getInstance().getMessageService();
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
    public void updatePkg(long id, long offset,String status, String msg) {
        IMessageService messageService = ServiceFactory.getInstance().getMessageService();
        if(ObjectUtil.isNotEmpty(messageService)){
            messageService.updatePkg(id,offset,status,msg);
        }
    }

    public IMessage query(String reqId) {
        IMessageService messageService = ServiceFactory.getInstance().getMessageService();
        if(ObjectUtil.isNotEmpty(messageService)){
            IMessage message = messageService.queryByReqNo(reqId);
            return message;
        }
        return null;
    }

    /**
     * 根据requestId查询历史记录，如果已经执行完成了则直接返回执行结果
     * 把最后执行结果返回给到云端
     * @param message
     */
    protected void resLstRsl2Cloud(IMessage message){
        String topicStr = message.getTopic();
        NeulinkTopicParser.Topic topic = NeulinkTopicParser.getInstance().parser(topicStr,message.getQos());
        String resTopic = resTopic();
        NeulinkService.getInstance().publishMessage(resTopic,topic.getVersion(),topic.getReqId(),message.getPayload(),message.getQos());
    }

    /**
     * 根据requestId查询历史记录，如果已经执行完成了则直接返回执行结果
     * 把最后执行结果返回给到云端
     * @param topic
     * @param result
     */
    protected void resLstRsl2Cloud(NeulinkTopicParser.Topic topic,String result){
        String resTopic = resTopic();
        if(resTopic==null||resTopic.trim().length()==0){
            return;
        }
        NeulinkService.getInstance().publishMessage(resTopic,topic.getVersion(),topic.getReqId(),result,topic.getQos());
    }

    public void clean(){

    }

    protected String resTopic(){
        return topic.getPrefix()+"/res/"+biz();
    }

    /**
     * 命令消息到达是日志状态默认为处理中，只有无法响应的请求【eg:reboot】消息到达是日志记录的状态是成功
     *
     * @return
     */
    protected String getStatus() {
        return IMessage.STATUS_PROCESS;
    }

    /**
     * 解析处理
     * @param payload
     * @return
     */
    public abstract Req parser(String payload);

    protected abstract Res responseWrapper(Req t, T result);

    protected abstract Res fail(Req t, String error);

    protected abstract Res fail(Req t,int code, String error);

    protected String biz(){
        return topic.getBiz();
    }

    protected ICmdListener<T,Req> getListener(){
        return ListenerFactory.getInstance().getExtendListener(biz());
    }
}

