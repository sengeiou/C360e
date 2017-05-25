package com.alfredwaiter.global;

import android.text.TextUtils;

import com.alfredbase.BaseApplication;
import com.alfredbase.UnCEHandler;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.model.MainPosInfo;
import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.javabean.model.SessionStatus;
import com.alfredbase.javabean.model.WaiterDevice;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.Store;
import com.alfredbase.utils.RxBus;
import com.alfredbase.utils.VibrationUtil;
import com.alfredwaiter.activity.Welcome;
import com.alfredwaiter.http.server.WaiterHttpServer;
import com.alfredwaiter.view.WaiterReloginDialog;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class App extends BaseApplication {

	public static final int VIEW_EVENT_SET_QTY = 10;
	private static final int DATABASE_VERSION = 14;
	private static final String DATABASE_NAME = "com.alfredwaiter";
	public static App instance;
	private RevenueCenter revenueCenter;
	private User user;
	private WaiterDevice waiterdev;
	private MainPosInfo mainPosInfo;
	private WaiterHttpServer httpServer = null;
	private String pairingIp;
	private int kotNotificationQty = 0;
	private SessionStatus sessionStatus;
	private String fromTableName;
	private String toTableName;
	public  String VERSION = "1.0.0";
	private Map<Integer, PrinterDevice> printerDevices = new ConcurrentHashMap<Integer, PrinterDevice>();
	private Observable<Object> observable;
	
	private String currencySymbol = "$";
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		SQLExe.init(this, DATABASE_NAME, DATABASE_VERSION);
		//HTTPServer.start();
        VERSION = getAppVersionName();
		httpServer = new WaiterHttpServer();
		startHttpServer();
		UnCEHandler catchExcep = new UnCEHandler(this, Welcome.class);
        Thread.setDefaultUncaughtExceptionHandler(catchExcep);
		CrashReport.initCrashReport(getApplicationContext(), "900042909", isOpenLog);

		observable = RxBus.getInstance().register("showRelogin");
		observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Object>() {
			@Override
			public void call(Object object) {
				if (getUser() != null && !TextUtils.isEmpty(Store.getString(instance, Store.EMPLOYEE_ID))){
					boolean flag = Store.getBoolean(getTopActivity(), Store.WAITER_SET_LOCK, true);
					if (flag){
						WaiterReloginDialog reloginDialog = new WaiterReloginDialog(getTopActivity(), true);
						reloginDialog.show();
					}
				}else {
					return;
				}
			}
		});
	}
    
	public void startHttpServer() {
		try {
			if (!httpServer.isAlive())
				this.httpServer.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void stopHttpServer(){
		if (this.httpServer!=null)
			this.httpServer.stop();
	}
	public RevenueCenter getRevenueCenter() {
		if (revenueCenter == null)
			revenueCenter = Store.getObject(this,
					Store.CURRENT_REVENUE_CENTER, RevenueCenter.class);
		return revenueCenter;
	}

	public void setRevenueCenter(RevenueCenter revenueCenter) {
		this.revenueCenter = revenueCenter;
	}

	public User getUser() {
		if (user == null) {
			user = Store.getObject(App.instance, Store.WAITER_USER, User.class);
		}
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		Store.saveObject(App.instance, Store.WAITER_USER, user);
	}

	@Override
	protected void onIPChanged() {
		super.onIPChanged();
		
	}

	public Map<Integer, PrinterDevice> getPrinterDevices() {
		return this.printerDevices;
	}

	public void setPrinterDevices(Map<Integer, PrinterDevice> printerDevices) {
		this.printerDevices = printerDevices;
	}

	@Override
	protected void onNetworkConnectionUpdate () {
		super.onNetworkConnectionUpdate();
		
	}

	public WaiterDevice getWaiterdev() {
		if(waiterdev == null){
			waiterdev = Store.getObject(this, Store.WAITER_DEVICE, WaiterDevice.class);
		}
		return waiterdev;
	}

	public void setWaiterdev(WaiterDevice waiterdev) {
		this.waiterdev = waiterdev;
		Store.saveObject(this, Store.WAITER_DEVICE,waiterdev);
	}	
	
	public MainPosInfo getMainPosInfo() {
		if(mainPosInfo == null){
			mainPosInfo = Store.getObject(this.getApplicationContext(), Store.MAINPOSINFO, MainPosInfo.class);
		}
		return mainPosInfo;
	}

	public void setMainPosInfo(MainPosInfo mainPosInfo) {
		this.mainPosInfo = mainPosInfo;
		Store.saveObject(this.getApplicationContext(),Store.MAINPOSINFO, mainPosInfo);
	}
	
	public String getPairingIp() {
		return pairingIp;
	}

	public void setPairingIp(String pairingIp) {
		this.pairingIp = pairingIp;
	}
	

	public int getKotNotificationQty() {
		return kotNotificationQty;
	}

	public void setKotNotificationQty(int kotNotificationQty) {
		this.kotNotificationQty = kotNotificationQty;
	}

	public SessionStatus getSessionStatus() {
		if(sessionStatus == null){
			sessionStatus = Store.getObject(instance, Store.SESSION_STATUS, SessionStatus.class);
		}
		return sessionStatus;
	}

	public void setSessionStatus(SessionStatus sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

	public String getFromTableName() {
		return fromTableName;
	}

	public void setFromTableName(String fromTableName) {
		this.fromTableName = fromTableName;
	}

	public String getToTableName() {
		return toTableName;
	}

	public void setToTableName(String toTableName) {
		this.toTableName = toTableName;
	}
	public void playVibration() {
		VibrationUtil.init(this);
		VibrationUtil.playVibratorOnce();		
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}
	
}
