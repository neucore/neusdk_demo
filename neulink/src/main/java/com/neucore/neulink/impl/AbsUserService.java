package com.neucore.neulink.impl;

import com.neucore.neulink.IUserService;

public abstract class AbsUserService implements IUserService {

    @Override
    public void onChanged(){
        load();
    }
}
