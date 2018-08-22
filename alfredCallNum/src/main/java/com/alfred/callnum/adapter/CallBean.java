package com.alfred.callnum.adapter;

import java.io.Serializable;

public class CallBean implements Serializable {

    private  String  callNumber;
    private  int  callType;
    private  int  callTag;

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public int getCallTag() {
        return callTag;
    }

    public void setCallTag(int callTag) {
        this.callTag = callTag;
    }

    private int id;
    private String name;
    private  int type;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    @Override
    public String toString() {
        return "CallBean{" +
                "callNumber='" + callNumber + '\'' +
                ", callType=" + callType +
                ", callTag=" + callTag +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
