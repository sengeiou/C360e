package com.alfredbase.javabean;

/**
 * Created by Alex on 16/9/22.
 */

public class TableInfo {
    /*
    posId, name, imageName, restaurantId, revenueId,xAxis, yAxis, placesId, resolution, shape, type,
    status, isDecorate, unionId, isActive, createTime, updateTime
     */
    private Integer posId;
    private String name;
    private String imageName;
    private Integer restaurantId;
    private Integer revenueId;
    private String xAxis;
    private String yAxis;
    private Integer placesId;
    private Integer resolution;
    private Integer shape;
    private Integer type;// 桌子图片类型(编号)
    private Integer status;
    private Integer isDecorate;
    private String unionId;
    private Integer isActive;
    private Long createTime;
    private Long updateTime;
    private Integer packs;
    private Integer rotate;
    //calculate the total orders that table has currently.
	private Integer orders = 0;

    private Integer isKiosk = 0;

    public Integer getPosId() {
        return posId;
    }

    public void setPosId(Integer posId) {
        this.posId = posId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Integer getRevenueId() {
        return revenueId;
    }

    public void setRevenueId(Integer revenueId) {
        this.revenueId = revenueId;
    }

    public String getxAxis() {
        return xAxis;
    }

    public void setxAxis(String xAxis) {
        this.xAxis = xAxis;
    }

    public String getyAxis() {
        return yAxis;
    }

    public void setyAxis(String yAxis) {
        this.yAxis = yAxis;
    }

    public Integer getPlacesId() {
        return placesId;
    }

    public void setPlacesId(Integer placesId) {
        this.placesId = placesId;
    }

    public Integer getResolution() {
        return resolution;
    }

    public void setResolution(Integer resolution) {
        this.resolution = resolution;
    }

    public Integer getShape() {
        return shape;
    }

    public void setShape(Integer shape) {
        this.shape = shape;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsDecorate() {
        return isDecorate;
    }

    public void setIsDecorate(Integer isDecorate) {
        this.isDecorate = isDecorate;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
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

    public Integer getPacks() {
        return packs;
    }

    public void setPacks(Integer packs) {
        this.packs = packs;
    }

    public Integer getRotate() {
        return rotate;
    }

    public void setRotate(Integer rotate) {
        this.rotate = rotate;
    }

    public Integer getOrders() {
        return orders;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
    }

    public Integer getIsKiosk() {
        return isKiosk;
    }

    public void setIsKiosk(Integer isKiosk) {
        this.isKiosk = isKiosk;
    }

    @Override
    public String toString() {
        return "TableInfo{" +
                "posId=" + posId +
                ", name='" + name + '\'' +
                ", imageName='" + imageName + '\'' +
                ", restaurantId=" + restaurantId +
                ", revenueId=" + revenueId +
                ", xAxis='" + xAxis + '\'' +
                ", yAxis='" + yAxis + '\'' +
                ", placesId=" + placesId +
                ", resolution=" + resolution +
                ", shape=" + shape +
                ", type=" + type +
                ", status=" + status +
                ", isDecorate=" + isDecorate +
                ", unionId='" + unionId + '\'' +
                ", isActive=" + isActive +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", packs=" + packs +
                ", rotate=" + rotate +
                ", orders=" + orders +
                ", isKiosk=" + isKiosk +
                '}';
    }
}
