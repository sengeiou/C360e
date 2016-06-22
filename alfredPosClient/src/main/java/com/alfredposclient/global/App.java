package com.alfredposclient.global;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;

import com.alfred.remote.printservice.IAlfredRemotePrintService;
import com.alfred.remote.printservice.RemotePrintServiceCallback;
import com.alfredbase.BaseActivity;
import com.alfredbase.BaseApplication;
import com.alfredbase.ParamConst;
import com.alfredbase.UnCEHandler;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.ItemCategory;
import com.alfredbase.javabean.ItemMainCategory;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotItemModifier;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.LocalDevice;
import com.alfredbase.javabean.Modifier;
import com.alfredbase.javabean.MonthlyPLUReport;
import com.alfredbase.javabean.MonthlySalesReport;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.PaymentSettlement;
import com.alfredbase.javabean.PrinterTitle;
import com.alfredbase.javabean.ReportDaySales;
import com.alfredbase.javabean.ReportDayTax;
import com.alfredbase.javabean.ReportHourly;
import com.alfredbase.javabean.ReportPluDayComboModifier;
import com.alfredbase.javabean.ReportPluDayItem;
import com.alfredbase.javabean.ReportPluDayModifier;
import com.alfredbase.javabean.RestaurantConfig;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.RoundAmount;
import com.alfredbase.javabean.Tables;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.UserTimeSheet;
import com.alfredbase.javabean.model.AlipayPushMsgDto;
import com.alfredbase.javabean.model.KDSDevice;
import com.alfredbase.javabean.model.LocalRestaurantConfig;
import com.alfredbase.javabean.model.MainPosInfo;
import com.alfredbase.javabean.model.PrintOrderItem;
import com.alfredbase.javabean.model.PrintOrderModifier;
import com.alfredbase.javabean.model.PrintReceiptInfo;
import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.javabean.model.PushMessage;
import com.alfredbase.javabean.model.ReportEntItem;
import com.alfredbase.javabean.model.ReportVoidItem;
import com.alfredbase.javabean.model.SessionStatus;
import com.alfredbase.javabean.model.WaiterDevice;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.Store;
import com.alfredbase.store.sql.CardsSettlementSQL;
import com.alfredbase.store.sql.NetsSettlementSQL;
import com.alfredbase.store.sql.UserSQL;
import com.alfredbase.store.sql.UserTimeSheetSQL;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.CommonUtil;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.ObjectFactory;
import com.alfredbase.utils.TimeUtil;
import com.alfredposclient.R;
import com.alfredposclient.activity.Welcome;
import com.alfredposclient.http.server.MainPosHttpServer;
import com.alfredposclient.jobs.CloudSyncJobManager;
import com.alfredposclient.jobs.KotJobManager;
import com.alfredposclient.service.PushService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class App extends BaseApplication {

	public static App instance;
	private RevenueCenter revenueCenter;
	private MainPosInfo mainPosInfo;
	public String VERSION = "1.0.8";
	private static final int DATABASE_VERSION = 22;
	private static final String DATABASE_NAME = "com.alfredposclient";
	/**
	 * User: Current cashier logged in
	 */
	private User user;
	private Map<String, User> activeUsers = new ConcurrentHashMap<String, User>();
	private Map<Integer, KDSDevice> kdsDevices = new ConcurrentHashMap<Integer, KDSDevice>();
	private Map<Integer, PrinterDevice> printerDevices = new ConcurrentHashMap<Integer, PrinterDevice>();
	private Map<Integer, WaiterDevice> waiterDevices = new ConcurrentHashMap<Integer, WaiterDevice>();
	// push message
	public Map<String, Integer> pushMsgMap = new HashMap<String, Integer>();
	
	public Map<Integer, AlipayPushMsgDto> alipayPush = new HashMap<Integer, AlipayPushMsgDto>();

	private List<Tables> notificationsOfGettingBill = Collections
			.synchronizedList(new ArrayList<Tables>());

	// Job Manager
	private KotJobManager kdsJobManager;

	private SessionStatus sessionStatus;
	private Long businessDate;
	private Long lastBusinessDate;

	private int indexOfRevenueCenter;
	private MainPosHttpServer httpServer;

	// system settings
	private SystemSettings systemSettings;

	// Remote Print Service
	private IAlfredRemotePrintService mRemoteService = null;

	// Sync sale data to cloud
	private CloudSyncJobManager syncJob = null;

	// 全局变量用于表示正在结账的 Order
	public Order orderInPayment = null;

	// public String roundType;

	// price include tax;
	// public int taxIncluded = 0;

	public boolean kot_print;

	// // 动态session类型
	// private List<Integer> sessionConfigType;
	//
	// // 货币符号
	// private String currencySymbol = "$";

	private LocalRestaurantConfig localRestaurantConfig;
	/*
	 * Print Service call back
	 */
	private RemotePrintServiceCallback mCallback = new RemotePrintServiceCallback();

	/*
	 * Remote Print Service Connection
	 */
	private ServiceConnection mRemoteConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mRemoteService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {

			mRemoteService = IAlfredRemotePrintService.Stub
					.asInterface(service);
			try {
				mRemoteService.registerCallBack(mCallback);
				String txt = mRemoteService.getMessage();
				mRemoteService.setMessage("Alfred Main POS");
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}
	};

	/*
	 * Alfred PUSH Service
	 */
	private PushService pushService;
	private ServiceConnection pushConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			pushService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pushService = ((PushService.Binder) service).getService();
			pushService.setListener(new PushListenerClient(App.instance));
			if (revenueCenter != null) {
				pushService
						.sentMessage(PushMessage.registClient(
								revenueCenter.getRestaurantId(),
								revenueCenter.getId()));
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();

		instance = this;

		SQLExe.init(this, DATABASE_NAME, DATABASE_VERSION);
		systemSettings = new SystemSettings(this);

		kdsJobManager = new KotJobManager(this);
		httpServer = new MainPosHttpServer();

		// Init remote print service
		connectRemotePrintService();
		if (getRevenueCenter() != null) {
			bindPushWebSocketService();
			migration();
		}
		VERSION = getAppVersionName();
		UnCEHandler catchExcep = new UnCEHandler(this, Welcome.class);
		Thread.setDefaultUncaughtExceptionHandler(catchExcep);
	}

	// for APP data migration
	private void migration() {
		if (DATABASE_VERSION <= 2)
			syncJob.syncAllUnsentMsg_migration();
	}

	@Override
	protected void onIPChanged() {
		super.onIPChanged();
		if (this.getMainPosInfo() != null) {
			this.mainPosInfo.setIP(CommonUtil.getLocalIpAddress());
			this.setMainPosInfo(mainPosInfo);
			Store.saveObject(getBaseContext(), Store.MAINPOSINFO, mainPosInfo);
		}
	}

	@Override
	protected void onNetworkConnectionUpdate() {
		super.onNetworkConnectionUpdate();
	}

	/*
	 * Generate Printer and KDS device from DB after the first setup
	 */
	public void initKdsAndPrinters() {
		List<LocalDevice> devices = CoreData.getInstance().getLocalDevices();

		for (LocalDevice item : devices) {
			int devid = item.getDeviceId();
			String ip = item.getIp();
			String mac = item.getMacAddress();
			String name = item.getDeviceName();
			String model = item.getDeviceMode();
			int type = item.getDeviceType();

			// load physical printer
			if (type == ParamConst.DEVICE_TYPE_PRINTER) {
				PrinterDevice pdev = new PrinterDevice();
				pdev.setDevice_id(devid);
				pdev.setIP(ip);
				pdev.setMac(mac);
				pdev.setName(name);
				pdev.setModel(model);
				pdev.setIsCahierPrinter(CoreData.getInstance()
						.isCashierPrinter(devid));
				printerDevices.put(devid, pdev);
			} else if (type == ParamConst.DEVICE_TYPE_KDS) {
				KDSDevice kdev = new KDSDevice();
				kdev.setDevice_id(devid);
				kdev.setIP(ip);
				kdev.setMac(mac);
				kdev.setName(name);
				kdsDevices.put(devid, kdev);
			} else if (type == ParamConst.DEVICE_TYPE_WAITER) {
				WaiterDevice wdev = new WaiterDevice();
				wdev.setIP(ip);
				wdev.setMac(mac);
				wdev.setWaiterId(devid);
				waiterDevices.put(devid, wdev);
			}
		}
	}

	/* HTTP Server */
	public void startHttpServer() {
		try {
			if (!httpServer.isAlive())
				this.httpServer.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	public RevenueCenter getRevenueCenter() {
		if (revenueCenter == null)
			revenueCenter = Store.getObject(this, Store.CURRENT_REVENUE_CENTER,
					RevenueCenter.class);
		return revenueCenter;
	}

	public void setRevenueCenter(RevenueCenter revenueCenter) {
		this.revenueCenter = revenueCenter;
	}

	/* Get all users currently connected to POS */
	public void addActiveUser(String userKey, User user) {
		activeUsers.put(userKey, user);
		Store.saveObject(this, Store.USER_AND_KEY, activeUsers);
	}

	public Map<String, User> getActiveUser() {
		if (activeUsers.isEmpty()) {
			if (Store.getObject(this, Store.USER_AND_KEY,
					new TypeToken<Map<String, User>>() {
					}.getType()) != null) {
				activeUsers = Store.getObject(this, Store.USER_AND_KEY,
						new TypeToken<ConcurrentHashMap<String, User>>() {
						}.getType());
			}

		}
		return activeUsers;
	}

	public User getUserByKey(String userKey) {
		return getActiveUser().get(userKey);
	}

	public void addKDSDevice(int deviceid, KDSDevice device) {
		this.kdsDevices.put(deviceid, device);
	}

	public void removeKDSDevice(int deviceid) {
		this.kdsDevices.remove(deviceid);
	}

	public KDSDevice getKDSDevice(int deviceid) {
		return this.kdsDevices.get(deviceid);
	}

	public Map<Integer, KDSDevice> getKDSDevices() {
		return this.kdsDevices;
	}

	public User getUser() {
		if (user == null) {
			user = Store.getObject(instance, Store.MAINPOS_USER, User.class);
		}
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		Store.saveObject(instance, Store.MAINPOS_USER, user);
	}

	public SessionStatus getSessionStatus() {
		return sessionStatus;
	}

	public void setSessionStatus(SessionStatus sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

	public Long getBusinessDate() {
		if (businessDate == null) {
			businessDate = (Long) Store.getLong(this, Store.BUSINESS_DATE);
		}
		// if all data is null, set today is businessDate
		if (businessDate == Store.DEFAULT_LONG_TYPE) {
			businessDate = TimeUtil.getNewBusinessDate();
		}
		return businessDate;
	}

	public void setBusinessDate(Long businessDate) {
		this.businessDate = businessDate;
	}

	public Long getLastBusinessDate() {
		if (lastBusinessDate == null) {
			lastBusinessDate = Store.getLong(this, Store.LAST_BUSINESSDATE);
		}
		// if all data is null, set today is businessDate
		if (lastBusinessDate == null
				|| lastBusinessDate == Store.DEFAULT_LONG_TYPE) {
			lastBusinessDate = TimeUtil.getNewBusinessDate();
		}
		return lastBusinessDate;
	}

	public void setLastBusinessDate(Long lastBusinessDate) {
		this.lastBusinessDate = lastBusinessDate;
	}

	public int getIndexOfRevenueCenter() {
		if (revenueCenter != null) {
			indexOfRevenueCenter = revenueCenter.getId();
		}
		return indexOfRevenueCenter;
	}

	public void setIndexOfRevenueCenter(int indexOfRevenueCenter) {
		this.indexOfRevenueCenter = indexOfRevenueCenter;
	}

	public KotJobManager getKdsJobManager() {
		return this.kdsJobManager;
	}

	public Map<Integer, WaiterDevice> getWaiterDevices() {
		return waiterDevices;
	}

	public void setWaiterDevices(Map<Integer, WaiterDevice> waiterDevices) {
		this.waiterDevices = waiterDevices;
	}

	public void addWaiterDevice(WaiterDevice waiterDevice) {
		this.waiterDevices.put(waiterDevice.getWaiterId(), waiterDevice);

		// timesheet: Login
		boolean login = false;
		ArrayList<UserTimeSheet> mUserTimeSheets = UserTimeSheetSQL
				.getUserTimeSheetsByEmpId(waiterDevice.getWaiterId(),
						App.instance.getLastBusinessDate());
		User user = UserSQL.getUserById(waiterDevice.getWaiterId());
		for (UserTimeSheet userTimeSheet : mUserTimeSheets) {
			if (userTimeSheet != null
					&& userTimeSheet.getStatus() == ParamConst.USERTIMESHEET_STATUS_LOGIN) {
				login = true;
			}
		}
		if (!login) {
			UserTimeSheet userTimeSheet = ObjectFactory.getInstance()
					.getUserTimeSheet(App.instance.getLastBusinessDate(), user,
							App.instance.getRevenueCenter());
			UserTimeSheetSQL.addUser(userTimeSheet);
		}
	}

	public void removeWaiterDevice(WaiterDevice waiterDevice) {
		this.waiterDevices.remove(waiterDevice.getWaiterId());
		boolean login = true;

		UserTimeSheet userTimeSheet = UserTimeSheetSQL.getUserTimeSheetByEmpId(
				waiterDevice.getWaiterId(), App.instance.getLastBusinessDate());
		if (userTimeSheet == null) {
			login = false;
		}
		if (login) {
			userTimeSheet.setLogoutTime(System.currentTimeMillis());
			userTimeSheet.setStatus(ParamConst.USERTIMESHEET_STATUS_LOGOUT);
			UserTimeSheetSQL.addUser(userTimeSheet);
		}
	}

	public boolean isWaiterLoginAllowed(WaiterDevice dev) {
		boolean ret = false;
		WaiterDevice waiterDevice = this.waiterDevices.get(dev.getWaiterId());
		if (waiterDevice != null)
			if (waiterDevice.getMac().equals(dev.getMac()))
				ret = true;
			else
				ret = false;
		else
			ret = true;
		return ret;
	}

	public PrinterDevice getPrinterDeviceById(int deviceId) {
		return printerDevices.get(deviceId);
	}

	public void setPrinterDevice(Integer deviceid, PrinterDevice device) {
		this.printerDevices.put(deviceid, device);
	}

	public void removePrinterDevice(Integer deviceid) {
		for (Iterator<Map.Entry<Integer, PrinterDevice>> it = printerDevices
				.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, PrinterDevice> entry = it.next();
			if (entry.getKey().intValue() == deviceid.intValue()) {
				it.remove();
			}
		}
	}

	public Map<Integer, PrinterDevice> getPrinterDevices() {
		return this.printerDevices;
	}

	public PrinterDevice getCahierPrinter() {
		// PrinterDevice dummy = new PrinterDevice();
		// dummy.setIP("192.168.0.11");
		// return dummy;

		for (Map.Entry<Integer, PrinterDevice> dev : printerDevices.entrySet()) {
			Integer key = dev.getKey();
			PrinterDevice devPrinter = dev.getValue();
			if (devPrinter.getIsCahierPrinter() > 0)
				return devPrinter;
		}
		return null;

	}

	/**
	 * @return the mainPosInfo
	 */
	public MainPosInfo getMainPosInfo() {
		return mainPosInfo;
	}

	public void setMainPosInfo(MainPosInfo mainPosInfo) {
		this.mainPosInfo = mainPosInfo;
	}

	/* Print service */
	public void connectRemotePrintService() {
		if (mRemoteService == null) {
			Intent bindIntent = new Intent(
					"alfred.intent.action.bindPrintService");
			bindIntent.putExtra("PRINTERKEY", "fxxxkprinting");
			bindIntent.setClassName("com.alfred.remote.printservice",
					"com.alfred.remote.printservice.PrintService");

			bindService(bindIntent, mRemoteConnection, Context.BIND_AUTO_CREATE);
		}
	}

	public void reconnectRemotePrintService() {
		Intent bindIntent = new Intent("alfred.intent.action.bindPrintService");
		bindIntent.putExtra("PRINTERKEY", "fxxxkprinting");
		bindIntent.setClassName("com.alfred.remote.printservice",
				"com.alfred.remote.printservice.PrintService");

		this.bindService(bindIntent, mRemoteConnection,
				Context.BIND_AUTO_CREATE);
	}

	public void disconnectRemotePrintService() {
		if (mRemoteConnection != null)
			unbindService(mRemoteConnection);
	}

	public boolean isKotPrint() {
		return kot_print = Store.getBoolean(instance, Store.KOT_PRINT, true);
	}

	public boolean remoteKotPrint(PrinterDevice printer, KotSummary kotsummary,
			ArrayList<KotItemDetail> itemDetailsList,
			ArrayList<KotItemModifier> modifiersList) {
		if (!isKotPrint()) {
			return true;
		}
		if (printer == null) {
			return false;
		}
		if (mRemoteService == null) {
			printerDialog();
			return false;
		}
		try {
			Gson gson = new Gson();
			String printstr = gson.toJson(printer);
			String kotsumStr = gson.toJson(kotsummary);
			String kdlstr = gson.toJson(itemDetailsList);
			String modstr = gson.toJson(modifiersList);
			if (isRevenueKiosk()){
				if (countryCode == ParamConst.CHINA)
				mRemoteService.printKioskKOT(printstr, kotsumStr, kdlstr,
						modstr, this.systemSettings.isKotPrintTogether(),
						this.systemSettings.isKotDoublePrint(), getPrintOrderNo(kotsummary.getOrderId().intValue()), 2);
				else
					mRemoteService.printKioskKOT(printstr, kotsumStr, kdlstr,
							modstr, this.systemSettings.isKotPrintTogether(),
							this.systemSettings.isKotDoublePrint(), null, 3);
			} else {
				int size = 3;
				if(countryCode == ParamConst.CHINA)
					size = 2;
				mRemoteService.printKOT(printstr, kotsumStr, kdlstr, modstr,
						this.systemSettings.isKotPrintTogether(),
						this.systemSettings.isKotDoublePrint(), size);
			}
			return true;
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean remoteOrderSummaryPrint(PrinterDevice printer, KotSummary kotsummary,
			ArrayList<KotItemDetail> itemDetailsList,
			ArrayList<KotItemModifier> modifiersList) {
		if (printer == null) {
			return false;
		}
		if(isRevenueKiosk()){
			return false;
		}
		if (mRemoteService == null) {
			printerDialog();
			return false;
		}
		try {
			Gson gson = new Gson();
			
			//Simple Hack: 客户联需要显示餐厅
			KotSummary fakeKotSummary  = new KotSummary();
			fakeKotSummary.setBusinessDate(kotsummary.getBusinessDate());
			fakeKotSummary.setCreateTime(kotsummary.getCreateTime());
			fakeKotSummary.setId(kotsummary.getId());
			fakeKotSummary.setOrderId(kotsummary.getOrderId());		
			fakeKotSummary.setOrderNo(kotsummary.getOrderNo());
			fakeKotSummary.setRevenueCenterId(kotsummary.getRevenueCenterId());
			fakeKotSummary.setRevenueCenterName(CoreData.getInstance().getRestaurant().getRestaurantName());
			fakeKotSummary.setTableId(kotsummary.getTableId());
			fakeKotSummary.setTableName(kotsummary.getTableName());
			fakeKotSummary.setUpdateTime(kotsummary.getUpdateTime());
			fakeKotSummary.setIsTakeAway(kotsummary.getIsTakeAway());
			
			String printstr = gson.toJson(printer);
			String kotsumStr = gson.toJson(fakeKotSummary);
			String kdlstr = gson.toJson(itemDetailsList);
			String modstr = gson.toJson(modifiersList);

			int size = 3;
			if(countryCode == ParamConst.CHINA)
				size = 2;
			mRemoteService.printBillSummary(printstr, kotsumStr, kdlstr, modstr,size);
			return true;
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
	}

	// public void remoteBillPrint(PrinterDevice printer, PrinterTitle title,
	// Order order, ArrayList<PrintOrderItem> orderItems,
	// ArrayList<PrintOrderModifier> orderModifiers,
	// Map<String,ArrayList<String>> taxes, Map<String, String> settlement ) {
	//
	// if (mRemoteService == null) {
	// printerDialog();
	// return ;
	// }
	// try {
	// Gson gson = new Gson();
	// String prtStr = gson.toJson(printer);
	// String prtTitle = gson.toJson(title);
	// String orderStr = gson.toJson(order);
	// String details = gson.toJson(orderItems);
	// String mods = gson.toJson(orderModifiers);
	// String tax = gson.toJson(taxes);
	// String payment = gson.toJson(settlement);
	// if (isRevenueKiosk())
	// mRemoteService.printKioskBill(prtStr,prtTitle,orderStr,details,mods,
	// tax, payment, this.systemSettings.isDoubleBillPrint(),
	// this.systemSettings.isDoubleReceiptPrint());
	// else
	// mRemoteService.printBill(prtStr,prtTitle,orderStr,details,mods,
	// tax, payment, this.systemSettings.isDoubleBillPrint(),
	// this.systemSettings.isDoubleReceiptPrint());
	// } catch (RemoteException e) {
	// e.printStackTrace();
	// }
	// }

	public void remoteBillPrint(PrinterDevice printer, PrinterTitle title,
			Order order, ArrayList<PrintOrderItem> orderItems,
			ArrayList<PrintOrderModifier> orderModifiers,
			List<Map<String, String>> taxes,
			List<PaymentSettlement> settlement, RoundAmount roundAmount) {

		if (mRemoteService == null) {
			printerDialog();
			return;
		}
		List<PrintReceiptInfo> printReceiptInfos = new ArrayList<PrintReceiptInfo>();
		if (settlement != null) {
			for (PaymentSettlement paymentSettlement : settlement) {
				PrintReceiptInfo printReceiptInfo = new PrintReceiptInfo();
				printReceiptInfo.setPaidAmount(paymentSettlement
						.getPaidAmount());
				printReceiptInfo.setPaymentTypeId(paymentSettlement
						.getPaymentTypeId());
				switch (paymentSettlement.getPaymentTypeId().intValue()) {
				case ParamConst.SETTLEMENT_TYPE_CASH:
					printReceiptInfo.setCashChange(paymentSettlement
							.getCashChange());
					break; 
				case ParamConst.SETTLEMENT_TYPE_MASTERCARD:
				case ParamConst.SETTLEMENT_TYPE_UNIPAY:
				case ParamConst.SETTLEMENT_TYPE_VISA:
				case ParamConst.SETTLEMENT_TYPE_DINNER_INTERMATIONAL:
				case ParamConst.SETTLEMENT_TYPE_AMEX:
				case ParamConst.SETTLEMENT_TYPE_JCB:
					printReceiptInfo
							.setCardNo(CardsSettlementSQL
									.getCardNoByPaymentIdAndPaymentSettlementId(
											paymentSettlement.getPaymentId()
													.intValue(),
											paymentSettlement.getId()
													.intValue()));
					break;
				case ParamConst.SETTLEMENT_TYPE_NETS:
					printReceiptInfo
							.setCardNo(NetsSettlementSQL
									.getNetsSettlementByPament(
											paymentSettlement.getPaymentId()
													.intValue(),
											paymentSettlement.getId()
													.intValue())
									.getReferenceNo()
									+ "");
					break;
				default:
					break;
				}
				printReceiptInfos.add(printReceiptInfo);
			}
		}
		try {
			Map<String, String> roundingMap = new HashMap<String, String>();
			String total = order.getTotal();
			String rounding = "0.00";
			if (roundAmount != null) {
				total = BH.sub(BH.getBD(order.getTotal()),
						BH.getBD(roundAmount.getRoundBalancePrice()), true)
						.toString();
				rounding = BH.getBD(roundAmount.getRoundBalancePrice())
						.toString();
			}
			roundingMap.put("Total", total);
			roundingMap.put("Rounding", rounding);
			Gson gson = new Gson();
			String prtStr = gson.toJson(printer);
			String prtTitle = gson.toJson(title);
			String orderStr = gson.toJson(order);
			String details = gson.toJson(orderItems);
			String mods = gson.toJson(orderModifiers);
			String tax = gson.toJson(taxes);
			String payment = gson.toJson(printReceiptInfos);
			String roundStr = gson.toJson(roundingMap);
			// gson.toJson(roundingMap);
			if (isRevenueKiosk())
				if(countryCode == ParamConst.CHINA)
				mRemoteService.printKioskBill(prtStr, prtTitle, orderStr,
						details, mods, tax, payment,
						this.systemSettings.isDoubleBillPrint(),
						this.systemSettings.isDoubleReceiptPrint(), roundStr, getPrintOrderNo(order.getId().intValue()));
				else
					mRemoteService.printKioskBill(prtStr, prtTitle, orderStr,
							details, mods, tax, payment,
							this.systemSettings.isDoubleBillPrint(),
							this.systemSettings.isDoubleReceiptPrint(), roundStr, null);
			else
				mRemoteService.printBill(prtStr, prtTitle, orderStr, details,
						mods, tax, payment,
						this.systemSettings.isDoubleBillPrint(),
						this.systemSettings.isDoubleReceiptPrint(), roundStr);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void kickOutCashDrawer(PrinterDevice printer) {
		Gson gson = new Gson();
		String prtStr = gson.toJson(printer);
		if (mRemoteService == null) {
			printerDialog();
			return;
		}
		try {
			mRemoteService.kickCashDrawer(prtStr);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Discover all physical printer devices Result is retured from callback
	 * Param: Handler: callback when printers are found
	 */
	public void discoverPrinter(Handler handler) {
		if (mRemoteService == null) {
			printerDialog();
			return;
		}
		try {
			mCallback.setHandler(handler);
			mRemoteService.listPrinters();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void remotePrintDaySalesReport(String xzType, PrinterDevice printer,
			PrinterTitle title, ReportDaySales reportData,
			List<ReportDayTax> taxData) {
		if (mRemoteService == null) {
			printerDialog();
			return;
		}
		try {
			Gson gson = new Gson();
			String prtStr = gson.toJson(printer);
			String prtTitle = gson.toJson(title);
			String report = gson.toJson(reportData);
			String tax = gson.toJson(taxData);
			mRemoteService.printDaySalesReport(xzType, prtStr, prtTitle,
					report, tax);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void remotePrintDetailAnalysisReport(String xzType,
			PrinterDevice printer, PrinterTitle title,  ReportDaySales reportData,
			List<ReportPluDayItem> plu, List<ReportPluDayModifier> modifier, List<ReportPluDayComboModifier> combData,
			List<ItemMainCategory> category, List<ItemCategory> items) {

		if (mRemoteService == null) {
			printerDialog();
			return;
		}
		try {
			Gson gson = new Gson();
			String prtStr = gson.toJson(printer);
			String prtTitle = gson.toJson(title);
			String pluStr = gson.toJson(plu);
			String catStr = gson.toJson(category);
			String itmStr = gson.toJson(items);
			String modStr = gson.toJson(modifier);
			String combStr = gson.toJson(combData);
			String sales = gson.toJson(reportData);
			mRemoteService.printDetailAnalysisReport(xzType, prtStr, prtTitle, sales,
					pluStr, modStr, combStr, catStr, itmStr);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void remotePrintSummaryAnalysisReport(String xzType,
			PrinterDevice printer, PrinterTitle title,
			List<ReportPluDayItem> plu, List<ReportPluDayModifier> modifier,
			List<ItemMainCategory> category, List<ItemCategory> items) {

		if (mRemoteService == null) {
			printerDialog();
			return;
		}
		try {
			Gson gson = new Gson();
			String prtStr = gson.toJson(printer);
			String prtTitle = gson.toJson(title);
			String pluStr = gson.toJson(plu);
			String catStr = gson.toJson(category);
			String itmStr = gson.toJson(items);
			String modStr = gson.toJson(modifier);
			mRemoteService.printSummaryAnalysisReport(xzType, prtStr, prtTitle,
					pluStr, modStr, catStr, itmStr);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void remotePrintHourlyReport(String xzType, PrinterDevice printer,
			PrinterTitle title, List<ReportHourly> hourly) {
		if (mRemoteService == null) {
			// Toast.makeText(this, "Printer service is not started yet",
			// 1000).show();
			printerDialog();
			return;
		}
		try {
			Gson gson = new Gson();
			String prtStr = gson.toJson(printer);
			String prtTitle = gson.toJson(title);
			String hourStr = gson.toJson(hourly);
			mRemoteService.printHourlyAnalysisReport(xzType, prtStr, prtTitle,
					hourStr);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void remotePrintVoidItemReport(String xzType, PrinterDevice printer,
			PrinterTitle title, ArrayList<ReportVoidItem> reportVoidItems) {
		if (mRemoteService == null) {
			// Toast.makeText(this, "Printer service is not started yet",
			// 1000).show();
			printerDialog();
			return;
		}
		try {
			Gson gson = new Gson();
			String prtStr = gson.toJson(printer);
			String prtTitle = gson.toJson(title);
			String voidItemStr = gson.toJson(reportVoidItems);
			mRemoteService.printVoidItemAnalysisReport(xzType, prtStr,
					prtTitle, voidItemStr);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void remotePrintEntItemReport(String xzType, PrinterDevice printer,
			PrinterTitle title, ArrayList<ReportEntItem> reportEntItems) {
		if (mRemoteService == null) {
			// Toast.makeText(this, "Printer service is not started yet",
			// 1000).show();
			printerDialog();
			return;
		}
		try {
			Gson gson = new Gson();
			String prtStr = gson.toJson(printer);
			String prtTitle = gson.toJson(title);
			String entItemStr = gson.toJson(reportEntItems);
			mRemoteService.printEntItemAnalysisReport(xzType, prtStr, prtTitle,
					entItemStr);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
	
	
	public void remotePrintModifierDetailAnalysisReport(String xzType,
			PrinterDevice printer, PrinterTitle title, List<ReportPluDayModifier> modifier,
			List<Modifier> category) {

		if (mRemoteService == null) {
			printerDialog();
			return;
		}
		try {
			Gson gson = new Gson();
			String prtStr = gson.toJson(printer);
			String prtTitle = gson.toJson(title);
			String catStr = gson.toJson(category);
			String modStr = gson.toJson(modifier);
			mRemoteService.printModifierDetailAnalysisReport(xzType, prtStr, prtTitle, modStr, catStr);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
	
	public void remotePrintComboDetailAnalysisReport(String xzType,
			PrinterDevice printer, PrinterTitle title, List<ReportPluDayItem> plu, List<ReportPluDayComboModifier> modifier) {

		if (mRemoteService == null) {
			printerDialog();
			return;
		}
		try {
			Gson gson = new Gson();
			String prtStr = gson.toJson(printer);
			String prtTitle = gson.toJson(title);
			String itemStr = gson.toJson(plu);
			String modStr = gson.toJson(modifier);
			mRemoteService.printComboDetailAnalysisReport(xzType, prtStr, prtTitle, itemStr, modStr);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
	
	public void remotePrintMonthlySalesReport(PrinterDevice printer, PrinterTitle title, int year, int month, List<MonthlySalesReport> sales) {

		if (mRemoteService == null) {
			printerDialog();
			return;
		}
		try {
			Gson gson = new Gson();
			String prtStr = gson.toJson(printer);
			String prtTitle = gson.toJson(title);
			String data = gson.toJson(sales);
			mRemoteService.printMonthlySaleReport(prtStr, prtTitle, year, month, data);
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void remotePrintMonthlyPLUReport(PrinterDevice printer, PrinterTitle title, int year, int month, List<MonthlyPLUReport> plu) {

		if (mRemoteService == null) {
			printerDialog();
			return;
		}
		try {
			Gson gson = new Gson();
			String prtStr = gson.toJson(printer);
			String prtTitle = gson.toJson(title);
			String data = gson.toJson(plu);
			mRemoteService.printMonthlyPLUReport(prtStr, prtTitle, year, month, data);
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
	
	public void printerDialog() {
		BaseActivity context = App.getTopActivity();
		DialogFactory.commonTwoBtnDialog(context, context.getResources()
				.getString(R.string.print_down), context.getResources()
				.getString(R.string.reconnect_print), context.getResources()
				.getString(R.string.no),
				context.getResources().getString(R.string.yes), null,
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						App.instance.reconnectRemotePrintService();
					}
				});
	}

	/** WebSocket PUSH Service **/
	public void bindPushWebSocketService() {
		this.bindService(PushService.startIntent(this.getApplicationContext()),
				this.pushConnection, Context.BIND_IMPORTANT);
		this.startService(PushService.startIntent(this.getApplicationContext()));
		syncJob = new CloudSyncJobManager(this);
	}

	public void unBindPushWebSocketSerive() {
		if (this.pushService != null) {
			this.unbindService(this.pushConnection);
			this.pushService = null;
		}
	}

	// Getting bill notification
	public void addGettingBillNotification(Tables tables) {
		this.notificationsOfGettingBill.add(tables);
		Store.saveObject(instance, Store.NOTIFICATIONS_OF_GETTING_BILL,
				notificationsOfGettingBill);
	}

	public void removeGettingBillNotification(Tables tables) {
		this.notificationsOfGettingBill.remove(tables);
		Store.saveObject(instance, Store.NOTIFICATIONS_OF_GETTING_BILL,
				notificationsOfGettingBill);
	}

	public List<Tables> getGetTingBillNotifications() {
		if (notificationsOfGettingBill.isEmpty()) {
			List<Tables> tabls = Store.getObject(instance,
					Store.NOTIFICATIONS_OF_GETTING_BILL,
					new TypeToken<List<Tables>>() {
					}.getType());
			if (tabls != null) {
				notificationsOfGettingBill = tabls;
			}
		}
		return this.notificationsOfGettingBill;
	}

	public SystemSettings getSystemSettings() {
		return systemSettings;
	}

	public Map<String, Integer> getPushMsgMap() {
		if (pushMsgMap.isEmpty()) {
			Map<String, Integer> map = Store.getObject(instance,
					Store.PUSH_MESSAGE, new TypeToken<Map<String, Integer>>() {
					}.getType());
			if (map != null) {
				pushMsgMap = map;
			}
		}
		return pushMsgMap;
	}

	public void setPushMsgMap(Map<String, Integer> pushMsgMap) {
		this.pushMsgMap = pushMsgMap;
	}

	public void addPushMsgItem(String msg, int msgValue) {
		pushMsgMap.put(msg, msgValue);
	}

	public boolean isRevenueKiosk() {
		if (revenueCenter != null
				&& revenueCenter.getIsKiosk() == ParamConst.REVENUECENTER_IS_KIOSK) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Cloud Sync manager: Sync Sales data to cloud
	 */
	public CloudSyncJobManager getSyncJob() {
		return syncJob;
	}

	public int height;

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	// // round的配置移到RestaurantConfig里面
	// public String getRoundType() {
	// if(TextUtils.isEmpty(roundType)){
	// setRoundType(CoreData.getInstance().getRestaurantConfigs());
	// }
	// if(TextUtils.isEmpty(roundType)){
	// roundType = ParamConst.ROUND_NONE;
	// }
	// return roundType;
	// }
	//
	// public void setRoundType(List<RestaurantConfig> restaurantConfigs) {
	// for(RestaurantConfig restaurantConfig : restaurantConfigs){
	// if(restaurantConfig.getParaType().intValue() ==
	// ParamConst.ROUND_RULE_TYPE){
	// this.roundType = restaurantConfig.getParaValue1();
	// }
	// }
	// }
	//
	// public List<Integer> getSessionConfigType() {
	// if(sessionConfigType == null){
	// this.sessionConfigType = Arrays.asList(new Integer[]{
	// ParamConst.SESSION_STATUS_BREAKFAST, ParamConst.SESSION_STATUS_LUNCH,
	// ParamConst.SESSION_STATUS_DINNER});
	// }
	// return sessionConfigType;
	// }
	//
	// public void setSessionConfigType(List<RestaurantConfig>
	// restaurantConfigs) {
	// for(RestaurantConfig restaurantConfig : restaurantConfigs){
	// if(restaurantConfig.getParaType().intValue() ==
	// ParamConst.SALE_SESSION_TYPE){
	// if(TextUtils.isEmpty(restaurantConfig.getParaValue1())){
	// this.sessionConfigType = Arrays.asList(new Integer[]{
	// ParamConst.SESSION_STATUS_BREAKFAST, ParamConst.SESSION_STATUS_LUNCH,
	// ParamConst.SESSION_STATUS_DINNER});
	// }else{
	// String[]
	// strArray=restaurantConfig.getParaValue1().split(ParamConst.PARA_VALUE_SPLIT);
	// if(strArray.length == 0){
	// this.sessionConfigType = Arrays.asList(new Integer[]{
	// ParamConst.SESSION_STATUS_BREAKFAST, ParamConst.SESSION_STATUS_LUNCH,
	// ParamConst.SESSION_STATUS_DINNER});
	// }else{
	// Integer[] intArray=new Integer[strArray.length];
	// for(int i=0;i<strArray.length;i++){
	// intArray[i]=Integer.parseInt(strArray[i]);
	// }
	// Comparator comparator=new Comparator<Integer>() {
	//
	// @Override
	// public int compare(Integer arg0, Integer arg1) {
	// return arg0.compareTo(arg1);
	// }
	// };
	//
	// this.sessionConfigType = Arrays.asList(intArray);
	// Collections.sort(this.sessionConfigType, comparator);
	// }
	// }
	//
	//
	// }
	// }
	//
	// }

	// public String getCurrencySymbol() {
	// return currencySymbol;
	// }
	//
	// public void setCurrencySymbol(List<RestaurantConfig> restaurantConfigs) {
	// for(RestaurantConfig restaurantConfig : restaurantConfigs){
	// if(restaurantConfig.getParaType().intValue() ==
	// ParamConst.CURRENCY_TYPE){
	// this.currencySymbol = restaurantConfig.getParaValue1();
	// }
	// }
	// }
	//
	// public int getTaxInclusiveTag() {
	// return taxIncluded;
	// }
	//
	// public void setTaxInclusiveTag(List<RestaurantConfig> restaurantConfigs)
	// {
	// for(RestaurantConfig restaurantConfig : restaurantConfigs){
	// if(restaurantConfig.getParaType().intValue() ==
	// ParamConst.PRICE_TAX_INCLUSIVE){
	// if (!TextUtils.isEmpty(restaurantConfig.getParaValue1()))
	// this.taxIncluded = Integer.valueOf(restaurantConfig.getParaValue1());
	// }
	// }
	// }

	public LocalRestaurantConfig getLocalRestaurantConfig() {
		if (localRestaurantConfig == null) {
			this.localRestaurantConfig = LocalRestaurantConfig.getInstance();
		}
		return localRestaurantConfig;
	}

	public void setLocalRestaurantConfig(
			List<RestaurantConfig> restaurantConfigs) {
		this.localRestaurantConfig = getLocalRestaurantConfig();
		for (RestaurantConfig restaurantConfig : restaurantConfigs) {
			switch (restaurantConfig.getParaType().intValue()) {
			case ParamConst.SALE_SESSION_TYPE:
				this.localRestaurantConfig
						.setSessionConfigType(restaurantConfig);
				break;
			case ParamConst.PRICE_TAX_INCLUSIVE:
				this.localRestaurantConfig.setIncludedTax(restaurantConfig);
				if (CoreData.getInstance().getTaxs() != null)
					this.localRestaurantConfig.getIncludedTax().setTax(
							CoreData.getInstance().getTax(
									Integer.parseInt(restaurantConfig
											.getParaValue1())));
				break;
			case ParamConst.ROUND_RULE_TYPE:
				this.localRestaurantConfig.setRoundType(restaurantConfig);
				break;
			case ParamConst.CURRENCY_TYPE:
				this.localRestaurantConfig.setCurrencySymbol(restaurantConfig);
				break;
			case ParamConst.DEF_DISCOUNT_TYPE:
				this.localRestaurantConfig.setDiscountOption(restaurantConfig);
				break;
			default:
				break;
			}
		}
	}
	
	public void addAlipayPushMessage(AlipayPushMsgDto alipayPushMsgDto){
		if(alipayPushMsgDto == null)
			return;
		this.alipayPush.put(alipayPushMsgDto.getPosBillNo(), alipayPushMsgDto);
	}
	
	public Map<Integer, AlipayPushMsgDto> getAlipayPushMessage(){
		if(this.alipayPush == null)
			return new HashMap<Integer, AlipayPushMsgDto>();
		else
			return this.alipayPush;
	}
	
	public String getPrintOrderNo(int orderId){
		int No = orderId % this.systemSettings.getMaxPrintOrderNo();
		if (No == 0){
			No = 100;
		}
		if(No > 3 && No < 13){
			No += 1;
		}else if ( No > 3 && No < this.systemSettings.getMaxPrintOrderNo()){
			No += 2;
		}
		return No + "";
	}

}
