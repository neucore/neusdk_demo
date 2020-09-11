package com.neucore.neusdk_demo.utils;

import android.graphics.Rect;

import java.util.List;

public class NCModeSelectEvent<T> {

    private int mode;
    private T message;
    private Rect rect;
    private List list;

    public int getMode(){
        return mode;
    }

    public void setMode(int mode){
        this.mode = mode;
    }

    public T getMessage(){
        return message;
    }

    public void setMessage(T message){
        this.message = message;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
}
