package com.neucore.neulink;

import com.neucore.neulink.impl.NeulinkTopicParser;

import cn.hutool.json.JSONObject;

public interface IMessageService extends IService {
    IMessage queryByReqNo(String reqNo);
    IMessage save(String reqNo,NeulinkTopicParser.Topic topic, String headers, String payload);
    void update(Long id,String status,String payload);
    void updatePkg(long id, long offset,String status, String payload);
}
