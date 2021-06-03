package com.chyorange.drawandguess.models;

public class BaseResponse <T>{

    public static final int BAD_PARAMS = 901;
    public static final int ERROR = 902;
    public static final int SUCCESS = 200;


    private int code;
    private String message;
    private T t;

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

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
