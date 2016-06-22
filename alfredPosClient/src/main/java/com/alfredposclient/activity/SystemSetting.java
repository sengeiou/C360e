package com.alfredposclient.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.LoadingDialog;
import com.alfredbase.ParamConst;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.model.PushMessage;
import com.alfredbase.javabean.model.SessionStatus;
import com.alfredbase.store.Store;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.TextTypeFace;
import com.alfredbase.view.ChangePasswordDialog;
import com.alfredbase.view.SlipButton;
import com.alfredbase.view.SlipButton.OnChangedListener;
import com.alfredposclient.R;
import com.alfredposclient.global.App;
import com.alfredposclient.global.SyncCentre;
import com.alfredposclient.global.SystemSettings;
import com.alfredposclient.global.UIHelp;

public class SystemSetting extends BaseActivity implements OnChangedListener,OnClickListener{

	private ImageView iv_sync_data;
	private TextView tv_syncdata_warn;
	private SlipButton sb_kot_print;
	private SlipButton sb_kot_print_together;
	private SlipButton sb_kot_double_print;
	private SlipButton sb_double_print_bill;
	private SlipButton sb_double_close_bill_print;
	private SlipButton sb_order_summary_print;
	private SystemSettings settings;
	private LoadingDialog loadingDialog;
	private int size = 0;
	private Map<String, Integer> syncMap;
	private TextTypeFace textTypeFace;
	private boolean refreshSession = false;
	private ChangePasswordDialog changePasswordDialog;

