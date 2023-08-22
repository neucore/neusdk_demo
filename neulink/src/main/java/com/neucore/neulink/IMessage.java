package com.neucore.neulink;

public interface IMessage {

    String STATUS_PROCESS = "运行中";
    String STATUS_SUCCESS = "成功";
    String STATUS_FAIL = "失败";

    Long getId();

    void setId(Long id);

    String getReqId();

    void setReqId(String reqId);

    String getTopic();

    void setTopic(String topic);

    long getOffset();

    void setOffset(long offset);

    void setHeaders(String headers);

    String getHeaders();

    String getPayload();

    void setPayload(String payload);

    String getPkgStatus();

    void setPkgStatus(String pkgStatus);

    String getStatus();

    void setStatus(String status);

    String getResult();

    void setResult(String result);

    Long getReqtime();

    void setReqtime(Long reqtime);

    Long getRestime();

    void setRestime(Long restime);

    int getQos();

    void setQos(int qos);

    boolean isRetained();

    void setRetained(boolean retained);
}
