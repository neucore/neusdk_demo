package com.neucore.neulink.impl.registry;

import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.IMessageService;
import com.neucore.neulink.impl.service.device.DefaultDeviceServiceImpl;
import com.neucore.neulink.IDeviceService;
import com.neucore.neulink.impl.service.resume.IFileService;

public class ServiceRegistry {

    private static ServiceRegistry instance = new ServiceRegistry();

    public static ServiceRegistry getInstance(){
        return instance;
    }

    private ILoginCallback loginCallback;

    public void setLoginCallback(ILoginCallback loginCallback) {
        this.loginCallback = loginCallback;
    }

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

    public IDeviceService getDeviceService() {
        return deviceService ;
    }

    public void setDeviceService(IDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    private IDeviceService deviceService = new DefaultDeviceServiceImpl();

    public IFileService getFileService() {
        return fileService;
    }

    public void setFileService(IFileService fileService) {
        this.fileService = fileService;
    }

    private IFileService fileService;
}
