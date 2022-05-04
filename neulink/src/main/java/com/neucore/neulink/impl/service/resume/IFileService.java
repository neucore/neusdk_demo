package com.neucore.neulink.impl.service.resume;

import java.util.Map;

public interface IFileService {
    public void update(String url, int thid,long pos);
    public Map<Integer, Long> getData(String path);
    public void save(String path,  Map<Integer, Long> thids);
    public void delete(String path);
}
