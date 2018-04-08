package com.alfredbase.utils;

import com.alfredbase.ParamConst;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class RoundUtil {
	public static BigDecimal getPriceAfterRound(String roundType,
			BigDecimal priceBeforeRound) {
		if (roundType == null) {
			return priceBeforeRound;
		}
		if (roundType.equalsIgnoreCase(ParamConst.ROUND_10CENTS)) {
			DecimalFormat doubleFormat = new DecimalFormat("0");
			BigDecimal bigDecimal = BH.div(priceBeforeRound, BH.getBDNoFormat("0.1"),
					false);
			return BH.mul(BH.getBDNoFormat(doubleFormat.format(bigDecimal)),
					BH.getBDNoFormat("0.1"), true);
		} else if (roundType.equalsIgnoreCase(
				ParamConst.ROUND_1DOLLAR)) {
			DecimalFormat doubleFormat = new DecimalFormat("0");
			BigDecimal bigDecimal = BH.div(priceBeforeRound, BH.getBDNoFormat("1.0"),
					false);
			return BH.mul(BH.getBDNoFormat(doubleFormat.format(bigDecimal)),
					BH.getBDNoFormat("1.0"), true);
		} else if (roundType.equalsIgnoreCase(
				ParamConst.ROUND_5CENTS)) {
			DecimalFormat doubleFormat = new DecimalFormat("0");
			BigDecimal bigDecimal = BH.div(priceBeforeRound, BH.getBDNoFormat("0.05"),
					false);
			return BH.mul(BH.getBDNoFormat(doubleFormat.format(bigDecimal)),
					BH.getBDNoFormat("0.05"), true);
		} else if(roundType.equalsIgnoreCase(ParamConst.ROUND_10CENTS_UP)){
			BigDecimal bigDecimal = BH.div(priceBeforeRound, BH.getBDNoFormat("0.1"),
					false);
			return BH.mul(BH.getBDNoFormat(bigDecimal.setScale(0, BigDecimal.ROUND_UP).toString()),
					BH.getBDNoFormat("0.1"), true);
		} else if(roundType.equalsIgnoreCase(ParamConst.ROUND_1DOLLAR_UP)){
			BigDecimal bigDecimal = BH.div(priceBeforeRound, BH.getBDNoFormat("1.0"),
					false);
			return BH.mul(BH.getBDNoFormat(bigDecimal.setScale(0, BigDecimal.ROUND_UP).toString()),
					BH.getBDNoFormat("1.0"), true);
		} else if(roundType.equalsIgnoreCase(ParamConst.ROUND_5CENTS_UP)){
			BigDecimal bigDecimal = BH.div(priceBeforeRound, BH.getBDNoFormat("0.05"),
					false);
			return BH.mul(BH.getBDNoFormat(bigDecimal.setScale(0, BigDecimal.ROUND_UP).toString()),
					BH.getBDNoFormat("0.05"), true);
		} else if (roundType.equalsIgnoreCase(
				ParamConst.ROUND_10CENTS_DOWN)) {
			BigDecimal bigDecimal = BH.div(priceBeforeRound, BH.getBDNoFormat("0.1"),
					false);
			return BH.mul(BH.getBDNoFormat(bigDecimal.setScale(0, BigDecimal.ROUND_DOWN).toString()),
					BH.getBDNoFormat("0.1"), true);
		} else if (roundType.equalsIgnoreCase(
				ParamConst.ROUND_5CENTS_DOWN)) {
			BigDecimal bigDecimal = BH.div(priceBeforeRound, BH.getBDNoFormat("0.05"),
					false);
			return BH.mul(BH.getBDNoFormat(bigDecimal.setScale(0, BigDecimal.ROUND_DOWN).toString()),
					BH.getBDNoFormat("0.05"), true);
		} else if (roundType.equalsIgnoreCase(
				ParamConst.ROUND_1DOLLAR_DOWN)) {
			BigDecimal bigDecimal = BH.div(priceBeforeRound, BH.getBDNoFormat("1.0"),
					false);
			return BH.mul(BH.getBDNoFormat(bigDecimal.setScale(0, BigDecimal.ROUND_DOWN).toString()),
					BH.getBDNoFormat("1.0"), true);
		} else {
			return priceBeforeRound;
		}
	}
	
	public static BigDecimal getRounding(String roundType,
			BigDecimal priceBeforeRound) {
		return BH.sub(priceBeforeRound, getPriceAfterRound(roundType, priceBeforeRound), true);
	}
}
