package com.cmhi.downloadtools.bean;

public class QueueEventMsg {
    private int type;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public QueueEventMsg(int type) {
        this.type = type;
    }

    public QueueEventMsg(int type, String msg) {
        this.type = type;
        this.msg = msg;
    }

}