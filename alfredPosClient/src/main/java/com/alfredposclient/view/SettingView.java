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
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.PrinterTitle;
import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.utils.ButtonClickTimer;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.LogUtil;
import com.alfredbase.utils.ObjectFactory;
import com.alfredbase.utils.RxBus;
import com.alfredbase.utils.TextTypeFace;
import com.alfredposclient.R;
import com.alfredposclient.global.App;
import com.alfredposclient.global.UIHelp;
import com.alfredposclient.utils.AlertToDeviceSetting;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

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
		findViewById(R.id.ll_printer_qr_code).setOnClickListener(this);

		if (App.instance.countryCode != ParamConst.CHINA) {
        	findViewById(R.id.monthly_sale_report_container).setVisibility(View.GONE);
        }
		if(App.instance.isRevenueKiosk()){
			findViewById(R.id.ll_printer_qr_code).setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.ll_printer_qr_code).setVisibility(View.VISIBLE);
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
				RxBus.getInstance().post(RxBus.RX_MSG_1, null);
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
//					App.instance.kickOutCashDrawer(printer);
					RxBus.getInstance().post("open_drawer", null);
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
			case R.id.ll_printer_qr_code: {
				Map<String, String> map = new HashMap<String, String>();
				map.put("companyId", CoreData.getInstance().getRestaurant().getCompanyId().intValue() + "");
				map.put("restaurantId", CoreData.getInstance().getRestaurant().getId().intValue() + "");
				map.put("tableId", "0");
				map.put("tableName", "");
				map.put("type", "2");
				mDrawerLayout.closeDrawer(Gravity.END);
				final String content = new Gson().toJson(map);
				DialogFactory.showQrCodeDialog(context, content, "",
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								PrinterTitle title = ObjectFactory.getInstance()
										.getPrinterTitleForQRCode(
												App.instance.getRevenueCenter(),
												App.instance.getUser().getFirstName()
														+ App.instance.getUser().getLastName(),
												"");
								App.instance.remitePrintTableQRCode(App.instance.getCahierPrinter(),
										title, 0 + "", content);
								LogUtil.d("SettingView", "打印二维码");
							}
						});

			}
				break;
			default:
				break;
			}
		}
	}

	public void openDrawer(){
		App.instance.kickOutCashDrawer(App.instance.getCahierPrinter());
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
		 textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_opendrawer));
		 textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_printer_qr_code));
		 textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_app_version));
	}
}
