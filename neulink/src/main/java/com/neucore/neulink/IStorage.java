package com.neucore.neulink;

public interface IStorage extends NeulinkConst {

    String uploadBak(String path,String requestId,int index);

    String uploadQData(String path,String requestId,int index);

    String uploadLog(String path,String requestId,int index);

    String uploadImage(String path,String requestId,int index);

}
