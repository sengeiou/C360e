package com.alfredbase.javabean;



import java.io.Serializable;

/**
 * TODO :
 * Param :
 *
 *
 * @ return :
 */
public class PromotionData implements Serializable {

    private Integer id;
    private Integer promotionId;
    private String promotionName;
    private Integer promotionType;// 0 item  1 order
    private String promotionAmount;
    private String discountPercentage;
    private Integer itemId;
    private String itemName;
    private Integer freeNum;
    private Integer freeItemId;
    private String freeItemName;
    private long createTime;
    private long updateTime;
     private Integer orderId;
     private Integer orderDetailId;
    private String discountPrice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public Integer getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(Integer promotionType) {
        this.promotionType = promotionType;
    }

    public String getPromotionAmount() {
        return promotionAmount;
    }

    public void setPromotionAmount(String promotionAmount) {
        this.promotionAmount = promotionAmount;
    }

    public String getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(String discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getFreeItemId() {
        return freeItemId;
    }

    public void setFreeItemId(Integer freeItemId) {
        this.freeItemId = freeItemId;
    }

    public String getFreeItemName() {
        return freeItemName;
    }

    public void setFreeItemName(String freeItemName) {
        this.freeItemName = freeItemName;
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

    public Integer getFreeNum() {
        return freeNum;
    }

    public void setFreeNum(Integer freeNum) {
        this.freeNum = freeNum;
    }

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(Integer orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    @Override
    public String toString() {
        return "PromotionData{" +
                "id=" + id +
                ", promotionId=" + promotionId +
                ", promotionName='" + promotionName + '\'' +
                ", promotionType=" + promotionType +
                ", promotionAmount='" + promotionAmount + '\'' +
                ", discountPercentage='" + discountPercentage + '\'' +
                ", itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", freeNum=" + freeNum +
                ", freeItemId=" + freeItemId +
                ", freeItemName='" + freeItemName + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", orderId=" + orderId +
                ", orderDetailId=" + orderDetailId +
                ", discountPrice='" + discountPrice + '\'' +
                '}';
    }
}
