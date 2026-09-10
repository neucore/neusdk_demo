package com.neucore.neulink;

public interface ILoginCallback {
    LoginUser login();
    LoginUser refresh(String refreshToken);
}
