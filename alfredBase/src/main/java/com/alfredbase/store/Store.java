package com.alfredbase.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alfredbase.store.sql.StoreValueSQL;
import com.alfredbase.utils.LogUtil;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Store {
	private static final String TAG = Store.class.getSimpleName();

	private static final String PACK_NAME = Store.class.getPackage().getName();

	public static final String CURRENT_REVENUE_CENTER = "CURRENT_REVENUE_CENTER";
	public static final String CALL_APP_IP = "CALL_APP_IP";
	public static final String LOGIN_RESULT = "LOGIN_RESULT";
	public static final String SESSION_STATUS = "SESSION_STATUS";
	public static final String BUSINESS_DATE = "BUSINESS_DATE";

	public static final String EMPLOYEE_ID = "EMPLOYEE_ID";
	public static final String PASSWORD = "PASSWORD";

	public static final String SYNC_DATA_TAG = "SYNC_DATA_TAG";

	public static final String MAINPOSINFO = "MainPosInfo";

	public static final String WAITER_DEVICE = "WAITER_DEVICE";

	public static final String KDS_DEVICE = "KDS_DEVICE";

	public static final String KDS_PRINTER = "KDS_PRINTER";

	public static final String USER_AND_KEY = "USER_AND_KEY";

	public static final String CURRENT_MAIN_POS_ID_CONNECTED = "CURRENT_MAIN_POS_ID_CONNECTED";

	public static final String NOTIFICATIONS_OF_GETTING_BILL = "NOTIFICATIONS_OF_GETTING_BILL";

	public static final String PUSH_MESSAGE = "PUSH_MESSAGE";

	public static final String MAINPOS_USER = "MAINPOS_USER";

	public static final String KDS_USER = "KDS_USER";

	public static final String WAITER_USER = "waiter_user";
	public static final String WAITER_PRINTER_DEVICE = "WAITER_PRINTER_DEVICE";
	public static final String WIFI_STR = "WIFI_STR";

	public static final String MAINPOSINFO_MAP = "MAINPOSINFO_MAP";

	public static final String LAST_BUSINESSDATE = "LAST_BUSINESSDATE";
	// print setting
	public static final String PRINT_SETTING_KOT_MODE_TOGETHER = "PRINT_SETTING_KOT_MODE_TOGETHER"; // 0:
																									// whole
																									// print,
																									// 1:
																									// seperate
																									// print
	public static final String PRINT_SETTING_KOT_MODE_DOUBLE = "PRINT_SETTING_KOT_MODE_DOUBLE"; // 0:
																								// 1
																								// copy,
																								// 1:
																								// 2
																								// copies

	public static final String PRINT_SETTING_BILL_MODE_DOUBLE = "PRINT_SETTING_BILL_MODE_DOUBLE"; // double
																									// bill
																									// print
	public static final String PRINT_SETTING_RECEIPT_MODE_DOUBLE = "PRINT_SETTING_RECEIPT_MODE_DOUBLE"; // double
																										// bill
																										// print
	public static final String PRINT_SETTING_ORDER_SUMMARY = "PRINT_SETTING_ORDER_SUMMARY"; // ORDER_SUMMARY

	public static final String WAITER_INSTRUCTION_HISTORY = "WAITER_INSTRUCTION_HISTORY";

	public static final String SEND_TABLE_NAME_LIST = "SEND_TABLE_NAME_LIST";

	public static final String KOT_PRINT = "KOT_PRINT";

	public static final String PRINT_REPORT_WHEN_CLOSE_SESSION = "PRINT_REPORT_WHEN_CLOSE_SESSION";
	public static final String PRINT_REPORT_PLU_CATEGORY = "PRINT_REPORT_PLU_CATEGORY";
	public static final String PRINT_REPORT_PLU_ITEM= "PRINT_REPORT_PLU_ITEM";
	public static final String PRINT_REPORT_PLU_MODIFIER = "PRINT_REPORT_PLU_MODIFIER";
	public static final String PRINT_REPORT_HOURLY_PAYMENT = "PRINT_REPORT_HOURLY_PAYMENT";
	public static final String PRINT_BEFORE_CLOSE_BILL = "PRINT_BEFORE_CLOSE_BILL";
	public static final String PRINT_CASH_CLOSE = "PRINT_CASH_CLOSE";
	public static final String AUTO_RECEIVE_ONLINE_ORDER = "AUTO_RECEIVE_ONLINE_ORDER";

	public static final String WELCOME_IMAGE_ID = "WELCOME_IMAGE_ID";

	public static final String POS_SYSTEM_UPDATE_INFO = "POS_SYSTEM_UPDATE_INFO";
	public static final String KDS_SYSTEM_UPDATE_INFO = "KDS_SYSTEM_UPDATE_INFO";
	public static final String WAITER_SYSTEM_UPDATE_INFO = "WAITER_SYSTEM_UPDATE_INFO";

	public static final String SUNMI_STYLE = "SUNMI_STYLE";
	public static final String SUNMI_DATA = "SUNMI_DATA";
	public static final String SUNMI_WELCOME = "SUNMI_WELCOME";

	public static final String COLOR_PICKER= "COLOR_PICKER";
	public static final String SET_BACKGROUND = "SET_BACKGROUND";

	public static final String TOP_MASKING_IS_USER = "TOP_MASKING_IS_USER";
	public static final String IS_SCREDDN_LOCK = "IS_SCREDDN_LOCK";
	public static final String REMOVE_TO_VOID = "REMOVE_TO_VOID";
	public static final String IS_TRANSFER_PRINT = "IS_TRANSFER_PRINT";

	public static final String LOCK_SCREEN = "LOCK_SCREEN";
	public static final String DISCOUNT_SELECTED_ITEM = "DISCOUNT_SELECTED_ITEM";

	public static final String MAX_ORDER_NO = "MAX_ORDER_NO";
	public static final String LABEL_DEVICE = "LABEL_DEVICE";
	public static final String WAITER_SET_LOCK = "WAITER_SET_LOCK";


	private Store() {
	};
	
	public static final long DEFAULT_LONG_TYPE = -1L;
	public static final float DEFAULT_FLOAT_TYPE = -123;
	public static final int DEFAULT_INT_TYPE = -123;  // 副屏只显示文字

	public static final int SUNMI_IMG = -122;  // 只显示图片
	public static final int SUNMI_TEXT = -123;  // 副屏只显示文字
	public static final int SUNMI_IMG_TEXT = -124;  // 显示图片和文字
	public static final int SUNMI_VIDEO = -121;
	public static final int SUNMI_VIDEO_TEXT = -125;

	private static final int TYPE_INT = 1;
	private static final int TYPE_STRING = 1;
	private static final int TYPE_DOUBLE = 1;
	private static final int TYPE_LONG = 1;



	public static final String DEFAULT_STRING_TYPE = "";
	public static SharedPreferences getSharedPreferences(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				PACK_NAME, Context.MODE_PRIVATE);
		return sharedPreferences;
	}

	public static void putString(Context context, String key, String value) {
//		SharedPreferences sharedPreferences = getSharedPreferences(context);
//		sharedPreferences.edit().putString(key, value).commit();
		StoreValueSQL.updateStore(key, value);
	}
	
	// 默认值为 "" 所以做判断的时候 注意用TextUtils
	public static String getString(Context context, String key) {
//		return getSharedPreferences(context).getString(key, DEFAULT_STRING_TYPE);
		return StoreValueSQL.getValue(key);
	}
	
	public static void putLong(Context context, String key, long value) {
//		SharedPreferences sharedPreferences = getSharedPreferences(context);
//		sharedPreferences.edit().putLong(key, value).commit();
		StoreValueSQL.updateStore(key, value+"");

	}
	// 默认值为 -1L 注意做判断是时候多加个 为-1L的判断
	public static long getLong(Context context, String key) {
//		return getSharedPreferences(context).getLong(key, DEFAULT_LONG_TYPE);
		String value = StoreValueSQL.getValue(key);
		if(TextUtils.isEmpty(value)){
			return -1l;
		}else{
			return Long.parseLong(value);
		}
	}
	
	public static void putInt(Context context, String key, int value) {
//		SharedPreferences sharedPreferences = getSharedPreferences(context);
//		sharedPreferences.edit().putInt(key, value).commit();
		StoreValueSQL.updateStore(key,value+"");
	}
	// 默认值为 -123 注意做判断的时候 多加个 为-123的判断
	public static int getInt(Context context, String key) {
//		return getSharedPreferences(context).getInt(key, DEFAULT_INT_TYPE);
		String value = StoreValueSQL.getValue(key);
		if(TextUtils.isEmpty(value)){
			return -123;
		}else{
			return Integer.parseInt(value);
		}
	}

	public static int getInt(Context context, String key, int defVal) {
//		return getSharedPreferences(context).getInt(key, defVal);
		String value = StoreValueSQL.getValue(key);
		if(TextUtils.isEmpty(value)){
			return defVal;
		}else{
			return Integer.parseInt(value);
		}
	}
	
	public static void putBoolean(Context context, String key, boolean value) {
//		SharedPreferences sharedPreferences = getSharedPreferences(context);
//		sharedPreferences.edit().putBoolean(key, value).commit();
		StoreValueSQL.updateStore(key, value+"");
	}
	
	public static boolean getBoolean(Context context, String key, boolean defVal) {
//		return getSharedPreferences(context).getBoolean(key, defVal);
		String value = StoreValueSQL.getValue(key);
		if(TextUtils.isEmpty(value)){
			return defVal;
		}else{
			return Boolean.parseBoolean(value);
		}
	}
	
	public static void putFloat(Context context, String key, float value) {
		SharedPreferences sharedPreferences = getSharedPreferences(context);
		sharedPreferences.edit().putFloat(key, value).commit();
	}
	// 默认值为 -123 注意做判断是时候多加个 为-123的判断
	public static float getFloat(Context context, String key) {
		return getSharedPreferences(context).getFloat(key, DEFAULT_FLOAT_TYPE);
	}

	// 存储对象类型
	public static synchronized boolean saveObject(Context context, String key,
			Object obj) {
		if (obj == null)
			return false;
//		SharedPreferences sharedPreferences = getSharedPreferences(context);
//		Editor editor = sharedPreferences.edit();
		try {
			Gson gson = new Gson();
			String value = gson.toJson(obj);
//			editor.putString(key, value);
			StoreValueSQL.updateStore(key, value);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 获取对象类型
	public static <Object> Object getObject(Context context, String key,
			Class<Object> classOfT) {
//		SharedPreferences preferences = getSharedPreferences(context);
//		String str = preferences.getString(key, "");
		String str = StoreValueSQL.getValue(key);
		if (str.length() == 0)
			return null;
		try {
			Gson gson = new Gson();
			return gson.fromJson(str, classOfT);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e("Store", key + ": format error in shared preference.");
		}
		return null;
	}
	
	// 获取对象类型
		public static <Object> Object getObject(Context context, String key,
				Type classOfT) {
//			SharedPreferences preferences = getSharedPreferences(context);
//			String str = preferences.getString(key, "");
			String str = StoreValueSQL.getValue(key);
			if (str.length() == 0)
				return null;
			try {
				Gson gson = new Gson();
				return gson.fromJson(str, classOfT);
			} catch (Exception e) {
				e.printStackTrace();
				LogUtil.e("Store", key + ": format error in shared preference.");
			}
			return null;
		}

	public static void remove(Context context, String key) {
//		SharedPreferences preferences = getSharedPreferences(context);
//		Editor editor = preferences.edit();
//		editor.remove(key);
//		editor.commit();
		StoreValueSQL.deleteStore(key);
	}

	/**
	 * 存储List<String>
	 */
	public static void putStrListValue(Context context, String key,
									   List<String> strList) {
		if (null == strList) {
			return;
		}
		int size = strList.size();
		putInt(context, key + "size", size);
		for (int i = 0; i < size; i++) {
			putString(context, key + i, strList.get(i));
		}
	}

	/**
	 * 取出List<String>
	 */
	public static List<String> getStrListValue(Context context, String key) {
		List<String> strList = new ArrayList<String>();
		int size = getInt(context, key + "size");
		for (int i = 0; i < size; i++) {
			strList.add(getString(context, key + i));
		}
		return strList;
	}


	/**
	 * 清空List<String>所有数据
	 **/
	public static void removeStrList(Context context, String key) {
		int size = getInt(context, key + "size");
		if (0 == size) {
			return;
		}
		remove(context, key + "size");
		for (int i = 0; i < size; i++) {
			remove(context, key + i);
		}
	}

	/**
	 * 清空List<String>单条数据
	 */
	public static void removeStrListItem(Context context, String key, String str) {
		int size = getInt(context, key + "size");
		if (0 == size) {
			return;
		}
		List<String> strList = getStrListValue(context, key);
		// 待删除的List<String>数据暂存
		List<String> removeList = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			if (str.equals(strList.get(i))) {
				if (i >= 0 && i < size) {
					removeList.add(strList.get(i));
					remove(context, key + i);
					putInt(context, key + "size", size - 1);
				}
			}
		}
		strList.removeAll(removeList);
		// 删除元素重新建立索引写入数据
		putStrListValue(context, key, strList);
	}


}
