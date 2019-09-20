package com.alfredbase.javabean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Arif S. on 2019-09-20
 * class to store price pattern
 */
public class ItemDetailPrice {
    private int id;
    private int itemId;
    @SerializedName("paraValue1")
    private int salesTypeId;
    private int taxId;
    private int paraType;
    private double itemPrice;
    private long createTime;
    private long updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getSalesTypeId() {
        return salesTypeId;
    }

    public void setSalesTypeId(int salesTypeId) {
        this.salesTypeId = salesTypeId;
    }

    public int getTaxId() {
        return taxId;
    }

    public void setTaxId(int taxId) {
        this.taxId = taxId;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getParaType() {
        return paraType;
    }

    public void setParaType(int paraType) {
        this.paraType = paraType;
    }

    @Override
    public String toString() {
        return "ItemDetailPrice{" +
                "id=" + id +
                ", itemId=" + itemId +
                ", salesTypeId=" + salesTypeId +
                ", taxId=" + taxId +
                ", paraType=" + paraType +
                ", itemPrice=" + itemPrice +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
