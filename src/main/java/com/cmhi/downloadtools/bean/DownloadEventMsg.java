package com.cmhi.downloadtools.bean;

public class DownloadEventMsg {
    private int type;
    private String msg;
    private int arg1;
    private int arg2;
    private Object obj;

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

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

    public DownloadEventMsg(int type) {
        this.type = type;
    }

    public DownloadEventMsg(int type, String msg) {
        this.type = type;
        this.msg = msg;
    }
    public DownloadEventMsg(int type, String msg,Object obj) {
        this.type = type;
        this.msg = msg;
        this.obj = obj;
    }

    public DownloadEventMsg(int type, int arg1) {
        this.type = type;
        this.arg1 = arg1;
    }

    public DownloadEventMsg(int type, int arg1, int arg2) {
        this.type = type;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }
    public DownloadEventMsg(int type, int arg1, String msg) {
        this.type = type;
        this.arg1 = arg1;
        this.msg = msg;
    }

    public int getArg1() {
        return arg1;
    }

    public void setArg1(int arg1) {
        this.arg1 = arg1;
    }
}