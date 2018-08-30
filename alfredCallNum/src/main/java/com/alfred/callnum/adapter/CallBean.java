package com.alfred.callnum.adapter;

import java.io.Serializable;

public class CallBean implements Serializable {

    private  String  callNumber;
    private  int  callType;
    private  int  callTag;
    private  boolean isUpdate = false;

    public CallBean(String callNumber, int callType, int callTag) {
        this.callNumber = callNumber;
        this.callType = callType;
        this.callTag = callTag;
    }

    public CallBean() {
    }

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

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    @Override
    public String toString() {
        return "CallBean{" +
                "callNumber='" + callNumber + '\'' +
                ", callType=" + callType +
                ", callTag=" + callTag +
                ", isUpdate=" + isUpdate +
                '}';
    }
}
