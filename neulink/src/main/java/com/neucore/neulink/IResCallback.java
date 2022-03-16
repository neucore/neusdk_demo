package com.neucore.neulink;

import com.neucore.neulink.extend.Result;

public interface IResCallback<T extends Result> {
    Class<T> getResultType();
    void onFinished(T result);
}
