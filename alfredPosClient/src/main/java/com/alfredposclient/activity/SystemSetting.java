package com.alfredposclient.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.alfredposclient.global.JavaConnectJS;
import com.alfredposclient.global.SyncCentre;
import com.alfredposclient.global.SystemSettings;
import com.alfredposclient.global.UIHelp;
import com.alfredposclient.popupwindow.SelectPrintWindow;
import com.alfredposclient.popupwindow.SetPAXWindow;
import com.alfredposclient.view.ColorPickerDialog;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemSetting extends BaseActivity implements OnChangedListener,OnClickListener{

	private ImageView iv_sync_data;
	private TextView tv_syncdata_warn;
	private SlipButton sb_kot_print;
	private SlipButton sb_kot_print_together;
	private SlipButton sb_kot_double_print;
	private SlipButton sb_double_print_bill;
	private SlipButton sb_double_close_bill_print;
	private SlipButton sb_order_summary_print;
	private SlipButton sb_session_report_print;
	private SlipButton sb_plu_category;
	private SlipButton sb_plu_item;
	private SlipButton sb_plu_modifier;
	private SlipButton sb_hourly_payment;
	private SlipButton sb_print_before_close;
	private SlipButton sb_auto_receive_app;
	private SlipButton sb_cash_close_print;
	private SlipButton sb_top_masking_use;
	private SlipButton sb_top_screen_lock;
	private SlipButton sb_cancel_order_void;
	private SlipButton sb_transfer_print;
	private SlipButton sb_auto_table;
	private SystemSettings settings;
	private LoadingDialog loadingDialog;
	private int size = 0;
	private Map<String, Integer> syncMap;
	private TextTypeFace textTypeFace;
	private boolean refreshSession = false;
	private ChangePasswordDialog changePasswordDialog;
	private SelectPrintWindow selectPrintWindow;
	public static final int UPDATE_PASSWORD_TAG = 2000;
	public static final int SET_MAX_ORDER_NO = 2001;
	private TextView tv_callnum;
	private ColorPickerDialog colorPickerDialog;
	private SetPAXWindow setPAXWindow;
	private LinearLayout ll_max_order_no;
	private TextView tv_max_order_no;
	private int maxOrderNo;
//sb_plu_category
	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_system_setting);
		if(App.instance.isRevenueKiosk()){
			findViewById(R.id.ll_app_order).setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.ll_app_order).setVisibility(View.GONE);
		}
		syncMap = App.instance.getPushMsgMap();
		settings = new SystemSettings(context);
		loadingDialog = new LoadingDialog(context);
		loadingDialog.setTitle(context.getResources().getString(R.string.loading));
		iv_sync_data = (ImageView) findViewById(R.id.iv_sync_data);
		selectPrintWindow = new SelectPrintWindow(context, findViewById(R.id.rl_root),handler);
		tv_syncdata_warn = (TextView) findViewById(R.id.tv_syncdata_warn);
		sb_kot_print = (SlipButton) findViewById(R.id.sb_kot_print);
		sb_kot_print_together = (SlipButton) findViewById(R.id.sb_kot_print_together);
		sb_kot_double_print = (SlipButton) findViewById(R.id.sb_kot_double_print);
		tv_callnum = (TextView)findViewById(R.id.tv_callnum);
		sb_double_print_bill = (SlipButton) findViewById(R.id.sb_double_print_bill);
		sb_double_close_bill_print = (SlipButton)findViewById(R.id.sb_double_close_bill_print);
		sb_order_summary_print = (SlipButton)findViewById(R.id.sb_order_summary_print);
		sb_session_report_print = (SlipButton)findViewById(R.id.sb_session_report_print);
		sb_plu_category = (SlipButton)findViewById(R.id.sb_plu_category);
		sb_plu_item = (SlipButton)findViewById(R.id.sb_plu_item);
		sb_plu_modifier = (SlipButton)findViewById(R.id.sb_plu_modifier);
		sb_hourly_payment = (SlipButton)findViewById(R.id.sb_hourly_payment);
		sb_print_before_close = (SlipButton)findViewById(R.id.sb_print_before_close);
		sb_cash_close_print = (SlipButton)findViewById(R.id.sb_cash_close_print);
		sb_auto_receive_app = (SlipButton)findViewById(R.id.sb_auto_receive_app);
		sb_top_masking_use = (SlipButton)findViewById(R.id.sb_top_masking_use);
		sb_top_screen_lock = (SlipButton)findViewById(R.id.sb_top_screen_lock);
		sb_cancel_order_void = (SlipButton)findViewById(R.id.sb_cancel_order_void);
		sb_transfer_print = (SlipButton)findViewById(R.id.sb_transfer_print);
		sb_auto_table = (SlipButton)findViewById(R.id.sb_auto_table);

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
		sb_session_report_print.setOnChangedListener(this);
		sb_plu_category.setOnChangedListener(this);
		sb_plu_item.setOnChangedListener(this);
		sb_plu_modifier.setOnChangedListener(this);
		sb_hourly_payment.setOnChangedListener(this);
		sb_print_before_close.setOnChangedListener(this);
		sb_cash_close_print.setOnChangedListener(this);
		sb_auto_receive_app.setOnChangedListener(this);
		sb_top_masking_use.setOnChangedListener(this);
		sb_top_screen_lock.setOnChangedListener(this);
		sb_cancel_order_void.setOnChangedListener(this);
		sb_transfer_print.setOnChangedListener(this);
		sb_auto_table.setOnChangedListener(this);

		findViewById(R.id.ll_set_callnum).setOnClickListener(this);
		findViewById(R.id.ll_set_pwd).setOnClickListener(this);
		findViewById(R.id.ll_set_lock_time).setOnClickListener(this);
		findViewById(R.id.ll_set_color).setOnClickListener(this);
		if(App.instance.isRevenueKiosk()){
			findViewById(R.id.rl_order_summary).setVisibility(View.GONE);
			findViewById(R.id.view_order_summary_print).setVisibility(View.GONE);
		}
		((TextView)findViewById(R.id.tv_app_version)).setText(context.getResources().getString(R.string.version) + App.instance.VERSION);
		
		initViewData();
		initTextTypeFace();
		setPAXWindow = new SetPAXWindow(context, findViewById(R.id.rl_root),
				handler);
		tv_max_order_no = (TextView) findViewById(R.id.tv_max_order_no);
		maxOrderNo = Store.getInt(context, Store.MAX_ORDER_NO, 0);
		refreshMaxOrderNo();
		ll_max_order_no = (LinearLayout) findViewById(R.id.ll_max_order_no);
		ll_max_order_no.setOnClickListener(this);
	}

	private void refreshMaxOrderNo(){
		if(maxOrderNo > 0){
			tv_max_order_no.setText(maxOrderNo + "");
		}else{
			tv_max_order_no.setText("");
		}
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
		if(settings.isPrintWhenCloseSession()){
			sb_session_report_print.setChecked(true);
		}else{
			sb_session_report_print.setChecked(false);
		}
		if(settings.isPrintPluCategory()){
			sb_plu_category.setChecked(true);
		}else{
			sb_plu_category.setChecked(false);
		}
		if(settings.isPrintPluItem()){
			sb_plu_item.setChecked(true);
		}else{
			sb_plu_item.setChecked(false);
		}
		if(settings.isPrintPluModifier()){
			sb_plu_modifier.setChecked(true);
		}else{
			sb_plu_modifier.setChecked(false);
		}
		if(settings.isPrintHourlyPayment()){
			sb_hourly_payment.setChecked(true);
		}else{
			sb_hourly_payment.setChecked(false);
		}
		if(settings.isPrintBeforCloseBill()){
			sb_print_before_close.setChecked(true);
		}else{
			sb_print_before_close.setChecked(false);
		}
		if(settings.isCashClosePrint()){
			sb_cash_close_print.setChecked(true);
		}else{
			sb_cash_close_print.setChecked(false);
		}
		if(settings.isAutoRecevingOnlineOrder()){
			sb_auto_receive_app.setChecked(true);
		}else{
			sb_auto_receive_app.setChecked(false);
		}
		if(settings.isTopMaskingIsUser()){
			sb_top_masking_use.setChecked(true);
		}else{
			sb_top_masking_use.setChecked(false);
		}
		if(settings.isScreenLock()){
			sb_top_screen_lock.setChecked(true);
		}else{
			sb_top_screen_lock.setChecked(false);
		}
		if(settings.isRemoveToVoid()){
			sb_cancel_order_void.setChecked(true);
		}else{
			sb_cancel_order_void.setChecked(false);
		}
		if(settings.isTransferPrint()){
			sb_transfer_print.setChecked(true);
		}else{
			sb_transfer_print.setChecked(false);
		}
		if(settings.isAutoToTable()){
			sb_auto_table.setChecked(true);
		}else{
			sb_auto_table.setChecked(false);
		}
		if(TextUtils.isEmpty(App.instance.getCallAppIp())){
			tv_callnum.setText(null);
		}else{
			tv_callnum.setText(App.instance.getCallAppIp());
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
//									map.put(PushMessage.PLACE_TABLE, 1);
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
		case R.id.ll_set_lock_time:
			int time = Store.getInt(App.instance, Store.RELOGIN_TIME);
			int hour = 0;
			int min = 0;
			int second = 0;
			if(time > 0){
				hour = time/(60*60*1000);
				min = time/(60*1000)%60;
				second = (time/1000)%(60*60)%60;
			}
			final List<Object> options1Items = new ArrayList<>();
			for(int i = 0; i <= 12;i++){
				if(i < 10){
					options1Items.add("0"+i);
				}else{
					options1Items.add(""+i);
				}
			}
			final List<Object> mins = new ArrayList<>();
			for(int i = 0; i < 60; i++){
				if(i < 10){
					mins.add("0"+i);
				}else{
					mins.add(""+i);
				}
			}
			OptionsPickerView<Object> optionsPickerBuilder = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
				@Override
				public void onOptionsSelect(int options1, int options2, int options3, View v) {
					String hourStr = (String) options1Items.get(options1);
					String minStr = (String) mins.get(options2);
					String secondStr = (String) mins.get(options3);
					int h = Integer.parseInt(hourStr);
					int m = Integer.parseInt(minStr);
					int s = Integer.parseInt(secondStr);
					if(h == 0 && m == 0 && s < 30){
						DialogFactory.showOneButtonCompelDialog(SystemSetting.this, getResources().getString(R.string.warning), "time must >= 30 second", null);
						return;
					}
					int time = s * 1000
							+ m * 60 *1000
							+ h * 60 * 60 * 1000;
					Store.putInt(App.instance, Store.RELOGIN_TIME, time);
					App.instance.setTime(time);
				}
			}).setTitleText("set time")
			.setTitleSize(30)
			.setContentTextSize(25)//设置滚轮文字大小
			.setDividerColor(Color.BLACK)//设置分割线的颜色
			.setBgColor(Color.WHITE)
			.setTitleBgColor(getResources().getColor(R.color.bg_gray_line))
			.setTitleColor(Color.BLACK)
			.setCancelColor(Color.BLACK)
			.setSubmitColor(Color.BLACK)
			.setSubmitText(getResources().getString(R.string.ok))
			.setCancelText(getResources().getString(R.string.cancel))
			.setLineSpacingMultiplier(1.5f)
			.setTextColorCenter(Color.TRANSPARENT)
			.setLabels("hour", "minute", "second")
			.isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
			.isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
			.setBackgroundId(getResources().getColor(R.color.white)) //设置外部遮罩颜色
			.build();
			optionsPickerBuilder.setNPicker(options1Items, mins, mins);
			optionsPickerBuilder.setSelectOptions(hour,min,second);
			optionsPickerBuilder.show();
			break;
		case R.id.ll_set_callnum:
			selectPrintWindow.show("");
			break;
		case R.id.ll_set_color:
			colorPickerDialog = new ColorPickerDialog(SystemSetting.this, Store.getInt(SystemSetting.this, Store.COLOR_PICKER, Color.WHITE), getString(R.string.color_select), new ColorPickerDialog.OnColorChangedListener() {
				@Override
				public void colorChanged(int color) {
					Store.putInt(SystemSetting.this, Store.COLOR_PICKER, color);
				}
			});
			colorPickerDialog.show();
			break;
		case R.id.ll_max_order_no:
			String maxOrderStr = tv_max_order_no.getText().toString();

			if(TextUtils.isEmpty(maxOrderStr)){
				setPAXWindow.show(SetPAXWindow.MAX_ORDER_NO, "0", "Max Order No");
			}else{
				setPAXWindow.show(SetPAXWindow.MAX_ORDER_NO, maxOrderStr, "Max Order No");
			}

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
			}
				break;
			case SET_MAX_ORDER_NO:
				String maxOrderNoStr = (String) msg.obj;
				if(!TextUtils.isEmpty(maxOrderNoStr)){
					maxOrderNo = Integer.parseInt(maxOrderNoStr);
					Store.putInt(context, Store.MAX_ORDER_NO,maxOrderNo);
				}
				refreshMaxOrderNo();
				break;

			case JavaConnectJS.ACTION_ASSIGN_PRINTER_DEVICE:
				String ip = (String)msg.obj;
				App.instance.setCallAppIp(ip);
				tv_callnum.setText(ip);
				break;
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
	protected void onDestroy() {
		super.onDestroy();
		if (colorPickerDialog != null && colorPickerDialog.isShowing()) {
			colorPickerDialog.dismiss();
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
		case R.id.sb_session_report_print:
			if(checkState){
				sb_session_report_print.setChecked(true);
				settings.setPrintWhenCloseSession(ParamConst.DEFAULT_TRUE);
			}else{
				sb_session_report_print.setChecked(false);
				settings.setPrintWhenCloseSession(ParamConst.DEFAULT_FALSE);
			}
			break;
		case R.id.sb_plu_category:
			if(checkState){
				sb_plu_category.setChecked(true);
				settings.setPrintPluCategory(ParamConst.DEFAULT_TRUE);
			}else{
				sb_plu_category.setChecked(false);
				settings.setPrintPluCategory(ParamConst.DEFAULT_FALSE);
			}
			break;
		case R.id.sb_plu_item:
			if(checkState){
				sb_plu_item.setChecked(true);
				settings.setPrintPluItem(ParamConst.DEFAULT_TRUE);
			}else{
				sb_plu_item.setChecked(false);
				settings.setPrintPluItem(ParamConst.DEFAULT_FALSE);
			}
			break;
		case R.id.sb_plu_modifier:
			if(checkState){
				sb_plu_modifier.setChecked(true);
				settings.setPrintPluModifier(ParamConst.DEFAULT_TRUE);
			}else{
				sb_plu_modifier.setChecked(false);
				settings.setPrintPluModifier(ParamConst.DEFAULT_FALSE);
			}
			break;
		case R.id.sb_hourly_payment:
			if(checkState){
				sb_hourly_payment.setChecked(true);
				settings.setPrintHourlyPayment(ParamConst.DEFAULT_TRUE);
			}else{
				sb_hourly_payment.setChecked(false);
				settings.setPrintHourlyPayment(ParamConst.DEFAULT_FALSE);
			}
			break;
		case R.id.sb_print_before_close:
			if(checkState){
				sb_print_before_close.setChecked(true);
				settings.setPrintBeforCloseBill(ParamConst.DEFAULT_TRUE);
			}else{
				sb_print_before_close.setChecked(false);
				settings.setPrintBeforCloseBill(ParamConst.DEFAULT_FALSE);
			}
			break;
		case R.id.sb_cash_close_print:
			if(checkState){
				sb_cash_close_print.setChecked(true);
				settings.setCashClosePrint(ParamConst.DEFAULT_TRUE);
			}else{
				sb_cash_close_print.setChecked(false);
				settings.setCashClosePrint(ParamConst.DEFAULT_FALSE);
			}
			break;
		case R.id.sb_auto_receive_app:
			if(checkState){
				sb_auto_receive_app.setChecked(true);
				settings.setAutoRecevingOnlineOrder(ParamConst.DEFAULT_TRUE);
			}else{
				sb_auto_receive_app.setChecked(false);
				settings.setAutoRecevingOnlineOrder(ParamConst.DEFAULT_FALSE);
			}
			break;
		case R.id.sb_top_masking_use:
			if(checkState){
				sb_top_masking_use.setChecked(true);
				settings.setTopMaskingIsUser(ParamConst.DEFAULT_TRUE);
			}else{
				sb_top_masking_use.setChecked(false);
				settings.setTopMaskingIsUser(ParamConst.DEFAULT_FALSE);
			}
			break;
		case R.id.sb_top_screen_lock:
			if(checkState){
				sb_top_screen_lock.setChecked(true);
				settings.setScreenLock(ParamConst.DEFAULT_TRUE);
			}else{
				sb_top_screen_lock.setChecked(false);
				settings.setScreenLock(ParamConst.DEFAULT_FALSE);
			}
			break;
		case R.id.sb_cancel_order_void:
			if(checkState){
				sb_cancel_order_void.setChecked(true);
				settings.setRemoveToVoid(ParamConst.DEFAULT_TRUE);
			}else{
				sb_cancel_order_void.setChecked(false);
				settings.setRemoveToVoid(ParamConst.DEFAULT_FALSE);
			}
			break;
		case R.id.sb_transfer_print:
			if(checkState){
				sb_transfer_print.setChecked(true);
				settings.setTransferPrint(ParamConst.DEFAULT_TRUE);
			}else{
				sb_transfer_print.setChecked(false);
				settings.setTransferPrint(ParamConst.DEFAULT_FALSE);
			}
			break;
		case R.id.sb_auto_table:
			if(checkState){
				sb_auto_table.setChecked(true);
				settings.setAutoToTable(ParamConst.DEFAULT_TRUE);
			}else{
				sb_auto_table.setChecked(false);
				settings.setAutoToTable(ParamConst.DEFAULT_FALSE);
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
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_print_before_close));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_session_report_print));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_change_color));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_plu_category));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_plu_item));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_plu_modifier));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_hourly_payment));
		textTypeFace.setTrajanProRegular(tv_syncdata_warn);
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_top_screen_lock));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_top_masking_use));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_max_order_no_str));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_cash_close_print));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_auto_receive_app));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_cancel_order_void));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_transfer_print));
	}
}
