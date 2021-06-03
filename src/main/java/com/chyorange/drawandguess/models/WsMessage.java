package com.chyorange.drawandguess.models;

public class WsMessage {
    public static final int USER_READY = 201;
    public static final int    USER_CANCEL_READY = 202;

    public static final int ROOM_READY = 301;
    public static final int ROOM_WAIT = 302;

    public static final int SERVER_NOTIFY = 101;;

    int action;
    String data;
    String msg;

    String fromUserId;

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
