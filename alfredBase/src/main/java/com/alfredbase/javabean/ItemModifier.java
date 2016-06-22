package com.alfredbase.javabean;

import com.alfredbase.utils.CommonUtil;

public class ItemModifier {
	private Integer id;

	private Integer restaurantId;

	private Integer itemId;

	// 无用
	private Integer modifierId;

	private Integer modifierCategoryId;
	
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

	public Integer getItemId() {
		if (CommonUtil.isNull(itemId))
			return 0;
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Integer getModifierId() {
		if (CommonUtil.isNull(modifierId))
			return 0;
		return modifierId;
	}

	public void setModifierId(Integer modifierId) {
		this.modifierId = modifierId;
	}

	public Integer getModifierCategoryId() {
		if (CommonUtil.isNull(modifierCategoryId))
			return 0;
		return modifierCategoryId;
	}

	public void setModifierCategoryId(Integer modifierCategoryId) {
		this.modifierCategoryId = modifierCategoryId;
	}

	@Override
	public String toString() {
		return "ItemModifier [id=" + id + ", restaurantId=" + restaurantId
				+ ", itemId=" + itemId + ", modifierId=" + modifierId
				+ ", modifierCategoryId=" + modifierCategoryId + "]";
	}

}