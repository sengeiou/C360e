package com.alfredbase;


public class ParamHelper {
	public static String getPrintOrderNO(int orderNO) {
		String preStr = String.format("%06d", orderNO);
		return preStr;
	}

	public static String getPrintOrderBillNo(int prefix, int orderBillNo) {
		String preStr = String.format("%02d%06d",prefix,orderBillNo);
		return preStr;
	}
	
	public static String getPrintOrderBillNo(int orderBillNo) {
		String preStr = String.format("%08d",orderBillNo);
		return preStr;
	}
}
