package com.alfredbase.utils;

import android.text.TextUtils;

import com.alfredbase.BaseApplication;
import com.alfredbase.ParamConst;
import com.alfredbase.store.Store;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * BigDecimal 辅助类
 * 
 * @author
 *
 */
public class BH {

	public static final int FORMAT_AFTER = 1;//小数点后两位四舍五入
	public static final int FORMAT_FRONT = 2;//小数点前两位四舍五入

	private static final DecimalFormat doubleFormat = new DecimalFormat("0.00");
	private static final DecimalFormat threeFormat = new DecimalFormat("0.000");
	private static final DecimalFormat fourFormat = new DecimalFormat("0.0000");// 运算工程中使用。
	public static final DecimalFormat intFormat = new DecimalFormat("0");
	//private static DecimalFormat format = doubleFormat;
	static DecimalFormat format;
	private static boolean isDouble = true;
	private static int type=100;
//	private static int operatingType=100;

	public static void initFormart(boolean isdouble){
//		if(isdouble){
//			format = doubleFormat;
//		}else{
//			format = intFormat;
//		}
//		isDouble = isdouble;
//		Store.putInt(BaseApplication.instance, Store.FORMAT_MONEY_TYPE, 2);
//		type = Store.getInt(BaseApplication.instance, Store.FORMAT_MONEY_TYPE, 0);
		format = new DecimalFormat(formatZero()+"");
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
		//Store.putInt(BaseApplication.instance, Store.FORMAT_MONEY_TYPE, 1);
	//	int type = Store.getInt(BaseApplication.instance, Store.FORMAT_MONEY_TYPE, 0);
		if (CommonUtil.isNull(string))
			return new BigDecimal("0.00");
		BigDecimal value2 = null;
		int money;
		if (type >=10) {
			if (string.toString().contains(".")) {
				money = Integer.valueOf(string.toString().substring(0, string.toString().indexOf(".")));
			} else {
				money=Integer.valueOf(string.toString());
			}
			int r;
			r = money % type;
			money -= r;
			if (r >= type/2) {
				money += type;
			}

			return new BigDecimal(money);
		} else {
			value2 = new BigDecimal(string);
			//return new BigDecimal(doubleFormat.format(value2));
			return value2.setScale(type, BigDecimal.ROUND_HALF_UP);
		}
	}


	public static BigDecimal formatMoney(Integer integer
	) {
//		Store.putInt(BaseApplication.instance, Store.FORMAT_MONEY_TYPE, 1);
//		int type = Store.getInt(BaseApplication.instance, Store.FORMAT_MONEY_TYPE, 0);
		if (CommonUtil.isNull(integer))
			return new BigDecimal(isDouble ? ParamConst.DOUBLE_ZERO : ParamConst.INT_ZERO);
		BigDecimal value2 = null;
		int money;
		if (type >=10) {
			if (integer.toString().contains(".")) {
				money = Integer.valueOf(integer.toString().substring(0, integer.toString().indexOf(".")));
			} else {
				money= integer.intValue();
			}
			int r;
			r = money % type;
			money -= r;
			if (r >= type/2) {
				money += type;
			}

			return new BigDecimal(money);
		} else {
			value2 = new BigDecimal(integer);
			//return new BigDecimal(doubleFormat.format(value2));
			return value2.setScale(type, BigDecimal.ROUND_HALF_UP);
		}
	}


	public static BigDecimal formatZero(
	) {
		BigDecimal newZero = BigDecimal.ZERO;
		if (type<10){
		BigDecimal zero = new BigDecimal(ParamConst.INT_ZERO);

		newZero = newZero.add(zero.setScale(type+2, RoundingMode.HALF_UP));
	}else if(type==10){
			newZero= new BigDecimal("0.0") ;
		}else {
			newZero= new BigDecimal("0") ;
		}
		return newZero;
	}

	/**
	 * 获取 格式后的等差
	 *
	 * @param value
	 * @param
	 * @param
	 * @return
	 */

	public static BigDecimal formatRound(BigDecimal value
	) {
		value=BH.sub(BH.formatMoney(value.toString()),value,true);
		return value;
	}


