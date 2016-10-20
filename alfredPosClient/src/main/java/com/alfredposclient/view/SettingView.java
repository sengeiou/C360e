package com.alfredposclient.view;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.ParamConst;
import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.utils.ButtonClickTimer;
import com.alfredbase.utils.RxBus;
import com.alfredbase.utils.TextTypeFace;
import com.alfredposclient.R;
import com.alfredposclient.global.App;
import com.alfredposclient.global.UIHelp;
import com.alfredposclient.utils.AlertToDeviceSetting;

public class SettingView extends LinearLayout implements OnClickListener {

	private BaseActivity context;
	private DrawerLayout mDrawerLayout;

	public SettingView(Context context) {
		super(context);
		init(context);
	}

	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SettingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void setParams(BaseActivity parent,DrawerLayout drawerLayout) {
		this.context = parent;
		this.mDrawerLayout = drawerLayout;
	}
	
	private void init(Context context) {
		View.inflate(context, R.layout.setting_window, this);
		

		findViewById(R.id.ll_devices).setOnClickListener(this);
		findViewById(R.id.ll_reprint_bill).setOnClickListener(this);
		findViewById(R.id.ll_edit_settlement).setOnClickListener(
				this);
		findViewById(R.id.ll_boh_settlement).setOnClickListener(
				this);
		findViewById(R.id.ll_stored_card).setOnClickListener(this);
		findViewById(R.id.ll_dashboard).setOnClickListener(this);
		findViewById(R.id.ll_close).setOnClickListener(this);
		findViewById(R.id.ll_xz_report).setOnClickListener(this);
		findViewById(R.id.ll_entplu).setOnClickListener(this);
		findViewById(R.id.ll_voidplu).setOnClickListener(this);
		findViewById(R.id.ll_clock_select).setOnClickListener(this);
		findViewById(R.id.ll_cash_inout).setOnClickListener(this);
		findViewById(R.id.ll_system).setOnClickListener(this);
		findViewById(R.id.ll_opendrawer).setOnClickListener(this);
		findViewById(R.id.iv_setting_close).setOnClickListener(this);
		findViewById(R.id.ll_setting_title).setOnClickListener(null);	
		findViewById(R.id.ll_monthly_sale_report).setOnClickListener(this);
		findViewById(R.id.ll_monthly_plu_report).setOnClickListener(this);

		if (App.instance.countryCode != ParamConst.CHINA) {
        	findViewById(R.id.monthly_sale_report_container).setVisibility(View.GONE);
        }
		((TextView)findViewById(R.id.tv_app_version)).setText(context.getResources().getString(R.string.version) + App.instance.VERSION);
		initTextTypeFace();
	}
	
	public void initOptionsNoSessionOpen() {
		findViewById(R.id.ll_edit_settlement).setVisibility(
				View.GONE);
		findViewById(R.id.ll_close).setVisibility(View.GONE);		
		
	}
	
	public void initOptionsSessionOpen() {
		findViewById(R.id.ll_edit_settlement).setVisibility(
				View.VISIBLE);
		findViewById(R.id.ll_close).setVisibility(View.INVISIBLE);		
	}
	
	@Override
	public void onClick(View v) {
		if (ButtonClickTimer.canClick(v)) {
			switch (v.getId()) {
			case R.id.ll_devices: {
				UIHelp.startDevicesHtml(context);
				break;
			}
			case R.id.ll_reprint_bill: {
				UIHelp.startReprintBillHtml(context);
				break;
			}
			case R.id.ll_edit_settlement: {
				UIHelp.startEditSettlementHtml(context);
				break;
			}
			case R.id.ll_boh_settlement: {
				UIHelp.startBOHSettlementHtml(context);
				break;
			}
			case R.id.ll_stored_card: {
				RxBus.getInstance().post("showStoredCard", null);
//				mDrawerLayout.closeDrawer(Gravity.END);
				break;
			}
			case R.id.ll_dashboard: {
				UIHelp.startDashboardHtml(context);
				break;
			}
			case R.id.ll_xz_report: {
				UIHelp.startXZReportHtml(context);
				break;
			}
			case R.id.ll_entplu: {
				UIHelp.startEntPluHtml(context);
				break;
			}
			case R.id.ll_voidplu: {
				UIHelp.startEntPluHtml(context);
				break;
			}
			case R.id.ll_close: {
				// UIHelp.startOpenRestaruant(context);
				// context.overridePendingTransition(R.anim.slide_top_in,
				// R.anim.anim_null);
				context.finish();
				break;
			}
			case R.id.ll_clock_select:
				UIHelp.startClockInOROut(context);
				break;
			case R.id.ll_cash_inout:
				UIHelp.startCashInOut(context);
				break;
			case R.id.ll_system:
				UIHelp.startSystemSetting(context);
				break;
			case R.id.ll_opendrawer:
				PrinterDevice printer = App.instance.getCahierPrinter();
				if (printer == null) {
					AlertToDeviceSetting.noKDSorPrinter(context,
							context.getResources().getString(R.string.no_cashier_printer));
				} else {
					App.instance.kickOutCashDrawer(printer);
				}
				break;
			case R.id.iv_setting_close:
				mDrawerLayout.closeDrawer(Gravity.END);
				break;
			case R.id.ll_monthly_sale_report:
				UIHelp.startMonthlySalesReport(context);
				break;				
			case R.id.ll_monthly_plu_report:
				UIHelp.startMonthlyPLUReport(context);
				break;	
			default:
				break;
			}
		}
	}
	 private void initTextTypeFace() {
		 TextTypeFace textTypeFace = TextTypeFace.getInstance();
		 textTypeFace.setTrajanProBlod((TextView)findViewById(R.id.tv_setting));
		 textTypeFace.setTrajanProBlod((TextView)findViewById(R.id.tv_edit));
		 textTypeFace.setTrajanProBlod((TextView)findViewById(R.id.tv_report));
		 textTypeFace.setTrajanProBlod((TextView)findViewById(R.id.tv_system_title));
		 textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_reprint_bill));
		 textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_edit_settlement));
		 textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_boh_settlement));
		 textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_stored_card_setting));
		 textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_dashboard));
		 textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_xz_report));
		 textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_clock_select));
		 textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_cash_inout));
		 textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_devices));
		 textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_system));
		 textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_close));
		 textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_app_version));
	}
}
