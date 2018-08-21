package com.alfredposclient.http.server;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.TextUtils;

import com.alfredbase.APPConfig;
import com.alfredbase.BaseActivity;
import com.alfredbase.BaseApplication;
import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.http.APIName;
import com.alfredbase.http.AlfredHttpServer;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.HappyHour;
import com.alfredbase.javabean.HappyHourWeek;
import com.alfredbase.javabean.ItemCategory;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.ItemHappyHour;
import com.alfredbase.javabean.ItemMainCategory;
import com.alfredbase.javabean.ItemModifier;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotItemModifier;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.LocalDevice;
import com.alfredbase.javabean.Modifier;
import com.alfredbase.javabean.MultiOrderRelation;
import com.alfredbase.javabean.MultiReportRelation;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderBill;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.OrderDetailTax;
import com.alfredbase.javabean.OrderModifier;
import com.alfredbase.javabean.OrderSplit;
import com.alfredbase.javabean.Payment;
import com.alfredbase.javabean.PaymentMethod;
import com.alfredbase.javabean.PaymentSettlement;
import com.alfredbase.javabean.PlaceInfo;
import com.alfredbase.javabean.Printer;
import com.alfredbase.javabean.PrinterGroup;
import com.alfredbase.javabean.PrinterTitle;
import com.alfredbase.javabean.ReportDayPayment;
import com.alfredbase.javabean.ReportDaySales;
import com.alfredbase.javabean.ReportDayTax;
import com.alfredbase.javabean.ReportHourly;
import com.alfredbase.javabean.ReportPluDayComboModifier;
import com.alfredbase.javabean.ReportPluDayItem;
import com.alfredbase.javabean.ReportPluDayModifier;
import com.alfredbase.javabean.Restaurant;
import com.alfredbase.javabean.RestaurantConfig;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.RoundAmount;
import com.alfredbase.javabean.SettlementRestaurant;
import com.alfredbase.javabean.SubPosBean;
import com.alfredbase.javabean.TableInfo;
import com.alfredbase.javabean.Tax;
import com.alfredbase.javabean.TaxCategory;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.UserOpenDrawerRecord;
import com.alfredbase.javabean.UserRestaurant;
import com.alfredbase.javabean.model.KDSDevice;
import com.alfredbase.javabean.model.KotNotification;
import com.alfredbase.javabean.model.MainPosInfo;
import com.alfredbase.javabean.model.PrintOrderItem;
import com.alfredbase.javabean.model.PrintOrderModifier;
import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.javabean.model.SessionStatus;
import com.alfredbase.javabean.model.TableAndKotNotificationList;
import com.alfredbase.javabean.model.WaiterDevice;
import com.alfredbase.javabean.system.VersionUpdate;
import com.alfredbase.javabean.temporaryforapp.AppOrder;
import com.alfredbase.store.Store;
import com.alfredbase.store.TableNames;
import com.alfredbase.store.sql.CommonSQL;
import com.alfredbase.store.sql.HappyHourSQL;
import com.alfredbase.store.sql.HappyHourWeekSQL;
import com.alfredbase.store.sql.ItemCategorySQL;
import com.alfredbase.store.sql.ItemDetailSQL;
import com.alfredbase.store.sql.ItemHappyHourSQL;
import com.alfredbase.store.sql.ItemMainCategorySQL;
import com.alfredbase.store.sql.ItemModifierSQL;
import com.alfredbase.store.sql.KotItemDetailSQL;
import com.alfredbase.store.sql.KotItemModifierSQL;
import com.alfredbase.store.sql.KotNotificationSQL;
import com.alfredbase.store.sql.KotSummarySQL;
import com.alfredbase.store.sql.ModifierSQL;
import com.alfredbase.store.sql.MultiOrderRelationSQL;
import com.alfredbase.store.sql.MultiReportRelationSQL;
import com.alfredbase.store.sql.OrderBillSQL;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.OrderDetailTaxSQL;
import com.alfredbase.store.sql.OrderModifierSQL;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredbase.store.sql.OrderSplitSQL;
import com.alfredbase.store.sql.PaymentMethodSQL;
import com.alfredbase.store.sql.PlaceInfoSQL;
import com.alfredbase.store.sql.PrinterGroupSQL;
import com.alfredbase.store.sql.PrinterSQL;
import com.alfredbase.store.sql.RestaurantConfigSQL;
import com.alfredbase.store.sql.RestaurantSQL;
import com.alfredbase.store.sql.SettlementRestaurantSQL;
import com.alfredbase.store.sql.SubPosBeanSQL;
import com.alfredbase.store.sql.SubPosCommitSQL;
import com.alfredbase.store.sql.TableInfoSQL;
import com.alfredbase.store.sql.TaxCategorySQL;
import com.alfredbase.store.sql.TaxSQL;
import com.alfredbase.store.sql.UserRestaurantSQL;
import com.alfredbase.store.sql.UserSQL;
import com.alfredbase.store.sql.temporaryforapp.AppOrderSQL;
import com.alfredbase.utils.CommonUtil;
import com.alfredbase.utils.IntegerUtils;
import com.alfredbase.utils.LogUtil;
import com.alfredbase.utils.ObjectFactory;
import com.alfredbase.utils.OrderHelper;
import com.alfredbase.utils.RxBus;
import com.alfredposclient.R;
import com.alfredposclient.activity.MainPage;
import com.alfredposclient.activity.NetWorkOrderActivity;
import com.alfredposclient.activity.kioskactivity.KioskHoldActivity;
import com.alfredposclient.global.App;
import com.alfredposclient.global.SyncCentre;
import com.alfredposclient.global.UIHelp;
import com.alfredposclient.jobs.CloudSyncJobManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moonearly.utils.service.TcpUdpFactory;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainPosHttpServer extends AlfredHttpServer {
	private String TAG = MainPosHttpServer.class.getSimpleName();
	public MainPosHttpServer() {
		super(APPConfig.HTTP_SERVER_PORT);
	}

	private Object lockObject = new Object();

	public static final String MIME_JAVASCRIPT = "text/javascript";
	public static final String MIME_CSS = "text/css";
	public static final String MIME_JPEG = "image/jpeg";
	public static final String MIME_PNG = "image/png";
	public static final String MIME_SVG = "image/svg+xml";
	public static final String MIME_JSON = "application/json";
	public static final String RESULT_CODE = "resultCode";
	public static final Gson gson = new Gson();

	@Override
	public Response doGet(String uri, Method mothod, final Map<String, String> params, String body){
		return super.doGet(uri,mothod,params,body);
	}

	@Override
	public Response doDesktopPost(String apiName, Method mothod, Map<String, String> params, String body) {
		LogUtil.d(TAG, "apiName : " + apiName + " body : " + body);
		Map<String, Object> result = new HashMap<String, Object>();
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(body);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return this.getForbiddenResponse("");
		}
		if (TextUtils.isEmpty(apiName)) {
			return this.getForbiddenResponse("");
		} else {
			if(apiName.equals(APIName.CALLNUM_ASSIGNREVENUE)){
				try{
					String ip = jsonObject.getString("ip");
					Store.putString(App.instance,Store.CALL_APP_IP, ip);
					result.put(RESULT_CODE, ResultCode.SUCCESS);
				}catch (Exception e){
					result.put("resultCode", ResultCode.JSON_DATA_ERROR);
				}
				return this.getJsonResponse(new Gson().toJson(result));

			}

			int deviceType = jsonObject.optInt("deviceType");
			if (deviceType != 5) {
				result.put("resultCode", ResultCode.JSON_DATA_ERROR);
				return this.getJsonResponse(new Gson().toJson(result));
			}
			SessionStatus sessionStatus = App.instance.getSessionStatus();
			if (sessionStatus == null) {
				result.put("resultCode", ResultCode.SESSION_IS_CLOSED);
				return this.getJsonResponse(new Gson().toJson(result));
			}
			if (apiName.equals(APIName.DESKTOP_LOGIN)) {
				int employee_Id = 0;
				try {
					String employeeId = jsonObject.optString("employeeId");
					employee_Id = Integer.parseInt(employeeId);
				} catch (Exception e) {
					result.put("resultCode", ResultCode.JSON_DATA_ERROR);
				}
				User user = CoreData.getInstance().getUserByEmpId(employee_Id);
				if (user != null) {
					result.put("resultCode", ResultCode.SUCCESS);
					result.put("revenue", App.instance.getRevenueCenter());
					result.put("user", user);
				} else {
					result.put("resultCode", ResultCode.USER_EMPTY);
				}
				return this.getJsonResponse(new Gson().toJson(result));
			} else {
				int revenueId = 0;
				int userId = 0;
				try {
					revenueId = jsonObject.optInt("revenueId");
					userId = jsonObject.optInt("userId");
				} catch (Exception e) {
					result.put("resultCode", ResultCode.JSON_DATA_ERROR);
					return this.getJsonResponse(new Gson().toJson(result));
				}
				User user = UserSQL.getUserById(userId);
				if (user == null) {
					result.put("resultCode", ResultCode.USER_EMPTY);
					return this.getJsonResponse(new Gson().toJson(result));
				}
				if (apiName.equals(APIName.DESKTOP_GETTABLE)) {
					if(App.instance.isRevenueKiosk()){
						result.put("resultCode", ResultCode.REVENUE_IS_KIOSK);
						return this.getJsonResponse(new Gson().toJson(result));
					}
					if (revenueId == App.instance.getRevenueCenter().getId().intValue()) {
						List<PlaceInfo> placeList = PlaceInfoSQL.getAllPlaceInfo();
						List<TableInfo> tableInfoList = TableInfoSQL.getAllTables();
						result.put("resultCode", ResultCode.SUCCESS);
						result.put("placeList", placeList);
						result.put("tableList", tableInfoList);
					} else {
						result.put("resultCode", ResultCode.REVENUE_EMPLY);
					}
					return this.getJsonResponse(new Gson().toJson(result));
				}else if (apiName.equals(APIName.DESKTOP_PRINTBILL)) {
					Response resp;
					Gson gson = new Gson();
		/*No waiter apps in kiosk mode */
					if (App.instance.isRevenueKiosk()) {
						result.put("resultCode", ResultCode.USER_NO_PERMIT);
						resp = this.getJsonResponse(new Gson().toJson(result));
						return resp;
					}
					try {
						int orderId = jsonObject.optInt("orderId");
						Order loadOrder = OrderSQL.getUnfinishedOrder(orderId);
						if (loadOrder == null) {
							result.put("resultCode", ResultCode.ORDER_FINISHED);
							resp = this.getJsonResponse(new Gson().toJson(result));
							return resp;
						}
						String tableName = jsonObject.getString("tableName");
						if(orderId > 0){
							Order order = OrderSQL.getOrder(orderId);
							if(order == null){
								result.put("resultCode", ResultCode.ORDER_NO_PLACE);
								resp = this.getJsonResponse(new Gson().toJson(result));
								return resp;
							}
							if(order.getOrderStatus().intValue() == ParamConst.ORDER_STATUS_FINISHED){
								result.put("resultCode", ResultCode.ORDER_FINISHED);
								resp = this.getJsonResponse(new Gson().toJson(result));
								return resp;
							}
							OrderBill orderBill = OrderBillSQL
									.getOrderBillByOrder(order);
							if(orderBill == null){
								result.put("resultCode", ResultCode.ORDER_NO_PLACE);
								resp = this.getJsonResponse(new Gson().toJson(result));
								return resp;
							}

							PrinterTitle title = ObjectFactory.getInstance()
									.getPrinterTitle(
											App.instance.getRevenueCenter(),
											order,
											App.instance.getUser().getFirstName()
													+ App.instance.getUser()
													.getLastName(),
											tableName, 1);
							ArrayList<PrintOrderItem> orderItems = ObjectFactory
									.getInstance().getItemList(
											OrderDetailSQL.getOrderDetails(order
													.getId()));
							ArrayList<PrintOrderModifier> orderModifiers = ObjectFactory
									.getInstance().getItemModifierList(order, OrderDetailSQL.getOrderDetails(order
											.getId()));
							List<Map<String, String>> taxMap = OrderDetailTaxSQL
									.getTaxPriceSUMForPrint(App.instance.getLocalRestaurantConfig().getIncludedTax().getTax(), order);
							PrinterDevice printer = App.instance.getCahierPrinter();
							App.instance.remoteBillPrint(printer, title, order,
									orderItems, orderModifiers, taxMap, null, null);
							OrderSQL.updateOrderStatus(ParamConst.ORDER_STATUS_UNPAY, orderId);
						}
						result.put("resultCode", ResultCode.SUCCESS);
						resp = this.getJsonResponse(new Gson().toJson(result));

					} catch (Exception e) {
						e.printStackTrace();
						resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.internal_error));
					}
					return resp;
				} else if (apiName.equals(APIName.DESKTOP_GETBILL)) {
					Response resp;
					try {
						if(App.instance.isRevenueKiosk()){
							result.put("resultCode", ResultCode.REVENUE_IS_KIOSK);
							return this.getJsonResponse(new Gson().toJson(result));
						}
						int tableId = jsonObject.optInt("tableId");
						//Table status in waiter APP is not same that of table in POS
						//need get latest status on app.
						TableInfo tabInPOS = TableInfoSQL.getTableById(tableId);
						if(tabInPOS == null){
							result.put("resultCode", ResultCode.JSON_DATA_ERROR);
							resp = this.getJsonResponse(new Gson().toJson(result));
							return resp;
						}

						App.getTopActivity().httpRequestAction(
								MainPage.VIEW_EVNT_GET_BILL_PRINT, tabInPOS);

						result.put("resultCode", ResultCode.SUCCESS);
						resp = this.getJsonResponse(new Gson().toJson(result));
					} catch (Exception e) {
						e.printStackTrace();
						resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.internal_error));
					}
					return  resp;
				} else if (apiName.equals(APIName.DESKTOP_SELECTTABLE)) {
					if(App.instance.isRevenueKiosk()){
						result.put("resultCode", ResultCode.REVENUE_IS_KIOSK);
						return this.getJsonResponse(new Gson().toJson(result));
					}
					int tableId = 0;
					int persons = 0;
					int orderId = -1;
					try {
						tableId = jsonObject.optInt("tableId");
						persons = jsonObject.optInt("persons");
						if(!jsonObject.isNull("orderId")){
							orderId = jsonObject.optInt("orderId");
						}
					} catch (Exception e) {
						e.printStackTrace();
						result.put("resultCode", ResultCode.JSON_DATA_ERROR);
					}
					if(orderId != -1){
						Order order = OrderSQL.getOrder(orderId);
						if(order != null && order.getOrderStatus().intValue() == ParamConst.ORDER_STATUS_FINISHED){
							result.put("resultCode", ResultCode.ORDER_FINISHED);
							return this.getJsonResponse(new Gson().toJson(result));
						}
					}
					if (tableId != 0 || persons != 0) {
						TableInfo tableInfo = TableInfoSQL.getTableById(tableId);
						if(tableInfo == null){
							result.put("resultCode", ResultCode.TABLE_EMPLY);
							return this.getJsonResponse(new Gson().toJson(result));
						}
						tableInfo.setStatus(ParamConst.TABLE_STATUS_DINING);
						TableInfoSQL.updateTables(tableInfo);
						tableInfo.setPacks(persons);
//						Order order = ObjectFactory.getInstance().getOrder
						Order order = ObjectFactory.getInstance().getOrder(
								ParamConst.ORDER_ORIGIN_TABLE, App.instance.getSubPosBeanId(), tableInfo,
								App.instance.getRevenueCenter(),
								user,
								App.instance.getSessionStatus(),
								App.instance.getBusinessDate(),
								App.instance.getIndexOfRevenueCenter(),
								ParamConst.ORDER_STATUS_OPEN_IN_WAITER,
								App.instance.getLocalRestaurantConfig()
										.getIncludedTax().getTax());
						List<OrderDetail> orderDetailListR = OrderDetailSQL.getAllOrderDetailsByOrder(order);
						List<OrderModifier> orderModifierListR = OrderModifierSQL.getAllOrderModifier(order);
						result.put("resultCode", ResultCode.SUCCESS);
						result.put("order", order);
						result.put("orderDetailList", orderDetailListR);
						result.put("orderModifierList", orderModifierListR);
						try {
							JSONObject jsonObject1 = new JSONObject();
							jsonObject1.put("tableId", tableInfo.getPosId().intValue());
							jsonObject1.put("status", ParamConst.TABLE_STATUS_DINING);
							jsonObject1.put("RX", RxBus.RX_REFRESH_TABLE);
							TcpUdpFactory.sendUdpMsg(BaseApplication.UDP_INDEX_WAITER,TcpUdpFactory.UDP_REQUEST_MSG+ jsonObject1.toString(),null);
							TcpUdpFactory.sendUdpMsg(BaseApplication.UDP_INDEX_EMENU,TcpUdpFactory.UDP_REQUEST_MSG+ jsonObject1.toString(),null);
						}catch (Exception e){
							e.printStackTrace();
						}
						App.getTopActivity().httpRequestAction(
								MainPage.REFRESH_TABLES_STATUS, tableInfo);
					} else {
						result.put("resultCode", ResultCode.JSON_DATA_ERROR);
					}
					return this.getJsonResponse(new Gson().toJson(result));
				} else if (apiName.equals(APIName.DESKTOP_GETITEM)) {
					List<ItemMainCategory> itemMainCategoryList = ItemMainCategorySQL.getAllAvaiableItemMainCategoryInRevenueCenter();
					List<ItemCategory> itemCategoryList = ItemCategorySQL.getAllItemCategory();
					List<ItemDetail> itemDetailList = ItemDetailSQL.getAllItemDetail();
					List<ItemModifier> itemModifierList = ItemModifierSQL.getAllItemModifier();
					List<Modifier> modifierList = ModifierSQL.getAllModifier();

					result.put("resultCode", ResultCode.SUCCESS);
					result.put("itemMainCategoryList", itemMainCategoryList);
					result.put("itemCategoryList", itemCategoryList);
					result.put("itemDetailList", itemDetailList);
					result.put("itemModifierList", itemModifierList);
					result.put("modifierList", modifierList);
					return this.getJsonResponse(new Gson().toJson(result));
				} else if(apiName.equals(APIName.DESKTOP_KIOSKORDER)) {
					Response resp;
					if (!App.instance.isRevenueKiosk()) {
						result.put("resultCode", ResultCode.USER_NO_PERMIT);
						return this.getJsonResponse(new Gson().toJson(result));
					}
					try {
						Gson gson = new Gson();
						Order order = ObjectFactory.getInstance().addOrderFromKioskDesktop(
								ParamConst.ORDER_ORIGIN_TABLE, App.instance.getSubPosBeanId(),
								TableInfoSQL.getKioskTable(),
								App.instance.getRevenueCenter(),
								user,
								App.instance.getSessionStatus(),
								App.instance.getBusinessDate(),
								App.instance.getLocalRestaurantConfig().getIncludedTax().getTax());
						ArrayList<OrderDetail> waiterOrderDetails = gson.fromJson(
								jsonObject.optString("orderDetailList"),
								new TypeToken<ArrayList<OrderDetail>>() {
								}.getType());
						ArrayList<OrderModifier> waiterOrderModifiers = gson.fromJson(
								jsonObject.optString("orderModifierList"),
								new TypeToken<ArrayList<OrderModifier>>() {
								}.getType());
						// waiter 过来的数据 存到 pos的DB中 不带Id存储
						for (OrderDetail orderDetail : waiterOrderDetails) {
							synchronized (lockObject) {
								int oldOrderDetailId = orderDetail.getId();
								ObjectFactory.getInstance().getOrderDetailFromKiosk(order, orderDetail);
								OrderDetailSQL.addOrderDetailETCForWaiter(orderDetail);
								if (waiterOrderModifiers != null
										&& !waiterOrderModifiers.isEmpty()) {
									for (OrderModifier orderModifier : waiterOrderModifiers) {
										if (orderModifier.getOrderDetailId().intValue() == oldOrderDetailId) {
											Modifier modifier = ModifierSQL.getModifierById(orderModifier.getModifierId().intValue());
											OrderModifier localOrderModifier =
													ObjectFactory.getInstance().getOrderModifier(order, orderDetail, modifier, 0);
											OrderModifierSQL
													.updateOrderModifier(localOrderModifier);
										}
									}
								}
							}
						}
						order = OrderSQL.getOrder(order.getId().intValue());
						result.put("resultCode", ResultCode.SUCCESS);
						result.put("orderNo", order.getOrderNo());
						resp = this.getJsonResponse(new Gson().toJson(result));
						int count = OrderSQL.getKioskHoldCount(App.instance.getBusinessDate(), App.instance.getSessionStatus());
						App.instance.setKioskHoldNum(count);
						if(App.getTopActivity() != null){
							App.getTopActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
									Ringtone r = RingtoneManager.getRingtone(App.instance, notification);
									r.play();
								}
							});
							if( App.getTopActivity() instanceof KioskHoldActivity) {
								App.getTopActivity().httpRequestAction(
										MainPage.VIEW_EVENT_SET_DATA, App.getTopActivity());
							}
						}
					}catch (Exception e){
						e.printStackTrace();
						result.clear();
						result.put("resultCode", ResultCode.ORDER_ERROR);
						resp = this.getJsonResponse(new Gson().toJson(result));
					}
					return resp;
				} else if (apiName.equals(APIName.DESKTOP_COMMITORDER)) {
					if(App.instance.isRevenueKiosk()){
						result.put("resultCode", ResultCode.REVENUE_IS_KIOSK);
						return this.getJsonResponse(new Gson().toJson(result));
					}
					Response resp;
					if (App.instance.isRevenueKiosk()) {
						result.put("resultCode", ResultCode.USER_NO_PERMIT);
						return this.getJsonResponse(new Gson().toJson(result));
					}

					try {
						Gson gson = new Gson();
						Order order = gson.fromJson(jsonObject.optJSONObject("order")
								.toString(), Order.class);
						Order loadOrder = OrderSQL.getUnfinishedOrder(order.getId());
						if (loadOrder == null) {
							result.put("resultCode", ResultCode.ORDER_FINISHED);
							resp = this.getJsonResponse(new Gson().toJson(result));
							return resp;
						}
						if ((App.instance.orderInPayment != null && App.instance.orderInPayment.getId().intValue() == order.getId().intValue())) {
							result.put("resultCode", ResultCode.NONEXISTENT_ORDER);
							resp = this.getJsonResponse(new Gson().toJson(result));
							return resp;
						}
						if(App.instance.getClosingOrderId() == order.getId()){
							result.put("resultCode", ResultCode.ORDER_HAS_CLOSING);
							resp = this.getJsonResponse(new Gson().toJson(result));
							return resp;
						}
						ArrayList<OrderDetail> waiterOrderDetails = gson.fromJson(
								jsonObject.optString("orderDetailList"),
								new TypeToken<ArrayList<OrderDetail>>() {
								}.getType());
						ArrayList<OrderModifier> waiterOrderModifiers = gson.fromJson(
								jsonObject.optString("orderModifierList"),
								new TypeToken<ArrayList<OrderModifier>>() {
								}.getType());
						// 检测是否有 订单是已经完成的拆单中的
						for (OrderDetail orderDetail : waiterOrderDetails) {
							OrderSplit loadOrderSplit = OrderSplitSQL.getOrderSplitByOrderAndGroupId(order, orderDetail.getGroupId());
							if (loadOrderSplit != null && loadOrderSplit.getOrderStatus() == ParamConst.ORDERSPLIT_ORDERSTATUS_FINISHED) {
								result.put("resultCode", ResultCode.ORDER_FINISHED);
								resp = this.getJsonResponse(new Gson().toJson(result));
								return resp;
							}
						}

						// waiter 过来的数据 存到 pos的DB中 不带Id存储
						for (OrderDetail orderDetail : waiterOrderDetails) {
							synchronized (lockObject) {
								OrderDetail orderDetailFromPOS = OrderDetailSQL
										.getOrderDetailByCreateTime(
												System.currentTimeMillis(),
												orderDetail.getOrderId());
								if (orderDetailFromPOS != null) {
									continue;
								} else {

									int orderDetailId = CommonSQL
											.getNextSeq(TableNames.OrderDetail);
									int oldOrderDetailId = orderDetail.getId();
									orderDetail
											.setOrderDetailStatus(ParamConst.ORDERDETAIL_STATUS_ADDED);
									orderDetail.setId(orderDetailId);
									OrderDetailSQL.addOrderDetailETC(orderDetail);
									if (orderDetail.getGroupId().intValue() > 0) {
										OrderSplit orderSplit = ObjectFactory.getInstance().getOrderSplit(order, orderDetail.getGroupId(), App.instance.getLocalRestaurantConfig()
												.getIncludedTax().getTax());
										OrderSplitSQL.updateOrderSplitByOrder(order, orderSplit);
										orderDetail.setOrderSplitId(orderSplit.getId());
										OrderDetailSQL.updateOrderDetail(orderDetail);
										OrderDetailTaxSQL.updateOrderSplitIdbyOrderDetail(orderDetail);
									}
									if (waiterOrderModifiers != null
											&& !waiterOrderModifiers.isEmpty()) {
										for (OrderModifier orderModifier : waiterOrderModifiers) {
											if (orderModifier.getOrderDetailId().intValue() == oldOrderDetailId) {
												Modifier modifier = ModifierSQL.getModifierById(orderModifier.getModifierId().intValue());
												OrderModifier localOrderModifier =
														ObjectFactory.getInstance().getOrderModifier(order, orderDetail,modifier,0);
												OrderModifierSQL
														.updateOrderModifier(localOrderModifier);
											}
										}
									}
								}
							}
						}

						order = OrderSQL.getOrder(order.getId().intValue());

						order.setOrderStatus(ParamConst.ORDER_STATUS_OPEN_IN_POS);

						//	当前Order未完成时更新状态
						OrderSQL.updateUnFinishedOrderFromWaiter(order);
						//	这边重新从数据中获取OrderDetail 不依赖于waiter过来的数据
						List<OrderDetail> orderDetails = OrderDetailSQL
								.getOrderDetails(order.getId());
						if (!orderDetails.isEmpty()) {
							Order placedOrder = OrderSQL.getOrder(order.getId());
							if (placedOrder.getOrderNo().intValue() == 0) {
								order.setOrderNo(OrderHelper.calculateOrderNo(order.getBusinessDate()));
								OrderSQL.updateOrderNo(order);
							}
							OrderBill orderBill = ObjectFactory.getInstance().getOrderBill(
									order, App.instance.getRevenueCenter());
							OrderBillSQL.add(orderBill);
						}
						String kotCommitStatus;
						KotSummary kotSummary = ObjectFactory.getInstance().getKotSummary(
								TableInfoSQL.getTableById(order.getTableId()).getName(),
								order, App.instance.getRevenueCenter(), App.instance.getBusinessDate());
						List<Integer> orderDetailIds = new ArrayList<Integer>();
						ArrayList<KotItemDetail> kotItemDetails = new ArrayList<KotItemDetail>();
						ArrayList<KotItemModifier> kotItemModifiers = new ArrayList<KotItemModifier>();
						kotCommitStatus = ParamConst.JOB_NEW_KOT;
						for (OrderDetail orderDetail : orderDetails) {
							if (orderDetail.getOrderDetailStatus() >= ParamConst.ORDERDETAIL_STATUS_PREPARED) {
								continue;
							}
							if (orderDetail.getOrderDetailStatus() == ParamConst.ORDERDETAIL_STATUS_KOTPRINTERD) {
								kotCommitStatus = ParamConst.JOB_UPDATE_KOT;
							} else {
								KotItemDetail kotItemDetail = ObjectFactory
										.getInstance()
										.getKotItemDetail(
												order,
												orderDetail,
												CoreData.getInstance().getItemDetailById(
														orderDetail.getItemId()),
												kotSummary, App.instance.getSessionStatus(), ParamConst.KOTITEMDETAIL_CATEGORYID_MAIN);
								kotItemDetail.setItemNum(orderDetail.getItemNum());
								if (kotItemDetail.getKotStatus() == ParamConst.KOT_STATUS_UNDONE) {
									kotCommitStatus = ParamConst.JOB_UPDATE_KOT;
									kotItemDetail
											.setKotStatus(ParamConst.KOT_STATUS_UPDATE);
								}
								KotItemDetailSQL.update(kotItemDetail);
								kotItemDetails.add(kotItemDetail);
								orderDetailIds.add(orderDetail.getId());
								ArrayList<OrderModifier> orderModifiers = OrderModifierSQL
										.getOrderModifiers(order, orderDetail);
								for (OrderModifier orderModifier : orderModifiers) {
									KotItemModifier kotItemModifier = ObjectFactory
											.getInstance().getKotItemModifier(
													kotItemDetail,
													orderModifier,
													CoreData.getInstance().getModifier(
															orderModifier.getModifierId()));
									KotItemModifierSQL.update(kotItemModifier);
									kotItemModifiers.add(kotItemModifier);
								}
							}
						}
						KotSummarySQL.update(kotSummary);
						List<OrderDetail> orderDetailListR = OrderDetailSQL.getAllOrderDetailsByOrder(order);
						List<OrderModifier> orderModifierListR = OrderModifierSQL.getAllOrderModifier(order);
						result.put("order", order);
						result.put("orderDetailList", orderDetailListR);
						result.put("orderModifierList", orderModifierListR);
						result.put("resultCode", ResultCode.SUCCESS);
						resp = this.getJsonResponse(new Gson().toJson(result));
						// App.getTopActivity().httpRequestAction(MainPage.WAITER_SEND_KDS,
						// kotMap);
						Map<String, Object> orderMap = new HashMap<String, Object>();
						orderMap.put("orderId", order.getId());
						orderMap.put("orderDetailIds", orderDetailIds);
						if (!App.instance.isRevenueKiosk() && App.instance.getSystemSettings().isOrderSummaryPrint()) {
							PrinterDevice printer = App.instance.getCahierPrinter();
							if (printer == null) {
								UIHelp.showToast(
										App.getTopActivity(), App.getTopActivity().getResources().getString(R.string.setting_printer));
							} else {
								App.instance.remoteOrderSummaryPrint(printer, kotSummary, kotItemDetails, kotItemModifiers);
							}
						}

						App.instance.getKdsJobManager()
								.tearDownKot(kotSummary, kotItemDetails, kotItemModifiers,
										kotCommitStatus, orderMap);
						App.getTopActivity().httpRequestAction(
								MainPage.VIEW_EVENT_SET_DATA, order.getId());
					} catch (Exception e) {
						e.printStackTrace();
						result.clear();
						result.put("resultCode", ResultCode.ORDER_ERROR);
						resp = this.getJsonResponse(new Gson().toJson(result));
					}
					return resp;
				}else{
					return this.getNotFoundResponse();
				}
			}
		}
	}


	@Override
	public Response doSubPosPost(String apiName, Method mothod, Map<String, String> params, String body) {
		if (App.instance.getPosType() == ParamConst.POS_TYPE_MAIN){
			LogUtil.d(TAG, "apiName : " + apiName + " body : " + body);
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(body);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return this.getForbiddenResponse("");
			}
			String appVersion = jsonObject.optString("appVersion");
			if (apiName == null || TextUtils.isEmpty(appVersion)) {
				return this.getForbiddenResponse("");
			}
			Map<String, Object> result = new HashMap<String, Object>();

			if(!App.instance.isRevenueKiosk()){
				result.put(RESULT_CODE, ResultCode.IS_NOT_KIOSK);
				return this.getJsonResponse(gson.toJson(result));
			}
			if(!appVersion.endsWith(App.instance.VERSION)){
				result.put("resultCode", ResultCode.APP_VERSION_UNREAL);
				result.put("posVersion", App.instance.VERSION);
				String value = MobclickAgent.getConfigParams(App.getTopActivity(), "updateVersion");
				result.put("versionUpdate", value);
				return this.getJsonResponse(new Gson().toJson(result));
			}
			if(App.instance.getSessionStatus() == null){
				result.put("resultCode", ResultCode.SESSION_IS_CLOSED);
				return this.getJsonResponse(new Gson().toJson(result));
			}

			if(apiName.equals(APIName.SUBPOS_CHOOSEREVENUE)) {
				return subChooseRevenue();
			} else if(apiName.equals(APIName.SUBPOS_LOGIN)){
				return subPosLogin(body);
			} else if(apiName.equals(APIName.SUBPOS_UPDATE_DATA)){
				return updateAllData();
			}
			int userId = jsonObject.optInt("userId");
			User user = CoreData.getInstance().getUserById(userId);
			if(user == null){
				result.put("resultCode", ResultCode.USER_NO_PERMIT);
				return this.getJsonResponse(new Gson().toJson(result));
			}
			if(apiName.equals(APIName.SUBPOS_COMMIT_ORDER)){
				return commitOrder(body);
			}else if(apiName.equals(APIName.SUBPOS_COMMIT_REPORT)){
				return commitReport(body);
			}


			return getForbiddenResponse("Not Support yet");
		}else{
			return getForbiddenResponse("Not Support yet");
		}
	}

	private Response subChooseRevenue(){
		Map<String, Object> result = new HashMap<>();
		result.put(RESULT_CODE, ResultCode.SUCCESS);
		return this.getJsonResponse(gson.toJson(result));
	}

	private Response subPosLogin(String params){
		Response resp = null;
		try {
			JSONObject jsonObject = new JSONObject(params);
			String employeeId = jsonObject.optString("employeeId");
			String password = jsonObject.optString("password");
			String deviceId = jsonObject.optString("deviceId");
			User user = CoreData.getInstance().getUser(employeeId, password);
			Map<String, Object> result = new HashMap<>();
			if (user != null) {
				if(user.getType() != ParamConst.USER_TYPE_MANAGER && user.getType() != ParamConst.USER_TYPE_POS){
					result.put(RESULT_CODE, ResultCode.USER_NO_PERMIT);
					return this.getJsonResponse(new Gson().toJson(result));
				}
				result.put("resultCode", ResultCode.SUCCESS);
				result.put("user", user);
				SubPosBean subPosBean = SubPosBeanSQL.getSubPosBeanByDeviceId(deviceId);
				if(subPosBean == null){
					subPosBean = new SubPosBean();
					subPosBean.setId(CommonSQL.getNextSeq(TableNames.SubPosBean));
					subPosBean.setDeviceId(deviceId);
					subPosBean.setUserName(user.getFirstName() + user.getLastName());
					subPosBean.setNumTag(""+ (char) (subPosBean.getId() + 64));
					subPosBean.setSubPosStatus(ParamConst.SUB_POS_STATUS_OPEN);
					SubPosBeanSQL.updateSubPosBean(subPosBean);
				}else{
					subPosBean.setSubPosStatus(ParamConst.SUB_POS_STATUS_OPEN);
					subPosBean.setUserName(user.getFirstName() + user.getLastName());
					SubPosBeanSQL.updateSubPosBean(subPosBean);
				}
				result.put("subPosBean", subPosBean);
				resp = this.getJsonResponse(gson.toJson(result));
			} else {
				result.put("resultCode", ResultCode.USER_NO_PERMIT);
				resp = this.getJsonResponse(gson.toJson(result));
			}
		} catch (Exception e) {
			e.printStackTrace();
			resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.login_failed));
		}
		return resp;
	}


	private Response updateAllData(){
		Response resp = null;
		try {
			List<User> users = UserSQL.getAllUser();
			Restaurant restaurant = RestaurantSQL.getRestaurant();
			RevenueCenter revenueCenter = App.instance.getRevenueCenter();
			List<ItemMainCategory> itemMainCategories = ItemMainCategorySQL.getAllItemMainCategory();
			List<ItemCategory> itemCategories = ItemCategorySQL.getAllItemCategory();
			List<ItemDetail> itemDetails = ItemDetailSQL.getAllItemDetail();
			List<Modifier> modifiers = ModifierSQL.getAllModifier();
			List<Printer> printers = PrinterSQL.getAllPrinter();
			List<TableInfo> tableInfos = TableInfoSQL.getAllTables();
			List<PlaceInfo> placeInfos = PlaceInfoSQL.getAllPlaceInfo();
			List<ItemModifier> itemModifiers = ItemModifierSQL.getAllItemModifier();
			List<TaxCategory> taxCategories = TaxCategorySQL.getAllTaxCategory();
			List<Tax> taxes = TaxSQL.getAllTax();
			List<ItemHappyHour> itemHappyHours = ItemHappyHourSQL.getAllItemHappyHour();
			List<HappyHourWeek> happyHourWeeks = HappyHourWeekSQL.getAllHappyHourWeek();
			List<HappyHour> happyHours = HappyHourSQL.getAllHappyHour();
			List<RestaurantConfig> restaurantConfigs = RestaurantConfigSQL.getAllRestaurantConfig();
			List<PrinterGroup> printerGroups = PrinterGroupSQL.getAllPrinterGroup();
			List<UserRestaurant> userRestaurants = UserRestaurantSQL.getAll();
//			List<LocalDevice> localDevices = LocalDeviceSQL.getAllLocalDevice();
			List<PaymentMethod> paymentMethods = PaymentMethodSQL.getAllPaymentMethod();
			List<SettlementRestaurant> settlementRestaurants = SettlementRestaurantSQL.getAllSettlementRestaurant();
			Map<String, Object> map = new HashMap<>();
			SessionStatus sessionStatus = App.instance.getSessionStatus();
			map.put("sessionStatus", sessionStatus);
			map.put("users", users);
			map.put("restaurant", restaurant);
			map.put("revenueCenter", revenueCenter);
			map.put("itemMainCategories", itemMainCategories);
			map.put("itemCategories", itemCategories);
			map.put("itemDetails", itemDetails);
			map.put("modifiers", modifiers);
			map.put("printers", printers);
			map.put("tableInfos", tableInfos);
			map.put("placeInfos", placeInfos);
			map.put("itemModifiers", itemModifiers);
			map.put("taxCategories", taxCategories);
			map.put("taxes", taxes);
			map.put("itemHappyHours", itemHappyHours);
			map.put("happyHourWeeks", happyHourWeeks);
			map.put("happyHours", happyHours);
			map.put("restaurantConfigs", restaurantConfigs);
			map.put("printerGroups", printerGroups);
			map.put("userRestaurants", userRestaurants);
//			map.put("localDevices", localDevices);
			map.put("paymentMethods", paymentMethods);
			map.put("settlementRestaurants", settlementRestaurants);
			map.put("resultCode", ResultCode.SUCCESS);
			resp = this.getJsonResponse(gson.toJson(map));
		} catch (Exception e){
			e.printStackTrace();
			resp = this.getInternalErrorResponse(App.instance.getResources().getString(R.string.sync_data_failed));
		}
		return resp;
	}
	private Response commitOrder(String params){
		Response resp = null;
		try {
			Map<String, Object> map = new HashMap<>();
			JSONObject jsonObject = new JSONObject(params);
			Order order = gson.fromJson(jsonObject.getString("order"), Order.class);
			int subPosBeanId = jsonObject.optInt("subPosBeanId");
			MultiOrderRelation multiOrderRelation = MultiOrderRelationSQL.getMultiOrderRelationBySubOrderId(subPosBeanId, order.getId().intValue(), order.getCreateTime());
			if(multiOrderRelation != null){
				map.put("resultCode", ResultCode.RECEIVE_MSG_EXIST);
				return this.getJsonResponse(gson.toJson(map));
			}

			List<OrderDetail> orderDetails = gson.fromJson(jsonObject.getString("orderDetails"),
					new TypeToken<List<OrderDetail>>(){}.getType());
			List<OrderModifier> orderModifiers = gson.fromJson(jsonObject.getString("orderModifiers"),
					new TypeToken<List<OrderModifier>>(){}.getType());
			List<Payment> payments = gson.fromJson(jsonObject.getString("payments"),
					new TypeToken<List<Payment>>(){}.getType());
			List<PaymentSettlement> paymentSettlements = gson.fromJson(jsonObject.getString("paymentSettlements"),
					new TypeToken<List<PaymentSettlement>>(){}.getType());
			List<OrderDetailTax> orderDetailTaxs = gson.fromJson(jsonObject.getString("orderDetailTaxs"),
					new TypeToken<List<OrderDetailTax>>(){}.getType());
			List<OrderSplit> orderSplits = gson.fromJson(jsonObject.getString("orderSplits"),
					new TypeToken<List<OrderSplit>>(){}.getType());
			List<OrderBill> orderBills = gson.fromJson(jsonObject.getString("orderBills"),
					new TypeToken<List<OrderBill>>(){}.getType());
			List<RoundAmount> roundAmounts = gson.fromJson(jsonObject.getString("roundAmounts"),
					new TypeToken<List<RoundAmount>>(){}.getType());
			boolean isSuccessful = SubPosCommitSQL.commitOrder(subPosBeanId, order, orderSplits, orderBills, payments, orderDetails,
					orderModifiers, orderDetailTaxs, paymentSettlements, roundAmounts);
			map.put("resultCode", ResultCode.SUCCESS);
			if(isSuccessful) {
				final int orderId = order.getId().intValue();
				new Thread(new Runnable() {
					@Override
					public void run() {
						Order placeOrder = OrderSQL.getOrder(orderId);
						List<OrderSplit> placeOrderSplit = OrderSplitSQL.getUnFinishedOrderSplits(orderId);
						KotSummary kotSummary = ObjectFactory.getInstance()
								.getKotSummary(
										TableInfoSQL.getTableById(
												placeOrder.getTableId()).getName(), placeOrder,
										App.instance.getRevenueCenter(),
										App.instance.getBusinessDate());
						if (placeOrderSplit != null && placeOrderSplit.size() > 0) {
							for (OrderSplit orderSplit : placeOrderSplit) {
								if (kotSummary != null) {
									ArrayList<KotItemModifier> kotItemModifiers = new ArrayList<KotItemModifier>();
									List<OrderDetail> placedOrderDetails = OrderDetailSQL.getOrderDetailsByOrderAndOrderSplit(orderSplit);
									List<Integer> orderDetailIds = new ArrayList<Integer>();
									ArrayList<KotItemDetail> kotItemDetails = new ArrayList<KotItemDetail>();
									for (OrderDetail orderDetail : placedOrderDetails) {
										orderDetailIds.add(orderDetail.getId());
										KotItemDetail kotItemDetail = ObjectFactory
												.getInstance()
												.getKotItemDetail(
														placeOrder,
														orderDetail,
														CoreData.getInstance()
																.getItemDetailById(
																		orderDetail
																				.getItemId()),
														kotSummary,
														App.instance.getSessionStatus(), ParamConst.KOTITEMDETAIL_CATEGORYID_MAIN);
										kotItemDetail.setItemNum(orderDetail
												.getItemNum());
//										if (kotItemDetail.getKotStatus() == ParamConst.KOT_STATUS_UNDONE) {
//											kotCommitStatus = ParamConst.JOB_UPDATE_KOT;
//											kotItemDetail
//													.setKotStatus(ParamConst.KOT_STATUS_UPDATE);
//										}
										KotItemDetailSQL.update(kotItemDetail);
										kotItemDetails.add(kotItemDetail);
										orderDetailIds.add(orderDetail.getId());
										ArrayList<OrderModifier> orderModifiers = OrderModifierSQL
												.getOrderModifiers(placeOrder, orderDetail);
										for (OrderModifier orderModifier : orderModifiers) {
											if (orderModifier.getStatus().intValue() == ParamConst.ORDER_MODIFIER_STATUS_NORMAL) {
												KotItemModifier kotItemModifier = ObjectFactory
														.getInstance()
														.getKotItemModifier(
																kotItemDetail,
																orderModifier,
																CoreData.getInstance()
																		.getModifier(
																				orderModifier
																						.getModifierId()));
												KotItemModifierSQL.update(kotItemModifier);
												kotItemModifiers.add(kotItemModifier);
											}
										}
									}
//									for (KotItemDetail kot : kotItemDetails) {
//										ArrayList<KotItemModifier> kotItemModifierObj = KotItemModifierSQL
//												.getKotItemModifiersByKotItemDetail(kot.getId());
//										if (kotItemModifierObj != null)
//											kotItemModifiers.addAll(kotItemModifierObj);
//									}
									Map<String, Object> orderMap = new HashMap<String, Object>();
									orderMap.put("orderId", orderSplit.getOrderId());
									orderMap.put("orderDetailIds", orderDetailIds);
									App.instance.getKdsJobManager().tearDownKot(
											kotSummary, kotItemDetails,
											kotItemModifiers, ParamConst.JOB_NEW_KOT,
											orderMap);
								}
							}
						} else {
							if (kotSummary != null) {
								ArrayList<KotItemModifier> kotItemModifiers = new ArrayList<KotItemModifier>();
								ArrayList<KotItemDetail> kotItemDetails = new ArrayList<>();
								List<OrderDetail> placedOrderDetails = OrderDetailSQL.getOrderDetails(placeOrder.getId());
								List<Integer> orderDetailIds = new ArrayList<Integer>();
								for (OrderDetail orderDetail : placedOrderDetails) {
									orderDetailIds.add(orderDetail.getId());
									KotItemDetail kotItemDetail = ObjectFactory
											.getInstance()
											.getKotItemDetail(
													placeOrder,
													orderDetail,
													CoreData.getInstance()
															.getItemDetailById(
																	orderDetail
																			.getItemId()),
													kotSummary,
													App.instance.getSessionStatus(), ParamConst.KOTITEMDETAIL_CATEGORYID_MAIN);
									kotItemDetail.setItemNum(orderDetail
											.getItemNum());
//										if (kotItemDetail.getKotStatus() == ParamConst.KOT_STATUS_UNDONE) {
//											kotCommitStatus = ParamConst.JOB_UPDATE_KOT;
//											kotItemDetail
//													.setKotStatus(ParamConst.KOT_STATUS_UPDATE);
//										}
									KotItemDetailSQL.update(kotItemDetail);
									kotItemDetails.add(kotItemDetail);
									orderDetailIds.add(orderDetail.getId());
									ArrayList<OrderModifier> orderModifiers = OrderModifierSQL
											.getOrderModifiers(placeOrder, orderDetail);
									for (OrderModifier orderModifier : orderModifiers) {
										if (orderModifier.getStatus().intValue() == ParamConst.ORDER_MODIFIER_STATUS_NORMAL) {
											KotItemModifier kotItemModifier = ObjectFactory
													.getInstance()
													.getKotItemModifier(
															kotItemDetail,
															orderModifier,
															CoreData.getInstance()
																	.getModifier(
																			orderModifier
																					.getModifierId()));
											KotItemModifierSQL.update(kotItemModifier);
											kotItemModifiers.add(kotItemModifier);
										}
									}
								}
//								for (KotItemDetail kot : kotItemDetails) {
//									ArrayList<KotItemModifier> kotItemModifierObj = KotItemModifierSQL
//											.getKotItemModifiersByKotItemDetail(kot.getId());
//									if (kotItemModifierObj != null)
//										kotItemModifiers.addAll(kotItemModifierObj);
//								}
//								TableInfo tableInfo = TableInfoSQL.getTableById(placeOrder.getTableId().intValue());
//								PrinterTitle title = ObjectFactory.getInstance()
//										.getPrinterTitle(
//												App.instance.getRevenueCenter(),
//												placeOrder,
//												App.instance.getUser().getFirstName()
//														+ App.instance.getUser().getLastName(),
//												tableInfo.getName(), 1);

								Map<String, Object> orderMap = new HashMap<String, Object>();

								orderMap.put("orderId", placeOrder.getId());
								orderMap.put("orderDetailIds", orderDetailIds);
//								orderMap.put("paidOrder", placeOrder);
//								orderMap.put("title", title);
//								orderMap.put("placedOrderDetails", placedOrderDetails);
								App.instance.getKdsJobManager().tearDownKot(
										kotSummary, kotItemDetails,
										kotItemModifiers, ParamConst.JOB_NEW_KOT,
										orderMap);
							}
						}
					}

				}).start();
			}
			resp = this.getJsonResponse(gson.toJson(map));
		} catch (Exception e){
			e.printStackTrace();
			resp = this.getInternalErrorResponse(App.instance.getResources().getString(R.string.sync_data_failed));
		}
		return resp;
	}


	private Response commitReport(String params){
		Response resp = null;
		try {
			Map<String, Object> map = new HashMap<>();
			JSONObject jsonObject = new JSONObject(params);
			ReportDaySales reportDaySales = gson.fromJson(jsonObject.getString("reportDaySales"), ReportDaySales.class);
			int subPosBeanId = jsonObject.optInt("subPosBeanId");
			MultiReportRelation multiReportRelation = MultiReportRelationSQL.getMultiReportRelationBySubReportId(subPosBeanId, reportDaySales.getId(), reportDaySales.getCreateTime());
			if(multiReportRelation != null){
				map.put("resultCode", ResultCode.RECEIVE_MSG_EXIST);
				return this.getJsonResponse(gson.toJson(map));
			}
			List<ReportDayTax> reportDayTaxs = gson.fromJson(jsonObject.getString("reportDayTaxs"),
					new TypeToken<List<ReportDayTax>>(){}.getType());
			List<ReportDayPayment> reportDayPayments = gson.fromJson(jsonObject.getString("reportDayPayments"),
					new TypeToken<List<ReportDayPayment>>(){}.getType());
			List<ReportPluDayItem> reportPluDayItems = gson.fromJson(jsonObject.getString("reportPluDayItems"),
					new TypeToken<List<ReportPluDayItem>>(){}.getType());
			List<ReportPluDayModifier> reportPluDayModifiers = gson.fromJson(jsonObject.getString("reportPluDayModifiers"),
					new TypeToken<List<ReportPluDayModifier>>(){}.getType());
			List<ReportHourly> reportHourlys = gson.fromJson(jsonObject.getString("reportHourlys"),
					new TypeToken<List<ReportHourly>>(){}.getType());
			List<ReportPluDayComboModifier> reportPluDayComboModifiers = gson.fromJson(jsonObject.getString("reportPluDayComboModifiers"),
					new TypeToken<List<ReportPluDayComboModifier>>(){}.getType());
			List<UserOpenDrawerRecord> userOpenDrawerRecords = gson.fromJson(jsonObject.getString("userOpenDrawerRecords"),
					new TypeToken<List<UserOpenDrawerRecord>>(){}.getType());

			SubPosCommitSQL.commitReport(subPosBeanId, reportDaySales, reportDayTaxs, reportDayPayments, reportPluDayItems,
					reportPluDayModifiers, reportHourlys, reportPluDayComboModifiers, userOpenDrawerRecords);
			SubPosBean subPosBean = SubPosBeanSQL.getSubPosBeanById(subPosBeanId);
			if(subPosBean != null){
				subPosBean.setSubPosStatus(ParamConst.SUB_POS_STATUS_CLOSE);
				SubPosBeanSQL.updateSubPosBean(subPosBean);
			}
			map.put("resultCode", ResultCode.SUCCESS);
			resp = this.getJsonResponse(gson.toJson(map));
		}catch (Exception e){
			e.printStackTrace();
			resp = this.getInternalErrorResponse(App.instance.getResources().getString(R.string.sync_data_failed));
		}
		return resp;
	}


	@Override
	public Response doPost(String apiName, Method mothod,
			Map<String, String> params, String body) {
		LogUtil.d(TAG, "apiName : " + apiName + " body : " + body);
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(body);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return this.getForbiddenResponse("");
		}
		String appVersion = jsonObject.optString("appVersion");
		if (apiName == null || TextUtils.isEmpty(appVersion)) {
			return this.getForbiddenResponse("");
		}
		if(!appVersion.endsWith(App.instance.VERSION)){
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("resultCode", ResultCode.APP_VERSION_UNREAL);
			result.put("posVersion", App.instance.VERSION);
			VersionUpdate versionUpdate = new VersionUpdate();
			String value = MobclickAgent.getConfigParams(App.getTopActivity(), "updateVersion");
			result.put("versionUpdate", value);
			return this.getJsonResponse(new Gson().toJson(result));
		} else if (apiName.equals(APIName.LOGIN_LOGINVERIFY)) {// 厨房登录
			return handlerLogin(body);
		}else if (apiName.equals(APIName.GET_PRINTERS)) {// 厨房请求是否有新的数据
			return handlerKitGetPrinters(body);
		} else if (apiName.equals(APIName.EMPLOYEE_ID)) { // waiter请求配对
			return hanlderWaiterGetRevenues(body);
		}
		if (apiName.equals(APIName.HAPPYHOUR_GETHAPPYHOUR)) {// 欢乐时间
			return handlerHappyHourInfo();
		} else if (apiName.equals(APIName.ITEM_GETITEM)) {// 菜的信息
			return handlerItemInfo(body);
		} else if (apiName.equals(APIName.ITEM_GETITEMCATEGORY)) {// 菜分类信息
			return handlerItemCategoryInfo(body);
		} else if (apiName.equals(APIName.ITEM_GETMODIFIER)) {// 配料信息
			return handlerModifierInfo(body);
		} else if (apiName.equals(APIName.RESTAURANT_GETPLACEINFO)) {// 位置信息
			return handlerPlaceInfo(body);
		} else if (apiName.equals(APIName.RESTAURANT_GETRESTAURANTINFO)) {// 餐厅信息
			return handlerRestaurantInfo(body);
		} else if (apiName.equals(APIName.TAX_GETTAX)) {// 税的信息
			return handlerTaxInfo();
		} else if (apiName.equals(APIName.USER_GETUSER)) {// 用户信息
			return handlerUserInfo(body);
		} else if (apiName.equals(APIName.PAIRING_COMPLETE)) {
			return handlerPairingComplete(body);
		} else {
				String userKey = jsonObject.optString("userKey");
				if (TextUtils.isEmpty(userKey)
						|| App.instance.getUserByKey(userKey) == null) {
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("resultCode", ResultCode.USER_NO_PERMIT);
					return this.getJsonResponse(new Gson().toJson(result));
				}
			if (apiName.equals(APIName.LOGIN_LOGOUT)) {// 注销
				return handlerLogout(body);
			} else if (apiName.equals(APIName.SELECT_TABLES)) {// 选择桌子
				return handlerSelectTables(body);
			} else if (apiName.equals(APIName.GET_ORDERDETAILS)) {// 获取不是waiter点的
																	// orderDetails
				return handlerGetOrderDetails(body);
			} else if (apiName.equals(APIName.COMMIT_ORDER)) {// Waiter提交订单信息
				return handlerCommitOrder(body);
			} else if (apiName.equals(APIName.KDS_IP_CHANGE)) {
				return this.handlerKDSIpChange(body);
			} else if (apiName.equals(APIName.KOT_ITEM_COMPLETE)) { // 厨房提交item做完数据
				return handlerKOTItemComplete(body);
			} else if (apiName.equals(APIName.CANCEL_COMPLETE)) {// 厨房取消做完的菜
				return cancelComplete(body);
			} else if (apiName.equals(APIName.COLLECT_KOT_ITEM)) { // waiter
																	// 点击取菜
				return handlerCollectKotItem(body);
			} else if (apiName.equals(APIName.GET_KOT_NOTIFICATION)) {
				return handlerGetKotNotifications();
			} else if (apiName.equals(APIName.GET_BILL)) {
				return handlerGetBill(body);
			} else if (apiName.equals(APIName.PRINT_BILL)) {
				return handlerPrintBill(body);
			} else if (apiName.equals(APIName.CALL_SPECIFY_THE_NUMBER)){
				return handlerCallSpecifyNumber(body);
			} else if (apiName.equals(APIName.UNSEAT_TABLE)){
				return handlerWaiterUnseatTable(body);
			} else if (apiName.equals(APIName.VOID_ITEM)){
				return handlerWaiterVoidItem(body);
			} else {
				return this.getNotFoundResponse();
			}
		}
	}

	private Response handlerLogout(String params) {
		Response resp;
		Map<String, Object> result = new HashMap<String, Object>();
		Gson gson = new Gson();
		try {
			JSONObject jsonObject = new JSONObject(params);
			int deviceType = jsonObject.optInt("deviceType");
			String device = jsonObject.optString("device");
			if (!TextUtils.isEmpty(device)) {
				if (deviceType == ParamConst.DEVICE_TYPE_WAITER) {
					final WaiterDevice waiterDevice = gson.fromJson(device,
							WaiterDevice.class);
					App.instance.removeWaiterDevice(waiterDevice);
					CoreData.getInstance().removeLocalDeviceByDeviceIdAndIP(waiterDevice.getWaiterId(),waiterDevice.getIP());
					// Notify Waiter pairing complete
					App.getTopActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							App.getTopActivity().httpRequestAction(
									ParamConst.HTTP_REQ_REFRESH_WAITER_PAIRED,
									waiterDevice);
						}
					});
				} else if (deviceType == ParamConst.DEVICE_TYPE_KDS) {
					final KDSDevice kdsDevice = gson.fromJson(device,
							KDSDevice.class);
					App.instance.removeKDSDevice(kdsDevice.getDevice_id());
					CoreData.getInstance().removeLocalDeviceByDeviceIdAndIP(kdsDevice.getDevice_id(), kdsDevice.getIP());
					// Notify KDS pairing complete
					App.getTopActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							App.getTopActivity().httpRequestAction(
									ParamConst.HTTP_REQ_REFRESH_KDS_PAIRED,null);
						}
					});
				}
				result.put("mainPosInfo", App.instance.getMainPosInfo());
				result.put("resultCode", ResultCode.SUCCESS);
				resp = this.getJsonResponse(new Gson().toJson(result));
			} else {
				resp = this.getInternalErrorResponse("Device is invalid");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			resp = this.getInternalErrorResponse("");
		}
		return resp;
	}


	private Response hanlderWaiterGetRevenues(String params) {
		Response resp;
		Map<String, Object> result = new HashMap<String, Object>();
		Gson gson = new Gson();
		try {
			JSONObject jsonObject = new JSONObject(params);
			int employeeId = jsonObject.optInt("employee_ID");
			User user = CoreData.getInstance().getUserByEmpId(employeeId);
			if (App.instance.isRevenueKiosk()) {
				result.put("resultCode", ResultCode.USER_NO_PERMIT);
				resp = this.getJsonResponse(new Gson().toJson(result));
				return resp;
			}
			if (user != null) {
				RevenueCenter revenueCenter = App.instance.getRevenueCenter();
				Boolean isPermitted = CoreData.getInstance()
						.checkUserWaiterAccessInRevcenter(user.getId(),
								revenueCenter.getRestaurantId(),
								revenueCenter.getId());
				if (isPermitted) {
					List<RevenueCenter> revenueCenters = CoreData.getInstance()
							.getRevenueCenters();
					List<Integer> revenueCenterIds = UserRestaurantSQL
							.getRevenueIdByUserId(user.getId());
					result.put("revenueCenters", revenueCenters);
					result.put("revenueCenterIds", revenueCenterIds);
					result.put("user", user);
					result.put("revenue", revenueCenter);
					result.put("resultCode", ResultCode.SUCCESS);
				} else {
					result.put("resultCode", ResultCode.USER_NO_PERMIT);
					result.put("printers", null);
				}
				resp = this.getJsonResponse(new Gson().toJson(result));
			} else {
				result.put("resultCode", ResultCode.USER_NO_PERMIT);
				resp = this.getJsonResponse(new Gson().toJson(result));
			}
		} catch (Exception e) {
			// TODO: handle exception
			resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.internal_error));
		}
		return resp;
	}

	private Response handlerPairingComplete(String params) {
		Response resp;
		Map<String, Object> result = new HashMap<String, Object>();
		Gson gson = new Gson();
		try {
			JSONObject jsonObject = new JSONObject(params);
			int deviceType = jsonObject.optInt("deviceType");
			String device = jsonObject.optString("device");
			if (!TextUtils.isEmpty(device)) {
				if (deviceType == ParamConst.DEVICE_TYPE_WAITER && 
						!App.instance.isRevenueKiosk()) {
					final WaiterDevice waiterDevice = gson.fromJson(device,
							WaiterDevice.class);
//					if (App.instance.isWaiterLoginAllowed(waiterDevice)) {
						App.instance.addWaiterDevice(waiterDevice);
						LocalDevice localDevice = ObjectFactory
								.getInstance()
								.getLocalDevice("", "waiter", ParamConst.DEVICE_TYPE_WAITER,
										waiterDevice.getWaiterId(),
										waiterDevice.getIP(), waiterDevice.getMac(), "",0);
						CoreData.getInstance().addLocalDevice(localDevice);
						// Notify Waiter pairing complete
						App.getTopActivity().runOnUiThread(new Runnable() {
	
							@Override
							public void run() {
								App.getTopActivity().httpRequestAction(
										ParamConst.HTTP_REQ_CALLBACK_WAITER_PAIRED,
										waiterDevice);
							}
						});
//					} else {
//						result.put("resultCode", ResultCode.USER_LOGIN_EXIST);
//						resp = this.getJsonResponse(new Gson().toJson(result));
//						return resp;
//					}
				} else if (deviceType == ParamConst.DEVICE_TYPE_KDS) {
					KDSDevice kdsDevice = new KDSDevice();
					kdsDevice = gson.fromJson(device,
							KDSDevice.class);

					App.instance.addKDSDevice(kdsDevice.getDevice_id(),
							kdsDevice);
					final LocalDevice localDevice = ObjectFactory.getInstance()
							.getLocalDevice(kdsDevice.getName(),"kds",
									ParamConst.DEVICE_TYPE_KDS,
									kdsDevice.getDevice_id(),
									kdsDevice.getIP(), kdsDevice.getMac(), "",0);
					CoreData.getInstance().addLocalDevice(localDevice);
					final String kdsStr = kdsDevice == null ? "空的" : kdsDevice.toString();
					final String localStr = localDevice == null ? "空的" : localDevice.toString();

					// Notify KDS pairing complete
					final KDSDevice finalKdsDevice = kdsDevice;
					App.getTopActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
//							UIHelp.showToast(App.getTopActivity(), kdsStr);
//							UIHelp.showToast(App.getTopActivity(), localStr);
							App.getTopActivity().httpRequestAction(
									ParamConst.HTTP_REQ_CALLBACK_KDS_PAIRED,
									finalKdsDevice);
						}
					});
				}
				result.put("mainPosInfo", App.instance.getMainPosInfo());
				result.put("resultCode", ResultCode.SUCCESS);
				resp = this.getJsonResponse(new Gson().toJson(result));
			} else {
				resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.device_invalid));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			resp = this.getInternalErrorResponse("");
		}
		return resp;
	}

	private Response handlerLogin(String params) {
		Response resp = null;
		try {
			JSONObject jsonObject = new JSONObject(params);
			String employee_ID = jsonObject.optString("employee_ID");
			String password = jsonObject.optString("password");
			Integer type = jsonObject.optInt("type");
			User user = CoreData.getInstance().getUser(employee_ID, password);
			Map<String, Object> result = new HashMap<String, Object>();
			if (user != null) {
				SessionStatus sessionStatus = App.instance.getSessionStatus();
				if (sessionStatus == null) {
					result.put("resultCode", ResultCode.SESSION_IS_CLOSED);
					resp = this.getJsonResponse(new Gson().toJson(result));
				} else {
					String userKey = UUID.randomUUID().toString();

					MainPosInfo mainPosInfo = App.instance.getMainPosInfo();
					App.instance.addActiveUser(userKey, user);

					Gson gson = new Gson();
					if (type == ParamConst.USER_TYPE_KOT) {
						KDSDevice dev = gson.fromJson(jsonObject
								.optJSONObject("device").toString(),
								KDSDevice.class);
						LocalDevice localDevice = ObjectFactory.getInstance()
								.getLocalDevice(dev.getName(),"kds",
										ParamConst.DEVICE_TYPE_KDS,
										dev.getDevice_id(),
										dev.getIP(), dev.getMac(), "",0);

						CoreData.getInstance().addLocalDevice(localDevice);
						App.instance.addKDSDevice(dev.getDevice_id(), dev);
						List<KotItemDetail> kotItemDetails = new ArrayList<KotItemDetail>();
						List<KotItemModifier> kotItemModifiers = new ArrayList<KotItemModifier>();
						List<KotSummary> kotSummaryList = new ArrayList<KotSummary>();
						if(App.instance.isRevenueKiosk()){
							kotSummaryList = KotSummarySQL.getUndoneKotSummaryByBusinessDateForKiosk(App.instance
									.getBusinessDate());
						}else{
							kotSummaryList = KotSummarySQL
									.getUndoneKotSummaryByBusinessDateAndOrderUnfinish(App.instance
											.getBusinessDate());
						}

						List<PrinterGroup> printerGroupList = CoreData
								.getInstance().getPrinterGroupByPrinter(
										dev.getDevice_id());

						for (KotSummary kotSummary : kotSummaryList) {
							for (PrinterGroup printerGroup : printerGroupList) {
								kotItemDetails
										.addAll(KotItemDetailSQL
												.getKotItemDetailByKotSummaryAndPrinterGroup(
														kotSummary.getId(),
														printerGroup
																.getPrinterGroupId()));
							}
						}
						for (KotItemDetail kotItemDetail : kotItemDetails) {
							kotItemModifiers
									.addAll(KotItemModifierSQL
											.getKotItemModifiersByKotItemDetail(kotItemDetail
													.getId()));
						}

						result.put("kotSummaryList", kotSummaryList);
						result.put("kotItemDetails", kotItemDetails);
						result.put("kotItemModifiers", kotItemModifiers);
						result.put("user", user);
						result.put("resultCode", ResultCode.SUCCESS);
						result.put("userKey", userKey);
						result.put("mainPosInfo", mainPosInfo);
						result.put("session", sessionStatus);
						result.put("businessDate", App.instance.getBusinessDate());
						resp = this.getJsonResponse(new Gson().toJson(result));
						
					} else if (type == ParamConst.USER_TYPE_WAITER && 
								!App.instance.isRevenueKiosk()) {
						
							//no need waiter app in kiosk mode
							WaiterDevice dev =  gson.fromJson(
									jsonObject.optJSONObject("device").toString(),
									WaiterDevice.class);
							//waiter can login one device at one time
//							if (App.instance.isWaiterLoginAllowed(dev)) {
								App.instance.addWaiterDevice(dev);
								LocalDevice localDevice = ObjectFactory
										.getInstance()
										.getLocalDevice("", "waiter", ParamConst.DEVICE_TYPE_WAITER,
												dev.getWaiterId(),
												dev.getIP(), dev.getMac(), "",0);
								CoreData.getInstance().addLocalDevice(localDevice);
								result.put("user", user);
								result.put("resultCode", ResultCode.SUCCESS);
								result.put("userKey", userKey);
								result.put("mainPosInfo", mainPosInfo);
								result.put("session", sessionStatus);
								result.put("businessDate", App.instance.getBusinessDate());	
								result.put("currencySymbol", App.instance.getLocalRestaurantConfig().getCurrencySymbol());
								result.put("isDouble", App.instance.getLocalRestaurantConfig().getCurrencySymbolType() >= 0);
//							} else {
//								result.put("resultCode", ResultCode.USER_LOGIN_EXIST);
//							}
							
							resp = this.getJsonResponse(new Gson().toJson(result));
					}
				}
			} else {
				result.put("resultCode", ResultCode.USER_NO_PERMIT);
				resp = this.getJsonResponse(new Gson().toJson(result));
			}
		} catch (Exception e) {
			e.printStackTrace();
			resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.login_failed));
		}
		return resp;
	}

	private Response handlerRestaurantInfo(String params) {
		Response resp;
		Map<String, Object> result = new HashMap<String, Object>();
		if (!isValidUser(params)) {
			result.put("resultCode", ResultCode.USER_NO_PERMIT);
			resp = this.getJsonResponse(new Gson().toJson(result));
		} else {
			result.put("revenueList", CoreData.getInstance()
					.getRevenueCenters());
			result.put("printerList", CoreData.getInstance().getPrinters());
			result.put("restaurant", CoreData.getInstance().getRestaurant());
			result.put("resultCode", ResultCode.SUCCESS);
			resp = this.getJsonResponse(new Gson().toJson(result));
		}
		return resp;
	}

	private Response handlerUserInfo(String params) {
		// if (!isValidUser()) {
		// send404();
		// return;
		// }
		Response resp;
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("userList", CoreData.getInstance().getUsers());
		result.put("resultCode", ResultCode.SUCCESS);
		resp = this.getJsonResponse(new Gson().toJson(result));
		return resp;
	}

	private Response handlerItemCategoryInfo(String params) {
		// if (!isValidUser()) {
		// send404();
		// return;
		// }
		Response resp;
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("subCategoryList", CoreData.getInstance()
				.getItemCategories());
		result.put("mainCategoryList", CoreData.getInstance()
				.getItemMainCategories());
		result.put("resultCode", ResultCode.SUCCESS);
		resp = this.getJsonResponse(new Gson().toJson(result));
		return resp;
	}

	private Response handlerItemInfo(String params) {
		Response resp;
		// if (!isValidUser()) {
		// send404();
		// return;
		// }
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("itemList", CoreData.getInstance().getItemDetails());
		result.put("resultCode", ResultCode.SUCCESS);
		resp = this.getJsonResponse(new Gson().toJson(result));
		return resp;
	}

	private Response handlerModifierInfo(String params) {
		Response resp;
		// if (!isValidUser()) {
		// send404();
		// return;
		// }
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("itemModifierList", CoreData.getInstance()
				.getItemModifiers());
		result.put("modifierList", CoreData.getInstance().getModifierList());
		result.put("resultCode", ResultCode.SUCCESS);
		resp = this.getJsonResponse(new Gson().toJson(result));
		return resp;
	}

	private Response handlerTaxInfo() {
		Response resp;
		// if (!isValidUser()) {
		// send404();
		// return;
		// }
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("taxList", CoreData.getInstance().getTaxs());
		result.put("taxCategoryList", CoreData.getInstance().getTaxCategories());
		result.put("resultCode", ResultCode.SUCCESS);
		resp = this.getJsonResponse(new Gson().toJson(result));
		return resp;
	}

	private Response handlerHappyHourInfo() {
		// if (!isValidUser()) {
		// send404();
		// return;
		// }
		Response resp;
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("itemHappyHourList", CoreData.getInstance()
				.getItemHappyHours());
		result.put("happyHourWeekList", CoreData.getInstance()
				.getHappyHourWeeks());
		result.put("happyHourList", CoreData.getInstance().getHappyHours());
		result.put("resultCode", ResultCode.SUCCESS);
		resp = this.getJsonResponse(new Gson().toJson(result));
		return resp;
	}

	private Response handlerPlaceInfo(String params) {
		Response resp;
		// if (!isValidUser()) {
		// send404();
		// return;
		// }
		Integer revenueId = 0;
		Map<String, Object> result = new HashMap<String, Object>();
		
		/*No waiter apps in kiosk mode */
		if (App.instance.isRevenueKiosk()) {
			result.put("resultCode", ResultCode.USER_NO_PERMIT);
			resp = this.getJsonResponse(new Gson().toJson(result));
			return resp;			
		}		
		try {
			revenueId = new JSONObject(params).optInt("revenueId");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		result.put("placeList", PlaceInfoSQL.getAllPlaceInfo());
		result.put("tableList", TableInfoSQL.getAllTables());
		result.put("printMap", App.instance.getPrinterDevices());
		result.put("resultCode", ResultCode.SUCCESS);
		resp = this.getJsonResponse(new Gson().toJson(result));
		return resp;
	}

	//Waiter Select Table from Waiter APP
	private Response handlerSelectTables(String params) {
		Response resp;
		Map<String, Object> result = new HashMap<String, Object>();
		
		/*No waiter apps in kiosk mode */
		if (App.instance.isRevenueKiosk()) {
			result.put("resultCode", ResultCode.USER_NO_PERMIT);
			resp = this.getJsonResponse(new Gson().toJson(result));
			return resp;			
		}
		
		if (!isValidUser(params)) {
			result.put("resultCode", ResultCode.USER_NO_PERMIT);
			resp = this.getJsonResponse(new Gson().toJson(result));
			return resp;
		}

		try {
			JSONObject jsonObject = new JSONObject(params);
			Gson gson = new Gson();
			TableInfo tables = gson.fromJson(jsonObject.optJSONObject("tables")
					.toString(), TableInfo.class);
			String userKey = jsonObject.optString("userKey");
			result.put("tempItems", ItemDetailSQL.getAllTempItemDetail());
			if (TableInfoSQL.getTableById(tables.getPosId())
					.getStatus() == ParamConst.TABLE_STATUS_IDLE) {
//				TablesSQL.updateTables(tables);
				TableInfoSQL.updateTables(tables);
				//clean all KOT summary and KotDetails if the table is EMPTY
				KotSummary kotSummary = KotSummarySQL.getKotSummaryByTable(tables.getPosId().intValue());
				if(kotSummary != null){
					List<KotItemDetail> kotItemDetails = KotItemDetailSQL.getKotItemDetailByKotSummaryIdUndone(kotSummary);
					if(kotItemDetails != null)
					for(KotItemDetail kotItemDetail : kotItemDetails){
						kotItemDetail.setKotStatus(ParamConst.KOT_STATUS_DONE);
						KotItemDetailSQL.update(kotItemDetail);
					}
					kotSummary.setStatus(ParamConst.KOTS_STATUS_DONE);
					KotSummarySQL.update(kotSummary);
				}
			
//				CoreData.getInstance().setTableList(TablesSQL.getAllTables());
				Order order = ObjectFactory.getInstance().getOrder(
						ParamConst.ORDER_ORIGIN_WAITER, App.instance.getSubPosBeanId(), tables,
						App.instance.getRevenueCenter(),
						App.instance.getUserByKey(userKey),
						App.instance.getSessionStatus(),
						App.instance.getBusinessDate(),
						App.instance.getIndexOfRevenueCenter(),
						ParamConst.ORDER_STATUS_OPEN_IN_WAITER,
						App.instance.getLocalRestaurantConfig()
						.getIncludedTax().getTax());
				// ArrayList<OrderDetail> orderDetails = OrderDetailSQL
				// .getOrderDetailByOrderIdAndOrderOriginId(order.getId(),
				// ParamConst.ORDER_ORIGIN_WAITER);
				result.put("order", order);
				result.put("resultCode", ResultCode.SUCCESS);
				resp = this.getJsonResponse(new Gson().toJson(result));
				try {
					JSONObject jsonObject1 = new JSONObject();
					jsonObject1.put("tableId", tables.getPosId().intValue());
					jsonObject1.put("status", ParamConst.TABLE_STATUS_DINING);
					jsonObject1.put("RX", RxBus.RX_REFRESH_TABLE);
					TcpUdpFactory.sendUdpMsg(BaseApplication.UDP_INDEX_WAITER,TcpUdpFactory.UDP_REQUEST_MSG+ jsonObject1.toString(),null);
					TcpUdpFactory.sendUdpMsg(BaseApplication.UDP_INDEX_EMENU,TcpUdpFactory.UDP_REQUEST_MSG+ jsonObject1.toString(),null);
				}catch (Exception e){
					e.printStackTrace();
				}
				App.getTopActivity().httpRequestAction(
						MainPage.REFRESH_TABLES_STATUS, tables);
			} else {// 错误处理
				// result.put("resultCode", ResultCode.UNKNOW_ERROR);
				// send200(new Gson().toJson(result));

				// 暂时也回复正常数据，便于测试
//				TablesSQL.updateTables(tables);
				TableInfoSQL.updateTables(tables);
				Order order = ObjectFactory.getInstance().getOrder(
						ParamConst.ORDER_ORIGIN_WAITER, App.instance.getSubPosBeanId(), tables,
						App.instance.getRevenueCenter(),
						App.instance.getUserByKey(userKey),
						App.instance.getSessionStatus(),
						App.instance.getBusinessDate(),
						App.instance.getIndexOfRevenueCenter(),
						ParamConst.ORDER_STATUS_OPEN_IN_WAITER,
						App.instance.getLocalRestaurantConfig()
						.getIncludedTax().getTax());
				ArrayList<OrderDetail> orderDetails = OrderDetailSQL
						.getAllOrderDetailsByOrder(order);
				result.put("order", order);
				result.put("resultCode", ResultCode.SUCCESS);
				result.put("orderDetails", orderDetails);
				resp = this.getJsonResponse(new Gson().toJson(result));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.internal_error));
		}
		return resp;
	}

	private Response handlerGetOrderDetails(String params) {
		Response resp;
		Map<String, Object> result = new HashMap<String, Object>();
		
		/*No waiter apps in kiosk mode */
		if (App.instance.isRevenueKiosk()) {
			result.put("resultCode", ResultCode.USER_NO_PERMIT);
			resp = this.getJsonResponse(new Gson().toJson(result));
			return resp;			
		}
		
		if (!isValidUser(params)) {
			result.put("resultCode", ResultCode.USER_NO_PERMIT);
			resp = this.getJsonResponse(new Gson().toJson(result));
			return resp;
		}
		try {
			JSONObject jsonObject = new JSONObject(params);
			Gson gson = new Gson();
			Order order = gson.fromJson(jsonObject.optString("order"),
					Order.class);
			ArrayList<OrderDetail> orderDetails = OrderDetailSQL
					.getOrderDetailByOrderIdAndOrderOriginId(order.getId(),
							ParamConst.ORDER_ORIGIN_WAITER);
			result.put("otherOrderDetails", orderDetails);
			result.put("resultCode", ResultCode.SUCCESS);
			resp = this.getJsonResponse(new Gson().toJson(result));
		} catch (JSONException e) {
			e.printStackTrace();
			resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.internal_error));
		}
		return resp;
	}

	private Response handlerCommitOrder(String params) {
		System.out.println("1111111111111");
		Response resp;
		Map<String, Object> result = new HashMap<String, Object>();
		
		/*No waiter apps in kiosk mode */
		if (App.instance.isRevenueKiosk()) {
			result.put("resultCode", ResultCode.USER_NO_PERMIT);
			resp = this.getJsonResponse(new Gson().toJson(result));
			return resp;			
		}
		
		if (!isValidUser(params)) {
			result.put("resultCode", ResultCode.USER_NO_PERMIT);
			resp = this.getJsonResponse(new Gson().toJson(result));
			return resp;
		}
		try {
			JSONObject jsonObject = new JSONObject(params);
			Gson gson = new Gson();
			Order order = gson.fromJson(jsonObject.optJSONObject("order")
					.toString(), Order.class);
			Order loadOrder = OrderSQL.getUnfinishedOrder(order.getId());
			if (loadOrder == null) {
				result.put("resultCode", ResultCode.ORDER_FINISHED);
				resp = this.getJsonResponse(new Gson().toJson(result));
				return resp;
			}
			if((App.instance.orderInPayment != null && App.instance.orderInPayment.getId().intValue() == order.getId().intValue())){
				result.put("resultCode", ResultCode.NONEXISTENT_ORDER);
				resp = this.getJsonResponse(new Gson().toJson(result));
				return resp;
			}
			if(App.instance.getClosingOrderId() == order.getId()){
				result.put("resultCode", ResultCode.ORDER_HAS_CLOSING);
				resp = this.getJsonResponse(new Gson().toJson(result));
				return resp;
			}
			ArrayList<OrderDetail> waiterOrderDetails = gson.fromJson(
					jsonObject.optString("orderDetails"),
					new TypeToken<ArrayList<OrderDetail>>() {
					}.getType());
			ArrayList<OrderModifier> waiterOrderModifiers = gson.fromJson(
					jsonObject.optString("orderModifiers"),
					new TypeToken<ArrayList<OrderModifier>>() {
					}.getType());
			if(OrderDetailSQL.getOrderDetailCountByOrder(order) > 0) {
				// 检测是否有 订单是已经完成的拆单中的
				for (OrderDetail orderDetail : waiterOrderDetails) {
					OrderSplit loadOrderSplit = OrderSplitSQL.getOrderSplitByOrderAndGroupId(order, orderDetail.getGroupId());
					if (loadOrderSplit != null && loadOrderSplit.getOrderStatus() == ParamConst.ORDERSPLIT_ORDERSTATUS_FINISHED) {
						result.put("resultCode", ResultCode.ORDER_SPLIT_IS_SETTLED);
						result.put("groupId", loadOrderSplit.getGroupId());
						resp = this.getJsonResponse(new Gson().toJson(result));
						return resp;
					}
				}
			}
			LogUtil.i(TAG, "------11111");
			// waiter 过来的数据 存到 pos的DB中 不带Id存储
			for (OrderDetail orderDetail : waiterOrderDetails) {
				synchronized (lockObject) {
					OrderDetail orderDetailFromPOS = OrderDetailSQL
							.getOrderDetailByCreateTime(
									orderDetail.getCreateTime(),
									orderDetail.getOrderId());
					if (orderDetailFromPOS != null) {
						continue;
					} else {
						
						int orderDetailId = CommonSQL
								.getNextSeq(TableNames.OrderDetail);
						int oldOrderDetailId = orderDetail.getId();
						orderDetail
								.setOrderDetailStatus(ParamConst.ORDERDETAIL_STATUS_ADDED);
						orderDetail.setId(orderDetailId);
						OrderDetailSQL.addOrderDetailETC(orderDetail);
						if(orderDetail.getGroupId().intValue() > 0){
							OrderSplit orderSplit = ObjectFactory.getInstance().getOrderSplit(order, orderDetail.getGroupId(),App.instance.getLocalRestaurantConfig()
									.getIncludedTax().getTax());
							OrderSplitSQL.updateOrderSplitByOrder(order, orderSplit);
							orderDetail.setOrderSplitId(orderSplit.getId());
							OrderDetailSQL.updateOrderDetail(orderDetail);
							OrderDetailTaxSQL.updateOrderSplitIdbyOrderDetail(orderDetail);
						}
						if (waiterOrderModifiers != null
								&& !waiterOrderModifiers.isEmpty()) {
							for (OrderModifier orderModifier : waiterOrderModifiers) {
								if (orderModifier.getOrderDetailId().intValue() == oldOrderDetailId) {
									orderModifier.setOrderDetailId(orderDetailId);
									orderModifier.setId(CommonSQL
											.getNextSeq(TableNames.OrderModifier));
									OrderModifierSQL
											.updateOrderModifier(orderModifier);
								}
							}
						}
					}
				}
			}
			LogUtil.i(TAG, "=====11111");
			order.setOrderStatus(ParamConst.ORDER_STATUS_OPEN_IN_POS);
			
			//	当前Order未完成时更新状态
			OrderSQL.updateUnFinishedOrderFromWaiter(order);
			//	这边重新从数据中获取OrderDetail 不依赖于waiter过来的数据
			List<OrderDetail> orderDetails = OrderDetailSQL
					.getOrderDetails(order.getId());
			if(!orderDetails.isEmpty()){
				Order placedOrder = OrderSQL.getOrder(order.getId());
				if(placedOrder.getOrderNo().intValue() == 0){
					order.setOrderNo(OrderHelper.calculateOrderNo(order.getBusinessDate()));
					OrderSQL.updateOrderNo(order);
				}
				OrderBill orderBill = ObjectFactory.getInstance().getOrderBill(
						order, App.instance.getRevenueCenter());
				OrderBillSQL.add(orderBill);
			}
			LogUtil.i(TAG, "4444444444");
//			RoundAmount roundAmount = ObjectFactory.getInstance()
//					.getRoundAmount(order, orderBill, App.instance.getLocalRestaurantConfig().getRoundType());
//			RoundAmountSQL.update(roundAmount);
			String kotCommitStatus;
			KotSummary kotSummary = ObjectFactory.getInstance().getKotSummaryForPlace(
					TableInfoSQL.getTableById(order.getTableId()).getName(),
					order, App.instance.getRevenueCenter(), App.instance.getBusinessDate());
			User user = UserSQL.getUserById(order.getUserId());
			if(user != null){
				String empName = user.getFirstName() + user.getLastName();
				kotSummary.setEmpName(empName);
				KotSummarySQL.updateKotSummaryEmpById(empName, kotSummary.getId().intValue());
			}
			List<Integer> orderDetailIds = new ArrayList<Integer>();
			ArrayList<KotItemDetail> kotItemDetails = new ArrayList<KotItemDetail>();
			ArrayList<KotItemModifier> kotItemModifiers = new ArrayList<KotItemModifier>();
			kotCommitStatus = ParamConst.JOB_NEW_KOT;
			for (OrderDetail orderDetail : orderDetails) {
				if (orderDetail.getOrderDetailStatus() >= ParamConst.ORDERDETAIL_STATUS_PREPARED) {
					continue;
				}
				if (orderDetail.getOrderDetailStatus() == ParamConst.ORDERDETAIL_STATUS_KOTPRINTERD) {
					kotCommitStatus = ParamConst.JOB_UPDATE_KOT;
				} else {
					KotItemDetail kotItemDetail = ObjectFactory
							.getInstance()
							.getKotItemDetail(
									order,
									orderDetail,
									CoreData.getInstance().getItemDetailById(
											orderDetail.getItemId()),
									kotSummary, App.instance.getSessionStatus(), ParamConst.KOTITEMDETAIL_CATEGORYID_MAIN);
					kotItemDetail.setItemNum(orderDetail.getItemNum());
					if (kotItemDetail.getKotStatus() == ParamConst.KOT_STATUS_UNDONE) {
						kotCommitStatus = ParamConst.JOB_UPDATE_KOT;
						kotItemDetail
								.setKotStatus(ParamConst.KOT_STATUS_UPDATE);
					}
					KotItemDetailSQL.update(kotItemDetail);
					kotItemDetails.add(kotItemDetail);
					orderDetailIds.add(orderDetail.getId());
					ArrayList<OrderModifier> orderModifiers = OrderModifierSQL
							.getOrderModifiers(order, orderDetail);
					for (OrderModifier orderModifier : orderModifiers) {
						KotItemModifier kotItemModifier = ObjectFactory
								.getInstance().getKotItemModifier(
										kotItemDetail,
										orderModifier,
										CoreData.getInstance().getModifier(
												orderModifier.getModifierId()));
						KotItemModifierSQL.update(kotItemModifier);
						kotItemModifiers.add(kotItemModifier);
					}
				}
			}
			result.put("order", order);
			result.put("orderDetails", OrderDetailSQL.getOrderDetails(order.getId()));
			result.put("orderModifiers",
					OrderModifierSQL.getAllOrderModifier(order));
			result.put("orderDetailTaxs",
					OrderDetailTaxSQL.getAllOrderDetailTax(order));
			result.put("resultCode", ResultCode.SUCCESS);
			resp = this.getJsonResponse(new Gson().toJson(result));
			// App.getTopActivity().httpRequestAction(MainPage.WAITER_SEND_KDS,
			// kotMap);
			Map<String, Object> orderMap = new HashMap<String, Object>();
			orderMap.put("orderId", order.getId());
			orderMap.put("orderDetailIds", orderDetailIds);
			LogUtil.i(TAG, "3333333333333");
			if (!kotItemDetails.isEmpty()) {
				KotSummarySQL.update(kotSummary);
				if (!App.instance.isRevenueKiosk() && App.instance.getSystemSettings().isOrderSummaryPrint()) {
					PrinterDevice printer = App.instance.getCahierPrinter();
					if (printer == null) {
						UIHelp.showToast(
								App.getTopActivity(), App.getTopActivity().getResources().getString(R.string.setting_printer));
					} else {
						App.instance.remoteOrderSummaryPrint(printer, kotSummary, kotItemDetails, kotItemModifiers);
					}
				}
				App.instance.getKdsJobManager()
						.tearDownKot(kotSummary, kotItemDetails, kotItemModifiers,
								kotCommitStatus, orderMap);
				App.getTopActivity().httpRequestAction(
						MainPage.VIEW_EVENT_SET_DATA, order.getId());
			}else{
				KotSummarySQL.deleteKotSummary(kotSummary);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.internal_error));
		}
		LogUtil.i(TAG, "22222222222222");
		return resp;
	}

	/*
	 * Handle request from KDS
	 */

	private Response handlerKDSIpChange(String params) {
		Map<String, Object> result = new HashMap<String, Object>();
		Response resp;
		try {
			JSONObject jsonObject = new JSONObject(params);
			/*
			 * {"device_id":220,
			 *  "mac":"98:3b:16:18:5a:94",
			 * "userKey":"f1b02b44-0a92-4c6c-a473-0534844067bc",
				"appVersion":"1.0.9",
				"ip":"192.168.0.8",
				"name":"KDS 1 KITCHEN"}
			*/
			String ip_str = jsonObject.optString("ip");
			int devideid = jsonObject.optInt("device_id");
			String mac = jsonObject.optString("mac");
			String devicename = jsonObject.optString("name");
			

			
			KDSDevice device = new KDSDevice();
			device.setDevice_id(devideid);
			device.setIP(ip_str);
			device.setMac(mac);
			device.setName(devicename);

			LocalDevice localDevice = ObjectFactory.getInstance()
					.getLocalDevice(device.getName(),"kds",
							ParamConst.DEVICE_TYPE_KDS,
							device.getDevice_id(),
							device.getIP(), device.getMac(), "",0);
			
			CoreData.getInstance().addLocalDevice(localDevice);
			
			App.instance.addKDSDevice(devideid, device);

			result.put("resultCode", ResultCode.SUCCESS);
			resp = this.getJsonResponse(new Gson().toJson(result));
		} catch (JSONException e) {
			e.printStackTrace();
			resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.internal_error));
		}
		return resp;
	}

