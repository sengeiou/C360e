package com.alfredwaiter.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.ParamConst;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.model.MainPosInfo;
import com.alfredbase.javabean.model.WaiterDevice;
import com.alfredbase.store.Store;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.ScreenSizeUtil;
import com.alfredbase.utils.TextTypeFace;
import com.alfredbase.view.SlipButton;
import com.alfredwaiter.R;
import com.alfredwaiter.global.App;
import com.alfredwaiter.global.SyncCentre;
import com.alfredwaiter.global.UIHelp;
import com.alfredwaiter.view.WaiterReloginDialog;

public class Setting extends BaseActivity {
	public static final int HANDLER_LOGOUT_SUCCESS = 1;
	public static final int HANDLER_LOGOUT_FAILED = 2;

	private LinearLayout ll_setting_content;
	private SlipButton sb_kot_notification;
	private SlipButton sb_zone_notification;
	private TextView tv_version;
	private WaiterReloginDialog waiterReloginDialog;

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HANDLER_LOGOUT_SUCCESS:
				User user = App.instance.getUser();
				if (user != null) {
					Store.remove(context, Store.WAITER_USER);
				}
				App.instance.popAllActivityExceptOne(EmployeeID.class);
				UIHelp.startEmployeeID(context);
				break;
			case HANDLER_LOGOUT_FAILED:
				UIHelp.showToast(context, ResultCode.getErrorResultStr(context, (Throwable)msg.obj, 
						context.getResources().getString(R.string.revenue_center)));
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	protected void initView() {
		super.initView();
		ScreenSizeUtil.initScreenScale(context);
		setContentView(R.layout.activity_setting);
		init();
		setData();
	}
	
	private HashMap<String, Object> getIntentData() {
		Intent intent = getIntent();
		HashMap<String, Object> map = (HashMap<String, Object>) intent.getSerializableExtra("attrMap");
		return map;
	}
	
	private void init() {
		initTextTypeFace();
		ll_setting_content = (LinearLayout) findViewById(R.id.ll_setting_content);
//		sb_kot_notification = (SlipButton) findViewById(R.id.sb_kot_notification);
//		sb_zone_notification = (SlipButton) findViewById(R.id.sb_zone_notification);
		tv_version = (TextView) findViewById(R.id.tv_version);
		
		findViewById(R.id.tv_order_history).setOnClickListener(this);
		findViewById(R.id.tv_bump_mob_app).setOnClickListener(this);
		findViewById(R.id.tv_connect_pos).setOnClickListener(this);
		findViewById(R.id.tv_data_sync).setOnClickListener(this);
		findViewById(R.id.tv_reset).setOnClickListener(this);
		findViewById(R.id.iv_back).setOnClickListener(this);
		findViewById(R.id.tv_logout).setOnClickListener(this);
		findViewById(R.id.tv_clock).setOnClickListener(this);
		//		sb_kot_notification.SetOnChangedListener(new SlipButtonChangeListener() {
//			
//			@Override
//			public void OnChanged(boolean CheckState) {
//				if (CheckState) {
//					//TODO
//				}else {
//					//TODO
//				}
//			}
//		});
//		sb_zone_notification.SetOnChangedListener(new SlipButtonChangeListener() {
//			
//			@Override
//			public void OnChanged(boolean CheckState) {
//				if (CheckState) {
//					//TODO
//				}else {
//					//TODO
//				}
//			}
//		});
	}

	private void setData() {
		ll_setting_content.setVisibility((Integer)getIntentData().get("visibilityMap"));
		tv_version.setText(App.instance.VERSION);
	}
	
	@Override
	public void handlerClickEvent(View v) {
		super.handlerClickEvent(v);
		switch (v.getId()) {
		case R.id.iv_back: 
			finish();			break;
		case R.id.tv_order_history:
			//TODO
			break;
		case R.id.tv_bump_mob_app:
			//TODO
			break;
		case R.id.tv_connect_pos:
			//TODO
			break;
		case R.id.tv_data_sync:
			//TODO
			break;
		case R.id.tv_reset:
			DialogFactory.commonTwoBtnDialog(context, 
					context.getResources().getString(R.string.system_reset), 
					context.getResources().getString(R.string.clean_data),
					context.getResources().getString(R.string.no), 
					context.getResources().getString(R.string.yes), null, 
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								App.instance.finishAllActivityExceptOne(Setting.class);
								App.instance.getTopActivity().finish();
								User user = Store.getObject(context, Store.WAITER_USER, User.class);
								if (user != null) {
									Store.remove(context, Store.WAITER_USER);
								}
								MainPosInfo mainPosInfo = App.instance.getMainPosInfo();
								if (mainPosInfo !=null) {
									Store.remove(context, Store.MAINPOSINFO);
								}
								UIHelp.startConnectPOS(context);
							}
						});			
			break;
		case R.id.tv_logout:
			DialogFactory.commonTwoBtnDialog(context, 
					context.getResources().getString(R.string.logout_title), 
					context.getResources().getString(R.string.logout_content),
					context.getResources().getString(R.string.no), 
					context.getResources().getString(R.string.yes), null, 
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								WaiterDevice waiterDevice = App.instance.getWaiterdev();
								if (waiterDevice == null) {
									return ;
								}
								Map<String, Object> parameters = new HashMap<String, Object>();
								parameters.put("device", waiterDevice);
								parameters.put("deviceType",ParamConst.DEVICE_TYPE_WAITER);
								SyncCentre.getInstance().logout(context, parameters, handler);
							}
						});				
			break;
			case R.id.tv_clock:
				waiterReloginDialog = new WaiterReloginDialog(this, false);
				waiterReloginDialog.show();
				break;
		default:
			break;
		}
	}
	
	//改变字体
	private void initTextTypeFace() {
		TextTypeFace textTypeFace = TextTypeFace.getInstance();
		textTypeFace.setTrajanProBlod((TextView)findViewById(R.id.tv_setting));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_order_history));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_bump_mob_app));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_connect_pos));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_data_sync));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_kot_notification));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_zone_notification));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_reset));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_clock));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_version_name));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_version));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_logout));
	}
}
