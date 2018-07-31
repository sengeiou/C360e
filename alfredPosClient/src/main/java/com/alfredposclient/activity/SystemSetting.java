package com.alfredposclient.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
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

import com.alfredposclient.R;
import com.alfredposclient.global.App;
import com.alfredposclient.global.JavaConnectJS;
import com.alfredposclient.global.SyncCentre;
import com.alfredposclient.global.SystemSettings;
import com.alfredposclient.global.UIHelp;
import com.alfredposclient.popupwindow.SelectPrintWindow;
import com.alfredposclient.popupwindow.SetPAXWindow;
import com.alfredposclient.view.ColorPickerDialog;
import com.alfredposclient.view.MyToggleButton;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemSetting extends BaseActivity implements OnClickListener,MyToggleButton.OnToggleStateChangeListeren{

	private ImageView iv_sync_data;
	private TextView tv_syncdata_warn;
	private MyToggleButton mt_kot_print;
	private MyToggleButton mt_kot_print_together;
	private MyToggleButton mt_kot_double_print;
	private MyToggleButton mt_double_print_bill;
	private MyToggleButton mt_double_close_bill_print;
	private MyToggleButton mt_order_summary_print;
	private MyToggleButton mt_session_report_print;
	private MyToggleButton mt_plu_category;
	private MyToggleButton mt_plu_item;
	private MyToggleButton mt_plu_modifier;
	private MyToggleButton mt_hourly_payment;
	private MyToggleButton mt_print_before_close;
	private MyToggleButton mt_auto_receive_app;
	private MyToggleButton mt_cash_close_print;
	private MyToggleButton mt_top_masking_use;
	private MyToggleButton mt_top_screen_lock;
	private MyToggleButton mt_cancel_order_void;
	private MyToggleButton mt_transfer_print;
	private MyToggleButton mt_auto_table,mt_of_pax,mt_print_lable_direction;
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
	private TextView tv_max_order_no,tv_print_lable;

	private LinearLayout ll_print_lable,ll_print_bill,ll_print_lable_direction;
	private View v_print_lable;
	private int maxOrderNo;
	MyToggleButton mt_print_lable;
	MyToggleButton mt_print_bill;
	private int textsize,textcolor;
	private TextView tv_lable_upOrdown;

	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_system_setting);
		if(App.instance.isRevenueKiosk()){
			findViewById(R.id.ll_app_order).setVisibility(View.VISIBLE);
			findViewById(R.id.ll_print_lable).setVisibility(View.VISIBLE);
			findViewById(R.id.ll_print_bill).setVisibility(View.VISIBLE);
			findViewById(R.id.ll_print_lable_direction).setVisibility(View.VISIBLE);
			findViewById(R.id.ll_of_pax).setVisibility(View.GONE);
			findViewById(R.id.ll_pos_setting).setVisibility(View.GONE);
			findViewById(R.id.ll_transfer_print).setVisibility(View.GONE);

		}else{
			findViewById(R.id.ll_print_lable).setVisibility(View.GONE);
			findViewById(R.id.ll_app_order).setVisibility(View.GONE);
			findViewById(R.id.ll_print_bill).setVisibility(View.GONE);
			findViewById(R.id.ll_print_bill).setVisibility(View.GONE);
			findViewById(R.id.ll_of_pax).setVisibility(View.VISIBLE);
			findViewById(R.id.ll_pos_setting).setVisibility(View.VISIBLE);
			findViewById(R.id.ll_transfer_print).setVisibility(View.VISIBLE);
		}
		syncMap = App.instance.getPushMsgMap();
		settings = App.instance.getSystemSettings();
		loadingDialog = new LoadingDialog(context);
		loadingDialog.setTitle(context.getResources().getString(R.string.loading));
		iv_sync_data = (ImageView) findViewById(R.id.iv_sync_data);
		selectPrintWindow = new SelectPrintWindow(context, findViewById(R.id.rl_root),handler);
		tv_syncdata_warn = (TextView) findViewById(R.id.tv_syncdata_warn);
		mt_kot_print = (MyToggleButton) findViewById(R.id.mt_kot_print);
		mt_kot_print_together = (MyToggleButton) findViewById(R.id.mt_kot_print_together);
		mt_kot_double_print = (MyToggleButton) findViewById(R.id.mt_kot_double_print);
		tv_callnum = (TextView)findViewById(R.id.tv_callnum);
		mt_double_print_bill = (MyToggleButton) findViewById(R.id.mt_double_print_bill);
		mt_double_close_bill_print = (MyToggleButton)findViewById(R.id.mt_double_close_bill_print);
		mt_order_summary_print = (MyToggleButton)findViewById(R.id.mt_order_summary_print);
		mt_session_report_print = (MyToggleButton)findViewById(R.id.mt_session_report_print);
		mt_plu_category = (MyToggleButton)findViewById(R.id.mt_plu_category);
		mt_plu_item = (MyToggleButton)findViewById(R.id.mt_plu_item);
		mt_plu_modifier = (MyToggleButton)findViewById(R.id.mt_plu_modifier);
		mt_hourly_payment = (MyToggleButton)findViewById(R.id.mt_hourly_payment);
		mt_print_before_close = (MyToggleButton)findViewById(R.id.mt_print_before_close);
		mt_cash_close_print = (MyToggleButton)findViewById(R.id.mt_cash_close_print);
		mt_auto_receive_app = (MyToggleButton)findViewById(R.id.mt_auto_receive_app);
		mt_top_masking_use = (MyToggleButton)findViewById(R.id.mt_top_masking_use);
		mt_top_screen_lock = (MyToggleButton)findViewById(R.id.mt_top_screen_lock);
		mt_cancel_order_void = (MyToggleButton)findViewById(R.id.mt_cancel_order_void);
		mt_transfer_print = (MyToggleButton)findViewById(R.id.mt_transfer_print);
		mt_auto_table = (MyToggleButton)findViewById(R.id.mt_auto_table);
		mt_of_pax=(MyToggleButton)findViewById(R.id.mt_of_pax);
		tv_lable_upOrdown=(TextView)findViewById(R.id.tv_lable_upOrdown);


		if (syncMap.isEmpty()) {
			tv_syncdata_warn.setText(context.getResources().getString(R.string.no_update));
			tv_syncdata_warn.setVisibility(View.GONE);
		} else {
			tv_syncdata_warn.setText(context.getResources().getString(R.string.receive) + 
					syncMap.size() + context.getResources().getString(R.string._message));
			tv_syncdata_warn.setTextColor(Color.RED);
		}
		iv_sync_data.setOnClickListener(this);
	//	iv_sync_data.setVisibility();
		mt_print_lable =(MyToggleButton) findViewById(R.id.mt_print_lable);
		mt_print_lable_direction =(MyToggleButton) findViewById(R.id.mt_print_lable_direction);
		mt_print_bill=(MyToggleButton)findViewById(R.id.mt_print_bill) ;
		mt_print_lable_direction.setOnStateChangeListeren(this);
		mt_print_lable.setOnStateChangeListeren(this);
		findViewById(R.id.iv_back).setOnClickListener(this);
		mt_kot_print.setOnStateChangeListeren(this);
		mt_kot_print_together.setOnStateChangeListeren(this);
		mt_kot_double_print.setOnStateChangeListeren(this);
		mt_double_print_bill.setOnStateChangeListeren(this);
		mt_double_close_bill_print.setOnStateChangeListeren(this);
		mt_order_summary_print.setOnStateChangeListeren(this);
		mt_session_report_print.setOnStateChangeListeren(this);
		mt_plu_category.setOnStateChangeListeren(this);
		mt_plu_item.setOnStateChangeListeren(this);
		mt_plu_modifier.setOnStateChangeListeren(this);
		mt_hourly_payment.setOnStateChangeListeren(this);
		mt_print_before_close.setOnStateChangeListeren(this);
		mt_cash_close_print.setOnStateChangeListeren(this);
		mt_auto_receive_app.setOnStateChangeListeren(this);
		mt_top_masking_use.setOnStateChangeListeren(this);
		mt_top_screen_lock.setOnStateChangeListeren(this);
		mt_cancel_order_void.setOnStateChangeListeren(this);
		mt_transfer_print.setOnStateChangeListeren(this);
		mt_auto_table.setOnStateChangeListeren(this);
		mt_print_bill.setOnStateChangeListeren(this);
		mt_of_pax.setOnStateChangeListeren(this);

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
        textsize= Store.getInt(SystemSetting.this, Store.TEXT_SIZE, 0);
		Store.putInt(SystemSetting.this, Store.T_TEXT_SIZE, textsize);
		textcolor= Store.getInt(SystemSetting.this, Store.COLOR_PICKER, Color.WHITE);
		Store.putInt(SystemSetting.this, Store.T_COLOR_PICKER, textsize);

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
			mt_kot_print.setChecked(true);
		}else {
			mt_kot_print.setChecked(false);
		}

		if (settings.isKotPrintTogether()) {
			mt_kot_print_together.setChecked(true);
		} else {
			mt_kot_print_together.setChecked(false);
		}
		if (settings.isKotPrintTogether()) {
			mt_kot_print_together.setChecked(true);
		} else {
			mt_kot_print_together.setChecked(false);
		}
		if (settings.isKotDoublePrint()) {
			mt_kot_double_print.setChecked(true);
		} else {
			mt_kot_double_print.setChecked(false);
		}
		if (settings.isDoubleBillPrint()) {
			mt_double_print_bill.setChecked(true);
		} else {
			mt_double_print_bill.setChecked(false);
		}
		if (settings.isDoubleReceiptPrint()) {
			mt_double_close_bill_print.setChecked(true);
		} else {
			mt_double_close_bill_print.setChecked(false);
		}
		if (settings.isOrderSummaryPrint()) {
			mt_order_summary_print.setChecked(true);
		} else {
			mt_order_summary_print.setChecked(false);
		}
		if(settings.isPrintWhenCloseSession()){
			mt_session_report_print.setChecked(true);
		}else{
			mt_session_report_print.setChecked(false);
		}
		if(settings.isPrintPluCategory()){
			mt_plu_category.setChecked(true);
		}else{
			mt_plu_category.setChecked(false);
		}
		if(settings.isPrintPluItem()){
			mt_plu_item.setChecked(true);
		}else{
			mt_plu_item.setChecked(false);
		}
		if(settings.isPrintPluModifier()){
			mt_plu_modifier.setChecked(true);
		}else{
			mt_plu_modifier.setChecked(false);
		}
		if(settings.isPrintHourlyPayment()){
			mt_hourly_payment.setChecked(true);
		}else{
			mt_hourly_payment.setChecked(false);
		}
		if(settings.isPrintBeforCloseBill()){
			mt_print_before_close.setChecked(true);
		}else{
			mt_print_before_close.setChecked(false);
		}
		if(settings.isCashClosePrint()){
			mt_cash_close_print.setChecked(true);
		}else{
			mt_cash_close_print.setChecked(false);
		}
		if(settings.isAutoRecevingOnlineOrder()){
			mt_auto_receive_app.setChecked(true);
		}else{
			mt_auto_receive_app.setChecked(false);
		}
		if(settings.isTopMaskingIsUser()){
			mt_top_masking_use.setChecked(true);
		}else{
			mt_top_masking_use.setChecked(false);
		}
		if(settings.isScreenLock()){
			mt_top_screen_lock.setChecked(true);
		}else{
			mt_top_screen_lock.setChecked(false);
		}
		if(settings.isRemoveToVoid()){
			mt_cancel_order_void.setChecked(true);
		}else{
			mt_cancel_order_void.setChecked(false);
		}
		if(settings.isTransferPrint()){
			mt_transfer_print.setChecked(true);
		}else{
			mt_transfer_print.setChecked(false);
		}
		if(settings.isAutoToTable()){
			mt_auto_table.setChecked(true);
		}else{
			mt_auto_table.setChecked(false);
		}

		if(settings.isPrintLable()){
			mt_print_lable.setChecked(true);
		}else{
			mt_print_lable.setChecked(false);
		}
		if(settings.isPrintLableD()){
			tv_lable_upOrdown.setText("Down");
			mt_print_lable_direction.setChecked(true);
		}else{
			tv_lable_upOrdown.setText("Up");
			mt_print_lable_direction.setChecked(false);
		}

		if(settings.isPrintBill()){
			mt_print_bill.setChecked(true);
		}else{
			mt_print_bill.setChecked(false);
		}

		if(settings.isOfPax()){
			mt_of_pax.setChecked(true);
		}else{
			mt_of_pax.setChecked(false);
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
									map.put(PushMessage.PAYMENT_METHOD, 1);
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
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_print_bill));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_print_lable));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_print_lable_direction));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_of_pax));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_auto_table));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_set_lock_time));
		textTypeFace.setTrajanProRegular(tv_lable_upOrdown);
	}