// Not used
//	private Response handlerKotComplete(String params) {
//		Response resp;
//		Map<String, Object> result = new HashMap<String, Object>();
//		try {
//			JSONObject jsonObject = new JSONObject(params);
//
//			Gson gson = new Gson();
//			List<KotItemDetail> kotItemDetails = new ArrayList<KotItemDetail>();
//			kotItemDetails = gson.fromJson(
//					jsonObject.optJSONObject("kotItemDtails").toString(),
//					new TypeToken<List<KotItemDetail>>() {
//					}.getType());
//
//			List<KotItemModifier> kotItemModifiers = new ArrayList<KotItemModifier>();
//			kotItemModifiers = gson.fromJson(
//					jsonObject.optJSONObject("kotItemModifiers").toString(),
//					new TypeToken<List<KotItemDetail>>() {
//					}.getType());
//
//			KotSummary kotSummary = gson.fromJson(
//					jsonObject.optJSONObject("kotSummary").toString(),
//					KotSummary.class);
//
//			result.put("resultCode", ResultCode.SUCCESS);
//			resp = this.getJsonResponse(new Gson().toJson(result));
//		} catch (Exception e) {
//			e.printStackTrace();
//			resp = this.getInternalErrorResponse("Internal Error");
//		}
//		return resp;
//	}

	private Response handlerKitGetPrinters(String params) {
		Response resp;
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			JSONObject jsonObject = new JSONObject(params);
			int printerType = jsonObject.optInt("printerType");
			int employeeId = jsonObject.optInt("employeeId");
			// verify employee
			User usr = CoreData.getInstance().getUserByEmpId(employeeId);
			if (usr != null) {
				RevenueCenter rc = App.instance.getRevenueCenter();
				Boolean isPermitted = CoreData.getInstance()
						.checkUserKDSAccessInRevcenter(usr.getId(),
								rc.getRestaurantId(), rc.getId());
				if (isPermitted) {
					List<Printer> printers = PrinterSQL
							.getAllPrinterByType(printerType);
					LogUtil.i("http", PrinterSQL.getAllPrinter().toString());
					result.put("printers", printers);
					result.put("user", usr);
					result.put("resultCode", ResultCode.SUCCESS);
				} else {
					result.put("resultCode", ResultCode.USER_NO_PERMIT);
					result.put("printers", null);
				}
			} else {
				result.put("resultCode", ResultCode.USER_NO_PERMIT);
				result.put("printers", null);
			}
			resp = this.getJsonResponse(new Gson().toJson(result));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.internal_error));
		}
		return resp;
	}

	// private Response handlerKdsPairComplete(String params) {
	// Response resp;
	// Map<String, Object> result = new HashMap<String, Object>();
	// Gson gson = new Gson();
	// try {
	// JSONObject jsonObject = new JSONObject(params);
	// final KDSDevice kds = gson.fromJson(jsonObject.optJSONObject("kds")
	// .toString(), KDSDevice.class);
	// App.instance.addKDSDevice(kds.getDevice_id(), kds);
	// MainPosInfo mainPosInfo = App.instance.getMainPosInfo();
	// result.put("resultCode", ResultCode.SUCCESS);
	// result.put("mainpos", mainPosInfo);
	// resp = this.getJsonResponse(new Gson().toJson(result));
	//
	// //Notify KDS pairing complete
	// App.getTopActivity().runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	// App.getTopActivity().httpRequestAction(ParamConst.HTTP_REQ_CALLBACK_KDS_PAIRED,
	// kds);
	// }
	// });
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// resp = this.getInternalErrorResponse("Internal Error");
	// }
	// return resp;
	// }

	private Response handlerKOTItemComplete(String params) {
		Map<String, Object> result = new HashMap<String, Object>();
		Response resp;
		Gson gson = new Gson();
		try {
			JSONObject jsonObject = new JSONObject(params);
			
			final KotSummary kotSummary = gson.fromJson(
					jsonObject.getString("kotSummary"), KotSummary.class);
			List<KotNotification> kotNotifications = new ArrayList<KotNotification>();
			ArrayList<KotItemDetail> kotItemDetails = gson.fromJson(
					jsonObject.optString("kotItemDetails"),
					new TypeToken<ArrayList<KotItemDetail>>() {
					}.getType());
			KotSummary localKotSummary = KotSummarySQL.getKotSummaryById(kotSummary.getId().intValue());
			if(localKotSummary == null){
				result.put("resultCode", ResultCode.KOTSUMMARY_IS_UNREAL);
				resp = this.getJsonResponse(new Gson().toJson(result));
				return resp;
			}
			// Bob: fix bug: filter out old data that may be in KDS
			ArrayList<KotItemDetail> filteredKotItemDetails = new ArrayList<KotItemDetail>();
			for (int i = 0; i < kotItemDetails.size(); i++) {
				KotItemDetail kotItemDetail = kotItemDetails.get(i);
				if (kotItemDetail.getOrderId().intValue() == kotSummary.getOrderId().intValue())
					filteredKotItemDetails.add(kotItemDetail);
			}
			
			List<KotItemDetail> resultKotItemDetails = new ArrayList<KotItemDetail>();
			// end bug fix
			
			for (int i = 0; i < filteredKotItemDetails.size(); i++) {
				KotItemDetail kotItemDetail = filteredKotItemDetails.get(i);

				// Bob v1.0.4: if orderdetail is done, no need add notification
				OrderDetailSQL.updateOrderDetailStatusById(
						ParamConst.ORDERDETAIL_STATUS_PREPARED,
						kotItemDetail.getOrderDetailId());

				KotItemDetail lastSubKotItemDetail = KotItemDetailSQL.getLastKotItemDetailByOrderDetailId(kotItemDetail.getOrderDetailId());
				if(lastSubKotItemDetail != null && lastSubKotItemDetail.getUnFinishQty() != (kotItemDetail.getUnFinishQty() + kotItemDetail.getFinishQty())){
					result.put("resultCode", ResultCode.KOT_COMPLETE_USER_FAILED);
					resp = this.getJsonResponse(new Gson().toJson(result));
					return resp;
				}else if (kotItemDetail.getUnFinishQty() == 0){
					kotItemDetail.setFinishQty(kotItemDetail.getItemNum());
					kotItemDetail.setKotStatus(ParamConst.KOT_STATUS_DONE);
					kotItemDetail.setFireStatus(ParamConst.FIRE_STATUS_DEFAULT);
				}
				KotItemDetail subKotItemDetail = ObjectFactory.getInstance()
						.getSubKotItemDetail(kotItemDetail);
				resultKotItemDetails.add(subKotItemDetail);
				KotNotification kotNotification = ObjectFactory.getInstance()
						.getKotNotification(App.instance.getSessionStatus(),
								kotSummary, subKotItemDetail);

				kotNotifications.add(kotNotification);
			}
			if (filteredKotItemDetails.size() > 0) {
				KotItemDetailSQL.addKotItemDetailList(filteredKotItemDetails);
				KotNotificationSQL.addKotNotificationList(kotNotifications);
				App.getTopActivity().httpRequestAction(
						MainPage.VIEW_EVENT_SET_DATA, kotSummary.getOrderId());
				result.put("resultCode", ResultCode.SUCCESS);
				result.put("resultKotItemDetails", resultKotItemDetails);
				result.put("kotSummaryId", kotSummary.getId());
				new Thread(new Runnable() {
					@Override
					public void run() {
						if(!TextUtils.isEmpty(App.instance.getCallAppIp())) {
							String orderNo = kotSummary.getNumTag() + kotSummary.getOrderNo().toString();
							if(App.instance.isRevenueKiosk()){
								orderNo = kotSummary.getNumTag() + IntegerUtils.fromat(App.instance.getRevenueCenter().getIndexId(), kotSummary.getOrderNo().toString());
							}
							SyncCentre.getInstance().callAppNo(App.instance, orderNo);

						}
						int count = KotItemDetailSQL.getKotItemDetailCountBySummaryId(kotSummary.getId());
						if(count == 0){
							KotSummarySQL.updateKotSummaryStatusById(ParamConst.KOTS_STATUS_DONE, kotSummary.getId());
						}

							/* no waiter in kiosk mode*/
						if (!App.instance.isRevenueKiosk())
//							App.getTopActivity().runOnUiThread(new Runnable() {
//
//								@Override
//								public void run() {
									SyncCentre.getInstance()
											.notifyWaiterToGetNotifications(
													App.getTopActivity(),
													KotNotificationSQL
															.getAllKotNotificationQty());
//								}
//							});
						if(count == 0){
							Order order = OrderSQL.getOrder(kotSummary.getOrderId().intValue());
							if(order != null && !IntegerUtils.isEmptyOrZero(order.getAppOrderId())){
								AppOrder appOrder = AppOrderSQL.getAppOrderById(order.getAppOrderId().intValue());
								appOrder.setOrderStatus(ParamConst.APP_ORDER_STATUS_PREPARED);
								AppOrderSQL.updateAppOrder(appOrder);
								CloudSyncJobManager cloudSync = App.instance.getSyncJob();
								if (cloudSync != null) {
									cloudSync.checkAppOrderStatus(
											App.instance.getRevenueCenter().getId().intValue(),
											appOrder.getId().intValue(),
											appOrder.getOrderStatus().intValue(), "",
											App.instance.getBusinessDate().longValue(), appOrder.getOrderNo());
								}
								if(App.getTopActivity() instanceof NetWorkOrderActivity){
									App.getTopActivity().httpRequestAction(Activity.RESULT_OK, "");
								}
							}

						}
					}
				}).start();
				resp = this.getJsonResponse(new Gson().toJson(result));
			} else {
				result.put("resultCode", ResultCode.KOT_COMPLETE_FAILED);
				resp = this.getJsonResponse(new Gson().toJson(result));
			}
		} catch (Exception e) {
			e.printStackTrace();
			resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.internal_error));
		}

		return resp;
	}


