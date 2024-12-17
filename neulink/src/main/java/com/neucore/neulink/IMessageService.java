package com.neucore.neulink;

public interface IMessageService extends IService {
    IMessage queryByReqNo(String reqNo);
    IMessage save(String reqNo,String headers, String payload);
    void update(Long id,String status,String payload);
    void updatePkg(Long id, long offset,String status, String payload);
}
