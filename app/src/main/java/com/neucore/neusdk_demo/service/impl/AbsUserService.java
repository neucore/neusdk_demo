package com.neucore.neusdk_demo.service.impl;

import com.neucore.neusdk_demo.service.IUserService;

public abstract class AbsUserService implements IUserService {

    @Override
    public void onChanged(){
        load();
    }
}
