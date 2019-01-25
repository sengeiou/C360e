package com.alfredbase.utils;

import android.text.TextUtils;

import com.alfredbase.BaseApplication;
import com.alfredbase.ParamConst;
import com.alfredbase.store.Store;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * BigDecimal 辅助类
 * 
 * @author 冯小卫 2014-5-26
 *
 */
public class BH {

	public static final int FORMAT_AFTER = 1;//小数点后两位四舍五入
	public static final int FORMAT_FRONT = 2;//小数点前两位四舍五入

	private static final DecimalFormat doubleFormat = new DecimalFormat("0.00");
	private static final DecimalFormat threeFormat = new DecimalFormat("0.000");
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
	 * 格式化金额
	 *
	 * @param string
	 * @param
	 * @param
	 * @return
	 */
	public static BigDecimal formatMoney(String string
	) {
		Store.putInt(BaseApplication.instance, Store.FORMAT_MONEY_TYPE, 1);
		int type = Store.getInt(BaseApplication.instance, Store.FORMAT_MONEY_TYPE, 0);
		if (CommonUtil.isNull(string))
			return new BigDecimal(isDouble ? ParamConst.DOUBLE_ZERO : ParamConst.INT_ZERO);
		BigDecimal value2 = null;
		int money;
		if (type == FORMAT_FRONT) {
			if (string.toString().contains(".")) {
				money = Integer.valueOf(string.toString().substring(0, string.toString().indexOf(".")));
			} else {
				return value2;
			}
			int r;
			r = money % 100;
			money -= r;
			if (r >= 50) {
				money += 100;
			}

			return new BigDecimal(money);
		} else {
			value2 = new BigDecimal(string);
			return value2.setScale(1, BigDecimal.ROUND_HALF_UP);
		}
	}


	public static BigDecimal formatMoney(Integer integer
	) {
		Store.putInt(BaseApplication.instance, Store.FORMAT_MONEY_TYPE, 1);
		int type = Store.getInt(BaseApplication.instance, Store.FORMAT_MONEY_TYPE, 0);
		if (CommonUtil.isNull(integer))
			return new BigDecimal(isDouble ? ParamConst.DOUBLE_ZERO : ParamConst.INT_ZERO);
		BigDecimal value2 = null;
		int money;
		if (type == FORMAT_FRONT) {
			if (integer.toString().contains(".")) {
				money = Integer.valueOf(integer.toString().substring(0, integer.toString().indexOf(".")));
			} else {
				return value2;
			}
			int r;
			r = money % 100;
			money -= r;
			if (r >= 50) {
				money += 100;
			}

			return new BigDecimal(money);
		} else {
			value2 = new BigDecimal(integer);
			return value2.setScale(1, BigDecimal.ROUND_HALF_UP);
		}
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
