package com.neucore.neulink.extend;

import com.neucore.neulink.IStorage;
import com.neucore.neulink.cfg.ConfigContext;
import com.neucore.neulink.storage.FTPStorage;
import com.neucore.neulink.storage.OSSStorage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StorageFactory {


    public final static String STORAGE_OSS = "OSS";

    public final static String STORAGE_FTP = "FTP";

    public final static String STORAGE_AWS = "AWS";

    private static Map<String,IStorage> storages = new ConcurrentHashMap<String, IStorage>();

    public static IStorage getInstance(){

        synchronized (StorageFactory.class){
            String type = ConfigContext.getInstance().getConfig("Storage.Type","OSS");
            if(STORAGE_OSS.equalsIgnoreCase(type) && !storages.containsKey(type.toUpperCase())){
                storages.put(type.toUpperCase(),new OSSStorage());
            }
            else if (STORAGE_FTP.equalsIgnoreCase(type) && !storages.containsKey(type.toUpperCase())){
                storages.put(type.toUpperCase(),new FTPStorage());
            }
//            else if (STORAGE_AWS.equalsIgnoreCase(type) && !storages.containsKey(type.toUpperCase())){
//                storages.put(type.toUpperCase(),new AwsStorage());
//            }
            if(!storages.containsKey(type.toUpperCase())) {
                throw new RuntimeException(type+" 存储类型不支持");
            }
            return storages.get(type.toUpperCase());
        }
    }
}
