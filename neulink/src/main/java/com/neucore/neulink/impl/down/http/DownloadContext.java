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

import cn.hutool.core.util.ObjectUtil;

public class DownloadContext implements NeulinkConst {

    private static final String TAG = TAG_PREFIX+"DownloadContext";

    private String reqNo;
    private Integer threadNum;
    private Map<Integer,Long> blocksData = new HashMap<>();;
    private String storeDir;
    public DownloadContext(String storeDir,String reqNo,Integer threadNum){
        this.storeDir = storeDir;
        this.reqNo = reqNo;
        this.threadNum = threadNum;
        File file = new File(String.format("%s/tmp",storeDir));
        file.mkdirs();
    }

    public Map<Integer,Long> init(){
        for (int i=1;i<threadNum+1;i++){
            File file = new File(String.format("%s/tmp/%s.block",storeDir,i));
            if(file.exists()){
                ObjectInputStream ois = null;
                try {
                    ois = new ObjectInputStream(new FileInputStream(file));

                    Long size = (Long)ois.readObject();
                    NeuLogUtils.iTag(TAG, String.format("已经下载的长度 %s",size));
                    blocksData.put(i,size);
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
                blocksData.put(i,0L);
                store(i,0L);
            }
        }
        return blocksData;
    }

    public void store(Integer block,Long size){
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;
        try{
            File file = new File(String.format("%s/tmp/%s.block",storeDir,block));
            file.createNewFile();
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(size);
            FileDescriptor fd = fos.getFD();
            fd.sync();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(ObjectUtil.isNotEmpty(fos)){
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
            if(ObjectUtil.isNotEmpty(oos)){
                try {
                    oos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public Map<Integer, Long> getBlocksData() {
        return blocksData;
    }

    public void clear(){
        FileUtils.deleteDirectory(String.format("%s/tmp",storeDir));
    }
}
