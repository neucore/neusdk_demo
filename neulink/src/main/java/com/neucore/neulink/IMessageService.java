package com.neucore.neulink;

import com.neucore.neulink.impl.IMessage;
import com.neucore.neulink.impl.NeulinkTopicParser;

public interface IMessageService extends IService {
    IMessage queryByReqNo(String reqNo);
    IMessage save(NeulinkTopicParser.Topic topic, String payload);
    void update(Long id,String status,String message);
    void updatePkg(long id, long offset,String status, String msg);
}
