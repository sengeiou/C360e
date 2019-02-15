package com.alfredbase.javabean;



import java.io.Serializable;
import java.util.List;

/**
 * TODO :
 * Param :
 * Created by yangk on 2019/2/8.
 *
 * @ return :
 */
public class PromotionAndWeekVo implements Serializable {


    private Integer id;

    private Integer type;

    private String promotionName;

    private Integer restaurantId;

    private Integer isActive;

    private List<PromotionWeek> promotionWeekList;

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

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<PromotionWeek> getPromotionWeekList() {
        return promotionWeekList;
    }

    public void setPromotionWeekList(List<PromotionWeek> promotionWeekList) {
        this.promotionWeekList = promotionWeekList;
    }

    @Override
    public String toString() {
        return "PromotionAndWeekVo{" +
                "id=" + id +
                ", type=" + type +
                ", promotionName='" + promotionName + '\'' +
                ", restaurantId=" + restaurantId +
                ", isActive=" + isActive +
                ", promotionWeekList=" + promotionWeekList +
                '}';
    }
}
