package com.alfredbase.utils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.alfredbase.BaseActivity;
import com.alfredbase.R;

public class CommonUtil {
	public static boolean isNull(String text) {
		if (text == null)
			return true;
		if (text.equals(""))
			return true;
		if (text.equalsIgnoreCase("null"))
			return true;
		return false;
	}

	public static boolean isNull(Integer integer) {
		if (integer == null)
			return true;
		else
			return false;
	}

	public static boolean isNull(Long longNum) {
		if (longNum == null)
			return true;
		else
			return false;
	}

	public static boolean isNull(Double doubleNum) {
		if (doubleNum == null)
			return true;
		else
			return false;
	}

	public static boolean isNull(BigDecimal bigDecimal) {
		if (bigDecimal == null)
			return true;
		else
			return false;
	}

	public static String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifi.getConnectionInfo();
		LogUtil.d("LocalMacAddress", wifiInfo.getMacAddress().toString());
		return wifiInfo.getMacAddress().toString();
	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(inetAddress
									.getHostAddress())) {
						LogUtil.d("LocalIpAddress", inetAddress
								.getHostAddress().toString());
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void inputMethodSet(BaseActivity baseActivity) {
		InputMethodManager imm = (InputMethodManager) baseActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 得到InputMethodManager的实例
		if (imm.isActive()) {
			// 如果开启
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
					InputMethodManager.HIDE_NOT_ALWAYS);
			// 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
		}
	}
	
	public static void hideSoftkeyBoard(BaseActivity parent) {
		InputMethodManager imm = (InputMethodManager)parent.getSystemService(Context.INPUT_METHOD_SERVICE);    
		//得到InputMethodManager的实例  
		if (imm != null) {
			imm.hideSoftInputFromWindow(parent.getCurrentFocus().getWindowToken()
					,InputMethodManager.HIDE_NOT_ALWAYS); 
		}		
	}

	/**
	 * 将字符串中的中文转化为拼音,其他字符不变
	 * 
	 * @param inputString
	 * @return
	 */
	public static String getInitial(String inputString) {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);

		char[] input = inputString.trim().toCharArray();
		String output = "";

		try {
			for (int i = 0; i < input.length; i++) {
				if (java.lang.Character.toString(input[i]).matches(
						"[\\u4E00-\\u9FA5]+")) {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(
							input[i], format);
					output += temp[0].toUpperCase();
				} else {
					// if (input[i] > 128 || input[i] < 65) {
					if (input[i] > 128 || input[i] < 48) {
						if (i == 0)
							output += "#";
					} else {
						output += java.lang.Character.toString(input[i])
								.toUpperCase();
					}
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return output;
	}

	public static void copyBean(Object sourceBean, Object targetBean) {

		if (sourceBean == null || targetBean == null) {
			// new RuntimeException("传入对象为空！");
			targetBean = null;
			return;
		}

		Method[] mthsSource = sourceBean.getClass().getMethods();

		Method[] mthsTarget = targetBean.getClass().getMethods();
		for (Method method : mthsTarget) {
			if (method.getName().startsWith("set")) {
				// 复制成员变量值
				copyValue(method, mthsSource, sourceBean, targetBean);
			}
		}
	}

	private static void copyValue(Method methodTarget, Method[] mthsSource,
			Object sourceBean, Object targetBean) {

		String name = "get" + methodTarget.getName().substring(3);
		try {
			for (Method method : mthsSource) {
				if (name.equals(method.getName())) {
					methodTarget.invoke(targetBean, method.invoke(sourceBean));
					break;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	
	public static String getReportType(Context context, int sessionType) {
		Resources rc = context.getResources();
		String type = rc.getString(R.string.report_day_sale);
				
		switch (sessionType) {
			case 0 :
				type = rc.getString(R.string.report_whole_day_sale);
				break;
			case 1 :
				type = rc.getString(R.string.report_breakfast_sale);
				break;
			case 2 :
				type = rc.getString(R.string.report_lunch_sale);
				break;
			case 3 :
				type = rc.getString(R.string.report_dinner_sale);
				break;
			case 4 :
				type = rc.getString(R.string.report_supper_sale);
				break;
		
		}
		return type;
	}
}
