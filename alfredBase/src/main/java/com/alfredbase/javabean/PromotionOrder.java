package com.alfredbase.javabean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * TODO :
 * Param :
 * Created by yangk on 2019/2/8.
 *
 * @ return :
 */
public class PromotionOrder implements Serializable {


    private static final long serialVersionUID = -6709227918145280458L;

    private Integer id;

    private Integer promotionId;

    private String discountPrice;

    private String discountPercentage;

    private long createTime;

    private long updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(String discountPercentage) {
        this.discountPercentage = discountPercentage;
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

    @Override
    public String toString() {
        return "PromotionOrder{" +
                "id=" + id +
                ", promotionId=" + promotionId +
                ", discountPrice='" + discountPrice + '\'' +
                ", discountPercentage='" + discountPercentage + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