	public static final int UPDATE_PASSWORD_TAG = 2000;
	 
	
	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_system_setting);
		syncMap = App.instance.getPushMsgMap();
		settings = new SystemSettings(context);
		loadingDialog = new LoadingDialog(context);
		loadingDialog.setTitle(context.getResources().getString(R.string.loading));
		iv_sync_data = (ImageView) findViewById(R.id.iv_sync_data);
		tv_syncdata_warn = (TextView) findViewById(R.id.tv_syncdata_warn);
		sb_kot_print = (SlipButton) findViewById(R.id.sb_kot_print);
		sb_kot_print_together = (SlipButton) findViewById(R.id.sb_kot_print_together);
		sb_kot_double_print = (SlipButton) findViewById(R.id.sb_kot_double_print);
		
		sb_double_print_bill = (SlipButton) findViewById(R.id.sb_double_print_bill);
		sb_double_close_bill_print = (SlipButton)findViewById(R.id.sb_double_close_bill_print);
		sb_order_summary_print = (SlipButton)findViewById(R.id.sb_order_summary_print);
		
		if (syncMap.isEmpty()) {
			tv_syncdata_warn.setText(context.getResources().getString(R.string.no_update));
			tv_syncdata_warn.setVisibility(View.GONE);
		} else {
			tv_syncdata_warn.setText(context.getResources().getString(R.string.receive) + 
					syncMap.size() + context.getResources().getString(R.string._message));
			tv_syncdata_warn.setTextColor(Color.RED);
		}
		iv_sync_data.setOnClickListener(this);
		findViewById(R.id.iv_back).setOnClickListener(this);
		sb_kot_print.setOnChangedListener(this);
		sb_kot_print_together.setOnChangedListener(this);
		sb_kot_double_print.setOnChangedListener(this);
		sb_double_print_bill.setOnChangedListener(this);
		sb_double_close_bill_print.setOnChangedListener(this);
		sb_order_summary_print.setOnChangedListener(this);
		findViewById(R.id.ll_set_pwd).setOnClickListener(this);
		if(App.instance.isRevenueKiosk()){
			findViewById(R.id.rl_order_summary).setVisibility(View.GONE);
		}
		((TextView)findViewById(R.id.tv_app_version)).setText(context.getResources().getString(R.string.version) + App.instance.VERSION);
		
		initViewData();
		initTextTypeFace();
	}

	private void initViewData() {
		if (App.instance.isKotPrint()) {
			sb_kot_print.setChecked(true);
		}else {
			sb_kot_print.setChecked(false);
		}
		if (settings.isKotPrintTogether()) {
			sb_kot_print_together.setChecked(true);
		} else {
			sb_kot_print_together.setChecked(false);
		}
		if (settings.isKotDoublePrint()) {
			sb_kot_double_print.setChecked(true);
		} else {
			sb_kot_double_print.setChecked(false);
		}
		if (settings.isDoubleBillPrint()) {
			sb_double_print_bill.setChecked(true);
		} else {
			sb_double_print_bill.setChecked(false);
		}
		if (settings.isDoubleReceiptPrint()) {
			sb_double_close_bill_print.setChecked(true);
		} else {
			sb_double_close_bill_print.setChecked(false);
		}
		if (settings.isOrderSummaryPrint()) {
			sb_order_summary_print.setChecked(true);
		} else {
			sb_order_summary_print.setChecked(false);
		}
		changePasswordDialog = new ChangePasswordDialog(context, handler);
	}

	@Override
	public void handlerClickEvent(View v) {
		super.handlerClickEvent(v);
		switch (v.getId()) {
		case R.id.iv_sync_data:
			
			SessionStatus sst= App.instance.getSessionStatus();
			if (sst == null) {
				if(syncMap != null && !syncMap.isEmpty()){
					syncDataAction(syncMap);
				} else {
					DialogFactory.commonTwoBtnDialog(
							SystemSetting.this,
							SystemSetting.this.getResources().getString(
									R.string.warning),
							SystemSetting.this.getResources().getString(
									R.string.manually_update),
							SystemSetting.this.getResources().getString(
									R.string.no), SystemSetting.this
									.getResources().getString(R.string.yes), null,
							new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									Map<String, Integer> map = new HashMap<String, Integer>();
									map.put(PushMessage.HAPPY_HOURS, 1);
									map.put(PushMessage.PRINTER, 1);
									map.put(PushMessage.ITEM, 1);
									map.put(PushMessage.MODIFIER, 1);
									map.put(PushMessage.USER, 1);
									map.put(PushMessage.PLACE_TABLE, 1);
									map.put(PushMessage.TAX, 1);
									syncDataAction(map);
								}
							});
				}
			}else { 
				UIHelp.showToast(context,context.getResources().getString(R.string.no_sync_date));
			}
			Intent intent = getIntent();
			intent.putExtra("refreshSession", refreshSession);
			setResult(RESULT_OK, intent);
			break;
		case R.id.iv_back:
			this.finish();
			break;
		case R.id.ll_set_pwd:
			changePasswordDialog.show();
			break;
		default:
			break;
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ResultCode.SUCCESS:
				size = size -1;
				if (size == 0) {
					loadingDialog.dismiss();
					Store.remove(context,  Store.PUSH_MESSAGE);
					App.instance.setPushMsgMap(new HashMap<String, Integer>());
					tv_syncdata_warn.setText(getResources().getString(R.string.update_successful));
				} 
				break;
			case ResultCode.CONNECTION_FAILED:
					loadingDialog.dismiss();
					UIHelp.showToast(context, ResultCode.getErrorResultStr(context, (Throwable) msg.obj, 
							context.getResources().getString(R.string.server)));
					size = 0;
				break;
			case ChangePasswordDialog.UPDATE_PASSWORD_ACTION:
				changePasswordDialog.dismiss();
				loadingDialog.show();
				Map<String, Object> map = (Map<String, Object>) msg.obj;
				User user = (User) map.get("user");
				String newPassword = (String) map.get("newPassword");
				SyncCentre.getInstance().updatePassword(context, map, handler);
				break;
			case UPDATE_PASSWORD_TAG:{
				loadingDialog.dismiss();
				switch ((Integer)msg.obj) {
				case ResultCode.SUCCESS:
					UIHelp.showShortToast(context, context.getResources().getString(R.string.password_update_successfully));
					break;
				case ResultCode.USER_EMPTY:
					UIHelp.showShortToast(context, context.getResources().getString(R.string.employee_no_exist));
					break;
				default:
					break;
				}
				break;
			}
			default:
				break;
			}
		};
	};
	
	private void syncDataAction(Map<String, Integer> syncMap) {
		loadingDialog.show();
		for (String key : syncMap.keySet()) {
			size = size + 1;
			SyncCentre.getInstance().pushCommonData(context, key, handler);
			if(key.equals(PushMessage.REST_CONFIG)){
				refreshSession = true;
			}
		}
	}

	@Override
	public void OnChanged(SlipButton slipButton, boolean checkState) {
		switch (slipButton.getId()) {
		case R.id.sb_kot_print:
			if (checkState) {
				sb_kot_print.setChecked(true);
				Store.putBoolean(context, Store.KOT_PRINT, true);
			}else {
				sb_kot_print.setChecked(false);
				Store.putBoolean(context, Store.KOT_PRINT, false);
			}
			break;
		case R.id.sb_kot_print_together:
			if (checkState) {
				sb_kot_print_together.setChecked(true);
				settings.setKotPrintTogether(ParamConst.DEFAULT_TRUE);
			} else {
				sb_kot_print_together.setChecked(false);
				settings.setKotPrintTogether(ParamConst.DEFAULT_FALSE);
			}
			break;
		case R.id.sb_kot_double_print:
			if (checkState) {
				sb_kot_double_print.setChecked(true);
				settings.setKotDoublePrint(ParamConst.DEFAULT_TRUE);
			} else {
				sb_kot_double_print.setChecked(false);
				settings.setKotDoublePrint(ParamConst.DEFAULT_FALSE);
			}
			break;			
			
		case R.id.sb_double_print_bill:
			if (checkState) {
				sb_double_print_bill.setChecked(true);
				settings.setDoubleBillPrint(ParamConst.DEFAULT_TRUE);
			} else {
				sb_double_print_bill.setChecked(false);
				settings.setDoubleBillPrint(ParamConst.DEFAULT_FALSE);
			}
			break;
		case R.id.sb_double_close_bill_print:
			if (checkState) {
				sb_double_close_bill_print.setChecked(true);
				settings.setDoubleReceiptPrint(ParamConst.DEFAULT_TRUE);
			} else {
				sb_double_close_bill_print.setChecked(false);
				settings.setDoubleReceiptPrint(ParamConst.DEFAULT_FALSE);
			}
			break;
		case R.id.sb_order_summary_print:
			if (checkState) {
				sb_order_summary_print.setChecked(true);
				settings.setOrderSummaryPrint(ParamConst.ORDER_SUMMARY_PRINT_TRUE);
			} else {
				sb_order_summary_print.setChecked(false);
				settings.setOrderSummaryPrint(ParamConst.ORDER_SUMMARY_PRINT_FALSE);
			}
			break;
		default:
			break;
		}
	}
	
	private void initTextTypeFace() {
		textTypeFace = TextTypeFace.getInstance();
		textTypeFace.setTrajanProBlod((TextView)findViewById(R.id.tv_setting));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_kot_print));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_app_version));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_kot_print_together));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_kot_double_print));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_double_print_bill));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_double_close_bill_print));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_order_summary_print));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_change_pwd));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_sync_data));
		textTypeFace.setTrajanProRegular(tv_syncdata_warn);
	}
}
