package com.alfredbase.javabean;

import java.io.Serializable;

public class OrderUser implements Serializable {

    private Integer id;
    private Integer orderUserId;
    private Integer userId;
    private Integer orderId;
    private Long businessDate;
    private String transactionAmount;
    private Long createTime;
    private Long updateTime;
    public OrderUser(){}
    public OrderUser (Integer id, Integer orderUserId, Integer userId, Integer orderId,
                      Long businessDate, String transactionAmount, Long createTime, Long updateTime) {
        super();
        this.id = id;
        this.orderUserId = orderUserId;
        this.userId = userId;
        this.orderId = orderId;
        this.businessDate = businessDate;
        this.transactionAmount = transactionAmount;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderUserId() {
        return orderUserId;
    }

    public void setOrderUserId(Integer orderUserId) {
        this.orderUserId = orderUserId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Long getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(Long businessDate) {
        this.businessDate = businessDate;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
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
        return "Order{" +
                "id=" + id +
                ", orderUserId=" + orderUserId +
                ", userId=" + userId +
                ", orderId=" + orderId +
                ", businessDate=" + businessDate +
                ", transactionAmount='" + transactionAmount + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime  +
                "}";
    }

}
