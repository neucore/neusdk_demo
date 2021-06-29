package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IProcessor;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.util.DatesUtil;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.MD5Utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public abstract class GProcessor<Req extends Cmd, Res extends CmdRes, T> implements IProcessor {

    protected String TAG = this.getClass().getSimpleName();
    private Context context;
    //protected MessageDaoUtils messageDaoUtils;
    protected Object lock = new Object();

    public GProcessor(Context context) {
        this.context = context;
        //this.messageDaoUtils = new MessageDaoUtils(context);
    }

    public Context getContext() {
        return context;
    }

    public void execute(NeulinkTopicParser.Topic topic, String payload) {
        payload = auth(topic,payload);
        //检查当前请求是否已经已经到达过
        synchronized (lock){
//            Message msg = query(topic.getReqId());
//            if (msg != null &&
//                    (!"blib".equalsIgnoreCase(topic.getBiz()) ||//非目标库批量同步
//                            ("blib".equalsIgnoreCase(topic.getBiz()) &&
//                                    Message.STATUS_SUCCESS.equalsIgnoreCase(msg.getStatus())//成功的目标库同步操作直接返回
//                            )
//                    )
//            ) {
//                resLstRsl2Cloud(msg);
//                return;
//            }

            long id = 0;
            Req t = null;
            try {
//                if (msg == null) {
//                    msg = insert(topic, payload);
//                }
//                id = msg.getId();
                long reqTime = DatesUtil.getNowTimeStamp();//msg.getReqtime();
                t = parser(payload);
                t.setReqtime(reqTime);
                T result = process(topic, t);
                Res res = responseWrapper(t, result);
                String jsonStr = JSonUtils.toString(res);
//                if (res.getCode() == 200){//支撑多包批处理，所有包处理成功才叫做成功
//                    update(id, Message.STATUS_SUCCESS, res.getMsg());
//                }
//                else {
//                    update(id, Message.STATUS_FAIL, res.getMsg());//支撑多包批处理，当某个包处理失败的断点续传机制
//                }
                resLstRsl2Cloud(topic, jsonStr);
            }
            catch(NeulinkException ex){
                //update(id,Message.STATUS_FAIL, ex.getMessage());
                Res res = fail(t, ex.getCode(),ex.getMsg());
                String jsonStr = JSonUtils.toString(res);
                resLstRsl2Cloud(topic, jsonStr);
            }
            catch (Throwable ex) {
                Log.e(TAG,"execute",ex);
                //update(id,Message.STATUS_FAIL, ex.getMessage());
                Res res = fail(t, ex.getMessage());
                String jsonStr = JSonUtils.toString(res);
                resLstRsl2Cloud(topic, jsonStr);
            }
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

//    public Message insert(NeulinkTopicParser.Topic topic, String payload) {
//
//        Message msg = new Message();
//
//        msg.setReqId(topic.getReqId());
//
//        msg.setReqtime(DatesUtil.getNowTimeStamp());
//
//        msg.setRestime(DatesUtil.getNowTimeStamp());
//
//        msg.setTopic(topic.toString());
//
//        msg.setStatus(getStatus());
//
//        msg.setPayload(payload);
//
//        messageDaoUtils.insertMessage(msg);
//
//        return msg;
//    }

    /**
     *
     * @param id
     * @param status
     * @param msg
     */
//    public void update(long id, String status, String msg) {
//        messageDaoUtils.update(id,status,msg);
//    }

    /**
     *
     * @param id
     * @param offset
     * @param status
     * @param msg
     */
//    public void updatePkg(long id, long offset,String status, String msg) {
//        messageDaoUtils.updatePkg(id,offset,status,msg);
//    }

//    public Message query(String reqId) {
//        List<Message> messages = messageDaoUtils.queryReqId(reqId, 0);
//        if (messages != null && messages.size() > 0) {
//            return messages.get(0);
//        }
//        return null;
//    }

    /**
     * 根据requestId查询历史记录，如果已经执行完成了则直接返回执行结果
     * 把最后执行结果返回给到云端
     * @param message
     */
//    protected void resLstRsl2Cloud(Message message){
//        String topicStr = message.getTopic();
//        NeulinkTopicParser.Topic topic = NeulinkTopicParser.getInstance().parser(topicStr,message.getQos());
//        String resTopic = resTopics.get(topic.getBiz());
//        NeulinkService.getInstance().publishMessage(resTopic,topic.getVersion(),topic.getReqId(),message.getPayload(),message.getQos());
//    }

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

    /**
     * 命令消息到达是日志状态默认为处理中，只有无法响应的请求【eg:reboot】消息到达是日志记录的状态是成功
     *
     * @return
     */
//    protected String getStatus() {
//        return Message.STATUS_PROCESS;
//    }

    /**
     * neulink协议处理
     * @param topic
     * @param payload
     * @return
     */
    public abstract T process(NeulinkTopicParser.Topic topic, Req payload);

    /**
     * 解析处理
     * @param payload
     * @return
     */
    public abstract Req parser(String payload);

    protected abstract Res responseWrapper(Req t, T result);

    protected abstract Res fail(Req t, String error);

    protected abstract Res fail(Req t,int code, String error);

    protected abstract String resTopic();

    protected abstract ICmdListener getListener();
}

