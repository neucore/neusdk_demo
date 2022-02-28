package com.neucore.neulink.extend;

import com.neucore.neulink.IMessageService;
import com.neucore.neulink.IUserService;
import com.neucore.neulink.impl.service.device.DeviceServiceImpl;
import com.neucore.neulink.impl.service.device.IDeviceService;

public class ServiceFactory {

    private static ServiceFactory instance = new ServiceFactory();

    public static ServiceFactory getInstance(){
        return instance;
    }

    private ILoginCallback loginCallback;

    public void setLoginCallback(ILoginCallback loginCallback) {
        this.loginCallback = loginCallback;
    }

    public ILoginCallback getLoginCallback() {
        return loginCallback;
    }


    private IUserService userService;

    public IUserService getUserService() {
        return userService;
    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
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
}
