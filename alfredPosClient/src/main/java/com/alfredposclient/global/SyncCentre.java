package com.alfredposclient.global;

import android.content.Context;
import android.os.Handler;

import com.alfredbase.APPConfig;
import com.alfredbase.BaseActivity;
import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.http.APIName;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.SyncMsg;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.model.KDSDevice;
import com.alfredbase.javabean.model.PushMessage;
import com.alfredbase.javabean.model.WaiterDevice;
import com.alfredbase.utils.CommonUtil;
import com.alfredposclient.http.HTTPKDSRequest;
import com.alfredposclient.http.HTTPWaiterRequest;
import com.alfredposclient.http.HttpAPI;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class SyncCentre {

	private Timer timer;

	private TimerTask timerTask;

	private static AsyncHttpClient httpClient;

	private static SyncCentre instance;
	private static SyncHttpClient syncHttpClient;
	public static final int MODE_FIRST_SYNC = 1;
	public static final int MODE_PUSH_SYNC = 2;
	
	private SyncCentre() {

	}

	public static SyncCentre getInstance() {
		init();
		return instance;
	}

	private static void init() {
		if (instance == null) {
			instance = new SyncCentre();

			httpClient = new AsyncHttpClient();
			httpClient.addHeader("Connection", "close");
			httpClient.setMaxRetriesAndTimeout(0, 5 * 1000);
			httpClient.setTimeout(20 * 1000);
			syncHttpClient = new SyncHttpClient();
			syncHttpClient.addHeader("Connection", "close");
			syncHttpClient.setTimeout(20 * 1000);
			syncHttpClient.setMaxRetriesAndTimeout(0, 5 * 1000);
		}
	}
	
/*  We are using JOB. no need this function
	public void startTimerTask(final BaseActivity baseActivity,final Long bizDate) {
		if (timer == null) {
			timer = new Timer();
			timerTask = new TimerTask() {
				@Override
				public void run() {
					syncOrderInfo(baseActivity,bizDate);
				}
			};
			// 30分钟跑一次定时任务
			timer.schedule(timerTask, 0, 30 * 60 * 1000);
		}
	}
*/
	public void stopTimerTask() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public void login(Context context, Map<String, Object> parameters,
			Handler handler) {
		HttpAPI.login(context, parameters,
				getAbsoluteUrl(APIName.LOGIN_LOGINVERIFY), httpClient, handler);
	}

	public void getRestaurantInfo(Context context,
			Map<String, Object> parameters, Handler handler) {
		HttpAPI.getRestaurantInfo(context, parameters,
				getAbsoluteUrl(APIName.RESTAURANT_GETRESTAURANTINFO),
				httpClient, handler, MODE_FIRST_SYNC);
	}

	public void syncCommonData(Context context, Handler handler) {
		HttpAPI.getUser(context, getAbsoluteUrl(APIName.USER_GETUSER),
				httpClient, handler, MODE_FIRST_SYNC);

		HttpAPI.getItemCategory(context,
				getAbsoluteUrl(APIName.ITEM_GETITEMCATEGORY), httpClient, handler, MODE_FIRST_SYNC);

		HttpAPI.getModifier(context, getAbsoluteUrl(APIName.ITEM_GETMODIFIER),
				httpClient, handler, MODE_FIRST_SYNC);

		HttpAPI.getTax(context, getAbsoluteUrl(APIName.TAX_GETTAX), httpClient, handler, MODE_FIRST_SYNC);

		HttpAPI.getHappyHour(context,
				getAbsoluteUrl(APIName.HAPPYHOUR_GETHAPPYHOUR), httpClient, handler, MODE_FIRST_SYNC);
	}

	public void getPlaceInfo(Context context, Map<String, Object> parameters,
			Handler handler) {
		HttpAPI.getItem(context, getAbsoluteUrl(APIName.ITEM_GETITEM),
				httpClient, handler, MODE_FIRST_SYNC);
//		HttpAPI.getPlaceInfo(context, parameters,
//				getAbsoluteUrl(APIName.RESTAURANT_GETPLACEINFO), httpClient,
//				handler, MODE_FIRST_SYNC);
		HttpAPI.getPlaceTable(context, getAbsoluteUrl(APIName.RESTAURANT_GETPLACEINFONEW), httpClient, parameters, handler);
	}

	public void getBindDeviceIdInfo(Context context,
			Map<String, Object> parameters, Handler handler) {
		HttpAPI.getBindDeviceIdInfo(context, parameters,
				getAbsoluteUrl(APIName.RESTAURANT_BINDDEVICEID), httpClient,
				handler);
	}

	public void getBOHSettlement(Context context, Handler handler) {
		HttpAPI.getBOHSettlement(context,
				getAbsoluteUrl(APIName.BOH_GETBOHHOLDUNPAID), httpClient,
				handler);
	}

	public void upDateBOHHoldPaid(Context context,
			Map<String, Object> parameters, Handler handler) {
		HttpAPI.uploadBOHPaidInfo(context,
				getAbsoluteUrl(APIName.BOH_UPDATEBOHHOLDPAID), httpClient,
				parameters, handler);
	}

	public void logout(Context context, Handler handler) {
		HttpAPI.logout(context, getAbsoluteUrl(APIName.LOGIN_LOGOUT),
				httpClient, handler);
	}

	/*
	 *  Sync Order Info to Cloud : for JOB
	 * */
	public void cloudSyncUploadOrderInfo( BaseActivity context,
			SyncMsg syncMsg, Handler handler) {
			HttpAPI.cloudSync(context, syncMsg,
					getAbsoluteUrl("receive/dataMsg"), syncHttpClient);
	}
	/*
	 *  Sync X/Z Report Info to Cloud : for JOB
	 * */	
	public void cloudSyncUploadReportInfo( BaseActivity context,
			SyncMsg syncMsg, Handler handler) {
				HttpAPI.cloudSync(context, syncMsg,
						getAbsoluteUrl("receive/dataMsg"), syncHttpClient);
	}
	
	/*load day sales report from cloud */
	public void loadCloudDaySalesReport( BaseActivity context,
			Map<String, Object> parameters, Handler handler) {
				HttpAPI.loadCloudXZReport(context, 
						getAbsoluteUrl(APIName.RESTAURANT_DAYSALES_REPORT), 
						httpClient, parameters, handler);
	}
	
	public void loadCloudMonthlySalesReport( BaseActivity context,
			Map<String, Object> parameters, Handler handler) {
				HttpAPI.loadCloudMonthlySalesReport(context, 
						getAbsoluteUrl(APIName.RESTAURANT_MONTHLY_SALE_REPORT), 
						httpClient, parameters, handler);
	}
	
	public void loadCloudMonthlyPLUReport( BaseActivity context,
			Map<String, Object> parameters, Handler handler) {
				HttpAPI.loadCloudMonthlyPLUReport(context, 
						getAbsoluteUrl(APIName.RESTAURANT_MONTHLY_PLU_REPORT), 
						httpClient, parameters, handler);
	}
	
	public void getOrderFromApp( BaseActivity context,
			Map<String, Object> parameters) {
				HttpAPI.getOrderFromApp(context, 
						getAbsoluteUrl(APIName.POSORDER_GETORDERBYQRCODE), 
						httpClient, parameters);
	}
	
	public void pushCommonData(Context context, String type, Handler handler) {
		if (type.equals(PushMessage.HAPPY_HOURS)) {
			HttpAPI.getHappyHour(context,
					getAbsoluteUrl(APIName.HAPPYHOUR_GETHAPPYHOUR), httpClient, handler, MODE_PUSH_SYNC);
		} else if (type.equals(PushMessage.PRINTER)) {
			HttpAPI.getRestaurantInfo(context, null,
					getAbsoluteUrl(APIName.RESTAURANT_GETRESTAURANTINFO),
					httpClient, handler, MODE_PUSH_SYNC);
		} else if (type.equals(PushMessage.ITEM)) {
			HttpAPI.getItem(context, getAbsoluteUrl(APIName.ITEM_GETITEM),
					httpClient, handler, MODE_PUSH_SYNC);
			HttpAPI.getItemCategory(context,
					getAbsoluteUrl(APIName.ITEM_GETITEMCATEGORY), httpClient, null, MODE_PUSH_SYNC);
		} else if (type.equals(PushMessage.MODIFIER)) {
			HttpAPI.getModifier(context,
					getAbsoluteUrl(APIName.ITEM_GETMODIFIER), httpClient, handler, MODE_PUSH_SYNC);
		} else if (type.equals(PushMessage.USER)) {
			HttpAPI.getUser(context, getAbsoluteUrl(APIName.USER_GETUSER),
					httpClient, handler, MODE_PUSH_SYNC);
		} else if (type.equals(PushMessage.RESTAURANT) || type.equals(PushMessage.REST_CONFIG)) {
			HttpAPI.getRestaurantInfo(context, null,
					getAbsoluteUrl(APIName.RESTAURANT_GETRESTAURANTINFO),
					httpClient, handler, MODE_PUSH_SYNC);
		} else if (type.equals(PushMessage.PLACE_TABLE)) {
			RevenueCenter revenueCenter = App.instance.getRevenueCenter();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("revenueCenter", revenueCenter);
//			HttpAPI.getPlaceTable(context, parameters,
//					getAbsoluteUrl(APIName.RESTAURANT_GETPLACEINFO),
//					httpClient, handler);
		} else if (type.equals(PushMessage.TAX)) {
			HttpAPI.getTax(context, getAbsoluteUrl(APIName.TAX_GETTAX),
					httpClient, handler, MODE_PUSH_SYNC);
		} else if(type.equals(PushMessage.PUSH_ORDER)){
			
		}
	}
	
	public void updatePassword(Context context,Map<String, Object> map, Handler handler){
		User user = (User) map.get("user");
		String newPassword = (String) map.get("newPassword");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empId", user.getEmpId());
		parameters.put("oldPassword", user.getPassword());
		parameters.put("newPassword", newPassword);
		HttpAPI.updatePassword(context, getAbsoluteUrl(APIName.USER_UPDATEPASSWORD), httpClient, parameters, handler, user);
	}

	public void getAppOrderById(Context context,Map<String, Object> parameters, Handler handler, boolean canCheck){
		HttpAPI.getAppOrderById(context, getAbsoluteUrl(APIName.POSORDER_GETPAIEDAPPORDERBYID), syncHttpClient, parameters, handler, canCheck);
	}
	public void appOrderRefund(Context context, int appOrderId, Handler handler){
		HttpAPI.appOrderRefund(context, getAbsoluteUrl(APIName.APP_ORDER_REFUND), httpClient, appOrderId, handler);
	}
	public void updateTableStatusForApp(Context context, int tableId, Handler handler){
		HttpAPI.updateTableStatusForApp(context, getAbsoluteUrl(APIName.TABLE_UPDATETABLESTATUS), httpClient, tableId, handler);
	}
	public void getAllAppOrder(Context context,Map<String, Object> parameters, Handler handler){
		HttpAPI.getAllAppOrder(context, getAbsoluteUrl(APIName.POSORDER_GETALLPAIEDAPPORDER), httpClient, parameters, handler);
	}
	public void updateAppOrderStatus(Context context, SyncMsg syncMsg){
		HttpAPI.updateAppOrderStatus(context, getAbsoluteUrl(APIName.POSORDER_UPDATEAPPORDERSTATUS), syncHttpClient, syncMsg);
	}
	public void recevingAppOrderStatus(Context context, int appOrderId, Handler handler){
		HttpAPI.recevingAppOrder(context, getAbsoluteUrl(APIName.UPDATE_MANUALAPPORDERSTATUS), httpClient, appOrderId, handler);
	}

	public void updatePlaceTable(Context context,Map<String, Object> parameters, Handler handler){
		HttpAPI.updatePlaceTable(context,getAbsoluteUrl(APIName.RESTAURANT_CHANGEPLACE),httpClient, parameters, handler);
	}
	public void getPlaceTable(Context context,Map<String, Object> parameters, Handler handler){
		HttpAPI.getPlaceTable(context,getAbsoluteUrl(APIName.RESTAURANT_GETPLACEINFONEW),httpClient, parameters, handler);
	}
	public void registStoredCard(Context context,Map<String, Object> parameters, Handler handler){
		HttpAPI.registStoredCard(context,getAbsoluteUrl(APIName.MEMBERSHIP_ACTIVATECARD),httpClient, parameters, handler);
	}
	public void updateStoredCardValue(Context context,Map<String, Object> parameters, Handler handler){
		HttpAPI.updateStoredCardValue(context,getAbsoluteUrl(APIName.MEMBERSHIP_OPERATEBALANCE),httpClient, parameters, handler);
	}
	public void closeStoredCard(Context context, Map<String, Object> parameters, Handler handler){
		HttpAPI.closeStoredCardValue(context,getAbsoluteUrl(APIName.MEMBERSHIP_REPORTCARD),httpClient, parameters, handler);
	}
	public void changeStoredCard(Context context, Map<String, Object> parameters, Handler handler){
		HttpAPI.changeStoredCardValue(context,getAbsoluteUrl(APIName.MEMBERSHIP_REATTENDCARD),httpClient, parameters, handler);
	}
	public void getAppVersion(Context context, Map<String, Object> parameters, int applicationTypes){
		HttpAPI.getAppVersion(context,getAbsoluteUrl(APIName.SOFTWARE_GETVERSION),httpClient, parameters, applicationTypes);
	}
//	public void downloadApk(String url){
//		HttpAPI.downloadApk(url,new AsyncHttpClient());
//	}

	// Backend Server IP
	private String getAbsoluteUrl(String relativeUrl) {
		if (App.instance.isDebug) {
			return "http://172.16.0.190:8087/alfred-api/" + relativeUrl;
//			return "http://192.168.0.120:8083/alfred-api/" + relativeUrl;
		} else if (App.instance.isOpenLog) {
			return "http://139.224.17.126/alfred-api/" + relativeUrl;
		} else {
//			return "http://54.169.45.214/alfred-api/" + relativeUrl;52.77.208.125
			return "http://www.servedbyalfred.com/alfred-api/" + relativeUrl;
		}
	}

	// private String getAbsoluteKDSUrl(String relativeUrl) {
	// return "http://192.168.0.18"
	// +":"+APPConfig.KDS_HTTP_SERVER_PORT+"/"+relativeUrl;
	// }

	// WaiterBacken Server URLList
//	private List<String> getAbsoluteWaiterUrlList1(String relativeUrl) {
//		List<String> urls = new ArrayList<String>();
//		Map<Integer, WaiterDevice> waiterDeviceMap = App.instance
//				.getWaiterDevices();
//		Set<Integer> key = waiterDeviceMap.keySet();
//		for (Integer index : key) {
//			WaiterDevice waiterDevice = waiterDeviceMap.get(index);
//			urls.add("http://" + waiterDevice.getIP() + ":"
//					+ APPConfig.WAITER_HTTP_SERVER_PORT + "/" + relativeUrl);
//		}
//		return urls;
//	}

	// KDSBacken Server URLList
//	private List<String> getAbsoluteKDSUrlList1(String relativeUrl) {
//		List<String> urls = new ArrayList<String>();
//		Map<Integer, KDSDevice> kdsDeviceMap = App.instance.getKDSDevices();
//		Set<Integer> key = kdsDeviceMap.keySet();
//		for (Integer index : key) {
//			KDSDevice kdsDevice = kdsDeviceMap.get(index);
//			urls.add("http://" + kdsDevice.getIP() + ":"
//					+ APPConfig.KDS_HTTP_SERVER_PORT + "/" + relativeUrl);
//		}
//		return urls;
//	}

	private String getAbsoluteKDSUrlForJob(KDSDevice kdsDevice,
			String relativeUrl) {
		return "http://" + kdsDevice.getIP() + ":"
				+ APPConfig.KDS_HTTP_SERVER_PORT + "/" + relativeUrl;
	}

	/**
	 * forWaiter
	 */
	/*
	 * notify waiter to get notification list parameters: {"total":##}
	 */
	public void notifyWaiterToGetNotifications(final BaseActivity context,
			int KotNotificationqty) {

		Map<Integer, WaiterDevice> waiterDeviceMap = App.instance.getWaiterDevices();
		Set<Integer> key = waiterDeviceMap.keySet();
		for (Integer index : key) {
			WaiterDevice waiterDevice = waiterDeviceMap.get(index);
			String url = "http://" + waiterDevice.getIP() + ":" + APPConfig.WAITER_HTTP_SERVER_PORT + "/" + APIName.KOT_NOTIFICATION;
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("total", KotNotificationqty);
			HTTPWaiterRequest.sendKotNotification(context, parameters, url, waiterDevice.clone(), httpClient);
		}

	}

	public void sendSessionClose(Context context, Map<String, Object> parameters) {

		Map<Integer, WaiterDevice> waiterDeviceMap = App.instance.getWaiterDevices();
		Set<Integer> key = waiterDeviceMap.keySet();
		for (Integer index : key) {
			WaiterDevice waiterDevice = waiterDeviceMap.get(index);
			String url = "http://" + waiterDevice.getIP() + ":" + APPConfig.WAITER_HTTP_SERVER_PORT + "/" + APIName.CLOSE_SESSION;
			HTTPWaiterRequest.sendSessionClose(context, parameters, url, waiterDevice.clone(), httpClient);
		}
		
		Map<Integer, KDSDevice> kdsDeviceMap = App.instance.getKDSDevices();
		key = kdsDeviceMap.keySet();
		for (Integer index : key) {
			KDSDevice kdsDevice = kdsDeviceMap.get(index);
			String url = "http://" + kdsDevice.getIP() + ":"
					+ APPConfig.KDS_HTTP_SERVER_PORT + "/" + APIName.CLOSE_SESSION;
			HTTPKDSRequest.sendSessionClose(context, parameters, url,kdsDevice.clone(), httpClient);
		}
	}

	public void transferTable(Context context, Map<String, Object> parameters) {

		Map<Integer, WaiterDevice> waiterDeviceMap = App.instance.getWaiterDevices();
		Set<Integer> key = waiterDeviceMap.keySet();
		for (Integer index : key) {
			WaiterDevice waiterDevice = waiterDeviceMap.get(index);
			String url = "http://" + waiterDevice.getIP() + ":" + APPConfig.WAITER_HTTP_SERVER_PORT + "/" + APIName.TRANSFER_TABLE;
			HTTPWaiterRequest.transferTable(context, parameters, url, waiterDevice.clone(), httpClient);
		}

	}

	/**
	 * for KDS
	 */

	/* sync KOT data to KDS */
	public void syncSubmitKotToKDS(KDSDevice kdsDevice, BaseActivity context,
			Map<String, Object> parameters, Handler handler) throws Throwable {
		String url = getAbsoluteKDSUrlForJob(kdsDevice, APIName.SUBMIT_NEW_KOT);
		HTTPKDSRequest.syncSubmitKot(context, parameters, url, kdsDevice.clone(), syncHttpClient,
				handler);

	}

	public void syncTransferTable(KDSDevice kdsDevice, BaseActivity context,
			final Map<String, Object> parameters, Handler handler) {
		String url = getAbsoluteKDSUrlForJob(kdsDevice, APIName.TRANSFER_KOT);
		HTTPKDSRequest.transferTable(context, parameters, url, kdsDevice.clone(),syncHttpClient);

	}

	/**
	 * for callNum App
	 * @param context
	 * @param num
     */
	public void callAppNo(final Context context, String num) {
		String url ="http://" + App.instance.getCallAppIp() + ":8080/";
		HttpAPI.callAppNo(context, url, syncHttpClient, num);

	}

	//3rd party services
	public String requestAlipayUrl(final Map<String, Object> parameters) {
		//{restaurantKey:"h6s235",userKey:"19x6ljc",revenueId:27,orderId:123243,billNo:12312,amount:100}
		String url = null;
		StringBuffer param = new StringBuffer( "userKey="+CoreData.getInstance().getLoginResult().getUserKey()+"&");
		
		if (App.instance.isDebug) {
			url = "http://218.244.136.120:8080/alfred-api/" + APIName.REQUEST_ALIPAY;
			param.append("amount=0.01&");
		} else if (App.instance.isOpenLog) {
			url =  "http://218.244.136.120:8080/alfred-api/" + APIName.REQUEST_ALIPAY;
			param.append("amount=0.01&");			
		} else {
			if (App.instance.countryCode == ParamConst.CHINA) {
				url =  "http://121.40.168.178/alfred-api/" + APIName.REQUEST_ALIPAY;
			}else {
			    url =  "http://www.servedbyalfred.com/alfred-api/" + APIName.REQUEST_ALIPAY;
			}
			param.append("amount="+parameters.get("amount")+"&");
		}

		param.append("restaurantKey="+CoreData.getInstance().getLoginResult()
				.getRestaurantKey()+"&");
		param.append("version="+App.instance.VERSION+"&");
		param.append("deviceId="+CommonUtil.getLocalMacAddress(App.instance)+"&");
		param.append("revenueId="+App.instance.getRevenueCenter().getId()+"&");
		param.append("orderId="+parameters.get("orderId")+"&");
		param.append("billNo="+parameters.get("billNo")+"&");
		param.append("orderCreateTime="+parameters.get("orderCreateTime")+"&");
		param.append("appOrderId="+(parameters.get("appOrderId") == null ? "0" : parameters.get("appOrderId")));
		
		return (url+'?'+param);
		//HttpAPI.submitAlipay(context, url, httpClient, parameters);
	}
	
	public String getAlipayVerifyReturnUrl(){
		return getAbsoluteUrl("alipay/verifyReturnUrl");
	}
	
	public String getAlipayVerifyErrorNotifyUrl(){
		return getAbsoluteUrl("alipay/verifyErrorNotifyUrl");
	}
	
	public String getURLStart(){
		return getAbsoluteUrl("");
	}
}
