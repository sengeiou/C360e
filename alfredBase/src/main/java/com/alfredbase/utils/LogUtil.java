package com.alfredbase.utils;

import android.util.Log;

import com.alfredbase.BaseApplication;

public class LogUtil {

	public static void v(String tag, String msg) {
		if (BaseApplication.isOpenLog) {
			Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (BaseApplication.isOpenLog) {
			Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (BaseApplication.isOpenLog) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (BaseApplication.isOpenLog) {
			Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (BaseApplication.isOpenLog) {
			Log.e(tag, msg);
		}
	}

}
