package com.neucore.neulink.impl;

import com.neucore.neulink.IStorage;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.service.storage.MyFTPStorage;
import com.neucore.neulink.impl.service.storage.OSSStorage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StorageFactory {

    private static Map<String,IStorage> storages = new ConcurrentHashMap<String, IStorage>();

    private volatile static StorageFactory instance ;

    public static StorageFactory getInstance(){
        if (instance == null){
            synchronized (StorageFactory.class){
                if (instance == null){
                    instance = new StorageFactory();
                }
            }
        }
        return instance;
    }

    public IStorage create(){
        String type = ConfigContext.getInstance().getConfig("Storage.Type","OSS");
        return getInstance(type);
    }

    private IStorage getInstance(String type){
        synchronized (storages) {
            String typeKey = type.toUpperCase();
            if (ConfigContext.STORAGE_OSS.equalsIgnoreCase(type)
                    && !storages.containsKey(typeKey)) {
                storages.put(typeKey, new OSSStorage());
            } else if (ConfigContext.STORAGE_FTP.equalsIgnoreCase(type)
                    && !storages.containsKey(typeKey)) {
                storages.put(typeKey, new MyFTPStorage());
            } else if (ConfigContext.STORAGE_MYFTP.equalsIgnoreCase(type)
                    && !storages.containsKey(typeKey)) {
                storages.put(typeKey, new MyFTPStorage());
            }
            if (!storages.containsKey(type.toUpperCase())) {
                throw new RuntimeException(type + " 存储类型不支持");
            }
            return storages.get(typeKey);
        }
    }
}
