package com.alfredposclient.http.subpos;

import android.os.Handler;

import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.HappyHour;
import com.alfredbase.javabean.HappyHourWeek;
import com.alfredbase.javabean.ItemCategory;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.ItemHappyHour;
import com.alfredbase.javabean.ItemMainCategory;
import com.alfredbase.javabean.ItemModifier;
import com.alfredbase.javabean.LocalDevice;
import com.alfredbase.javabean.Modifier;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.PaymentMethod;
import com.alfredbase.javabean.PlaceInfo;
import com.alfredbase.javabean.Printer;
import com.alfredbase.javabean.PrinterGroup;
import com.alfredbase.javabean.Restaurant;
import com.alfredbase.javabean.RestaurantConfig;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.SettlementRestaurant;
import com.alfredbase.javabean.TableInfo;
import com.alfredbase.javabean.Tax;
import com.alfredbase.javabean.TaxCategory;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.UserRestaurant;
import com.alfredbase.javabean.model.SessionStatus;
import com.alfredbase.store.Store;
import com.alfredbase.store.sql.HappyHourSQL;
import com.alfredbase.store.sql.HappyHourWeekSQL;
import com.alfredbase.store.sql.ItemCategorySQL;
import com.alfredbase.store.sql.ItemDetailSQL;
import com.alfredbase.store.sql.ItemHappyHourSQL;
import com.alfredbase.store.sql.ItemMainCategorySQL;
import com.alfredbase.store.sql.ItemModifierSQL;
import com.alfredbase.store.sql.LocalDeviceSQL;
import com.alfredbase.store.sql.ModifierSQL;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredbase.store.sql.PaymentMethodSQL;
import com.alfredbase.store.sql.PlaceInfoSQL;
import com.alfredbase.store.sql.PrinterGroupSQL;
import com.alfredbase.store.sql.PrinterSQL;
import com.alfredbase.store.sql.RestaurantConfigSQL;
import com.alfredbase.store.sql.RestaurantSQL;
import com.alfredbase.store.sql.RevenueCenterSQL;
import com.alfredbase.store.sql.SettlementRestaurantSQL;
import com.alfredbase.store.sql.TableInfoSQL;
import com.alfredbase.store.sql.TaxCategorySQL;
import com.alfredbase.store.sql.TaxSQL;
import com.alfredbase.store.sql.UserRestaurantSQL;
import com.alfredbase.store.sql.UserSQL;
import com.alfredposclient.activity.kioskactivity.subpos.SubPosLogin;
import com.alfredposclient.global.App;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SubPosHttpAnalysis {
	public static final String TAG = SubPosHttpAnalysis.class.getSimpleName();
	public static void login(String responseBody, Handler handler) {
		Gson gson = new Gson();
		try {
			JSONObject object = new JSONObject(responseBody);
			int subPosStatus = object.optInt("subPosStatus");
			User user = gson.fromJson(object.getString("user"), User.class);
			App.instance.setUser(user);
			Long businessDate = object.optLong("businessDate");
			SessionStatus sessionStatus = gson.fromJson(object.getString("sessionStatus"), SessionStatus.class);
			Store.saveObject(App.instance, Store.SESSION_STATUS, sessionStatus);
			App.instance.setSessionStatus(sessionStatus);
			Store.putLong(App.instance, Store.BUSINESS_DATE, businessDate);
			Store.putLong(App.instance, Store.LAST_BUSINESSDATE, businessDate);
			App.instance.setBusinessDate(businessDate);
			App.instance.setLastBusinessDate(businessDate);
			handler.sendMessage(handler.obtainMessage(ResultCode.SUCCESS, subPosStatus));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public static void updateAllData(String responseBody, Handler handler) {
		Gson gson = new Gson();
		try {
			JSONObject object = new JSONObject(responseBody);
			List<User> users = gson.fromJson(object.getString("users"),
					new TypeToken<ArrayList<User>>() {
					}.getType());
			if(users != null && users.size() > 0){
				UserSQL.deleteAllUser();
				UserSQL.addUsers(users);
			}
			Restaurant restaurant = gson.fromJson(object.getString("restaurant"),
					Restaurant.class);
			if(restaurant != null){
				RestaurantSQL.deleteAllRestaurant();
				RestaurantSQL.addRestaurant(restaurant);
			}
			RevenueCenter revenueCenter = gson.fromJson(object.getString("revenueCenter"),
					RevenueCenter.class);
			if(revenueCenter != null){
				RevenueCenterSQL.deleteAllRevenueCenter();
				RevenueCenterSQL.addRevenueCenter(revenueCenter);
			}
			List<ItemMainCategory> itemMainCategories = gson.fromJson(object.getString("itemMainCategories"),
					new TypeToken<ArrayList<ItemMainCategory>>() {
					}.getType());
			if(itemMainCategories != null && itemMainCategories.size() > 0){
				ItemMainCategorySQL.deleteAllItemMainCategory();
				ItemMainCategorySQL.addItemMainCategory(itemMainCategories);
			}
			List<ItemCategory> itemCategories = gson.fromJson(object.getString("itemCategories"),
					new TypeToken<ArrayList<ItemCategory>>() {
					}.getType());
			if(itemCategories != null && itemCategories.size() > 0){
				ItemCategorySQL.deleteAllItemCategory();
				ItemCategorySQL.addItemCategoryList(itemCategories);
			}
			List<ItemDetail> itemDetails = gson.fromJson(object.getString("itemDetails"),
					new TypeToken<ArrayList<ItemDetail>>() {
					}.getType());
			if(itemDetails != null && itemDetails.size() > 0){
				ItemDetailSQL.deleteAllItemDetail();
				ItemDetailSQL.addItemDetailList(itemDetails);
			}
			List<Modifier> modifiers= gson.fromJson(object.getString("modifiers"),
					new TypeToken<ArrayList<Modifier>>() {
					}.getType());
			if(modifiers != null && modifiers.size() > 0){
				ModifierSQL.deleteAllModifier();
				ModifierSQL.addModifierList(modifiers);
			}
			List<Printer> printers = gson.fromJson(object.getString("printers"),
					new TypeToken<ArrayList<Printer>>() {
					}.getType());
			if(printers != null && printers.size() > 0 ){
				PrinterSQL.deleteAllPrinter();
				PrinterSQL.addPrinters(printers);
			}
			List<TableInfo> tableInfos = gson.fromJson(object.getString("tableInfos"),
					new TypeToken<ArrayList<TableInfo>>() {
					}.getType());
			if(tableInfos != null && tableInfos.size() > 0){
				TableInfoSQL.deleteAllTableInfo();
				TableInfoSQL.addTablesList(tableInfos);
			}
			List<PlaceInfo> placeInfos = gson.fromJson(object.getString("placeInfos"),
					new TypeToken<ArrayList<PlaceInfo>>() {
					}.getType());
			if(placeInfos != null && placeInfos.size() > 0){
				PlaceInfoSQL.deleteAllPlaceInfo();
				PlaceInfoSQL.addPlaceInfoList(placeInfos);
			}
			List<ItemModifier> itemModifiers = gson.fromJson(object.getString("itemModifiers"),
					new TypeToken<ArrayList<ItemModifier>>() {
					}.getType());
			if(itemModifiers != null && itemModifiers.size() > 0){
				ItemModifierSQL.deleteAllItemModifier();
				ItemModifierSQL.addItemModifierList(itemModifiers);
			}
			List<TaxCategory> taxCategories = gson.fromJson(object.getString("taxCategories"),
					new TypeToken<ArrayList<TaxCategory>>() {
					}.getType());
			if(taxCategories != null && taxCategories.size() > 0){
				TaxCategorySQL.deleteAllTaxCategory();
				TaxCategorySQL.addTaxCategorys(taxCategories);
			}
			List<Tax> taxes = gson.fromJson(object.getString("taxes"),
					new TypeToken<ArrayList<Tax>>() {
					}.getType());
			if(taxes != null && taxes.size() > 0){
				TaxSQL.deleteAllTax();
				TaxSQL.addTaxs(taxes);
			}
			List<ItemHappyHour> itemHappyHours = gson.fromJson(object.getString("itemHappyHours"),
					new TypeToken<ArrayList<ItemHappyHour>>() {
					}.getType());
			if(itemHappyHours != null && itemHappyHours.size() > 0){
				ItemHappyHourSQL.deleteAllItemHappyHour();
				ItemHappyHourSQL.addItemHappyHourList(itemHappyHours);
			}
			List<HappyHourWeek> happyHourWeeks = gson.fromJson(object.getString("happyHourWeeks"),
					new TypeToken<ArrayList<HappyHourWeek>>() {
					}.getType());
			if(happyHourWeeks != null && happyHourWeeks.size() > 0){
				HappyHourWeekSQL.deleteAllHappyHourWeek();
				HappyHourWeekSQL.addHappyHourWeekList(happyHourWeeks);
			}
			List<HappyHour> happyHours = gson.fromJson(object.getString("happyHours"),
					new TypeToken<ArrayList<HappyHour>>() {
					}.getType());
			if(happyHours != null && happyHours.size() > 0){
				HappyHourSQL.deleteAllHappyHour();
				HappyHourSQL.addHappyHourList(happyHours);
			}
			List<RestaurantConfig> restaurantConfigs = gson.fromJson(object.getString("restaurantConfigs"),
					new TypeToken<ArrayList<RestaurantConfig>>() {
					}.getType());
			if(restaurantConfigs != null && restaurantConfigs.size() > 0){
				RestaurantConfigSQL.deleteAllRestaurantConfig();
				RestaurantConfigSQL.addRestaurantConfigs(restaurantConfigs);
			}
			List<PrinterGroup> printerGroups = gson.fromJson(object.getString("printerGroups"),
					new TypeToken<ArrayList<PrinterGroup>>() {
					}.getType());
			if(printerGroups != null && printerGroups.size() > 0){
				PrinterGroupSQL.deletePrinter();
				PrinterGroupSQL.addPrinterGroups(printerGroups);
			}
			List<UserRestaurant> userRestaurants = gson.fromJson(object.getString("userRestaurants"),
					new TypeToken<ArrayList<UserRestaurant>>() {
					}.getType());
			if(userRestaurants != null && userRestaurants.size() > 0){
				UserRestaurantSQL.deleteAllUserRestaurant();
				UserRestaurantSQL.addUsers(userRestaurants);
			}
			List<LocalDevice> localDevices = gson.fromJson(object.getString("localDevices"),
					new TypeToken<ArrayList<LocalDevice>>() {
					}.getType());
			if(localDevices != null && localDevices.size() > 0){
				LocalDeviceSQL.deleteAllLocalDevice();
				LocalDeviceSQL.addLocalDeviceList(localDevices);
			}
			List<PaymentMethod> paymentMethods = gson.fromJson(object.getString("paymentMethods"),
					new TypeToken<ArrayList<PaymentMethod>>() {
					}.getType());
			if(paymentMethods != null && paymentMethods.size() > 0){
				PaymentMethodSQL.deleteAllPaymentMethod();
				PaymentMethodSQL.addPaymentMethod(paymentMethods);
			}
			List<SettlementRestaurant> settlementRestaurants = gson.fromJson(object.getString("settlementRestaurants"),
					new TypeToken<ArrayList<SettlementRestaurant>>() {
					}.getType());
			if(settlementRestaurants != null && settlementRestaurants.size() > 0){
				SettlementRestaurantSQL.deleteAllSettlementRestaurant();
				SettlementRestaurantSQL.addSettlementRestaurant(settlementRestaurants);
			}
			handler.sendEmptyMessage(SubPosLogin.UPDATE_ALL_DATA_SUCCESS);
			return;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		handler.sendEmptyMessage(SubPosLogin.UPDATE_ALL_DATA_FAILURE);
	}
	public static Order getOrder(String responseBody) {
		Gson gson = new Gson();
		try {
			JSONObject object = new JSONObject(responseBody);
			Order order = gson.fromJson(object.getString("order"), Order.class);
			OrderSQL.update(order);
			return order;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}


}