	public static BigDecimal formatOperation(BigDecimal value
	) {
		int money;
		if(type<10){
		value=	new BigDecimal(format.format(value));
		}else
		if(type==10){
			format=	new DecimalFormat("0.0");
			value=	new BigDecimal(format.format(value));
		}else {
			format=	new DecimalFormat("0");
			value=	new BigDecimal(format.format(value));
		}

		return value;
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
//		if (needFormat) {
			return new BigDecimal(format.format(value1.add(value2)));
//		} else {
//			return value1.add(value2);
//		}
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
		//if (needFormat) {
			return new BigDecimal(format.format(value1.subtract(value2)));
//		} else {
//			return value1.subtract(value2);
//		}
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
	//	if (needFormat) {
			return new BigDecimal(format.format(value1.multiply(value2)));
//		} else {
//			return value1.multiply(value2);
//		}
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
	//	if (needFormat) {
			return new BigDecimal(format.format(value1.divide(value2,5)));
//		} else {
//			return value1.divide(value2, 5, BigDecimal.ROUND_HALF_UP);
//		}
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
//		if (needFormat) {
			return value1.divide(value2, 3, BigDecimal.ROUND_HALF_UP);
//		} else {
//			return value1.divide(value2, 5, BigDecimal.ROUND_HALF_UP);
//		}
	}
//
//	public static BigDecimal getBD(Integer integer) {
//		if (CommonUtil.isNull(integer))
//			return new BigDecimal(isDouble ? ParamConst.DOUBLE_ZERO : ParamConst.INT_ZERO);
//		return new BigDecimal(format.format(new BigDecimal(integer)));
//	}
//
//	public static BigDecimal getBD(String string) {
//		if (CommonUtil.isNull(string))
//			return new BigDecimal(isDouble ? ParamConst.DOUBLE_ZERO : ParamConst.INT_ZERO);
//		return new BigDecimal(format.format(new BigDecimal(string)));
//	}
//
//	public static BigDecimal getBDNoFormat(String string){
//		if (CommonUtil.isNull(string))
//			return new BigDecimal(isDouble ? ParamConst.DOUBLE_ZERO : ParamConst.INT_ZERO);
//		return new BigDecimal(string);
//	}
//
//	public static BigDecimal getBDThirdFormat(String string){
//		if (CommonUtil.isNull(string))
//			return new BigDecimal(isDouble ? ParamConst.DOUBLE_ZERO : ParamConst.INT_ZERO);
//		return new BigDecimal(threeFormat.format(new BigDecimal(string)));
//	}
//
//	public static BigDecimal getBD(Double string) {
//		if (CommonUtil.isNull(string))
//			return new BigDecimal(isDouble ? ParamConst.DOUBLE_ZERO : ParamConst.INT_ZERO);
//		return new BigDecimal(format.format(new BigDecimal(string)));
//	}
//	public static BigDecimal getBD(BigDecimal bigDecimal) {
//		if (CommonUtil.isNull(bigDecimal))
//			return new BigDecimal(isDouble ? ParamConst.DOUBLE_ZERO : ParamConst.INT_ZERO);
//		return new BigDecimal(format.format(bigDecimal));
//	}


	public static BigDecimal getBD(Integer integer) {
		if (CommonUtil.isNull(integer))
			return formatZero();
		return new BigDecimal(integer);
	}

	public static BigDecimal getBD(String string) {
		if (CommonUtil.isNull(string))
			return formatZero();
		return formatOperation(new BigDecimal(string));
	}

	public static BigDecimal getBDNoFormat(String string){
		if (CommonUtil.isNull(string))
			return formatZero();
		return new BigDecimal(string);
	}

	public static BigDecimal getBDThirdFormat(String string){
		if (CommonUtil.isNull(string))
			return formatZero();
		return formatOperation(new BigDecimal(string));
	}

	public static BigDecimal getBD(Double string) {
		if (CommonUtil.isNull(string))
			return formatZero();
		return formatOperation(new BigDecimal(string));
	}
	public static BigDecimal getBD(BigDecimal bigDecimal) {
		if (CommonUtil.isNull(bigDecimal))
			return formatZero();
		return formatOperation(bigDecimal);
	}

	/**
	 * 求得绝对值
	 * 
	 * @param value1
	 * @param needFormat
	 * @return
	 */
	public static BigDecimal abs(BigDecimal value1,boolean needFormat) {
//		if (needFormat) {
			return new BigDecimal(format.format(value1.abs()));
//		} else {
//			return value1.abs();
//		}
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
