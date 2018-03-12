package com.alfredposclient.http;

import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.LoginResult;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.SyncMsg;
import com.alfredbase.store.sql.RevenueCenterSQL;
import com.alfredbase.utils.CommonUtil;
import com.alfredposclient.global.App;
import com.google.gson.Gson;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class HttpAssembling {
	public static final String CONTENT_TYPE = "text/plain;charset=UTF-8";

	public static StringEntity getLoginParam(int userID, String password,
			String bizID) throws UnsupportedEncodingException {
		StringEntity entity = new StringEntity("{empId:" + userID
				+ ",password:'" + password + "',restaurantKey:'" + bizID + "', version:'" + App.instance.VERSION + "', deviceId:'" +CommonUtil.getLocalMacAddress(App.instance) + "'}");
		return entity;
	}

	public static StringEntity getLoginParam(int userID, String password,
											 String bizID, int machineType) throws UnsupportedEncodingException {
		StringEntity entity = new StringEntity("{empId:" + userID
				+ ",password:'" + password + "',restaurantKey:'" + bizID + "', version:'" + App.instance.VERSION + "', deviceId:'" +CommonUtil.getLocalMacAddress(App.instance) + "', machineType : " + machineType + "}");
		return entity;
	}

	public static StringEntity getTokenParam()
			throws UnsupportedEncodingException {
		Gson gson = new Gson();
		LoginResult loginResult = CoreData.getInstance().getLoginResult();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userKey", loginResult.getUserKey());
		map.put("restaurantKey", loginResult.getRestaurantKey());
		map.put("version", App.instance.VERSION );
		map.put("deviceId", CommonUtil.getLocalMacAddress(App.instance));
		StringEntity entity = new StringEntity(gson.toJson(map));
		return entity;
	}
	
	public static StringEntity getRestaurantParam()
			throws UnsupportedEncodingException {
		Gson gson = new Gson();
		LoginResult loginResult = CoreData.getInstance().getLoginResult();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userKey", loginResult.getUserKey());
		map.put("restaurantKey", loginResult.getRestaurantKey());
		map.put("version", App.instance.VERSION );
		map.put("deviceId", CommonUtil.getLocalMacAddress(App.instance));
		int id = 0;
		if(App.instance.getRevenueCenter() != null){
			id = App.instance.getRevenueCenter().getId();
		}
		RevenueCenter revenueCenter = RevenueCenterSQL.getRevenueCenterById(id);
		map.put("revenueCenter", revenueCenter);
		StringEntity entity = new StringEntity(gson.toJson(map));
		return entity;
	}

	public static StringEntity getPlaceParam(RevenueCenter revenueCenter)
			throws UnsupportedEncodingException {
		Gson gson = new Gson();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userKey", CoreData.getInstance().getLoginResult().getUserKey());
		map.put("restaurantKey", CoreData.getInstance().getLoginResult()
				.getRestaurantKey());
		map.put("revenueId", revenueCenter.getId());
		map.put("version", App.instance.VERSION );
		map.put("deviceId", CommonUtil.getLocalMacAddress(App.instance));
		StringEntity entity = new StringEntity(gson.toJson(map),"UTF-8");
		return entity;
	}

	public static StringEntity getPlaceParam(Map<String, Object> map)
			throws UnsupportedEncodingException {
		Gson gson = new Gson();
		map.put("userKey", CoreData.getInstance().getLoginResult().getUserKey());
		map.put("restaurantKey", CoreData.getInstance().getLoginResult()
				.getRestaurantKey());
		map.put("version", App.instance.VERSION );
		map.put("deviceId", CommonUtil.getLocalMacAddress(App.instance));
		StringEntity entity = new StringEntity(gson.toJson(map),"UTF-8");
		return entity;
	}

	public static StringEntity getItemParam(RevenueCenter revenueCenter) throws UnsupportedEncodingException{
		return getPlaceParam(revenueCenter);
	}

	public static StringEntity getUploadOrderInfoParam(SyncMsg syncMsg)
			throws UnsupportedEncodingException {
		Gson gson = new Gson();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userKey", CoreData.getInstance().getLoginResult().getUserKey());
		map.put("restaurantKey", CoreData.getInstance().getLoginResult()
				.getRestaurantKey());
		map.put("syncMsg", syncMsg);
		map.put("version", App.instance.VERSION );
		map.put("deviceId", CommonUtil.getLocalMacAddress(App.instance));
		StringEntity entity = new StringEntity(gson.toJson(map),"UTF-8");
		return entity;
	}
	
	public static StringEntity getuploadBOHPaidInfo(Map<String, Object> map)
			throws UnsupportedEncodingException {
		Gson gson = new Gson();
		map.put("userKey", CoreData.getInstance().getLoginResult().getUserKey());
		map.put("restaurantKey", CoreData.getInstance().getLoginResult()
				.getRestaurantKey());
		map.put("version", App.instance.VERSION );
		map.put("deviceId", CommonUtil.getLocalMacAddress(App.instance));
		StringEntity entity = new StringEntity(gson.toJson(map),"UTF-8");
		return entity;
	}
	
	public static StringEntity getBindDeviceIdInfo(RevenueCenter revenueCenter)
			throws UnsupportedEncodingException {
		Gson gson = new Gson();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userKey", CoreData.getInstance().getLoginResult().getUserKey());
		map.put("restaurantKey", CoreData.getInstance().getLoginResult()
				.getRestaurantKey());
		map.put("revenueId", revenueCenter.getId());
		map.put("deviceId", CommonUtil.getLocalMacAddress(App.instance));
		map.put("version", App.instance.VERSION );
		StringEntity entity = new StringEntity(gson.toJson(map),"UTF-8");
		return entity;
	}

	public static StringEntity getloadReportInfo(Map<String, Object> map)
			throws UnsupportedEncodingException {
		Gson gson = new Gson();
		map.put("userKey", CoreData.getInstance().getLoginResult().getUserKey());
		map.put("restaurantKey", CoreData.getInstance().getLoginResult()
				.getRestaurantKey());
		map.put("version", App.instance.VERSION );
		map.put("deviceId", CommonUtil.getLocalMacAddress(App.instance));
		StringEntity entity = new StringEntity(gson.toJson(map),"UTF-8");
		return entity;
	}	

	public static StringEntity getloadMonthlySalesReportInfo(Map<String, Object> map)
			throws UnsupportedEncodingException {
		Gson gson = new Gson();
		map.put("userKey", CoreData.getInstance().getLoginResult().getUserKey());
		map.put("restaurantKey", CoreData.getInstance().getLoginResult()
				.getRestaurantKey());
		map.put("version", App.instance.VERSION );
		map.put("deviceId", CommonUtil.getLocalMacAddress(App.instance));
		StringEntity entity = new StringEntity(gson.toJson(map),"UTF-8");
		return entity;
	}	
	
	public static StringEntity getloadMonthlyPLUReportInfo(Map<String, Object> map)
			throws UnsupportedEncodingException {
		Gson gson = new Gson();
		map.put("userKey", CoreData.getInstance().getLoginResult().getUserKey());
		map.put("restaurantKey", CoreData.getInstance().getLoginResult()
				.getRestaurantKey());
		map.put("version", App.instance.VERSION );
		map.put("deviceId", CommonUtil.getLocalMacAddress(App.instance));
		StringEntity entity = new StringEntity(gson.toJson(map),"UTF-8");
		return entity;
	}	
	
	public static StringEntity encapsulateBaseInfo(Map<String, Object> map)
			throws UnsupportedEncodingException {
		Gson gson = new Gson();
		map.put("userKey", CoreData.getInstance().getLoginResult().getUserKey());
		map.put("restaurantKey", CoreData.getInstance().getLoginResult()
				.getRestaurantKey());
		map.put("version", App.instance.VERSION );
		map.put("deviceId", CommonUtil.getLocalMacAddress(App.instance));
		StringEntity entity = new StringEntity(gson.toJson(map),"UTF-8");
		return entity;
	}	
	public static StringEntity getAppVersion(Map<String, Object> map)
			throws UnsupportedEncodingException {
		Gson gson = new Gson();
		map.put("userKey", "118v4v8");
		map.put("restaurantKey", "19yyrpy");
		map.put("version", App.instance.VERSION );
		map.put("deviceId", CommonUtil.getLocalMacAddress(App.instance));
		StringEntity entity = new StringEntity(gson.toJson(map),"UTF-8");
		return entity;
	}

	
}
