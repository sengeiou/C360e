package com.alfredbase.javabean;

import com.alfredbase.utils.CommonUtil;

import java.io.Serializable;

public class Modifier implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7402839723567924675L;

	private Integer id;

	private Integer restaurantId;

	/**
	 * 类型(0类型名称，1具体的食材)
	 */
	private Integer type;

	private Integer categoryId;

	private String categoryName;

	private String price;

	private String modifierName;

	

	/**
	 * Is it available (-1 delete, 0 disable, 1 normal)
	 */
	private Integer isActive;
	/**
	 * Whether it is default (0 is not default, 1 default)
	 */
	private Integer isDefault;
	
	/**
	 * The modifier used as the markup package can be obtained by the itemId.
	 */
	private Integer itemId;
	
	/**
	 * Whether it is the dish under the course (0 ingredients, 1 course)
	 */
	private int isSet;
	/**
	 * Used in the package
	 */
	private int qty;
	/**
	 * Select the default item, 0 does not select the rule 1 must select the default item 2 arbitrarily select a few
	 * Note: This field is used by the ModifierGroup.
	 */
	private int mustDefault;
	
	/**
	 * Any number of choices is useful when mustDefault is 2.
	 * Note: This field is used by the ModifierGroup.
	 */
	private int  optionQty;
	private int  minNumber;

	private int  maxNumber;

	public int getMinNumber() {
		return minNumber;
	}

	public void setMinNumber(int minNumber) {
		this.minNumber = minNumber;
	}

	public int getMaxNumber() {
		return maxNumber;
	}

	public void setMaxNumber(int maxNumber) {
		this.maxNumber = maxNumber;
	}

	public Integer getId() {
		if (CommonUtil.isNull(id))
			return 0;
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRestaurantId() {
		if (CommonUtil.isNull(restaurantId))
			return 0;
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

	public Integer getType() {
		if (CommonUtil.isNull(type))
			return 0;
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getCategoryId() {
		if (CommonUtil.isNull(categoryId))
			return 0;
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		if (CommonUtil.isNull(categoryName))
			return "";
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName == null ? null : categoryName.trim();
	}

	public String getPrice() {
		if (CommonUtil.isNull(price))
			return "";
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getModifierName() {
		if (CommonUtil.isNull(modifierName))
			return "";
		return modifierName;
	}

	public void setModifierName(String modifierName) {
		this.modifierName = modifierName == null ? null : modifierName.trim();
	}

	public Integer getIsActive() {
		if (CommonUtil.isNull(isActive))
			return 0;
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Integer getIsDefault() {
		if (CommonUtil.isNull(isDefault))
			return 0;
		return isDefault;
	}

	public void setIsDefault(Integer isDefault) {
		this.isDefault = isDefault;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	
	public int getIsSet() {
		return isSet;
	}

	public void setIsSet(int isSet) {
		this.isSet = isSet;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}
	
	public int getMustDefault() {
		return mustDefault;
	}

	public void setMustDefault(int mustDefault) {
		this.mustDefault = mustDefault;
	}

	public int getOptionQty() {
		return optionQty;
	}

	public void setOptionQty(int optionQty) {
		this.optionQty = optionQty;
	}

	@Override
	public String toString() {
		return "Modifier{" +
				"id=" + id +
				", restaurantId=" + restaurantId +
				", type=" + type +
				", categoryId=" + categoryId +
				", categoryName='" + categoryName + '\'' +
				", price='" + price + '\'' +
				", modifierName='" + modifierName + '\'' +
				", isActive=" + isActive +
				", isDefault=" + isDefault +
				", itemId=" + itemId +
				", isSet=" + isSet +
				", qty=" + qty +
				", mustDefault=" + mustDefault +
				", optionQty=" + optionQty +
				", minNumber=" + minNumber +
				", maxNumber=" + maxNumber +
				'}';
	}

}