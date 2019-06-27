package com.alfredbase.javabean;

import java.io.Serializable;

public class ReportPromotion implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2676222687023136761L;
	private Integer id;
	private Integer restaurantId;
	private Integer revenueId;
	private String revenueName;
	private Long businessDate;

	private Integer amountQty;
	private String amountPromotion;
	private String promotionName;
	private Integer promotionId;
	private int daySalesId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getRevenueName() {
		return revenueName;
	}

	public void setRevenueName(String revenueName) {
		this.revenueName = revenueName;
	}

	public Long getBusinessDate() {
		return businessDate;
	}

	public void setBusinessDate(Long businessDate) {
		this.businessDate = businessDate;
	}

	public Integer getAmountQty() {
		return amountQty;
	}

	public void setAmountQty(Integer amountQty) {
		this.amountQty = amountQty;
	}

	public String getAmountPromotion() {
		return amountPromotion;
	}

	public void setAmountPromotion(String amountPromotion) {
		this.amountPromotion = amountPromotion;
	}

	public String getPromotionName() {
		return promotionName;
	}

	public void setPromotionName(String promotionName) {
		this.promotionName = promotionName;
	}

	public Integer getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Integer promotionId) {
		this.promotionId = promotionId;
	}

	public int getDaySalesId() {
		return daySalesId;
	}

	public void setDaySalesId(int daySalesId) {
		this.daySalesId = daySalesId;
	}

	@Override
	public String toString() {
		return "ReportPromotion{" +
				"id=" + id +
				", restaurantId=" + restaurantId +
				", revenueId=" + revenueId +
				", revenueName='" + revenueName + '\'' +
				", businessDate=" + businessDate +
				", amountQty=" + amountQty +
				", amountPrice='" + amountPromotion + '\'' +
				", promotionName='" + promotionName + '\'' +
				", promotionId=" + promotionId +
				", daySalesId=" + daySalesId +
				'}';
	}
}
