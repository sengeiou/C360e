package com.alfredbase.utils;

import com.alfredbase.ParamConst;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * BigDecimal 辅助类
 * 
 * @author
 *
 */
public class BH {
	private static final DecimalFormat doubleFormat = new DecimalFormat("0.00");
	private static final DecimalFormat threeFormat = new DecimalFormat("0.000");
	private static final DecimalFormat fourFormat = new DecimalFormat("0.0000");// 运算工程中使用。
	public static final DecimalFormat intFormat = new DecimalFormat("0");
	private static DecimalFormat format = doubleFormat;
	private static boolean isDouble = true;
	public static void initFormart(boolean isdouble){
		if(isdouble){
			format = doubleFormat;
		}else{
			format = intFormat;
		}
		isDouble = isdouble;
	}
	/**
	 * 加法
	 * 
	 * @param value1
	 * @param value2
	 * @param needFormat
	 * @return
	 */
	public static BigDecimal add(BigDecimal value1, BigDecimal value2,
			boolean needFormat) {
		if (needFormat) {
			return new BigDecimal(format.format(value1.add(value2)));
		} else {
			return value1.add(value2);
		}
	}

	/**
	 * 减法
	 * 
	 * @param value1
	 * @param value2
	 * @param needFormat
	 * @return
	 */
	public static BigDecimal sub(BigDecimal value1, BigDecimal value2,
			boolean needFormat) {
		if (needFormat) {
			return new BigDecimal(format.format(value1.subtract(value2)));
		} else {
			return value1.subtract(value2);
		}
	}

	/**
	 * 乘法
	 * 
	 * @param value1
	 * @param value2
	 * @param needFormat
	 * @return
	 */
	public static BigDecimal mul(BigDecimal value1, BigDecimal value2,
			boolean needFormat) {
		if (needFormat) {
			return new BigDecimal(format.format(value1.multiply(value2)));
		} else {
			return value1.multiply(value2);
		}
	}

	/**
	 * 除法
	 * 
	 * @param value1
	 * @param value2
	 * @param needFormat 为true时保留小数点后两位，
	 * @return
	 */
	public static BigDecimal div(BigDecimal value1, BigDecimal value2,
			boolean needFormat) {
		if (needFormat) {
			return new BigDecimal(format.format(value1.divide(value2,5)));
		} else {
			return value1.divide(value2, 5, BigDecimal.ROUND_HALF_UP);
		}
	}
	/**
	 * 除法
	 * 
	 * @param value1
	 * @param value2
	 * @param needFormat 为true时保留小数点后三位，
	 * @return
	 */
	public static BigDecimal divThirdFormat(BigDecimal value1, BigDecimal value2,
			boolean needFormat) {
		if (needFormat) {
			return value1.divide(value2, 3, BigDecimal.ROUND_HALF_UP);
		} else {
			return value1.divide(value2, 5, BigDecimal.ROUND_HALF_UP);
		}
	}

	public static BigDecimal getBD(Integer integer) {
		if (CommonUtil.isNull(integer))
			return new BigDecimal(isDouble ? ParamConst.DOUBLE_ZERO : ParamConst.INT_ZERO);
		return new BigDecimal(format.format(new BigDecimal(integer)));
	}

	public static BigDecimal getBD(String string) {
		if (CommonUtil.isNull(string))
			return new BigDecimal(isDouble ? ParamConst.DOUBLE_ZERO : ParamConst.INT_ZERO);
		return new BigDecimal(format.format(new BigDecimal(string)));
	}
	
	public static BigDecimal getBDNoFormat(String string){
		if (CommonUtil.isNull(string))
			return new BigDecimal(isDouble ? ParamConst.DOUBLE_ZERO : ParamConst.INT_ZERO);
		return new BigDecimal(string);
	}
	
	public static BigDecimal getBDThirdFormat(String string){
		if (CommonUtil.isNull(string))
			return new BigDecimal(isDouble ? ParamConst.DOUBLE_ZERO : ParamConst.INT_ZERO);
		return new BigDecimal(threeFormat.format(new BigDecimal(string)));
	}
	
	public static BigDecimal getBD(Double string) {
		if (CommonUtil.isNull(string))
			return new BigDecimal(isDouble ? ParamConst.DOUBLE_ZERO : ParamConst.INT_ZERO);
		return new BigDecimal(format.format(new BigDecimal(string)));
	}
	public static BigDecimal getBD(BigDecimal bigDecimal) {
		if (CommonUtil.isNull(bigDecimal))
			return new BigDecimal(isDouble ? ParamConst.DOUBLE_ZERO : ParamConst.INT_ZERO);
		return new BigDecimal(format.format(bigDecimal));
	}

	/**
	 * 求得绝对值
	 * 
	 * @param value1
	 * @param needFormat
	 * @return
	 */
	public static BigDecimal abs(BigDecimal value1,boolean needFormat) {
		if (needFormat) {
			return new BigDecimal(format.format(value1.abs()));
		} else {
			return value1.abs();
		}
	}
	/**
	 * 比较大小
	 * 为true时 前比后大
	 * 为false是 前小于等于后
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static boolean compare(BigDecimal value1, BigDecimal value2){
		if(value1.compareTo(value2) == 1){
			return true;
		} else {
			return false;
		}
	}

	public static boolean IsDouble(){
		return isDouble;
	}
//
//	/**
//	 * 保留小数点后两位
//	 * @param value1
//	 * @param needFormat
//	 * @return
//	 */
//	public static BigDecimal formatDouble(BigDecimal value1, boolean needFormat) {
//		if (needFormat) {
//			return new BigDecimal(format.format(value1));
//		} else {
//			return value1;
//		}
//	}
}
