package com.neucore.neulink;

public class NeulinkException extends RuntimeException implements NeulinkConst {

    private int code;
    private String msg;
    public NeulinkException(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
    public NeulinkException(int code, String msg, Throwable throwable){
        super(throwable);
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
