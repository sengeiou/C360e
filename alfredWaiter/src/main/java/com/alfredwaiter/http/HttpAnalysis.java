package com.alfredwaiter.http;

import android.os.Handler;

import com.alfredbase.global.CoreData;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.HappyHour;
import com.alfredbase.javabean.HappyHourWeek;
import com.alfredbase.javabean.ItemCategory;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.ItemHappyHour;
import com.alfredbase.javabean.ItemMainCategory;
import com.alfredbase.javabean.ItemModifier;
import com.alfredbase.javabean.Modifier;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.OrderDetailTax;
import com.alfredbase.javabean.OrderModifier;
import com.alfredbase.javabean.PlaceInfo;
import com.alfredbase.javabean.Printer;
import com.alfredbase.javabean.Restaurant;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.TableInfo;
import com.alfredbase.javabean.Tax;
import com.alfredbase.javabean.TaxCategory;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.model.MainPosInfo;
import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.javabean.model.SessionStatus;
import com.alfredbase.javabean.model.TableAndKotNotificationList;
import com.alfredbase.store.Store;
import com.alfredbase.store.sql.HappyHourSQL;
import com.alfredbase.store.sql.HappyHourWeekSQL;
import com.alfredbase.store.sql.ItemCategorySQL;
import com.alfredbase.store.sql.ItemDetailSQL;
import com.alfredbase.store.sql.ItemHappyHourSQL;
import com.alfredbase.store.sql.ItemMainCategorySQL;
import com.alfredbase.store.sql.ItemModifierSQL;
import com.alfredbase.store.sql.ModifierSQL;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.OrderDetailTaxSQL;
import com.alfredbase.store.sql.OrderModifierSQL;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredbase.store.sql.PlaceInfoSQL;
import com.alfredbase.store.sql.PrinterSQL;
import com.alfredbase.store.sql.RestaurantSQL;
import com.alfredbase.store.sql.RevenueCenterSQL;
import com.alfredbase.store.sql.TableInfoSQL;
import com.alfredbase.store.sql.TaxCategorySQL;
import com.alfredbase.store.sql.TaxSQL;
import com.alfredbase.store.sql.UserSQL;
import com.alfredwaiter.activity.EmployeeID;
import com.alfredwaiter.activity.KOTNotification;
import com.alfredwaiter.activity.MainPage;
import com.alfredwaiter.global.App;
import com.alfredwaiter.global.SyncCentre;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpAnalysis {
	public static void employeeId(int statusCode, Header[] headers,
			byte[] responseBody, Handler handler) {
		try {
			Gson gson = new Gson();
			JSONObject object = new JSONObject(new String(responseBody));
			List<RevenueCenter> revenueCenters = gson.fromJson(
					object.getString("revenueCenters"),
					new TypeToken<ArrayList<RevenueCenter>>() {
					}.getType());
			List<Integer> revenueCenterIds = gson.fromJson(
					object.getString("revenueCenterIds"),
					new TypeToken<ArrayList<Integer>>() {
					}.getType());
			RevenueCenter revenue = gson.fromJson(object.getString("revenue"), RevenueCenter.class);
			User user = gson.fromJson(object.optString("user"), User.class);
			App.instance.setUser(user);
			RevenueCenterSQL.deleteAllRevenueCenter();
			RevenueCenterSQL.addRevenueCenters(revenueCenters);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("revenueCenters", revenueCenters);
			map.put("revenueCenterIds", revenueCenterIds);
			map.put("revenue", revenue);
			handler.sendMessage(handler.obtainMessage(ResultCode.SUCCESS, map));
		} catch (JSONException e) {
			handler.sendMessage(handler.obtainMessage(ResultCode.CONNECTION_FAILED,e));
			e.printStackTrace();
		}

	}

	public static String login(int statusCode, Header[] headers,
			byte[] responseBody) {
		try {
			Gson gson = new Gson();
			JSONObject object = new JSONObject(new String(responseBody));
			String userKey = object.optString("userKey");
			String currencySymbol = object.optString("currencySymbol");
			boolean isDouble = object.optBoolean("isDouble");
			SessionStatus sessionStatus = gson.fromJson(
					object.optString("session"), SessionStatus.class);
			MainPosInfo mainPosInfo = gson.fromJson(object.optString("mainPosInfo"), MainPosInfo.class);

			SessionStatus localSessionStatus = App.instance.getSessionStatus();
			if (localSessionStatus == null
					|| localSessionStatus.getSession_status() != sessionStatus
							.getSession_status()) {
				OrderSQL.deleteAllOrder();
				OrderDetailSQL.deleteAllOrderDetail();
				OrderModifierSQL.deleteAllOrderModifier();
				OrderDetailTaxSQL.deleteAllOrderDetailTax();
			}
			Store.saveObject(App.instance, Store.SESSION_STATUS, sessionStatus);
			App.instance.setMainPosInfo(mainPosInfo);
			App.instance.setSessionStatus(sessionStatus);
			App.instance.setCurrencySymbol(currencySymbol, isDouble);
			CoreData.getInstance().setUserKey(userKey);
			return userKey;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void getRestaurantInfo(int statusCode, Header[] headers,
			byte[] responseBody) {
		try {
			JSONObject object = new JSONObject(new String(responseBody));
			Gson gson = new Gson();
			List<RevenueCenter> revenueCenters = gson.fromJson(
					object.getString("revenueList"),
					new TypeToken<ArrayList<RevenueCenter>>() {
					}.getType());

			CoreData.getInstance().setRevenueCenters(revenueCenters);
			RevenueCenterSQL.deleteAllRevenueCenter();
			RevenueCenterSQL.addRevenueCenters(revenueCenters);

			List<Printer> printers = gson.fromJson(
					object.getString("printerList"),
					new TypeToken<ArrayList<Printer>>() {
					}.getType());

			CoreData.getInstance().setPrinters(printers);
			PrinterSQL.deleteAllPrinter();
			PrinterSQL.addPrinters(printers);
			
			Restaurant restaurant = gson.fromJson(
					object.getString("restaurant"), Restaurant.class);
			CoreData.getInstance().setRestaurant(restaurant);
			RestaurantSQL.deleteAllRestaurant();
			RestaurantSQL.addRestaurant(restaurant);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static List<User> getUsers(int statusCode, Header[] headers,
			byte[] responseBody) {
		List<User> result = null;
		try {
			JSONObject object = new JSONObject(new String(responseBody));
			Gson gson = new Gson();
			result = gson.fromJson(object.getString("userList"),
					new TypeToken<ArrayList<User>>() {
					}.getType());
			CoreData.getInstance().setUsers(result);
			UserSQL.deleteAllUser();
			UserSQL.addUsers(result);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void getPlaceInfo(int statusCode, Header[] headers,
			byte[] responseBody) {
		try {
			JSONObject object = new JSONObject(new String(responseBody));
			Gson gson = new Gson();

			List<PlaceInfo> places = gson.fromJson(object.getString("placeList"),
					new TypeToken<ArrayList<PlaceInfo>>() {
					}.getType());
//			CoreData.getInstance().setPlaceList(places);
//			PlacesSQL.deleteAllPlaces();
//			PlacesSQL.addPlacesList(places);
			PlaceInfoSQL.deleteAllPlaceInfo();
			PlaceInfoSQL.addPlaceInfoList(places);

			List<TableInfo> tables = gson.fromJson(object.getString("tableList"),
					new TypeToken<ArrayList<TableInfo>>() {
					}.getType());
//			CoreData.getInstance().setTableList(tables);
//			TablesSQL.deleteAllTables();
//			TablesSQL.addTablesList(tables);
			TableInfoSQL.deleteAllTableInfo();
			TableInfoSQL.addTablesList(tables);

			if(object.has("printMap")){
				Map<Integer, PrinterDevice> printerDevices = gson.fromJson(object.getString("printMap"),
						new TypeToken<Map<Integer, PrinterDevice>>() {
						}.getType());
				App.instance.setPrinterDevices(printerDevices);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static Order selectTables(int statusCode, Header[] headers,
			byte[] responseBody) {
		Order order = null;
		try {
			JSONObject object = new JSONObject(new String(responseBody));
			Gson gson = new Gson();
			order = gson.fromJson(object.getJSONObject("order").toString(),
					Order.class);
			List<ItemDetail> itemDetails = gson.fromJson(object.getString("tempItems"),
					new TypeToken<ArrayList<ItemDetail>>() {
					}.getType());
			if (!itemDetails.isEmpty()) {
				ItemDetailSQL.addItemDetailList(itemDetails);
				CoreData.getInstance().setItemDetails(ItemDetailSQL.getAllItemDetail());
			}
			if(object.has("orderDetails")){
				List<OrderDetail> orderDetails = gson.fromJson(object.getString("orderDetails"), 
						new TypeToken<ArrayList<OrderDetail>>() {}.getType());
				if(!orderDetails.isEmpty()){
					OrderDetailSQL.addOrderDetailList(orderDetails);
				}
				
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return order;
	}

	public static void getItemCategory(int statusCode, Header[] headers,
			byte[] responseBody) {
		try {
			JSONObject object = new JSONObject(new String(responseBody));
			Gson gson = new Gson();
			List<ItemCategory> itemCategoryList = gson.fromJson(
					object.getString("subCategoryList"),
					new TypeToken<ArrayList<ItemCategory>>() {
					}.getType());
			CoreData.getInstance().setItemCategories(itemCategoryList);
			ItemCategorySQL.deleteAllItemCategory();
			ItemCategorySQL.addItemCategoryList(itemCategoryList);

			List<ItemMainCategory> itemMainCategories = gson.fromJson(
					object.getString("mainCategoryList"),
					new TypeToken<ArrayList<ItemMainCategory>>() {
					}.getType());
			CoreData.getInstance().setItemMainCategories(itemMainCategories);
			ItemMainCategorySQL.deleteAllItemMainCategory();
			ItemMainCategorySQL.addItemMainCategory(itemMainCategories);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void getItemDetail(int statusCode, Header[] headers,
			byte[] responseBody) {
		try {
			JSONObject object = new JSONObject(new String(responseBody));
			Gson gson = new Gson();
			List<ItemDetail> itemDetailList = gson.fromJson(
					object.getString("itemList"),
					new TypeToken<ArrayList<ItemDetail>>() {
					}.getType());
			ItemDetailSQL.deleteAllItemDetail();
			ItemDetailSQL.addItemDetailList(itemDetailList);
			CoreData.getInstance().setItemDetails(
					ItemDetailSQL.getAllItemDetail());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void getAllModifier(int statusCode, Header[] headers,
			byte[] responseBody) {
		try {
			JSONObject object = new JSONObject(new String(responseBody));
			Gson gson = new Gson();
			List<ItemModifier> itemModifierlList = gson.fromJson(
					object.getString("itemModifierList"),
					new TypeToken<ArrayList<ItemModifier>>() {
					}.getType());

			ItemModifierSQL.deleteAllItemModifier();
			ItemModifierSQL.addItemModifierList(itemModifierlList);
			CoreData.getInstance().setItemModifiers(itemModifierlList);

			List<Modifier> modifierList = gson.fromJson(
					object.getString("modifierList"),
					new TypeToken<ArrayList<Modifier>>() {
					}.getType());

			ModifierSQL.deleteAllModifier();
			ModifierSQL.addModifierList(modifierList);
			CoreData.getInstance().setModifierList(modifierList);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void getTax(int statusCode, Header[] headers,
			byte[] responseBody) {
		try {
			JSONObject object = new JSONObject(new String(responseBody));
			Gson gson = new Gson();
			List<TaxCategory> taxCategories = gson.fromJson(
					object.getString("taxCategoryList"),
					new TypeToken<ArrayList<TaxCategory>>() {
					}.getType());

			CoreData.getInstance().setTaxCategories(taxCategories);
			TaxCategorySQL.deleteAllTaxCategory();
			TaxCategorySQL.addTaxCategorys(taxCategories);

			List<Tax> taxs = gson.fromJson(object.getString("taxList"),
					new TypeToken<ArrayList<Tax>>() {
					}.getType());
			CoreData.getInstance().setTaxs(taxs);
			TaxSQL.deleteAllTax();
			TaxSQL.addTaxs(taxs);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void getHappyHour(int statusCode, Header[] headers,
			byte[] responseBody) {
		try {
			JSONObject object = new JSONObject(new String(responseBody));
			Gson gson = new Gson();
			List<ItemHappyHour> itemHappyHours = gson.fromJson(
					object.getString("itemHappyHourList"),
					new TypeToken<ArrayList<ItemHappyHour>>() {
					}.getType());

			CoreData.getInstance().setItemHappyHours(itemHappyHours);
			ItemHappyHourSQL.deleteAllItemHappyHour();
			ItemHappyHourSQL.addItemHappyHourList(itemHappyHours);

			List<HappyHourWeek> happyHourWeeks = gson.fromJson(
					object.getString("happyHourWeekList"),
					new TypeToken<ArrayList<HappyHourWeek>>() {
					}.getType());
			CoreData.getInstance().setHappyHourWeeks(happyHourWeeks);
			HappyHourWeekSQL.deleteAllHappyHourWeek();
			HappyHourWeekSQL.addHappyHourWeekList(happyHourWeeks);

			List<HappyHour> happyHours = gson.fromJson(
					object.getString("happyHourList"),
					new TypeToken<ArrayList<HappyHour>>() {
					}.getType());
			CoreData.getInstance().setHappyHours(happyHours);
			HappyHourSQL.deleteAllHappyHour();
			HappyHourSQL.addHappyHourList(happyHours);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void pairingComplete(int statusCode, Header[] headers,
			byte[] responseBody, Handler handler) {
		Gson gson = new Gson();
		try {
			JSONObject object = new JSONObject(new String(responseBody));
			MainPosInfo mainPosInfo = gson.fromJson(
					object.optString("mainPosInfo"), MainPosInfo.class);
			App.instance.setMainPosInfo(mainPosInfo);
			SyncCentre.getInstance().setIp(mainPosInfo.getIP());
			handler.sendEmptyMessage(EmployeeID.HANDLER_PAIRING_COMPLETE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void commitOrderAndOrderDetails(int statusCode,
			Header[] headers, byte[] responseBody, Handler handler) {
		Gson gson = new Gson();
		try {
			JSONObject jsonObject = new JSONObject(new String(responseBody));
			Order order = gson.fromJson(jsonObject.optString("order"),
					Order.class);
			List<OrderDetail> orderDetails = gson.fromJson(
					jsonObject.optString("orderDetails"),
					new TypeToken<List<OrderDetail>>() {
					}.getType());
			List<OrderModifier> orderModifiers = gson.fromJson(
					jsonObject.optString("orderModifiers"),
					new TypeToken<List<OrderModifier>>() {
					}.getType());
			List<OrderDetailTax> orderDetailTaxs = gson.fromJson(
					jsonObject.optString("orderModifiers"),
					new TypeToken<List<OrderDetailTax>>() {
					}.getType());
			OrderDetailSQL.deleteOrderDetailByOrder(order);
			OrderModifierSQL.deleteOrderModifierByOrder(order);
			OrderDetailSQL.addOrderDetailList(orderDetails);
			OrderModifierSQL.addOrderModifierList(orderModifiers);
			OrderDetailTaxSQL.deleteOrderDetailTax(order);
			OrderDetailTaxSQL.addOrderDetailTaxList(orderDetailTaxs);
			handler.sendMessage(handler.obtainMessage(ResultCode.SUCCESS,
					orderDetails));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getKotNotification(int statusCode, Header[] headers,
			byte[] responseBody, Handler handler) {
		Gson gson = new Gson();
		try {
			JSONObject jsonObject = new JSONObject(new String(responseBody));
			List<TableAndKotNotificationList> notifications = new ArrayList<TableAndKotNotificationList>();
			notifications = gson.fromJson(
					jsonObject.optString("notificationLists"),
					new TypeToken<List<TableAndKotNotificationList>>() {
					}.getType());
			handler.sendMessage(handler.obtainMessage(
					KOTNotification.VIEW_EVENT_GET_DATA, notifications));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void handlerGetOrderDetails(int statusCode, Header[] headers,
			byte[] responseBody, Handler handler) {
		Gson gson = new Gson();
		try {
			JSONObject jsonObject = new JSONObject(new String(responseBody));
			List<OrderDetail> orderDetails = gson.fromJson(
					jsonObject.optString("otherOrderDetails"),
					new TypeToken<List<OrderDetail>>() {
					}.getType());
			OrderDetailSQL.addOrderDetailList(orderDetails);
			handler.sendEmptyMessage(MainPage.VIEW_ENVENT_GET_ORDERDETAILS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
