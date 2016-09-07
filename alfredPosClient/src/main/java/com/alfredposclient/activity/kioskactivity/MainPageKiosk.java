package com.alfredposclient.activity.kioskactivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.alfredbase.BaseActivity;
import com.alfredbase.LoadingDialog;
import com.alfredbase.ParamConst;
import com.alfredbase.PrinterLoadingDialog;
import com.alfredbase.VerifyDialog;
import com.alfredbase.global.CoreData;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.ItemModifier;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotItemModifier;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.Modifier;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderBill;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.OrderModifier;
import com.alfredbase.javabean.OrderSplit;
import com.alfredbase.javabean.Payment;
import com.alfredbase.javabean.PaymentSettlement;
import com.alfredbase.javabean.Places;
import com.alfredbase.javabean.PrinterTitle;
import com.alfredbase.javabean.RoundAmount;
import com.alfredbase.javabean.Tables;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.model.PrintOrderItem;
import com.alfredbase.javabean.model.PrintOrderModifier;
import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.javabean.model.SessionStatus;
import com.alfredbase.javabean.temporaryforapp.TempModifierDetail;
import com.alfredbase.javabean.temporaryforapp.TempOrder;
import com.alfredbase.javabean.temporaryforapp.TempOrderDetail;
import com.alfredbase.store.Store;
import com.alfredbase.store.sql.KotItemDetailSQL;
import com.alfredbase.store.sql.KotItemModifierSQL;
import com.alfredbase.store.sql.KotSummarySQL;
import com.alfredbase.store.sql.ModifierSQL;
import com.alfredbase.store.sql.OrderBillSQL;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.OrderDetailTaxSQL;
import com.alfredbase.store.sql.OrderModifierSQL;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredbase.store.sql.OrderSplitSQL;
import com.alfredbase.store.sql.PaymentSettlementSQL;
import com.alfredbase.store.sql.PlacesSQL;
import com.alfredbase.store.sql.RoundAmountSQL;
import com.alfredbase.store.sql.TablesSQL;
import com.alfredbase.store.sql.temporaryforapp.TempModifierDetailSQL;
import com.alfredbase.store.sql.temporaryforapp.TempOrderDetailSQL;
import com.alfredbase.store.sql.temporaryforapp.TempOrderSQL;
import com.alfredbase.utils.CommonUtil;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.IntegerUtils;
import com.alfredbase.utils.JSONUtil;
import com.alfredbase.utils.ObjectFactory;
import com.alfredbase.utils.OrderHelper;
import com.alfredbase.utils.ScreenSizeUtil;
import com.alfredposclient.R;
import com.alfredposclient.activity.MainPage;
import com.alfredposclient.activity.NetWorkOrderActivity;
import com.alfredposclient.global.App;
import com.alfredposclient.global.JavaConnectJS;
import com.alfredposclient.global.UIHelp;
import com.alfredposclient.javabean.TablesStatusInfo;
import com.alfredposclient.jobs.CloudSyncJobManager;
import com.alfredposclient.popupwindow.CloseOrderSplitWindow;
import com.alfredposclient.popupwindow.CloseOrderWindow;
import com.alfredposclient.popupwindow.DiscountWindow;
import com.alfredposclient.popupwindow.DiscountWindow.ResultCall;
import com.alfredposclient.popupwindow.ModifyQuantityWindow;
import com.alfredposclient.popupwindow.ModifyQuantityWindow.DismissCall;
import com.alfredposclient.popupwindow.OpenItemWindow;
import com.alfredposclient.popupwindow.OrderSplitPrintWindow;
import com.alfredposclient.popupwindow.SetPAXWindow;
import com.alfredposclient.popupwindow.SetWeightWindow;
import com.alfredposclient.popupwindow.SpecialInstractionsWindow;
import com.alfredposclient.utils.AlertToDeviceSetting;
import com.alfredposclient.view.SelectOrderSplitDialog;
import com.alfredposclient.view.SettingView;
import com.alfredposclient.view.viewkiosk.MainPageMenuViewKiosk;
import com.alfredposclient.view.viewkiosk.MainPageOperatePanelKiosk;
import com.alfredposclient.view.viewkiosk.MainPageOrderViewKiosk;
import com.alfredposclient.view.viewkiosk.MainPageSearchViewKiosk;
import com.alfredposclient.view.viewkiosk.TopMenuViewKiosk;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainPageKiosk extends BaseActivity {
	private String TAG = MainPageKiosk.class.getSimpleName();
	private static final int GET_TABLESTATUSINFO_DATA = 100;

	public static final int REFRESH_ORDER_STATUS = 101;
	public static final int REFRESH_TABLES_STATUS = 102;
	public static final int VIEW_EVENT_DISMISS_TABLES = 103;
	public static final int VIEW_EVENT_SET_TABLE_PACKS = 104;
	public static final int VIEW_EVENT_SHOW_CLOSE_ORDER_WINDOW = 105;
	public static final int VIEW_EVENT_SHOW_TABLES = 106;
	public static final int VIEW_EVENT_SHOW_OPEN_ITEM_WINDOW = 107;
	public static final int VIEW_EVENT_SHOW_DISCOUNT_WINDOW = 108;
	public static final int VIEW_EVENT_ADD_ORDER_DETAIL = 109;
	public static final int VIEW_EVENT_SHOW_SEARCH = 110;
	public static final int VIEW_EVENT_DISMISS_OPEN_ITEM_WINDOW = 111;
	public static final int VIEW_EVENT_OPEN_MODIFIERS = 112;
	public static final int VIEW_EVENT_SET_DATA = 113;
	public static final int VIEW_EVENT_SHOW_MODIFY_QUANTITY_WINDOW = 114;
	public static final int VIEW_EVENT_DISMISS_SEARCH = 115;
	public static final int DISMISS_SOFT_INPUT = 116;
	public static final int VIEW_EVENT_SEARCH = 117;
	public static final int VIEW_EVENT_OPERATEPANEL = 118;
	public static final int VIEW_EVENT_CLOSE_BILL = 119;
	public static final int KOT_DONE_SET_DATA = 120;
	public static final int VIEW_EVNT_GET_BILL_PRINT = 121;
	public static final int VIEW_EVENT_SELECT_BILL = 122;
	public static final int DIALOG_DISMISS = 1000;
	public static final int VIEW_EVENT_SHOW_BILL_ON_HOLD = 123;
	public static final int VIEW_EVENT_SHOW_VOID = 124;
	public static final int VIEW_EVENT_SHOW_ENTERTAINMENT = 125;
	public static final int VIEW_EVENT_TANSFER_TABLE = 126;
	public static final int VIEW_EVENT_DISMISS_TABLES_AFTER_TRANSFER = 127;
	public static final int VIEW_EVENT_PLACE_ORDER = 128;
	public static final int ACTION_SWITCH_USER = 129;
	// public static final int ACTION_SWITCH_USER_ERROR = 130;
	private static final int VIEW_EVENT_DISMISS_TABLES_AFTER_MERGER = 131;
	private static final int ACTION_TRANSFER_TABLE = 132;
	public static final int WAITER_SEND_KDS = 133;
	public static final int VIEW_EVENT_KICK_CASHDRAWER = 134;
	public static final int VIEW_EVENT_SHOW_VOIDORFREE_WINDOW = 135;
	public static final int VIEW_EVENT_TANSFER_PAX = 136;
	public static final int VIEW_EVENT_VOID_OR_FREE = 137;
	public static final int VIEW_EVENT_SHOW_SPECIAL_INSTRACTIONS_WINDOW = 138;
	public static final int VIEW_EVENT_CLOSE_MODIFIER_VIEW = 139;
	public static final int VIEW_SUB_MENU = 140;
	public static final int VIEW_SUB_MENU_ALL = 141;
	public static final int VIEW_EVENT_SHOW_CLOSE_SPLIT_BILL= 142;
	public static final int VIEW_EVENT_CLOSE_SPLIT_BILL = 143;
	public static final int VIEW_EVENT_SHOW_TABLES_AFTER_CLOSE_BILL = 144;
	public static final int KIOSK_VIEW_EVENT_DELETE_ORDER = 1045;
	
	// KOT PRINT
	public static final int KOT_PRINT_FAILED = 200;
	public static final int KOT_PRINT_SUCCEED = 201;
	
	public static final int VIEW_EVENT_TAKE_AWAY = 145;
	public static final int VIEW_EVENT_SET_WEIGHT = 147;
	public static final int CHECK_TO_CLOSE_CUSTOM_NOTE_VIEW = 148;
	public static final int CONTROL_PAGE_ORDER_VIEW_MASK = 149;
	
	
	
	public static final String REFRESH_TABLES_BROADCAST = "REFRESH_TABLES_BROADCAST";
	public static final String REFRESH_COMMIT_ORDER = "REFRESH_COMMIT_ORDER";
	private static final String SHOW_TABLES = "SHOW_TABLES";
	private static final String TRANSFER_TABLE = "TRANSFER_TABLE";
	private static final String HANDLER_MSG_OBJECT_DISCOUNT = "DISCOUNT";
	public static final String HANDLER_MSG_OBJECT_BILL_ON_HOLD = "BILL_ON_HOLD";
	public static final String HANDLER_MSG_OBJECT_VOID = "VOID";
	public static final String HANDLER_MSG_OBJECT_ENTERTAINMENT = "ENTERTAINMENT";
	private static final String HANDLER_MSG_OBJECT_TRANSFER_TABLE = "TRANSFERTABLE";
	private static final String HANDLER_MSG_OBJECT_ITEM_QTY = "ITEM_QTY";
	private static final String HANDLER_MSG_OBJECT_VOID_OR_FREE = "VOID_OR_FREE";
	private TopMenuViewKiosk topMenuView;
	private MainPageSearchViewKiosk mainPageSearchView;
	public MainPageOrderViewKiosk orderView;
	private MainPageOperatePanelKiosk operatePanel;
	private MainPageMenuViewKiosk mainPageMenuView;
	private CloseOrderWindow closeOrderWindow; // settlement window
	private CloseOrderSplitWindow closeOrderSplitWindow;
	private SetPAXWindow setPAXWindow;
	private DiscountWindow discountWindow;
	private ModifyQuantityWindow modifyQuantityWindow;
	private OpenItemWindow openItemWindow;
	private SpecialInstractionsWindow specialInstractionsWindow;
	private SetWeightWindow setWeightWindow;
	private JavaConnectJS javaConnectJS;
	private Gson gson = new Gson();
	private boolean isShowTables = false;
	private boolean isTableFirstShow = true;
	private String tableShowAction = SHOW_TABLES;
	private List<Places> places = new ArrayList<Places>();
	private List<Tables> tables = new ArrayList<Tables>();
	private List<TablesStatusInfo> tableStatusInfo = new ArrayList<TablesStatusInfo>();
	private Tables currentTable;
	private Tables oldTable;
	private Order currentOrder;
	private Order oldOrder;
	private List<OrderDetail> orderDetails;
	private VerifyDialog verifyDialog;
	public LoadingDialog loadingDialog;

	public PrinterLoadingDialog printerLoadingDialog;
	
	public SelectOrderSplitDialog selectOrderSplitDialog;

	private OrderSplitPrintWindow orderSplitPrintWindow;
	
	private DrawerLayout mDrawerLayout; // activity滑动布局
	private SettingView settingView; // 右滑视图
	
	private int activityRequestCode = 0;
	
	private int appOrderId;
	
	private void initDrawerLayout() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setScrimColor(Color.TRANSPARENT);//关闭阴影
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);	//关闭手势滑动
		settingView = (SettingView) findViewById(R.id.settingView);
		settingView.setParams(this, mDrawerLayout);
	}
	
	@Override
	protected void initView() {
		setContentView(R.layout.activity_kiosk_main_page);
		super.initView();
		initDrawerLayout();
		ScreenSizeUtil.initScreenScale(context);
		verifyDialog = new VerifyDialog(context, handler);
		loadingDialog = new LoadingDialog(context);
		loadingDialog.setTitle("Loading");
		printerLoadingDialog = new PrinterLoadingDialog(context);
		printerLoadingDialog.setTitle("Sending to Kitchen");
		selectOrderSplitDialog = new SelectOrderSplitDialog(context, handler);
		
		topMenuView = (TopMenuViewKiosk) findViewById(R.id.topMenuView);
		topMenuView.setParams(this, handler,mDrawerLayout,settingView);
		topMenuView.setGetBillNum(App.instance.getGetTingBillNotifications()
				.size());
		mainPageSearchView = (MainPageSearchViewKiosk) findViewById(R.id.search);
		orderView = (MainPageOrderViewKiosk) findViewById(R.id.orderView);
		LinearLayout.LayoutParams ps1 = new LinearLayout.LayoutParams(
				(int) (750 * ScreenSizeUtil.width / ScreenSizeUtil.WIDTH_POS),
				LayoutParams.MATCH_PARENT);
		orderView.setLayoutParams(ps1);
		mainPageMenuView = (MainPageMenuViewKiosk) findViewById(R.id.mainPageMenuView);
		mainPageMenuView.setParent(context);
		operatePanel = (MainPageOperatePanelKiosk) findViewById(R.id.operatePanel);
		LinearLayout.LayoutParams ps = new LinearLayout.LayoutParams(
				(int) (250 * ScreenSizeUtil.width / ScreenSizeUtil.WIDTH_POS),
				LayoutParams.MATCH_PARENT);
		operatePanel.setLayoutParams(ps);
		closeOrderWindow = new CloseOrderWindow(this,
				findViewById(R.id.rl_root), handler);
		closeOrderSplitWindow = new CloseOrderSplitWindow(this,
				findViewById(R.id.rl_root), handler);
		setPAXWindow = new SetPAXWindow(context, findViewById(R.id.rl_root),
				handler);
		discountWindow = new DiscountWindow(this, findViewById(R.id.rl_root));
		openItemWindow = new OpenItemWindow();
		modifyQuantityWindow = new ModifyQuantityWindow(this,
				findViewById(R.id.rl_root));
		specialInstractionsWindow = new SpecialInstractionsWindow(context,
				findViewById(R.id.lv_order), handler);
		
		orderSplitPrintWindow = new OrderSplitPrintWindow(context,
				findViewById(R.id.lv_order), handler);
		setWeightWindow = new SetWeightWindow(context, findViewById(R.id.rl_root),
				handler);
		currentTable = TablesSQL.getKioskTable();
		setData();
	}

	private void setTablePacks(String tablePacks) {
		if (currentTable != null) {
			currentTable.setTablePacks(Integer.parseInt(tablePacks));
			currentTable.setTableStatus(ParamConst.TABLE_STATUS_DINING);
			TablesSQL.updateTables(currentTable);
		}
	}

	private void getPlaces() {
		places = PlacesSQL.getAllPlaces();
		getTables();
	}

	private void getTables() {
		tables = TablesSQL.getAllTables();
		handler.sendEmptyMessage(GET_TABLESTATUSINFO_DATA);
	}

	private void openModifiers(OrderDetail orderDetail,
			List<ItemModifier> itemModifiers) {
		mainPageMenuView
				.openModifiers(currentOrder, orderDetail, itemModifiers);
	}

	private void initOrder(Tables tables) {
		currentOrder = ObjectFactory.getInstance().getOrder(
				ParamConst.ORDER_ORIGIN_POS, tables,
				App.instance.getRevenueCenter(), App.instance.getUser(),
				App.instance.getSessionStatus(),
				App.instance.getBusinessDate(),
				App.instance.getIndexOfRevenueCenter(),
				ParamConst.ORDER_STATUS_OPEN_IN_POS,
				App.instance.getLocalRestaurantConfig()
				.getIncludedTax().getTax());
	}

	private void getTableStatusInfo() {
		for (int i = 0; i < places.size(); i++) {
			int id = places.get(i).getId();
			int idleNum = 0;
			int diningNum = 0;
			int inCheckoutNum = 0;
			for (int j = 0; j < tables.size(); j++) {
				if (tables.get(j).getPlacesId() != id) {
					continue;
				}
				int table_status = tables.get(j).getTableStatus();
				switch (table_status) {
				case ParamConst.TABLE_STATUS_IDLE:
					idleNum++;
					break;
				case ParamConst.TABLE_STATUS_DINING:
					diningNum++;
					break;
				case ParamConst.TABLE_STATUS_INCHECKOUT:
					inCheckoutNum++;
					break;
				default:
					break;
				}
			}
			tableStatusInfo.add(new TablesStatusInfo(id, idleNum, diningNum,
					inCheckoutNum));
		}
	}

	private void transferOrder(int tableId) {
		currentOrder.setTableId(tableId);
		OrderSQL.update(currentOrder);
//		handler.sendMessage(handler.obtainMessage(VIEW_EVENT_SET_TABLE_PACKS,
//				currentOrder.getPersons() + ""));
		setTablePacks(String.valueOf(currentOrder.getPersons()));
	}

	private void mergerOrder() {
		if (oldOrder == null) {
			return;
		}
		Order newOrder = OrderSQL.getUnfinishedOrderAtTable(currentTable, oldOrder.getBusinessDate());
		List<OrderDetail> orderDetails = OrderDetailSQL
				.getUnFreeOrderDetails(oldOrder);
		if (!orderDetails.isEmpty()) {
			for (OrderDetail orderDetail : orderDetails) {
				OrderDetail newOrderDetail = ObjectFactory.getInstance()
						.getOrderDetailForTransferTable(newOrder, orderDetail);
				OrderDetailSQL.addOrderDetailETC(newOrderDetail);
				List<OrderModifier> orderModifiers = OrderModifierSQL
						.getOrderModifiers(orderDetail);
				if (orderModifiers.isEmpty()) {
					continue;
				}
				for (OrderModifier orderModifier : orderModifiers) {
					OrderModifier newOrderModifier = ObjectFactory
							.getInstance().getOrderModifier(
									newOrder,
									newOrderDetail,
									CoreData.getInstance().getModifier(
											orderModifier.getModifierId()),
									orderModifier.getPrinterId().intValue());
					OrderModifierSQL.addOrderModifier(newOrderModifier);
				}
			}
		}
		OrderDetailSQL.deleteOrderDetailByOrder(oldOrder);
		OrderModifierSQL.deleteOrderModifierByOrder(oldOrder);
		OrderSQL.deleteOrder(oldOrder);
		initOrder(currentTable);

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
//			case REFRESH_TABLES_STATUS:
//				Tables table = (Tables) msg.obj;
//				web_tables
//						.loadUrl("javascript:JsConnectAndroid('RefreshTableStatus','"
//								+ getRefreshTableStatusStr(table) + "')");
//				System.out
//						.println("javascript:JsConnectAndroid('RefreshTableStatus','"
//								+ getRefreshTableStatusStr(table) + "')");
//				break;
			case GET_TABLESTATUSINFO_DATA:
				getTableStatusInfo();
				handler.sendEmptyMessage(JavaConnectJS.ACTION_CLICK_REFRESH);
				break;
			case VIEW_EVENT_DISMISS_TABLES:
//				dismissTables();
				if(loadingDialog != null && loadingDialog.isShowing())
					loadingDialog.dismiss();
				setData();
				break;
			case VIEW_EVENT_DISMISS_TABLES_AFTER_TRANSFER:
//				dismissTables();
				setData();
				break;
			case VIEW_EVENT_DISMISS_TABLES_AFTER_MERGER:
//				dismissTables();
				mergerOrderSetData();
				// App.instance.getKdsJobManager().transferTableDownKot(ParamConst.JOB_MERGER_KOT,
				// toKotSummary, fromKotSummary);
				break;
			case VIEW_EVENT_SET_TABLE_PACKS:
				setTablePacks((String) msg.obj);
				handler.sendEmptyMessage(VIEW_EVENT_DISMISS_TABLES);
				break;
			// Open settlement window
			case VIEW_EVENT_SHOW_CLOSE_ORDER_WINDOW:
				if(IntegerUtils.isEmptyOrZero(currentOrder.getAppOrderId())){
					currentOrder.setOrderStatus(ParamConst.ORDER_STATUS_UNPAY);
					OrderSQL.update(currentOrder);
					showCloseBillWindow();
				}else{
					TempOrder tempOrder = TempOrderSQL.getTempOrderByAppOrderId(currentOrder.getAppOrderId());
					if(tempOrder != null && tempOrder.getPaied() == ParamConst.TEMPORDER_PAIED){
						TempOrderISPaied();
					}else{
						currentOrder.setOrderStatus(ParamConst.ORDER_STATUS_UNPAY);
						OrderSQL.update(currentOrder);
						showCloseBillWindow();
					}
				}
				break;
			case VIEW_EVENT_SHOW_TABLES:{
				tableShowAction = SHOW_TABLES;
				if(currentOrder != null){
					if(orderDetails != null && orderDetails.size() <=0 ){
								KotSummarySQL.deleteKotSummaryByOrder(currentOrder);
					}
				}
//				showTables();
			}
				break;
			case VIEW_EVENT_SHOW_TABLES_AFTER_CLOSE_BILL:{
				selectOrderSplitDialog.dismiss();
				tableShowAction = SHOW_TABLES;
				currentOrder = null;
//				showTables();
				setData();
			}
				break;
			case VIEW_EVENT_SHOW_OPEN_ITEM_WINDOW:
				showOpenItemWindow();
				break;
			case VIEW_EVENT_DISMISS_OPEN_ITEM_WINDOW:
				dismissOpenItemWindow();
				break;
			case VIEW_EVENT_SHOW_SEARCH:
				showSearchView();
				break;
			case VIEW_EVENT_SEARCH:
				search((String) msg.obj);
				break;
			case VIEW_EVENT_DISMISS_SEARCH:
				dismissSearchView();
				mainPageSearchView.cancelSearch();
//				dismissSoftInput();
				break;
			case VIEW_EVENT_OPERATEPANEL: {
				mainPageMenuView.closeModifiers();
				
				PrinterDevice printer = App.instance.getCahierPrinter();
				List<Map<String, String>> taxMap = OrderDetailTaxSQL
						.getTaxPriceSUM(App.instance.getLocalRestaurantConfig().getIncludedTax().getTax(), currentOrder);

				ArrayList<PrintOrderItem> orderItems = ObjectFactory
						.getInstance().getItemList(
								OrderDetailSQL.getOrderDetails(currentOrder
										.getId()));
				OrderBill orderBill = OrderBillSQL
						.getOrderBillByOrder(currentOrder);
				if (OrderDetailSQL
						.getOrderDetailsCountUnPlaceOrder(currentOrder.getId()) > 0) {
					UIHelp.showToast(context,context.getResources().getString(R.string.place_before_print));
				} else if (printer == null) {
					UIHelp.showToast(context,context.getResources().getString(R.string.setting_printer));
				} else if (orderItems.size() > 0 && printer != null
						&& orderBill != null && orderBill.getBillNo() != null) {
					int orderSplitCount = OrderSplitSQL.getOrderSplitsCountByOrder(currentOrder);
					int orderDetailSplitCount =  OrderDetailSQL.getOrderDetailSplitCountByOrder(currentOrder);
					if(orderSplitCount != 0 && orderDetailSplitCount != 0){
						orderSplitPrintWindow.show(MainPageKiosk.this, currentOrder, handler);
					}else{
					PrinterLoadingDialog printerLoadingDialog = new PrinterLoadingDialog(
							context);
					printerLoadingDialog.setTitle(context.getResources().getString(R.string.bill_printing));
					printerLoadingDialog.showByTime(3000);
					PrinterTitle title = ObjectFactory.getInstance()
							.getPrinterTitle(
									App.instance.getRevenueCenter(),
									currentOrder,
									App.instance.getUser().getFirstName()
											+ App.instance.getUser()
													.getLastName(),
									currentTable.getTableName());
					currentOrder.setOrderStatus(ParamConst.ORDER_STATUS_UNPAY);
					OrderSQL.update(currentOrder);
					ArrayList<PrintOrderModifier> orderModifiers = ObjectFactory
							.getInstance().getItemModifierList(currentOrder,OrderDetailSQL.getOrderDetails(currentOrder
									.getId()));
					RoundAmount roundAmount = RoundAmountSQL.getRoundAmount(currentOrder);
					App.instance.remoteBillPrint(printer, title, currentOrder,
							orderItems, orderModifiers, taxMap, null, roundAmount);
					}
				} else {
					UIHelp.showToast(context, context.getResources().getString(R.string.no_items));
				}
			}
				break;
			case VIEW_EVNT_GET_BILL_PRINT: {
					Tables tables = (Tables) msg.obj;
					if (tables.getTableStatus() != ParamConst.TABLE_STATUS_DINING)
						return;
					if (verifyTableRepeat(tables)) {
						App.instance.addGettingBillNotification(tables);
					}
					topMenuView.setGetBillNum(App.instance
							.getGetTingBillNotifications().size());
				}
				break;
			case VIEW_EVENT_CLOSE_BILL: {
				HashMap<String, String> paymentMap = (HashMap<String, String>) msg.obj;
				final Order paidOrder = OrderSQL.getOrder(Integer.valueOf(paymentMap.get("orderId")));
				List<PaymentSettlement> paymentSettlements = PaymentSettlementSQL
						.getAllPaymentSettlementByPaymentId(Integer.valueOf(paymentMap.get("paymentId")));
//				KotSummary kotSummary = KotSummarySQL.getKotSummary(currentOrder.getId());
//				if(kotSummary != null){
//					kotSummary.setStatus(ParamConst.KOTS_STATUS_DONE);
//					KotSummarySQL.update(kotSummary);
//				}
				PrinterLoadingDialog printerLoadingDialog = new PrinterLoadingDialog(
						context);
				printerLoadingDialog.setTitle(context.getResources().getString(R.string.receipt_printing));
				printerLoadingDialog.showByTime(3000);
				PrinterDevice printer = App.instance.getCahierPrinter();
				PrinterTitle title = ObjectFactory.getInstance()
						.getPrinterTitle(
								App.instance.getRevenueCenter(),
								paidOrder,
								App.instance.getUser().getFirstName()
										+ App.instance.getUser().getLastName(),
								currentTable.getTableName());



				ArrayList<PrintOrderItem> orderItems = ObjectFactory
						.getInstance().getItemList(
								OrderDetailSQL.getOrderDetails(paidOrder
										.getId()));
				List<Map<String, String>> taxMap = OrderDetailTaxSQL
						.getTaxPriceSUM(App.instance.getLocalRestaurantConfig().getIncludedTax().getTax(), paidOrder);

				ArrayList<PrintOrderModifier> orderModifiers = ObjectFactory
						.getInstance().getItemModifierList(paidOrder, OrderDetailSQL.getOrderDetails(paidOrder
								.getId()));

				// ArrayList<OrderModifier> orderModifiers =
				// OrderModifierSQL.getAllOrderModifierByOrderAndNormal(currentOrder);

				if (orderItems.size() > 0 && printer != null) {
					RoundAmount roundAmount = RoundAmountSQL.getRoundAmount(paidOrder);
					App.instance.remoteBillPrint(printer, title, paidOrder,
							orderItems, orderModifiers, taxMap, paymentSettlements, roundAmount);
				}
				
				//Sent to Kitchen after close bill in kiosk mode
				String kotCommitStatus = ParamConst.JOB_NEW_KOT;
				List<OrderDetail> placedOrderDetails  = OrderDetailSQL.getOrderDetails(paidOrder.getId());
				List<Integer> orderDetailIds = new ArrayList<Integer>();
				ArrayList<OrderModifier> kotorderModifiers = new ArrayList<OrderModifier>();
				ArrayList<KotItemModifier> kotItemModifiers = new ArrayList<KotItemModifier>(); 
				for (OrderDetail orderDetail : placedOrderDetails) {
					orderDetailIds.add(orderDetail.getId());
				}

				KotSummary kotSummary = KotSummarySQL.getKotSummary(paidOrder.getId());
				if (kotSummary != null) {
					ArrayList<KotItemDetail> kotItemDetails = 
							KotItemDetailSQL.getKotItemDetailBySummaryIdandOrderId(kotSummary.getId(), paidOrder.getId());
					
					kotorderModifiers = OrderModifierSQL.getAllOrderModifierByOrderAndNormal(paidOrder);
					for (KotItemDetail kot : kotItemDetails) {
						ArrayList<KotItemModifier> kotItemModifierObj = KotItemModifierSQL
								.getKotItemModifiersByKotItemDetail(kot.getId());
						if (kotItemModifierObj != null)
							kotItemModifiers.addAll(kotItemModifierObj);
					}

					Map<String, Object> orderMap = new HashMap<String, Object>();
					orderMap.put("orderId", paidOrder.getId());
					orderMap.put("orderDetailIds", orderDetailIds);
					App.instance.getKdsJobManager().tearDownKot(
							kotSummary, kotItemDetails,
							kotItemModifiers, kotCommitStatus,
							orderMap);					
				}
				//end KOT print
				


				               
				// remove get bill notification
				removeNotificationTables();
				topMenuView.setGetBillNum(App.instance
						.getGetTingBillNotifications().size());
				/**
				 * 给后台发送log 信息
				 */
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						CloudSyncJobManager cloudSync = App.instance.getSyncJob();
						if (cloudSync!=null) {
							
							cloudSync.syncOrderInfoForLog(paidOrder.getId(), 
										App.instance.getRevenueCenter().getId(), 
										App.instance.getBusinessDate(), 1);
						}
					}
				}).start();
				break;
			}
			case VIEW_EVENT_CLOSE_SPLIT_BILL:{

				HashMap<String, String> paymentMap = (HashMap<String, String>) msg.obj;
				OrderSplit paidOrderSplit = OrderSplitSQL.get(Integer.valueOf(paymentMap.get("orderSplitId")));
				List<PaymentSettlement> paymentSettlements = PaymentSettlementSQL
						.getAllPaymentSettlementByPaymentId(Integer.valueOf(paymentMap.get("paymentId")));
//				KotSummary kotSummary = KotSummarySQL.getKotSummary(currentOrder.getId());
//				if(kotSummary != null){
//					kotSummary.setStatus(ParamConst.KOTS_STATUS_DONE);
//					KotSummarySQL.update(kotSummary);
//				}
				OrderBill orderBill = ObjectFactory.getInstance().getOrderBillByOrderSplit(paidOrderSplit, App.instance.getRevenueCenter());
				PrinterLoadingDialog printerLoadingDialog = new PrinterLoadingDialog(
						context);
				printerLoadingDialog.setTitle(context.getResources().getString(R.string.receipt_printing));
				printerLoadingDialog.showByTime(3000);
				PrinterDevice printer = App.instance.getCahierPrinter();
				PrinterTitle title = ObjectFactory.getInstance()
						.getPrinterTitleByOrderSplit(
								App.instance.getRevenueCenter(),
								currentOrder,
								paidOrderSplit,
								App.instance.getUser().getFirstName()
										+ App.instance.getUser().getLastName(),
								currentTable.getTableName(), orderBill, App.instance.getBusinessDate().toString());
				ArrayList<OrderDetail> orderSplitDetails = (ArrayList<OrderDetail>) OrderDetailSQL.getOrderDetailsByOrderAndOrderSplit(paidOrderSplit);
				

				ArrayList<PrintOrderItem> orderItems = ObjectFactory
						.getInstance().getItemList(orderSplitDetails);
				List<Map<String, String>> taxMap = OrderDetailTaxSQL
						.getOrderSplitTaxPriceSUM(App.instance.getLocalRestaurantConfig().getIncludedTax().getTax(), paidOrderSplit);

				ArrayList<PrintOrderModifier> orderModifiers = ObjectFactory
						.getInstance().getItemModifierList(currentOrder, orderSplitDetails);

				// ArrayList<OrderModifier> orderModifiers =
				// OrderModifierSQL.getAllOrderModifierByOrderAndNormal(currentOrder);
				Order temporaryOrder = new Order();
				temporaryOrder.setPersons(paidOrderSplit.getPersons());
				temporaryOrder.setSubTotal(paidOrderSplit.getSubTotal());
				temporaryOrder.setDiscountAmount(paidOrderSplit.getDiscountAmount());
				temporaryOrder.setTotal(paidOrderSplit.getTotal());
				temporaryOrder.setTaxAmount(paidOrderSplit.getTaxAmount());
				if (orderItems.size() > 0 && printer != null) {
					RoundAmount roundAmount = RoundAmountSQL.getRoundAmount(temporaryOrder);
					App.instance.remoteBillPrint(printer, title, temporaryOrder,
							orderItems, orderModifiers, taxMap, paymentSettlements, roundAmount);
				}
				//Sent to Kitchen after close bill in kiosk mode
				String kotCommitStatus = ParamConst.JOB_NEW_KOT;
				List<OrderDetail> placedOrderDetails  = OrderDetailSQL.getOrderDetailsByOrderAndOrderSplit(paidOrderSplit);
				List<Integer> orderDetailIds = new ArrayList<Integer>();
				ArrayList<OrderModifier> kotorderModifiers = new ArrayList<OrderModifier>();
				ArrayList<KotItemModifier> kotItemModifiers = new ArrayList<KotItemModifier>();

				KotSummary kotSummary = KotSummarySQL.getKotSummary(paidOrderSplit.getOrderId());
				if (kotSummary != null) {
					ArrayList<KotItemDetail> kotItemDetails = new ArrayList<KotItemDetail>();
					for (OrderDetail orderDetail : placedOrderDetails) {
						orderDetailIds.add(orderDetail.getId());
						KotItemDetail kotItemDetail = KotItemDetailSQL.getMainKotItemDetailByOrderDetailId(orderDetail.getId());
						kotItemDetails.add(kotItemDetail);
					}



//					kotorderModifiers = OrderModifierSQL.getAllOrderModifierByOrderAndNormal(paidOrder);
					for (KotItemDetail kot : kotItemDetails) {
						ArrayList<KotItemModifier> kotItemModifierObj = KotItemModifierSQL
								.getKotItemModifiersByKotItemDetail(kot.getId());
						if (kotItemModifierObj != null)
							kotItemModifiers.addAll(kotItemModifierObj);
					}

					Map<String, Object> orderMap = new HashMap<String, Object>();
					orderMap.put("orderId", paidOrderSplit.getOrderId());
					orderMap.put("orderDetailIds", orderDetailIds);
					App.instance.getKdsJobManager().tearDownKot(
							kotSummary, kotItemDetails,
							kotItemModifiers, kotCommitStatus,
							orderMap);
				}
				// remove get bill notification
				removeNotificationTables();
				topMenuView.setGetBillNum(App.instance
						.getGetTingBillNotifications().size());
				/**
				 * 给后台发送log 信息
				 */
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						CloudSyncJobManager cloudSync = App.instance.getSyncJob();
						if (cloudSync!=null) {
							
							cloudSync.syncOrderInfoForLog(currentOrder.getId(), 
										App.instance.getRevenueCenter().getId(), 
										App.instance.getBusinessDate(), 1);
						}
					}
				}).start();
				
			}
				break;
			case VIEW_EVENT_SHOW_DISCOUNT_WINDOW: {
				verifyDialog.show(HANDLER_MSG_OBJECT_DISCOUNT,
						(Map<String, Object>) msg.obj);
				break;
			}
			case VIEW_EVENT_SHOW_MODIFY_QUANTITY_WINDOW: {
				Map<String, Object> qtyMap = (Map<String, Object>) msg.obj;
				showModifyQuantityWindow(
						Integer.parseInt(qtyMap.get("quantity").toString()),
						(DismissCall) qtyMap.get("dismissCall"));
				break;
			}
			case VIEW_EVENT_ADD_ORDER_DETAIL:
				addOrderDetail((OrderDetail) msg.obj);
				break;
			case VIEW_EVENT_SET_DATA:
				setData();
				break;
			case DISMISS_SOFT_INPUT:
				dismissSoftInput();
				break;
			case VIEW_EVENT_OPEN_MODIFIERS: {
				Map<String, Object> map = (Map<String, Object>) msg.obj;
				openModifiers((OrderDetail) map.get("orderDetail"),
						(List<ItemModifier>) map.get("itemModifiers"));
				break;
			}

			case JavaConnectJS.ACTION_CLICK_TABLE: {
				Gson gson = new Gson();
				String json = (String) msg.obj;
				JSONObject jsonject;
				try {
					jsonject = new JSONObject(json);
					currentTable = gson.fromJson(jsonject.getString("table"),
							new TypeToken<Tables>() {
							}.getType());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (currentTable != null) {
					if (currentTable.getTableStatus() == ParamConst.TABLE_STATUS_IDLE) {
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								KotSummary kotSummary = KotSummarySQL.getKotSummaryByTable(currentTable);
								if(kotSummary != null){
									List<KotItemDetail> kotItemDetails = KotItemDetailSQL.getKotItemDetailByKotSummaryIdUndone(kotSummary);
									for(KotItemDetail kotItemDetail : kotItemDetails){
										kotItemDetail.setKotStatus(ParamConst.KOT_STATUS_DONE);
										KotItemDetailSQL.update(kotItemDetail);
									}
									kotSummary.setStatus(ParamConst.KOTS_STATUS_DONE);
									KotSummarySQL.update(kotSummary);
								}
							}
						}).start();
						setPAXWindow.show(SetPAXWindow.GENERAL_ORDER);
					} else {
						handler.sendMessage(handler
								.obtainMessage(MainPageKiosk.VIEW_EVENT_DISMISS_TABLES));
					}
				}
			}
				break;
			case JavaConnectJS.ACTION_CLICK_TABLE_TRANSFER: {
				Gson gson = new Gson();
				String json = (String) msg.obj;
				JSONObject jsonject;
				try {
					jsonject = new JSONObject(json);
					final Tables newTable = gson.fromJson(
							jsonject.getString("table"),
							new TypeToken<Tables>() {
							}.getType());
					if (newTable.getId().intValue() == oldTable.getId()
							.intValue()) {
						currentTable = newTable;
						if (currentTable.getTableStatus() == ParamConst.TABLE_STATUS_IDLE) {
							setPAXWindow.show(SetPAXWindow.GENERAL_ORDER);
						} else {
							handler.sendMessage(handler
									.obtainMessage(MainPageKiosk.VIEW_EVENT_DISMISS_TABLES));
						}
					} else {
						DialogFactory.commonTwoBtnDialog(
								context,
								context.getResources().getString(R.string.table_transfer),
								context.getResources().getString(R.string.transfer_) + 
									oldTable.getTableName() + 
								context.getResources().getString(R.string.to) + 
									newTable.getTableName() + "?",  
								context.getResources().getString(R.string.no),
								context.getResources().getString(R.string.yes), null, new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										loadingDialog.show();
										handler.sendMessage(handler
												.obtainMessage(
														ACTION_TRANSFER_TABLE,
														newTable));
									}
								});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
				break;
			case ACTION_TRANSFER_TABLE: {
				currentTable = (Tables) msg.obj;
				if (currentTable != null) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							if (currentTable.getTableStatus() == ParamConst.TABLE_STATUS_IDLE) {
								OrderBill orderBill = OrderBillSQL
										.getOrderBillByOrder(currentOrder);
								if (orderBill != null
										&& orderBill.getBillNo() != null) {
									currentOrder.setTableId(currentTable
											.getId());
									OrderSQL.update(currentOrder);
									KotSummary fromKotSummary = KotSummarySQL
											.getKotSummary(currentOrder.getId());
									fromKotSummary.setTableName(currentTable
											.getTableName());
									Map<String, Object> parameters = new HashMap<String, Object>();
									parameters.put("fromOrder", currentOrder);
									parameters.put("fromTableName",
											oldTable.getTableName());
									parameters.put("toTableName",
											currentTable.getTableName());
									parameters.put("action",
											ParamConst.JOB_TRANSFER_KOT);
									App.instance
											.getKdsJobManager()
											.transferTableDownKot(
													ParamConst.JOB_TRANSFER_KOT,
													null, fromKotSummary,
													parameters);
								} else {
									transferOrder(currentTable.getId());
								}
								handler.sendMessage(handler
										.obtainMessage(VIEW_EVENT_DISMISS_TABLES_AFTER_TRANSFER));
							} else {
								oldOrder = currentOrder;
								OrderBill orderBill = OrderBillSQL
										.getOrderBillByOrder(oldOrder);
								if (orderBill != null
										&& orderBill.getBillNo() != null) {
									initOrder(currentTable);
									KotSummary toKotSummary = KotSummarySQL
											.getKotSummary(currentOrder.getId());
									if (toKotSummary == null) {
										toKotSummary = ObjectFactory
												.getInstance()
												.getKotSummary(
														currentTable,
														currentOrder,
														App.instance
																.getRevenueCenter(),
														App.instance
																.getBusinessDate());
										KotSummarySQL.update(toKotSummary);
									}
									KotSummary fromKotSummary = KotSummarySQL
											.getKotSummary(oldOrder.getId());
									Map<String, Object> parameters = new HashMap<String, Object>();
									parameters.put("action",
											ParamConst.JOB_MERGER_KOT);
									parameters.put("fromOrder", oldOrder);
									parameters.put("fromTableName",
											oldTable.getTableName());
									parameters.put("toTableName",
											currentTable.getTableName());
									parameters.put("currentTableId",
											currentTable.getId());
									App.instance.getKdsJobManager()
											.transferTableDownKot(
													ParamConst.JOB_MERGER_KOT,
													toKotSummary,
													fromKotSummary, parameters);
								} else {
									mergerOrder();
									handler.sendMessage(handler
											.obtainMessage(VIEW_EVENT_DISMISS_TABLES_AFTER_MERGER));
								}

							}
							if (oldTable != null) {
								oldTable.setTableStatus(ParamConst.TABLE_STATUS_IDLE);
								TablesSQL.updateTables(oldTable);
							}
						}
					}).start();
				}
			}
				break;
			case JavaConnectJS.ACTION_LOAD_TABLES:
				getPlaces();
				break;
			case JavaConnectJS.ACTION_CLICK_BACK:
				onBackPressed();
				break;
