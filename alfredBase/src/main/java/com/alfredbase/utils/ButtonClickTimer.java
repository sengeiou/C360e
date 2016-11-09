package com.alfredbase.utils;

import android.view.View;

/**
 * 
 * @author 冯小卫 2014-3-10
 * 
 *         按钮点击控制类，在500毫秒之类，任何按钮不能点击两次
 */
public class ButtonClickTimer {
	public static long lastClickTime;
	public static View lastView;

	public static boolean canClick(View view) {
		if(lastView !=view){
			lastView = view;
			return true;
		}
		long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis - lastClickTime > 500) {
			lastClickTime = currentTimeMillis;
			return true;
		} else {
			lastClickTime = currentTimeMillis;
			return false;
		}
	}
	
	public static boolean canClick() {
		long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis - lastClickTime > 500) {
			lastClickTime = currentTimeMillis;
			return true;
		} else {
			lastClickTime = currentTimeMillis;
			return false;
		}
	}
	public static long lastLinkTime;
	public static boolean canLink() {
		long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis - lastLinkTime > 20000) {
			lastLinkTime = currentTimeMillis;
			return true;
		} else {
//			lastLinkTime = currentTimeMillis;
			return false;
		}
	}
}
