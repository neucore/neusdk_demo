package com.neucore.neulink;

import java.util.Map;

public interface IFileService {
    void update(String url, int thid,long pos);
    Map<Integer, Long> getData(String path);
    void save(String path,  Map<Integer, Long> thids);
    void delete(String path);
}
