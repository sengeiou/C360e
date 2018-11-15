package com.alfredwaiter.global;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.alfredbase.BaseActivity;
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
import com.alfredbase.utils.BH;
import com.alfredbase.utils.CommonUtil;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.RxBus;
import com.alfredbase.utils.VibrationUtil;
import com.alfredwaiter.activity.ConnectPOS;
import com.alfredwaiter.activity.EmployeeID;
import com.alfredwaiter.activity.Login;
import com.alfredwaiter.activity.SelectRevenue;
import com.alfredwaiter.activity.Welcome;
import com.alfredwaiter.http.server.WaiterHttpServer;
import com.alfredwaiter.view.WaiterReloginDialog;
import com.moonearly.model.UdpMsg;
import com.moonearly.utils.service.TcpUdpFactory;
import com.moonearly.utils.service.UdpServiceCallBack;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class App extends BaseApplication {

	public static final int VIEW_EVENT_SET_QTY = 10;
	private static final String DATABASE_NAME = "com.alfredwaiter";
	public static App instance;
	private RevenueCenter revenueCenter;


	private int  restaurantId;
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
	private Observable<String> observable1;

	private String currencySymbol = "$";
	public static boolean isleftMoved;
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		SQLExe.init(this, DATABASE_NAME, DATABASE_VERSION);
		update15to16();
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

		observable1 = RxBus.getInstance().register(RxBus.RX_WIFI_STORE);
		observable1.observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
			@Override
			public void call(String object) {
				BaseActivity baseActivity = getTopActivity();
				if(baseActivity instanceof Welcome
						|| baseActivity instanceof ConnectPOS
						|| baseActivity instanceof Login
						|| baseActivity instanceof EmployeeID
						|| baseActivity instanceof SelectRevenue) {
					return;
				}
				if(!object.equals(CommonUtil.getLocalIpAddress())){
					DialogFactory.showOneButtonCompelDialog(getTopActivity(), "Warning", "Your IP has changed.\nPlease ReLogin", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							SyncCentre.getInstance().cancelAllRequests();
							getTopActivity().dismissLoadingDialog();
							Store.remove(getTopActivity(),  Store.WAITER_USER);
							getTopActivity().startActivity(new Intent(getTopActivity(),Welcome.class));

						}
					});
				}
			}
		});
		wifiPolicyNever();
		TcpUdpFactory.startUdpServer(App.UDP_INDEX_WAITER, "Waiter", new UdpServiceCallBack() {
			@Override
			public void callBack(final UdpMsg udpMsg) {
				try {
					if (udpMsg != null) {
						if (udpMsg.getType() == 1) {
							RxBus.getInstance().post(RxBus.RECEIVE_IP_ACTION, udpMsg);
						} else {
							JSONObject jsonObject = new JSONObject(udpMsg.getName());
							RxBus.getInstance().post(jsonObject.getString("RX"), udpMsg.getName());
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
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


	public int getRestaurantId() {
			restaurantId = Store.getInt(this,
					Store.RESTAURANT_ID);

		return restaurantId;
	}

	public void setRestaurantId(int restaurantId) {
		this.restaurantId = restaurantId;
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

	public void setCurrencySymbol(String currencySymbol, boolean isDouble) {
		this.currencySymbol = currencySymbol;
		BH.initFormart(isDouble);
	}
	
}
