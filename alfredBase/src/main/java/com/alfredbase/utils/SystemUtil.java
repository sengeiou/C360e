package com.alfredbase.utils;

import java.util.Locale;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;

public class SystemUtil {
	private static String TAG = SystemUtil.class.getSimpleName();
	public static double getAvailMemory(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		LogUtil.d(TAG, mi.availMem / (1024 * 1024)+"---->M");
		return mi.availMem / (1024 * 1024);
	}
	
	public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

}