//			case JavaConnectJS.ACTION_CLICK_REFRESH:
//				web_tables
//						.loadUrl("javascript:JsConnectAndroid('RefreshTables','"
//								+ getJsonStr() + "')");
//				LogUtil.i(TAG, "javascript:JsConnectAndroid('RefreshTables','"
//						+ getJsonStr() + "')");
//				break;
			case VIEW_EVENT_PLACE_ORDER:
				mainPageMenuView.closeModifiers();
				break;
			case VIEW_EVENT_CLOSE_MODIFIER_VIEW:
				mainPageMenuView.closeModifiers();
				break;
			case VIEW_EVENT_SELECT_BILL:
				currentTable = (Tables) msg.obj;
				Tables tables = (Tables) msg.obj;
				if (tables.getTableStatus() == ParamConst.TABLE_STATUS_IDLE) {
					return;
				}				
				handler.sendMessage(handler
						.obtainMessage(MainPageKiosk.VIEW_EVENT_DISMISS_TABLES));
				App.instance.removeGettingBillNotification(currentTable);
				topMenuView.setGetBillNum(App.instance
						.getGetTingBillNotifications().size());
				break;
			case VerifyDialog.DIALOG_RESPONSE:{
				Map<String, Object> result = (Map<String, Object>) msg.obj;
				User user = (User) result.get("User");
				if (result.get("MsgObject").equals(HANDLER_MSG_OBJECT_DISCOUNT)) {
					Map<String, Object> map = (Map<String, Object>) result
							.get("Object");
					ObjectFactory.getInstance().getReportDiscount(currentTable,
							currentOrder, user,
							App.instance.getRevenueCenter(),
							App.instance.getBusinessDate());
					showDiscountWindow((Order) map.get("order"),
							(OrderDetail) map.get("orderDetail"),
							(ResultCall) map.get("resultCall"));
				} else if (result.get("MsgObject").equals(
						HANDLER_MSG_OBJECT_BILL_ON_HOLD)) {
					if (!verifyDialog.isShowing()) {
						if(closeOrderWindow.isShowing()){
							closeOrderWindow.setUser(user);
							closeOrderWindow.openMoneyKeyboard(View.GONE,
									ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD);
						} else if(closeOrderSplitWindow.isShowing()) {
							closeOrderSplitWindow.setUser(user);
							closeOrderSplitWindow.openMoneyKeyboard(View.GONE,
									ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD);
						}
						
					}
				} else if (result.get("MsgObject").equals(
						HANDLER_MSG_OBJECT_VOID)) {
					if (!verifyDialog.isShowing()) {
						if(closeOrderWindow.isShowing()){
							closeOrderWindow.setUser(user);
							closeOrderWindow.openMoneyKeyboard(View.GONE,
									ParamConst.SETTLEMENT_TYPE_VOID);
						} else if (closeOrderSplitWindow.isShowing()) {
							closeOrderSplitWindow.setUser(user);
							closeOrderSplitWindow.openMoneyKeyboard(View.GONE,
									ParamConst.SETTLEMENT_TYPE_VOID);
						}
					}
				} else if (result.get("MsgObject").equals(
						HANDLER_MSG_OBJECT_ENTERTAINMENT)) {
					if (!verifyDialog.isShowing()) {
						if (closeOrderWindow.isShowing()) {
						closeOrderWindow.setUser(user);
						closeOrderWindow.openMoneyKeyboard(View.GONE,
								ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT);
						} else if (closeOrderSplitWindow.isShowing()) {
							closeOrderSplitWindow.setUser(user);
							closeOrderSplitWindow.openMoneyKeyboard(View.GONE,
									ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT);
						}
					}
				} else if (result.get("MsgObject").equals(
						HANDLER_MSG_OBJECT_TRANSFER_TABLE)) {
					if (!verifyDialog.isShowing()) {
						tableShowAction = TRANSFER_TABLE;
//						showTables();
					}
				} else if (result.get("MsgObject").equals(
						HANDLER_MSG_OBJECT_VOID_OR_FREE)) {
					Map<String, Object> voidOrFreeMap = (Map<String, Object>) result
							.get("Object");
					int type = (Integer) voidOrFreeMap.get("type");
					final OrderDetail orderDetail = (OrderDetail) voidOrFreeMap
							.get("orderDetail");
					if (type == ParamConst.ORDERDETAIL_TYPE_VOID) {
						// voidORFreeWindow.voidItem();
						new Thread(new Runnable() {

							@Override
							public void run() {
								OrderDetailSQL.setOrderDetailToVoidOrFree(
										orderDetail,
										ParamConst.ORDERDETAIL_TYPE_VOID);
								handler.sendEmptyMessage(MainPageKiosk.VIEW_EVENT_SET_DATA);
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
								// OrderDetailSQL.deleteOrderDetail(freeOrderDetail);
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
											if (kotItemModifier!=null)
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
							}
						}).start();
					} else if (type == ParamConst.ORDERDETAIL_TYPE_FREE) {
						// voidORFreeWindow.freeItem();
						new Thread(new Runnable() {

							@Override
							public void run() {
								OrderDetailSQL.setOrderDetailToVoidOrFree(
										orderDetail,
										ParamConst.ORDERDETAIL_TYPE_FREE);
								handler.sendEmptyMessage(MainPageKiosk.VIEW_EVENT_SET_DATA);
							}
						}).start();
					}
				}
			}
				break;
			case VIEW_EVENT_SHOW_BILL_ON_HOLD:
				verifyDialog.show(HANDLER_MSG_OBJECT_BILL_ON_HOLD, null);
				break;
			case VIEW_EVENT_SHOW_VOID:
				verifyDialog.show(HANDLER_MSG_OBJECT_VOID, null);
				break;
			case VIEW_EVENT_SHOW_ENTERTAINMENT:
				verifyDialog.show(HANDLER_MSG_OBJECT_ENTERTAINMENT, null);
				break;
			case VIEW_EVENT_TANSFER_TABLE:
				verifyDialog.show(HANDLER_MSG_OBJECT_TRANSFER_TABLE, null);
				break;
			case VIEW_EVENT_KICK_CASHDRAWER: {
				PrinterDevice printer = App.instance.getCahierPrinter();
				if (printer == null) {
					AlertToDeviceSetting.noKDSorPrinter(context,
							context.getResources().getString(R.string.no_set_kds_printer));
				} else {
					App.instance.kickOutCashDrawer(printer);
				}
				break;
			}
			case ACTION_SWITCH_USER:
				UIHelp.startLogin(context);
				MainPageKiosk.this.finish();
				break;
			case ResultCode.CONNECTION_FAILED:
				UIHelp.showToast(context, ResultCode.getErrorResultStr(context,
						(Throwable) msg.obj, context.getResources().getString(R.string.server)));
				break;
			// kot Print status
			case KOT_PRINT_FAILED:
				loadingDialog.dismiss();
				printerLoadingDialog.dismiss();
				UIHelp.showToast(context, context.getResources().getString(R.string.place_order_failed));
				break;
			case KOT_PRINT_SUCCEED:
				// if(!orderDetails.isEmpty()){
				// List<OrderDetail> orderDetailUnDone =
				// OrderDetailSQL.getPartOrderDetailByOrderIdAndOrderDetailStatus(currentOrder.getId());
				// if(!orderDetailUnDone.isEmpty()){
				// for(OrderDetail orderDetail : orderDetailUnDone){
				// orderDetail.setOrderDetailStatus(ParamConst.ORDERDETAIL_STATUS_KOTPRINTERD);
				// OrderDetailSQL.updateOrderDetail(orderDetail);
				// }
				// setData();
				// UIHelp.showToast(context,"Place Order Success");
				// }
				// }
				if (currentOrder != null
						&& ((Integer) msg.obj).intValue() == currentOrder
								.getId().intValue()) {
					setData();
				}
				break;
			case VIEW_EVENT_SHOW_SPECIAL_INSTRACTIONS_WINDOW: {
				OrderDetail orderDetail = (OrderDetail) msg.obj;
				specialInstractionsWindow.show(orderDetail);
			}
				break;
			case ParamConst.JOB_TYPE_POS_TRANSFER_TABLE:
				int id = (Integer) msg.obj;
				transferOrder(id);
				break;
			case VIEW_EVENT_TANSFER_PAX:
				String pax = (String) msg.obj;
				setPAXWindow.show(pax, currentOrder);
				break;
			case VIEW_EVENT_VOID_OR_FREE:
				verifyDialog.show(HANDLER_MSG_OBJECT_VOID_OR_FREE,
						(Map<String, Object>) msg.obj);
				break;
			case VIEW_SUB_MENU:
				Map<String,Object> result = (Map<String, Object>)msg.obj;
				int itemMainCategoryId = (Integer) result.get("itemMainCategoryId");
				int itemCategoryId = (Integer) result.get("itemCategoryId");
				mainPageMenuView.closeSubMenu(itemMainCategoryId,itemCategoryId);
				break;
			case VIEW_SUB_MENU_ALL:
				int mainCategoryId = (Integer) msg.obj;
				mainPageMenuView.closeSubMenu(mainCategoryId);
				break;
			case VIEW_EVENT_SHOW_CLOSE_SPLIT_BILL:
				closeOrderSplitWindow.show(currentOrder, (OrderSplit)msg.obj);
				break;
			case KIOSK_VIEW_EVENT_DELETE_ORDER:
				OrderDetailSQL.deleteOrderDetailByOrder(currentOrder);
				KotSummarySQL.deleteKotSummaryByOrder(currentOrder);
				OrderBillSQL.deleteOrderBillByOrder(currentOrder);
				OrderSQL.deleteOrder(currentOrder);
				setData();
				break;
			case VIEW_EVENT_TAKE_AWAY:{
				OrderDetail orderDetail = (OrderDetail) msg.obj;
				if (orderDetail.getIsTakeAway() != ParamConst.TAKE_AWAY) {
					orderDetail.setIsTakeAway(ParamConst.TAKE_AWAY);
					if(orderDetail != null && !TextUtils.isEmpty(orderDetail.getSpecialInstractions())){
						orderDetail.setSpecialInstractions(orderDetail.getSpecialInstractions() + " "+context.getResources().getString(R.string.take_away));
					} else {
						orderDetail.setSpecialInstractions(context.getResources().getString(R.string.take_away));
					}
				}else {
					orderDetail.setIsTakeAway(ParamConst.NOT_TAKE_AWAY);
					if(orderDetail != null && !TextUtils.isEmpty(orderDetail.getSpecialInstractions())){
						orderDetail.setSpecialInstractions(orderDetail.getSpecialInstractions().toString().replace(context.getResources().getString(R.string.take_away), ""));
					} 
				}
				OrderHelper.getOrderDetailTax(currentOrder, orderDetail);
				OrderDetailSQL.updateOrderDetail(orderDetail);
				if(orderDetail != null && orderDetail.getOrderDetailStatus() < ParamConst.ORDERDETAIL_STATUS_KOTPRINTERD){
					handler.sendEmptyMessage(MainPage.VIEW_EVENT_SET_DATA);
				} else {
					String kotCommitStatus = ParamConst.JOB_UPDATE_KOT;
					KotItemDetail kotItemDetail = KotItemDetailSQL
							.getMainKotItemDetailByOrderDetailId(orderDetail
									.getId());
					kotItemDetail.setIsTakeAway(ParamConst.TAKE_AWAY);
					kotItemDetail.setSpecialInstractions(orderDetail.getSpecialInstractions());
					KotSummary kotSummary = KotSummarySQL.getKotSummary(orderDetail
							.getOrderId()); 
					KotItemDetailSQL.update(kotItemDetail);
					ArrayList<KotItemDetail> kotItemDetails = new ArrayList<KotItemDetail>();
					kotItemDetails.add(kotItemDetail);
					Map<String, Object> orderMap = new HashMap<String, Object>();
					ArrayList<Integer> orderDetailIds = new ArrayList<Integer>();
					orderDetailIds.add(orderDetail.getId());
					orderMap.put("orderId", orderDetail.getOrderId());
					orderMap.put("orderDetailIds", orderDetailIds);
					App.instance.getKdsJobManager().tearDownKot(
							kotSummary, kotItemDetails,
							new ArrayList<KotItemModifier>(),
							kotCommitStatus, orderMap);
				}
			}
				break;	
			case VIEW_EVENT_SET_WEIGHT:{
				OrderDetail orderDetail = (OrderDetail) msg.obj;
				setWeightWindow.show(orderDetail);
			}
				break;
			case CONTROL_PAGE_ORDER_VIEW_MASK:
				orderView.showOrCloseMask((Boolean) msg.obj);
				break;
			case CHECK_TO_CLOSE_CUSTOM_NOTE_VIEW:
				mainPageMenuView.checkToCloseCustomNoteView();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onBackPressed() {
//		if(closeOrderWindow != null && closeOrderWindow.isShowing()){
//			closeOrderWindow.onBack();
//			return;
//		}
//		if(closeOrderSplitWindow != null && closeOrderSplitWindow.isShowing()){
//			closeOrderSplitWindow.onBack();
//			return;
//		}
		if(selectOrderSplitDialog != null && selectOrderSplitDialog.isShowing()){
			selectOrderSplitDialog.dismiss();
		}
//		if (isShowTables
//				&& !isTableFirstShow
//				&& currentOrder != null
//				&& OrderSQL.getOrder(currentOrder.getId()).getOrderStatus() != ParamConst.ORDER_STATUS_FINISHED) {
//			closeTables();
//			isShowTables = false;
//			return;
//		}
		if (mainPageSearchView.getVisibility() == View.VISIBLE) {
			Message msg = handler.obtainMessage();
			msg.what = MainPageKiosk.VIEW_EVENT_DISMISS_SEARCH;
			handler.sendMessage(msg);
			return;
		}
		super.onBackPressed();
	}

	@Override
	public void finish() {
		SessionStatus sessionStatus = Store.getObject(context,
				Store.SESSION_STATUS, SessionStatus.class);
//		List<Order> unPlayOrders = 
//		Order order = OrderSQL.getOrderByUnPlay(sessionStatus);

//		if (order != null) {
//			if (!OrderDetailSQL.getOrderDetails(order.getId()).isEmpty()) {
//				UIHelp.showToast(context, "Orders are not closed");
//			} else {
//				Tables tables = TablesSQL.getTableById(order.getTableId());
//				tables.setTableStatus(ParamConst.TABLE_STATUS_IDLE);
//				TablesSQL.updateTables(tables);
//				OrderSQL.deleteOrder(order);
//				OrderBillSQL.deleteOrderBillByOrder(order);
//				super.finish();
//			}
//		} else {
			super.finish();
//		}
	}

	private String getJsonStr() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tableShowAction", tableShowAction);
		map.put("places", places);
		map.put("tables", tables);
		map.put("tableStatusInfo", tableStatusInfo);
		String str = gson.toJson(map);
		return JSONUtil.getJSONFromEncode(str);
	}

	private String getRefreshTableStatusStr(Tables table) {
		int id = table.getPlacesId();
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<Tables> tables = new ArrayList<Tables>();
		tables.add(table);
		map.put("id", id);
		map.put("tables", tables);
		String str = gson.toJson(map);
		return JSONUtil.getJSONFromEncode(str);
	}

	private void showDiscountWindow(Order order, OrderDetail orderDetail,
			ResultCall resultCall) {
		discountWindow.show(order, orderDetail, resultCall);
	}

	private void showCloseBillWindow() {
		OrderBill orderBill = OrderBillSQL.getOrderBillByOrder(currentOrder);
		if (currentOrder.getOrderStatus().intValue() != ParamConst.ORDER_STATUS_UNPAY) {
			UIHelp.showToast(context, context.getResources().getString(R.string.print_bill_));
			return;
		}
		if (orderBill != null && orderBill.getBillNo() != null) {
			List<OrderSplit> orderSplits = OrderSplitSQL.getOrderSplits(currentOrder);
			if(orderSplits.isEmpty()){
				closeOrderWindow.show(currentOrder, operatePanel.getWidth(), orderBill);
			}else{
				int count = OrderDetailSQL.getOrderDetailCountByGroupId(0, currentOrder.getId());
				if(count == 0){
					selectOrderSplitDialog.show(orderSplits, currentOrder);
				}else{
					UIHelp.showToast(context, context.getResources().getString(R.string.assign_items));
				}
			}
		} else {
			UIHelp.showToast(context, context.getResources().getString(R.string.not_complete_order));
		}
	}

	private void showOpenItemWindow() {
		openItemWindow.show(context, findViewById(R.id.rl_root), handler,
				currentOrder);
	}

	private void dismissOpenItemWindow() {
		openItemWindow.dismiss();
	}

	private void showSearchView() {
		mainPageSearchView.setVisibility(View.VISIBLE);
		mainPageSearchView.setParam(context, currentOrder, CoreData
				.getInstance().getItemDetails(), handler);
	}

	private void search(String key) {
		if (mainPageSearchView.getVisibility() == View.VISIBLE) {
			List<ItemDetail> itemDetails = CoreData.getInstance()
					.getItemDetails();
			List<ItemDetail> itemDetailList = new ArrayList<ItemDetail>();
			if (key != null) {
				key = key.trim().replaceAll("\\s+","");
				for (ItemDetail itemDtail : itemDetails) {
					String name = CommonUtil.getInitial(itemDtail.getItemName());
					if (name.contains(key) || name.contains(key.toUpperCase())) {
						itemDetailList.add(itemDtail);
						continue;
					}
				}
			}
			mainPageSearchView.setParam(context, currentOrder, itemDetailList,
					handler);
		}
	}

	private void dismissSoftInput() {
		// 得到InputMethodManager的实例
		if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
			// 如果开启
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			getWindow().getAttributes().softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED;
		}
	}

	private void dismissSearchView() {
		mainPageSearchView.setVisibility(View.GONE);
//		mainPageSearchView.setParam(context, currentOrder, CoreData
//				.getInstance().getItemDetails(""), handler);
	}

