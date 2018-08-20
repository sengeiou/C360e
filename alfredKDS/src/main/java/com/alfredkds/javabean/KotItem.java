package com.alfredbase.javabean.model;

import java.io.Serializable;
public class KotItem implements Serializable {

    private int id;
    private String itemDetailName;
    private String itemDetail;
    private String itemModName;
    private String tableName;
    private Integer callType;
    private Integer kotStatus;
    private Long createTime;
    private Long updateTime;
    private  Integer orderNo;
    private Integer summaryId;
    private Integer qty;
    private Integer itemDetailId;

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Integer getItemDetailId() {
        return itemDetailId;
    }

    public void setItemDetailId(Integer itemDetailId) {
        this.itemDetailId = itemDetailId;
    }

    public Integer getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(Integer summaryId) {
        this.summaryId = summaryId;
    }

    public Integer getCallType() {
        return callType;
    }

    public void setCallType(Integer callType) {
        this.callType = callType;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemDetailName() {
        return itemDetailName;
    }

    public void setItemDetailName(String itemDetailName) {
        this.itemDetailName = itemDetailName;
    }

    public String getItemDetail() {
        return itemDetail;
    }

    public void setItemDetail(String itemDetail) {
        this.itemDetail = itemDetail;
    }

    public String getItemModName() {
        return itemModName;
    }

    public void setItemModName(String itemModName) {
        this.itemModName = itemModName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    public Integer getKotStatus() {
        return kotStatus;
    }

    public void setKotStatus(Integer kotStatus) {
        this.kotStatus = kotStatus;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }


    @Override
    public String toString() {
        return "KotItem{" +
                "id=" + id +
                ", itemDetailName='" + itemDetailName + '\'' +
                ", itemDetail='" + itemDetail + '\'' +
                ", itemModName='" + itemModName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", callType=" + callType +
                ", kotStatus=" + kotStatus +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", orderNo=" + orderNo +
                ", summaryId=" + summaryId +
                '}';
    }
}
