package com.neucore.neulink.impl.registry;

import com.neucore.neulink.IDownloder;
import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.IMessageService;
import com.neucore.neulink.impl.service.device.DefaultDeviceServiceImpl;
import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.IFileService;
import com.neucore.neulink.impl.down.http.HttpDownloader;

public final class ServiceRegistry {

    private static ServiceRegistry instance = new ServiceRegistry();

    public static ServiceRegistry getInstance(){
        return instance;
    }
    @Deprecated
    private ILoginCallback loginCallback;
    @Deprecated
    public void setLoginCallback(ILoginCallback loginCallback) {
        this.loginCallback = loginCallback;
    }
    @Deprecated
    public ILoginCallback getLoginCallback() {
        return loginCallback;
    }

    private IMessageService messageService;
    public void setMessageService(IMessageService messageService){
        this.messageService = messageService;
    }

    public IMessageService getMessageService() {
        return messageService;
    }

    private IDeviceService defaultDeviceService = new DefaultDeviceServiceImpl();
    private IDeviceService deviceService = null;
    public IDeviceService getDeviceService() {
        return deviceService ==null?defaultDeviceService:deviceService;
    }

    public void setDeviceService(IDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    private IDownloder defaultDownloder = new HttpDownloader();
    private IDownloder downloder = null;

    public IDownloder getDownloder() {
        return downloder==null?defaultDownloder:downloder;
    }

    public void setDownloder(IDownloder downloder) {
        this.downloder = downloder;
    }
}
