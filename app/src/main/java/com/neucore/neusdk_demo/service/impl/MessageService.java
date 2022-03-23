package com.neucore.neusdk_demo.service.impl;

import android.content.Context;

import com.neucore.neulink.IMessageService;
import com.neucore.neulink.impl.IMessage;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.DatesUtil;
import com.neucore.neusdk_demo.service.db.MessageDaoUtils;
import com.neucore.neusdk_demo.service.db.bean.Message;

import java.util.List;

public class MessageService implements IMessageService {
    private MessageDaoUtils messageDaoUtils;

    public MessageService(Context context){
        messageDaoUtils = new MessageDaoUtils(context);
    }

    @Override
    public IMessage queryByReqNo(String reqNo) {
        List<Message> messages = messageDaoUtils.queryReqId(reqNo, 0);
        if (messages != null && messages.size() > 0) {
            return messages.get(0);
        }
        return null;
    }

    @Override
    public IMessage save(NeulinkTopicParser.Topic topic, String payload) {
        Message msg = new Message();

        msg.setReqId(topic.getReqId());

        msg.setReqtime(DatesUtil.getNowTimeStamp());

        msg.setRestime(DatesUtil.getNowTimeStamp());

        msg.setTopic(topic.toString());

        msg.setStatus(IMessage.STATUS_PROCESS);

        msg.setPayload(payload);

        messageDaoUtils.insertMessage(msg);

        return msg;
    }

    @Override
    public void update(Long id, String status, String msg) {
        messageDaoUtils.update(id,status,msg);
    }

    @Override
    public void updatePkg(long id, long offset, String status, String msg) {
        messageDaoUtils.updatePkg(id,offset,status,msg);
    }
}
