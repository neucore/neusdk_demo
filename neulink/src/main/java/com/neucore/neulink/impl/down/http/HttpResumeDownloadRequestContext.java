package com.neucore.neulink.impl.down.http;

import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.util.FileUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.hutool.core.util.ObjectUtil;

public class HttpResumeDownloadRequestContext implements NeulinkConst {

    private static final String TAG = TAG_PREFIX+"DownloadContext";

    private String reqNo;
    private Integer taskNum;
    private Map<Integer,Long> blocksData = new ConcurrentHashMap<>();;
    private String storeDir;
    public HttpResumeDownloadRequestContext(String storeDir, String reqNo, Integer taskNum){
        this.storeDir = storeDir;
        this.reqNo = reqNo;
        this.taskNum = taskNum;
        File file = new File(String.format("%s/tmp",storeDir));
        file.mkdirs();
    }

    public Map<Integer,Long> init(){
        for (int id=0;id<taskNum;id++){
            File file = new File(String.format("%s/tmp/%s.block",storeDir,id));
            if(file.exists()){
                ObjectInputStream ois = null;
                try {
                    ois = new ObjectInputStream(new FileInputStream(file));

                    Long size = (Long)ois.readObject();
                    NeuLogUtils.iTag(TAG, String.format("已经下载的长度 %s",size));
                    blocksData.put(id,size);
                } catch (Exception e) {
                }
                finally {
                    if(ObjectUtil.isNotEmpty(ois)){
                        try {
                            ois.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
            else{
                store(id,0L);
            }
        }
        return blocksData;
    }

    public void store(Integer taskId,Long size){
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;
        try{
            File file = new File(String.format("%s/tmp/%s.block",storeDir,taskId));
            file.createNewFile();
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(size);
            FileDescriptor fd = fos.getFD();
            fd.sync();
            blocksData.put(taskId,size);
        }
        catch (IOException e) {
        }
        finally {
            if(ObjectUtil.isNotEmpty(oos)){
                try {
                    oos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public Long getSize(Integer id){
        return blocksData.get(id);
    }

    public void clear(){
        FileUtils.deleteDirectory(String.format("%s/tmp",storeDir));
    }
}
