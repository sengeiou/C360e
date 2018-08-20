package com.alfredbase.javabean;

public class MultiReportRelation {
    private int id;
    private int mainReportId;
    private int subReportId;
    private int subPosBeanId;
    private long subReportCreateTime;

    public MultiReportRelation() {
    }

    public MultiReportRelation(int mainReportId, int subReportId, int subPosBeanId, long subReportCreateTime) {
        this.mainReportId = mainReportId;
        this.subReportId = subReportId;
        this.subPosBeanId = subPosBeanId;
        this.subReportCreateTime = subReportCreateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMainReportId() {
        return mainReportId;
    }

    public void setMainReportId(int mainReportId) {
        this.mainReportId = mainReportId;
    }

    public int getSubReportId() {
        return subReportId;
    }

    public void setSubReportId(int subReportId) {
        this.subReportId = subReportId;
    }

    public int getSubPosBeanId() {
        return subPosBeanId;
    }

    public void setSubPosBeanId(int subPosBeanId) {
        this.subPosBeanId = subPosBeanId;
    }

    public long getSubReportCreateTime() {
        return subReportCreateTime;
    }

    public void setSubReportCreateTime(long subReportCreateTime) {
        this.subReportCreateTime = subReportCreateTime;
    }

    @Override
    public String toString() {
        return "MultiReportRelation{" +
                "id=" + id +
                ", mainReportId=" + mainReportId +
                ", subReportId=" + subReportId +
                ", subPosBeanId=" + subPosBeanId +
                ", subReportCreateTime=" + subReportCreateTime +
                '}';
    }
}