//	private void showTables() {
//		getTables();
//		ObjectAnimator animator = ObjectAnimator.ofFloat(web_tables, "y",
//				web_tables.getY(), 0).setDuration(300);
//		animator.start();
//		isShowTables = true;
//		isTableFirstShow = false;
//	}

//	private void closeTables() {
//		ObjectAnimator animator = ObjectAnimator.ofFloat(web_tables, "y",
//				web_tables.getY(), web_tables.getY() + web_tables.getHeight())
//				.setDuration(300);
//		animator.start();
//		isShowTables = false;
//	}

//	private void dismissTables() {
//		ObjectAnimator animator = ObjectAnimator.ofFloat(web_tables, "y",
//				web_tables.getY(), web_tables.getY() + web_tables.getHeight())
//				.setDuration(300);
//		animator.start();
//		isShowTables = false;
//	}

	private void setData() {
		initOrder(currentTable);
		orderDetails = OrderDetailSQL.getOrderDetails(currentOrder.getId());
		mainPageMenuView.setParam(currentOrder, handler);
		mainPageMenuView.closeModifiers();
		orderView.setParam(this, currentOrder, orderDetails, handler);
		operatePanel.setParams(this, currentTable, currentOrder, orderDetails,
				handler);
		loadingDialog.dismiss();
		printerLoadingDialog.dismiss();
	}

	private void mergerOrderSetData() {
		// initOrder(currentTable);
		orderDetails = OrderDetailSQL.getOrderDetails(currentOrder.getId());
		mainPageMenuView.setParam(currentOrder, handler);
		orderView.setParam(this, currentOrder, orderDetails, handler);
		operatePanel.setParams(this, currentTable, currentOrder, orderDetails,
				handler);
		loadingDialog.dismiss();
		printerLoadingDialog.dismiss();
	}

	private void showModifyQuantityWindow(int quantity, DismissCall dismissCall) {
		modifyQuantityWindow.show(quantity, dismissCall);
	}

	private void addOrderDetail(OrderDetail orderDetail) {
		List<ItemModifier> itemModifiers = CoreData.getInstance()
				.getItemModifiers(
						CoreData.getInstance().getItemDetailById(
								orderDetail.getItemId()));
		OrderDetailSQL.addOrderDetailETC(orderDetail);
		setData();
		if (itemModifiers.size() > 0) {
			mainPageMenuView.openModifiers(currentOrder, orderDetail,
					itemModifiers);
		}
	}

	private boolean verifyTableRepeat(Tables newTables) {
		List<Tables> notificationTables = App.instance
				.getGetTingBillNotifications();
		if (notificationTables == null) {
			return true;
		} else {
			for (int i = 0; i < notificationTables.size(); i++) {
				if (notificationTables.get(i).getId().intValue() == newTables
						.getId().intValue()) {
					return false;
				}
			}
		}
		return true;
	}

	private void removeNotificationTables() {
		List<Tables> notificationTables = App.instance
				.getGetTingBillNotifications();
		if (notificationTables == null) {
			return;
		}
		for (int i = 0; i < notificationTables.size(); i++) {
			if (notificationTables.get(i).getId().intValue() == currentTable
					.getId().intValue()) {
				App.instance.removeGettingBillNotification(notificationTables
						.get(i));
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public Handler mmHandler = new Handler() {
		public void handleMessage(Message msg) {

		};
	};

	public void httpRequestAction(int action, Object obj) {
		switch (action) {
		case VIEW_EVENT_SET_DATA:
			int orderId = (Integer) obj;
			if (currentOrder != null && orderId == currentOrder.getId()) {
				handler.sendMessage(handler.obtainMessage(action));
			}
			break;
		case REFRESH_TABLES_STATUS:
			if (isShowTables) {
				Tables tables = (Tables) obj;
				handler.sendMessage(handler.obtainMessage(
						REFRESH_TABLES_STATUS, tables));
			}
			break;
		case VIEW_EVNT_GET_BILL_PRINT: {
			Tables tables = (Tables) obj;
			handler.sendMessage(handler.obtainMessage(VIEW_EVNT_GET_BILL_PRINT,
					tables));
		}
			break;
		case REFRESH_ORDER_STATUS:
			break;
		default:
			break;
		}
	};

	public void kotPrintStatus(int action, Object obj) {
		switch (action) {
		case KOT_PRINT_FAILED:
			handler.sendMessage(handler.obtainMessage(action));
			break;
		case KOT_PRINT_SUCCEED:
			handler.sendMessage(handler.obtainMessage(action, obj));
			break;
		case ParamConst.JOB_TYPE_POS_TRANSFER_TABLE:
			Order order = (Order) obj;
			setTablePacks(String.valueOf(order.getPersons()));
			handler.sendEmptyMessage(VIEW_EVENT_SET_DATA);
			break;
		case ParamConst.JOB_TYPE_POS_MERGER_TABLE:
			handler.sendEmptyMessage(VIEW_EVENT_DISMISS_TABLES);
			break;
		default:
			break;
		}
	};
	
	private void initAppOrder(final Tables tables) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				OrderSQL.deleteOrder(currentOrder);
				Order order = ObjectFactory.getInstance().getOrder(
						ParamConst.ORDER_ORIGIN_POS, tables,
						App.instance.getRevenueCenter(), App.instance.getUser(),
						App.instance.getSessionStatus(),
						App.instance.getBusinessDate(),
						App.instance.getIndexOfRevenueCenter(),
						ParamConst.ORDER_STATUS_OPEN_IN_POS,
						App.instance.getLocalRestaurantConfig()
						.getIncludedTax().getTax());
				List<TempOrderDetail> tempOrderDetails = TempOrderDetailSQL.getTempOrderDetailByAppOrderId(appOrderId);
				for (TempOrderDetail tempOrderDetail : tempOrderDetails){
					ItemDetail itemDetail = CoreData.getInstance().getItemDetailById(tempOrderDetail.getItemId());
					if(itemDetail == null){
						continue;
					}
					OrderDetail orderDetail = ObjectFactory.getInstance().getOrderDetail(order, itemDetail, 0);
					List<TempModifierDetail> tempModifierDetails = TempModifierDetailSQL.getTempOrderDetailByOrderDetailId(tempOrderDetail.getOrderDetailId());
					for(TempModifierDetail tempModifierDetail : tempModifierDetails){
						Modifier modifier = ModifierSQL.getModifierById(tempModifierDetail.getModifierId());
						if(modifier == null){
							continue;
						}
						OrderModifier orderModifier = ObjectFactory.getInstance().getOrderModifier(order, orderDetail, modifier, 0);
						OrderModifierSQL.addOrderModifier(orderModifier);
					}
					OrderDetailSQL.addOrderDetailETC(orderDetail);
				}
				handler.sendEmptyMessage(VIEW_EVENT_DISMISS_TABLES);
			}
		}).start();
	}
	
	private void TempOrderISPaied(){
		Payment payment = ObjectFactory.getInstance().getPayment(currentOrder, ObjectFactory.getInstance().getOrderBill(currentOrder, App.instance.getRevenueCenter()));
		ObjectFactory.getInstance().getPaymentSettlement(payment, ParamConst.SETTLEMENT_TYPE_THIRDPARTY, currentOrder.getTotal());
		currentOrder.setOrderStatus(ParamConst.ORDER_STATUS_FINISHED);
		OrderSQL.update(currentOrder);
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("orderId", String.valueOf(currentOrder.getId()));
		map.put("paymentId", String.valueOf(payment.getId().intValue()));
		handler.sendMessage(handler.obtainMessage(VIEW_EVENT_CLOSE_BILL,map));
	}

	public void openCustomNoteView(){
		mainPageMenuView.openCustomNoteView();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		setData();
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == NetWorkOrderActivity.CHECK_REQUEST_CODE && resultCode == NetWorkOrderActivity.CHECK_REQUEST_CODE){
			activityRequestCode = requestCode;
			appOrderId = data.getIntExtra("appOrderId", 0);
			if(appOrderId == 0)
				return;
			if(orderDetails != null && orderDetails.size() != 0){
				DialogFactory.showOneButtonCompelDialog(context, "警告", "请先关闭或删除当前订单", null);
			}else{
				loadingDialog.show();
				initAppOrder(currentTable);
			}
		}else{
			activityRequestCode = 0;
			mDrawerLayout.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					if (mDrawerLayout.isDrawerOpen(settingView)) {
						mDrawerLayout.closeDrawer(Gravity.END);
					}
				}
			}, 500);
		}
	}
}
