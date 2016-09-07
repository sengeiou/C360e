package com.alfredbase.utils;

public class IntegerUtils {
	public static boolean isEmptyOrZero(Integer num){
		if(num == null) {
			return true;
		}else if(num.intValue() == 0){
			return true;
		}else{
			return false;
		}
	}
	
	public static String ordinal(int i) {
	    String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
	    switch (i % 100) {
	    case 11:
	    case 12:
	    case 13:
	        return i + "th";
	    default:
	        return i + sufixes[i % 10];

	    }
	}

	/**
	 * 组合叫号
	 * @param index
	 * @param num
     * @return
     */
	public static String fromat(int index,String num){
		return index + String.format("%03d", Integer.parseInt(num));
	}
	/**
	 * 组合叫号
	 * @param index
	 * @param num
	 * @return
	 */
	public static int fromat(int index,int num){
		return Integer.parseInt(index + String.format("%03d", num));
	}
}