//not used
//	private Response handlerKOTComplete(String params) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		Response resp;
//		Gson gson = new Gson();
//		try {
//			JSONObject jsonObject = new JSONObject(params);
//			KotSummary kotSummary = gson.fromJson(
//					jsonObject.getString("kotSummary"), KotSummary.class);
//			KotSummarySQL.update(kotSummary);
//			result.put("resultCode", ResultCode.SUCCESS);
//			resp = this.getJsonResponse(new Gson().toJson(result));
//		} catch (JSONException e) {
//			e.printStackTrace();
//			resp = this.getInternalErrorResponse("Internal Error");
//		}
//		return resp;
//	}

	private boolean isValidUser(String params) {
		String userKey = null;
		try {
			userKey = new JSONObject(params).optString("userKey");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (CommonUtil.isNull(userKey)
				|| App.instance.getUserByKey(userKey) == null) {
			return false;
		}
		return true;
	}

	private Response handlerGetKotNotifications() {
		Response resp;
		Map<String, Object> result = new HashMap<String, Object>();		
		List<KotNotification> kotNotifications = KotNotificationSQL
				.getAllKotNotification();
		List<String> tableNames = KotNotificationSQL.getTableNameList();
		List<TableAndKotNotificationList> notificationLists = new ArrayList<TableAndKotNotificationList>();
		
		/*No waiter apps in kiosk mode */
		if (App.instance.isRevenueKiosk()) {
			result.put("resultCode", ResultCode.USER_NO_PERMIT);
			resp = this.getJsonResponse(new Gson().toJson(result));
			return resp;			
		}
		
		for (String tableName : tableNames) {
			TableAndKotNotificationList tableAndKotNotificationList = new TableAndKotNotificationList();
			tableAndKotNotificationList.setTableName(tableName);
			for (KotNotification kotNotification : kotNotifications) {
				if (kotNotification.getTableName().equals(tableName)) {
					tableAndKotNotificationList.getKotNotifications().add(
							kotNotification);
				}
			}
			notificationLists.add(tableAndKotNotificationList);
		}

		result.put("notificationLists", notificationLists);
		result.put("resultCode", ResultCode.SUCCESS);
		resp = this.getJsonResponse(new Gson().toJson(result));
		return resp;
	}

	private Response handlerCollectKotItem(String params) {
		Response resp;
		JSONObject jsonObject;
		Map<String, Object> result = new HashMap<String, Object>();
		Gson gson = new Gson();
		/*No waiter apps in kiosk mode */
		if (App.instance.isRevenueKiosk()) {
			result.put("resultCode", ResultCode.USER_NO_PERMIT);
			resp = this.getJsonResponse(new Gson().toJson(result));
			return resp;			
		}
		
		try {
			jsonObject = new JSONObject(params);
			KotNotification kotNotification = gson.fromJson(jsonObject
					.optJSONObject("kotNotification").toString(),
					KotNotification.class);
			kotNotification.setStatus(ParamConst.KOTNOTIFICATION_STATUS_DELETE);
			KotNotificationSQL.update(kotNotification);
//			App.getTopActivity().runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
					SyncCentre.getInstance().notifyWaiterToGetNotifications(
							App.getTopActivity(),
							KotNotificationSQL.getAllKotNotificationQty());
//				}
//			});
			result.put("resultCode", ResultCode.SUCCESS);
			resp = this.getJsonResponse(new Gson().toJson(result));
		} catch (JSONException e) {
			e.printStackTrace();
			resp = this.getInternalErrorResponse("");
		}
		return resp;
	}

	private Response cancelComplete(String params) {
		Map<String, Object> result = new HashMap<String, Object>();
		Response resp;
		Gson gson = new Gson();
		LogUtil.e(TAG, "cancelComplete1");
		try {
			JSONObject jsonObject = new JSONObject(params);
			KotSummary kotSummary = gson.fromJson(
					jsonObject.getString("kotSummary"), KotSummary.class);
			KotItemDetail kotItemDetail = gson.fromJson(
					jsonObject.optString("kotItemDetail"),KotItemDetail.class);
			List<KotItemDetail> newKotItemDetails = new ArrayList<KotItemDetail>();

			if (kotItemDetail.getCategoryId().intValue() != ParamConst.KOTITEMDETAIL_CATEGORYID_SUB) {
				resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.internal_error));
				return resp;
			}
			LogUtil.e(TAG, "cancelComplete2");
			KotItemDetail kItemDetail = KotItemDetailSQL.getKotItemDetailById(kotItemDetail.getId());
			if (kItemDetail == null) {
//				resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.internal_error));
				result.put("resultCode", ResultCode.KOT_COMPLETE_USER_FAILED);
				return resp = this.getJsonResponse(new Gson().toJson(result));
			}
			
			KotItemDetail localMainKotItemDetail = KotItemDetailSQL
					.getMainKotItemDetailByOrderDetailId(kotItemDetail
							.getOrderDetailId());
			localMainKotItemDetail.setUnFinishQty(localMainKotItemDetail
					.getUnFinishQty().intValue()
					+ kotItemDetail.getFinishQty().intValue());
			localMainKotItemDetail.setKotStatus(ParamConst.KOT_STATUS_UNDONE);
			newKotItemDetails.add(localMainKotItemDetail);

			LogUtil.e(TAG, "cancelComplete3");
			List<KotItemDetail> localSubKotItemDetails = KotItemDetailSQL
					.getOtherSubKotItemDetailsByOrderDetailId(kotItemDetail);
			for (KotItemDetail localSubKotItemDetail : localSubKotItemDetails) {
				localSubKotItemDetail.setUnFinishQty(localSubKotItemDetail
						.getUnFinishQty().intValue()
						+ kotItemDetail.getFinishQty().intValue());
				newKotItemDetails.add(localSubKotItemDetail);
			}
			if (localMainKotItemDetail.getUnFinishQty().intValue() == localMainKotItemDetail.getItemNum().intValue()) {
				OrderDetailSQL.updateOrderDetailStatusById(ParamConst.ORDERDETAIL_STATUS_KOTPRINTERD, kotItemDetail.getOrderDetailId());
			}
			LogUtil.e(TAG, "cancelComplete4");
			KotItemDetailSQL.deleteKotItemDetail(kotItemDetail);
			KotItemDetailSQL.addKotItemDetailList(newKotItemDetails);
			KotNotificationSQL.deleteAllKotNotificationsByKotItemDetail(kotItemDetail);
			App.getTopActivity().httpRequestAction(
					MainPage.VIEW_EVENT_SET_DATA, kotSummary.getOrderId());
			result.put("resultCode", ResultCode.SUCCESS);
			result.put("newKotItemDetails", newKotItemDetails);
			LogUtil.e(TAG, "cancelComplete5");
			resp = this.getJsonResponse(new Gson().toJson(result));
//			App.getTopActivity().runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
					SyncCentre.getInstance().notifyWaiterToGetNotifications(
							App.getTopActivity(),
							KotNotificationSQL.getAllKotNotificationQty());
//				}
//			});
			LogUtil.e(TAG, "cancelComplete6");
		} catch (Exception e) {
			e.printStackTrace();
			resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.internal_error));
		}
		LogUtil.e(TAG, "cancelComplete7");
		return resp;
	}

	private Response handlerGetBill(String params) {
		Map<String, Object> result = new HashMap<String, Object>();
		Response resp;
		Gson gson = new Gson();
		
		/*No waiter apps in kiosk mode */
		if (App.instance.isRevenueKiosk()) {
			result.put("resultCode", ResultCode.USER_NO_PERMIT);
			resp = this.getJsonResponse(new Gson().toJson(result));
			return resp;			
		}
		
		try {
			JSONObject jsonObject = new JSONObject(params);
			TableInfo tables = gson.fromJson(jsonObject.getString("table"),
					TableInfo.class);
            //Table status in waiter APP is not same that of table in POS
			//need get latest status on app.
			TableInfo tabInPOS = TableInfoSQL.getTableById(tables.getPosId());
			App.getTopActivity().httpRequestAction(
					MainPage.VIEW_EVNT_GET_BILL_PRINT, tabInPOS);

			result.put("resultCode", ResultCode.SUCCESS);
			resp = this.getJsonResponse(new Gson().toJson(result));

		} catch (Exception e) {
			e.printStackTrace();
			resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.internal_error));
		}

		return resp;
	}
	private Response handlerPrintBill(String params) {
		Map<String, Object> result = new HashMap<String, Object>();
		Response resp;
		Gson gson = new Gson();

		/*No waiter apps in kiosk mode */
		if (App.instance.isRevenueKiosk()) {
			result.put("resultCode", ResultCode.USER_NO_PERMIT);
			resp = this.getJsonResponse(new Gson().toJson(result));
			return resp;
		}

		try {
			JSONObject jsonObject = new JSONObject(params);
			int orderId = jsonObject.getInt("orderId");
			Order loadOrder = OrderSQL.getUnfinishedOrder(orderId);
			if (loadOrder == null) {
				result.put("resultCode", ResultCode.ORDER_FINISHED);
				resp = this.getJsonResponse(new Gson().toJson(result));
				return resp;
			}
			String tableName = jsonObject.getString("tableName");
			int deviceId = 0;
			if(jsonObject.has("deviceId")) {
				 deviceId = jsonObject.getInt("deviceId");
			}
			if(orderId > 0) {
				Order order = OrderSQL.getOrder(orderId);
				if (order == null) {
					result.put("resultCode", ResultCode.ORDER_NO_PLACE);
					resp = this.getJsonResponse(new Gson().toJson(result));
					return resp;
				}
				if (order.getOrderStatus().intValue() == ParamConst.ORDER_STATUS_FINISHED) {
					result.put("resultCode", ResultCode.ORDER_FINISHED);
					resp = this.getJsonResponse(new Gson().toJson(result));
					return resp;
				}
				List<OrderSplit> orderSplits = OrderSplitSQL.getUnFinishedOrderSplits(order.getId().intValue());
				if (orderSplits != null && orderSplits.size() > 0) {
					PrinterDevice printer = App.instance.getCahierPrinter();
					if (deviceId != 0) {
						printer = App.instance.getPrinterDeviceById(deviceId);
					}
					for (OrderSplit orderSplit : orderSplits) {
						if (orderSplit.getOrderStatus().intValue() >= ParamConst.ORDERSPLIT_ORDERSTATUS_PAYED) {
							continue;
						}
						OrderBill orderBill = ObjectFactory.getInstance()
								.getOrderBillByOrderSplit(orderSplit,
										App.instance.getRevenueCenter());
						if (orderBill == null) {
							result.put("resultCode", ResultCode.ORDER_NO_PLACE);
							resp = this.getJsonResponse(new Gson().toJson(result));
							return resp;
						}
						ArrayList<OrderDetail> orderDetails = (ArrayList<OrderDetail>) OrderDetailSQL
								.getOrderDetailsByOrderAndOrderSplit(orderSplit);
						if (orderDetails.isEmpty()) {
							continue;
						}
						List<Map<String, String>> taxMap = OrderDetailTaxSQL
								.getOrderSplitTaxPriceSUMForPrint(App.instance.getLocalRestaurantConfig().getIncludedTax().getTax(), orderSplit);
						ArrayList<PrintOrderItem> orderItems = ObjectFactory
								.getInstance().getItemList(orderDetails);

						PrinterTitle title = ObjectFactory.getInstance()
								.getPrinterTitleByOrderSplit(
										App.instance.getRevenueCenter(),
										order,
										orderSplit,
										App.instance.getUser().getFirstName()
												+ App.instance.getUser()
												.getLastName(),
										TableInfoSQL.getTableById(
												orderSplit.getTableId())
												.getName(), orderBill, order.getBusinessDate().toString(), 1);

						OrderSplitSQL.updateOrderSplitStatus(ParamConst.ORDERSPLIT_ORDERSTATUS_UNPAY, orderSplit.getId().intValue());
						ArrayList<PrintOrderModifier> orderModifiers = ObjectFactory
								.getInstance().getItemModifierListByOrderDetail(
										orderDetails);
						Order temporaryOrder = new Order();
						temporaryOrder.setPersons(orderSplit.getPersons());
						temporaryOrder.setSubTotal(orderSplit.getSubTotal());
						temporaryOrder.setDiscountAmount(orderSplit.getDiscountAmount());
						temporaryOrder.setTotal(orderSplit.getTotal());
						temporaryOrder.setTaxAmount(orderSplit.getTaxAmount());
						temporaryOrder.setOrderNo(order.getOrderNo());
						App.instance.remoteBillPrint(printer, title, temporaryOrder,
								orderItems, orderModifiers, taxMap, null, null);
					}
				} else {
					OrderBill orderBill = OrderBillSQL
							.getOrderBillByOrder(order);
					if (orderBill == null) {
						result.put("resultCode", ResultCode.ORDER_NO_PLACE);
						resp = this.getJsonResponse(new Gson().toJson(result));
						return resp;
					}
					PrinterTitle title = ObjectFactory.getInstance()
							.getPrinterTitle(
									App.instance.getRevenueCenter(),
									order,
									App.instance.getUser().getFirstName()
											+ App.instance.getUser()
											.getLastName(),
									tableName, 1);
					ArrayList<PrintOrderItem> orderItems = ObjectFactory
							.getInstance().getItemList(
									OrderDetailSQL.getOrderDetails(order
											.getId()));
					ArrayList<PrintOrderModifier> orderModifiers = ObjectFactory
							.getInstance().getItemModifierList(order, OrderDetailSQL.getOrderDetails(order
									.getId()));
					List<Map<String, String>> taxMap = OrderDetailTaxSQL
							.getTaxPriceSUMForPrint(App.instance.getLocalRestaurantConfig().getIncludedTax().getTax(), order);
					PrinterDevice printer = App.instance.getCahierPrinter();
					if (deviceId != 0) {
						printer = App.instance.getPrinterDeviceById(deviceId);
					}
					App.instance.remoteBillPrint(printer, title, order,
							orderItems, orderModifiers, taxMap, null, null);
					OrderSQL.updateOrderStatus(ParamConst.ORDER_STATUS_UNPAY, orderId);
				}
			}
			result.put("resultCode", ResultCode.SUCCESS);
			resp = this.getJsonResponse(new Gson().toJson(result));

		} catch (Exception e) {
			e.printStackTrace();
			resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.internal_error));
		}

		return resp;
	}

	private Response handlerCallSpecifyNumber(String params){
		Map<String, Object> result = new HashMap<String, Object>();
		Response resp;
		Gson gson = new Gson();
		try {
			JSONObject jsonObject = new JSONObject(params);
			final String str = jsonObject.getString("callnumber");
			new Thread(new Runnable() {
				@Override
				public void run() {
					if(!TextUtils.isEmpty(App.instance.getCallAppIp())) {
						SyncCentre.getInstance().callAppNo(App.getTopActivity(), str);
					}
				}
			}).start();
			result.put("resultCode", ResultCode.SUCCESS);
			resp = this.getJsonResponse(new Gson().toJson(result));
		} catch (Exception e) {
			e.printStackTrace();
			resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.internal_error));
		}

		return resp;
	}

	private Response handlerWaiterVoidItem(String params){
		Map<String, Object> result = new HashMap<String, Object>();
		Response resp;
		try {
			JSONObject jsonObject = new JSONObject(params);
			int orderDetailId = jsonObject.getInt("orderDetailId");
			OrderDetail orderDetail = OrderDetailSQL.getOrderDetail(orderDetailId);
			if (orderDetail.getIsFree().intValue() == ParamConst.FREE) {
				result.put("resultCode", ResultCode.VOID_ITEM_FAIL);
				return this.getJsonResponse(new Gson().toJson(result));
			} else if(!IntegerUtils.isEmptyOrZero(orderDetail.getAppOrderDetailId())){
				result.put("resultCode", ResultCode.VOID_ITEM_FAIL);
				return this.getJsonResponse(new Gson().toJson(result));
			}else if(orderDetail.getOrderSplitId() != null && orderDetail.getOrderSplitId().intValue() != 0){
				OrderSplit orderSplit = OrderSplitSQL.get(orderDetail.getOrderSplitId().intValue());
				if(orderSplit.getOrderStatus().intValue() == ParamConst.ORDER_STATUS_FINISHED) {
					result.put("resultCode", ResultCode.SPLIT_ORDER_FINISHED);
					return this.getJsonResponse(new Gson().toJson(result));
				}
			}
			if (orderDetail.getOrderDetailStatus() >= ParamConst.ORDERDETAIL_STATUS_KOTPRINTERD) {
				OrderDetailSQL.setOrderDetailToVoidOrFree(
						orderDetail,
						ParamConst.ORDERDETAIL_TYPE_VOID);
				String kotCommitStatus = ParamConst.JOB_VOID_KOT;
				KotItemDetail kotItemDetail = KotItemDetailSQL
						.getMainKotItemDetailByOrderDetailId(orderDetail
								.getId());
				kotItemDetail.setKotStatus(ParamConst.KOT_STATUS_VOID);
				KotSummary kotSummary = KotSummarySQL.getKotSummary(orderDetail
						.getOrderId());
				KotItemDetailSQL.update(kotItemDetail);
				ArrayList<KotItemDetail> kotItemDetails = new ArrayList<KotItemDetail>();
				kotItemDetails.add(kotItemDetail);
				OrderDetail freeOrderDetail = OrderDetailSQL
						.getOrderDetail(
								orderDetail.getOrderId(),
								orderDetail);
				if (freeOrderDetail != null) {
					KotItemDetail freeKotItemDetail = KotItemDetailSQL
							.getMainKotItemDetailByOrderDetailId(freeOrderDetail
									.getId());
					freeKotItemDetail.setKotStatus(ParamConst.KOT_STATUS_VOID);
					KotItemDetailSQL.update(freeKotItemDetail);
					kotItemDetails.add(freeKotItemDetail);
				}

				//Bob: fix issue: kot print no modifier showup
				// look for kot modifiers
				Order placedOrder = OrderSQL.getOrder(orderDetail.getOrderId());
				ArrayList<KotItemModifier> kotItemModifiers = new ArrayList<KotItemModifier>();
				ArrayList<OrderModifier> orderModifiers = OrderModifierSQL
						.getOrderModifiers(placedOrder, orderDetail);
				for (OrderModifier orderModifier : orderModifiers) {
					if (orderModifier.getStatus().intValue() == ParamConst.ORDER_MODIFIER_STATUS_NORMAL) {
						Modifier mod = CoreData.getInstance().getModifier(orderModifier.getModifierId());
						if (mod != null) {
							KotItemModifier kotItemModifier = KotItemModifierSQL
									.getKotItemModifier(kotItemDetail.getId(), mod.getId());
							if (kotItemModifier != null)
								kotItemModifiers.add(kotItemModifier);
						}
					}
				}
				//end fix

				Map<String, Object> orderMap = new HashMap<String, Object>();
				ArrayList<Integer> orderDetailIds = new ArrayList<Integer>();
				orderDetailIds.add(orderDetail.getId());
				orderMap.put("orderId", orderDetail.getOrderId());
				orderMap.put("orderDetailIds", orderDetailIds);
				App.instance.getKdsJobManager().tearDownKot(
						kotSummary, kotItemDetails,
						kotItemModifiers,
						kotCommitStatus, orderMap);
				try {
					JSONObject jsonObjectMsg = new JSONObject();
					jsonObjectMsg.put("orderId", orderDetail.getOrderId().intValue());
					jsonObjectMsg.put("RX", RxBus.RX_REFRESH_ORDER);
					TcpUdpFactory.sendUdpMsg(BaseApplication.UDP_INDEX_WAITER, TcpUdpFactory.UDP_REQUEST_MSG + jsonObjectMsg.toString(), null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				BaseActivity activity = App.getTopActivity();
				if(activity instanceof MainPage){
					activity.httpRequestAction(MainPage.VIEW_EVENT_SET_DATA, orderDetail.getOrderId().intValue());
				}
			}else{
				result.put("resultCode", ResultCode.VOID_ITEM_FAIL);
				return this.getJsonResponse(new Gson().toJson(result));
			}
			result.put("resultCode", ResultCode.SUCCESS);
			resp = this.getJsonResponse(new Gson().toJson(result));
		} catch (Exception e) {
			e.printStackTrace();
			resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.internal_error));
		}

		return resp;
	}


	private Response handlerWaiterUnseatTable(String params){
		Map<String, Object> result = new HashMap<String, Object>();
		Response resp;
		try {
			JSONObject jsonObject = new JSONObject(params);
			int tableId = jsonObject.getInt("tableId");
			TableInfo tableInfo = TableInfoSQL.getTableById(tableId);
			if(tableInfo != null && tableInfo.getStatus().intValue() != ParamConst.TABLE_STATUS_IDLE){
				Order order = OrderSQL.getUnfinishedOrderAtTable(tableInfo.getPosId(), App.instance.getBusinessDate());
				int placeOrderCount = OrderDetailSQL.getOrderDetailPlaceOrderCountByOrder(order);
				if(placeOrderCount > 0) {
					result.put("resultCode",ResultCode.CONNOT_UNSEAT_TABLE);
					return this.getJsonResponse(new Gson().toJson(result));
				}else{
					OrderDetailSQL.deleteOrderDetailByOrder(order);
					KotSummarySQL.deleteKotSummaryByOrder(order);
					OrderBillSQL.deleteOrderBillByOrder(order);
					OrderSQL.deleteOrder(order);
					tableInfo.setStatus(ParamConst.TABLE_STATUS_IDLE);
					TableInfoSQL.updateTables(tableInfo);
					BaseActivity activity = App.getTopActivity();
					if(activity instanceof MainPage){
						activity.httpRequestAction(MainPage.REFRESH_UNSEAT_TABLE_VIEW, order.getId().intValue());
					}
					try {
						JSONObject jsonObjectMsg= new JSONObject();
						jsonObjectMsg.put("tableId", tableInfo.getPosId().intValue());
						jsonObjectMsg.put("status", ParamConst.TABLE_STATUS_IDLE);
						jsonObjectMsg.put("RX", RxBus.RX_REFRESH_TABLE);
						TcpUdpFactory.sendUdpMsg(BaseApplication.UDP_INDEX_WAITER,TcpUdpFactory.UDP_REQUEST_MSG+ jsonObjectMsg.toString(),null);
						TcpUdpFactory.sendUdpMsg(BaseApplication.UDP_INDEX_EMENU,TcpUdpFactory.UDP_REQUEST_MSG+ jsonObjectMsg.toString(),null);
					}catch (Exception e){
						e.printStackTrace();
					}
				}

			}else{
				result.put("resultCode",ResultCode.CONNOT_UNSEAT_TABLE);
				return this.getJsonResponse(new Gson().toJson(result));
			}
			result.put("resultCode", ResultCode.SUCCESS);
			resp = this.getJsonResponse(new Gson().toJson(result));
		} catch (Exception e) {
			e.printStackTrace();
			resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.internal_error));
		}

		return resp;
	}
}