//	@Override
	public void onToggleStateChangeListeren(MyToggleButton Mybutton, Boolean checkState) {
		switch (Mybutton.getId()) {
			case R.id.mt_kot_print:
				if (checkState) {
					mt_kot_print.setChecked(true);
					Store.putBoolean(context, Store.KOT_PRINT, true);
				}else {
					mt_kot_print.setChecked(false);
					Store.putBoolean(context, Store.KOT_PRINT, false);
				}
				break;
			case R.id.mt_kot_print_together:
				if (checkState) {
					mt_kot_print_together.setChecked(true);
					settings.setKotPrintTogether(ParamConst.DEFAULT_TRUE);
				} else {
					mt_kot_print_together.setChecked(false);
					settings.setKotPrintTogether(ParamConst.DEFAULT_FALSE);
				}
				break;
			case R.id.mt_kot_double_print:
				if (checkState) {
					mt_kot_double_print.setChecked(true);
					settings.setKotDoublePrint(ParamConst.DEFAULT_TRUE);
				} else {
					mt_kot_double_print.setChecked(false);
					settings.setKotDoublePrint(ParamConst.DEFAULT_FALSE);
				}
				break;

			case R.id.mt_double_print_bill:
				if (checkState) {
					mt_double_print_bill.setChecked(true);
					settings.setDoubleBillPrint(ParamConst.DEFAULT_TRUE);
				} else {
					mt_double_print_bill.setChecked(false);
					settings.setDoubleBillPrint(ParamConst.DEFAULT_FALSE);
				}
				break;
			case R.id.mt_double_close_bill_print:
				if (checkState) {
					mt_double_close_bill_print.setChecked(true);
					settings.setDoubleReceiptPrint(ParamConst.DEFAULT_TRUE);
				} else {
					mt_double_close_bill_print.setChecked(false);
					settings.setDoubleReceiptPrint(ParamConst.DEFAULT_FALSE);
				}
				break;
			case R.id.mt_order_summary_print:
				if (checkState) {
					mt_order_summary_print.setChecked(true);
					settings.setOrderSummaryPrint(ParamConst.ORDER_SUMMARY_PRINT_TRUE);
				} else {
					mt_order_summary_print.setChecked(false);
					settings.setOrderSummaryPrint(ParamConst.ORDER_SUMMARY_PRINT_FALSE);
				}
				break;
			case R.id.mt_session_report_print:
				if(checkState){
					mt_session_report_print.setChecked(true);
					settings.setPrintWhenCloseSession(ParamConst.DEFAULT_TRUE);
				}else{
					mt_session_report_print.setChecked(false);
					settings.setPrintWhenCloseSession(ParamConst.DEFAULT_FALSE);
				}
				break;
			case R.id.mt_plu_category:
				if(checkState){
					mt_plu_category.setChecked(true);
					settings.setPrintPluCategory(ParamConst.DEFAULT_TRUE);
				}else{
					mt_plu_category.setChecked(false);
					settings.setPrintPluCategory(ParamConst.DEFAULT_FALSE);
				}
				break;
			case R.id.mt_plu_item:
				if(checkState){
					mt_plu_item.setChecked(true);
					settings.setPrintPluItem(ParamConst.DEFAULT_TRUE);
				}else{
					mt_plu_item.setChecked(false);
					settings.setPrintPluItem(ParamConst.DEFAULT_FALSE);
				}
				break;
			case R.id.mt_plu_modifier:
				if(checkState){
					mt_plu_modifier.setChecked(true);
					settings.setPrintPluModifier(ParamConst.DEFAULT_TRUE);
				}else{
					mt_plu_modifier.setChecked(false);
					settings.setPrintPluModifier(ParamConst.DEFAULT_FALSE);
				}
				break;
			case R.id.mt_hourly_payment:
				if(checkState){
					mt_hourly_payment.setChecked(true);
					settings.setPrintHourlyPayment(ParamConst.DEFAULT_TRUE);
				}else{
					mt_hourly_payment.setChecked(false);
					settings.setPrintHourlyPayment(ParamConst.DEFAULT_FALSE);
				}
				break;
			case R.id.mt_print_before_close:
				if(checkState){
					mt_print_before_close.setChecked(true);
					settings.setPrintBeforCloseBill(ParamConst.DEFAULT_TRUE);
				}else{
					mt_print_before_close.setChecked(false);
					settings.setPrintBeforCloseBill(ParamConst.DEFAULT_FALSE);
				}
				break;
			case R.id.mt_cash_close_print:
				if(checkState){
					mt_cash_close_print.setChecked(true);
					settings.setCashClosePrint(ParamConst.DEFAULT_TRUE);
				}else{
					mt_cash_close_print.setChecked(false);
					settings.setCashClosePrint(ParamConst.DEFAULT_FALSE);
				}
				break;
			case R.id.mt_auto_receive_app:
				if(checkState){
					mt_auto_receive_app.setChecked(true);
					settings.setAutoRecevingOnlineOrder(ParamConst.DEFAULT_TRUE);
				}else{
					mt_auto_receive_app.setChecked(false);
					settings.setAutoRecevingOnlineOrder(ParamConst.DEFAULT_FALSE);
				}
				break;
			case R.id.mt_top_masking_use:
				if(checkState){
					mt_top_masking_use.setChecked(true);
					settings.setTopMaskingIsUser(ParamConst.DEFAULT_TRUE);
				}else{
					mt_top_masking_use.setChecked(false);
					settings.setTopMaskingIsUser(ParamConst.DEFAULT_FALSE);
				}
				break;
			case R.id.mt_top_screen_lock:
				if(checkState){
					mt_top_screen_lock.setChecked(true);
					settings.setScreenLock(ParamConst.DEFAULT_TRUE);
				}else{
					mt_top_screen_lock.setChecked(false);
					settings.setScreenLock(ParamConst.DEFAULT_FALSE);
				}
				break;
			case R.id.mt_cancel_order_void:
				if(checkState){
					mt_cancel_order_void.setChecked(true);
					settings.setRemoveToVoid(ParamConst.DEFAULT_TRUE);
				}else{
					mt_cancel_order_void.setChecked(false);
					settings.setRemoveToVoid(ParamConst.DEFAULT_FALSE);
				}
				break;
			case R.id.mt_transfer_print:
				if(checkState){
					mt_transfer_print.setChecked(true);
					settings.setTransferPrint(ParamConst.DEFAULT_TRUE);
				}else{
					mt_transfer_print.setChecked(false);
					settings.setTransferPrint(ParamConst.DEFAULT_FALSE);
				}
				break;
			case R.id.mt_auto_table:
				if(checkState){
					mt_auto_table.setChecked(true);
					settings.setAutoToTable(ParamConst.DEFAULT_TRUE);
				}else{
					mt_auto_table.setChecked(false);
					settings.setAutoToTable(ParamConst.DEFAULT_FALSE);
				}
				break;
			case R.id.mt_print_lable:

				if(checkState){
					mt_print_lable.setChecked(true);
					settings.setPrintLable(ParamConst.DEFAULT_TRUE);
				}else{
					mt_print_lable.setChecked(false);
					settings.setPrintLable(ParamConst.DEFAULT_FALSE);
				}
				break;

			case R.id.mt_print_lable_direction:

				if(checkState){
					tv_lable_upOrdown.setText("Down");
					mt_print_lable_direction.setChecked(true);
					settings.setPrintLableD(ParamConst.DEFAULT_TRUE);
				}else{
					tv_lable_upOrdown.setText("Up");
					mt_print_lable_direction.setChecked(false);
					settings.setPrintLableD(ParamConst.DEFAULT_FALSE);
				}
				break;

			case R.id.mt_print_bill:

				if(checkState){
					mt_print_bill.setChecked(true);
					settings.setPrintBill(ParamConst.DEFAULT_TRUE);
				}else{
					mt_print_bill.setChecked(false);
					settings.setPrintBill(ParamConst.DEFAULT_FALSE);
				}
				break;


			case R.id.mt_of_pax:

				if(checkState){
					mt_of_pax.setChecked(true);
					settings.setOfPax(ParamConst.DEFAULT_TRUE);
				}else{
					mt_of_pax.setChecked(false);
					settings.setOfPax(ParamConst.DEFAULT_FALSE);
				}
				break;
			default:
				break;
		}
	}




//	public void onToggleStateChangeListeren(MyToggleButton toButton , Boolean state) {
//      switch (toButton.getId()) {
//		  case R.id.my_togglebut:
//
//			  Toast.makeText(SystemSetting.this, state == false ? "关" : "开",
//
//						Toast.LENGTH_SHORT).show();
//		  	break;
//	  }
//
//
//	}
}
