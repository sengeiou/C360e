package com.alfredbase.utils;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.alfredbase.BaseActivity;
import com.alfredbase.R;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

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
		if (Build.VERSION.SDK_INT < 23) {
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifi.getConnectionInfo();
			LogUtil.d("LocalMacAddress", wifiInfo.getMacAddress().toString());
			return wifiInfo.getMacAddress().toString();
		}else{
			return getLocalMacAddress1(context);
		}
	}

	public static String getLocalMacAddress1(Context context){

		String mac_s = "";
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(BaseActivity.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
		if (ni != null) {
		}
		try {
			byte[] mac;
			NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress
					.getByName(getLocalIpAddress()));
			mac = ne.getHardwareAddress();
			mac_s = byte2hex(mac);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(!TextUtils.isEmpty(mac_s)){
			return mac_s.replace('%', ':').toUpperCase();
		}
		String result = "";
		String Mac = "";
		result = callCmd("busybox ifconfig","HWaddr");

		//如果返回的result == null，则说明网络不可取
		if(result==null){
			result = "";
//            return "网络出错，请检查网络";
		}

		//对该行数据进行解析
		//例如：eth0      Link encap:Ethernet  HWaddr 00:16:E8:3E:DF:67
		if(result.length()>0 && result.contains("HWaddr")==true){
			Mac = result.substring(result.indexOf("HWaddr")+6, result.length()-1);
			LogUtil.i("test","Mac:"+Mac+" Mac.length: "+Mac.length());

            /*if(Mac.length()>1){
                Mac = Mac.replaceAll(" ", "");
                result = "";
                String[] tmp = Mac.split(":");
                for(int i = 0;i<tmp.length;++i){
                    result +=tmp[i];
                }
            }*/
			result = Mac;
			LogUtil.i("test",result+" result.length: "+result.length());
		}
		return result.trim().toUpperCase();
	}

	public static String byte2hex(byte[] b) // 二行制转字符串
	{
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
			if (n < b.length - 1)
				hs = hs + "%";
		}
		return hs.toUpperCase();
	}

	private static String callCmd(String cmd,String filter) {
		String result = "";
		String line = "";
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			InputStreamReader is = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(is);

			//执行命令cmd，只取结果中含有filter的这一行
			while ((line = br.readLine ()) != null && line.contains(filter)== false) {
				//result += line;
//                Log.i("test","line: "+line);
			}

			result = line;
//            Log.i("test","result: "+result);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
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

	/**
	 * 颜色换算
	 * @param color
	 * @return
	 */
	public static int[] getRgb(int color){
		int[] colors = new int[3];
		colors[0] = (color & 0xff0000) >> 16;
		colors[1] = (color & 0x00ff00) >> 8;
		colors[2] = (color & 0x0000ff);
		return colors;
	}

	/**
	 * 判断是不是深颜色
	 * @return
	 */
	public static boolean isShenRGB(int[] colors){
		int grayLevel = (int) (colors[0] * 0.299 + colors[1] * 0.587 + colors[2] * 0.114);
		if(grayLevel>=192){
			return true;
		}
		return false;
	}
}
