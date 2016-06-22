package com.alfredbase.javabean.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.text.TextUtils;

import com.alfredbase.ParamConst;
import com.alfredbase.javabean.RestaurantConfig;

public class LocalRestaurantConfig {
	private static LocalRestaurantConfig instance;
	// 动态session类型
	private List<Integer> sessionConfigType;

	// 货币符号
	private String currencySymbol;

	private IncludedTax includedTax;

	private String roundType;

	private String[] discountOption = new String[] { "5", "10", "15", "20" };

	public static LocalRestaurantConfig getInstance() {
		if (instance == null)
			instance = new LocalRestaurantConfig();
		return instance;
	}

	private LocalRestaurantConfig() {
		this.sessionConfigType = Arrays.asList(new Integer[] {
				ParamConst.SESSION_STATUS_BREAKFAST,
				ParamConst.SESSION_STATUS_LUNCH,
				ParamConst.SESSION_STATUS_DINNER });
		this.currencySymbol = "$";
		this.roundType = ParamConst.ROUND_NONE;
		this.includedTax = new IncludedTax();
	}

	public List<Integer> getSessionConfigType() {
		if (sessionConfigType == null) {
			this.sessionConfigType = Arrays.asList(new Integer[] {
					ParamConst.SESSION_STATUS_BREAKFAST,
					ParamConst.SESSION_STATUS_LUNCH,
					ParamConst.SESSION_STATUS_DINNER });
		}
		return sessionConfigType;
	}

	public void setSessionConfigType(RestaurantConfig restaurantConfig) {

		if (TextUtils.isEmpty(restaurantConfig.getParaValue1())) {
			this.sessionConfigType = Arrays.asList(new Integer[] {
					ParamConst.SESSION_STATUS_BREAKFAST,
					ParamConst.SESSION_STATUS_LUNCH,
					ParamConst.SESSION_STATUS_DINNER });
		} else {
			String[] strArray = restaurantConfig.getParaValue1().split(
					ParamConst.PARA_VALUE_SPLIT);
			if (strArray.length == 0) {
				this.sessionConfigType = Arrays.asList(new Integer[] {
						ParamConst.SESSION_STATUS_BREAKFAST,
						ParamConst.SESSION_STATUS_LUNCH,
						ParamConst.SESSION_STATUS_DINNER });
			} else {
				Integer[] intArray = new Integer[strArray.length];
				for (int i = 0; i < strArray.length; i++) {
					intArray[i] = Integer.parseInt(strArray[i]);
				}
				Comparator comparator = new Comparator<Integer>() {

					@Override
					public int compare(Integer arg0, Integer arg1) {
						return arg0.compareTo(arg1);
					}
				};

				this.sessionConfigType = Arrays.asList(intArray);
				Collections.sort(this.sessionConfigType, comparator);
			}
		}

	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencySymbol(RestaurantConfig restaurantConfig) {
		this.currencySymbol = restaurantConfig.getParaValue1();
	}

	public IncludedTax getIncludedTax() {
		return includedTax;
	}

	public void setIncludedTax(RestaurantConfig restaurantConfig) {
		if (!TextUtils.isEmpty(restaurantConfig.getParaValue1())) {
			this.includedTax.setId(Integer.valueOf(restaurantConfig
					.getParaValue1()));
			this.includedTax.setIncludedTaxName(restaurantConfig
					.getParaValue2());
		}
	}

	public String getRoundType() {
		return roundType;
	}

	public void setRoundType(RestaurantConfig restaurantConfig) {
		this.roundType = restaurantConfig.getParaValue1();
	}

	public String[] getDiscountOption() {
		return discountOption;
	}

	public void setDiscountOption(RestaurantConfig restaurantConfig) {
		if (!TextUtils.isEmpty(restaurantConfig.getParaValue1())) {
			String[] discountOption = restaurantConfig.getParaValue1().split(
					ParamConst.PARA_VALUE_SPLIT);
			if (discountOption.length > 0) {
				this.discountOption = discountOption;
			}
		}

	}

}
