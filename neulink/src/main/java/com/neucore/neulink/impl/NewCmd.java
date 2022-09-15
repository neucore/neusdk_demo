package com.neucore.neulink.impl;

import com.google.gson.annotations.SerializedName;

public class NewCmd<T> extends GCmd{
    @SerializedName("data")
    private T data;

    public T getData(){
        return data;
    }

    public void setData(T data){
        this.data = data;
    }
}
