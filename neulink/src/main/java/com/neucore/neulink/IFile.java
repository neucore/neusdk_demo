package com.neucore.neulink;

public interface IFile {
    Long getId();
    void setId(Long id);
    String getUrl();
    void setUrl(String url);
    Integer getThid();
    void setThid(Integer thid);
    Long getProcessed();
    void setProcessed(Long processed);
    Integer getStatus();
    void setStatus(Integer status);
}
