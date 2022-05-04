package com.neucore.neulink.extend;

import com.neucore.neulink.ILoginCallback;
import com.neucore.neulink.IMessageService;
import com.neucore.neulink.impl.service.device.DeviceServiceImpl;
import com.neucore.neulink.impl.service.device.IDeviceService;
import com.neucore.neulink.impl.service.resume.IFileService;

public class ServiceRegistrator {

    private static ServiceRegistrator instance = new ServiceRegistrator();

    public static ServiceRegistrator getInstance(){
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
        return deviceService;
    }

    public void setDeviceService(IDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    private IDeviceService deviceService = new DeviceServiceImpl();

    public IFileService getFileService() {
        return fileService;
    }

    public void setFileService(IFileService fileService) {
        this.fileService = fileService;
    }

    private IFileService fileService;
}
