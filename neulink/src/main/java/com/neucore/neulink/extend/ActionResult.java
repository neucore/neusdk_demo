package com.neucore.neulink.extend;

import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.util.IActionResult;

public class ActionResult<T> implements IActionResult,NeulinkConst {
    private int code=200;
    private String message = MESSAGE_SUCCESS;
    private T data;
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}