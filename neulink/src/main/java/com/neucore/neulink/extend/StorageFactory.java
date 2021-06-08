package com.neucore.neulink.extend;

import com.neucore.neulink.IStorage;
import com.neucore.neulink.cfg.ConfigContext;
import com.neucore.neulink.service.storage.MyFTPStorage;
import com.neucore.neulink.service.storage.OSSStorage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StorageFactory {

    private static Map<String,IStorage> storages = new ConcurrentHashMap<String, IStorage>();

    public static IStorage getInstance(){
        String type = ConfigContext.getInstance().getConfig("Storage.Type","OSS");
        return getInstance(type);
    }

    public static IStorage getInstance(String type){
        synchronized (storages) {
            if (ConfigContext.STORAGE_OSS.equalsIgnoreCase(type) && !storages.containsKey(type.toUpperCase())) {
                storages.put(type.toUpperCase(), new OSSStorage());
            } else if (ConfigContext.STORAGE_FTP.equalsIgnoreCase(type) && !storages.containsKey(type.toUpperCase())) {
                storages.put(type.toUpperCase(), new MyFTPStorage());
            } else if (ConfigContext.STORAGE_MYFTP.equalsIgnoreCase(type) && !storages.containsKey(type.toUpperCase())) {
                storages.put(type.toUpperCase(), new MyFTPStorage());
            }
            if (!storages.containsKey(type.toUpperCase())) {
                throw new RuntimeException(type + " 存储类型不支持");
            }
            return storages.get(type.toUpperCase());
        }
    }
}
