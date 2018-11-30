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
import com.alfredbase.http.APIName;
import com.alfredbase.javabean.PrinterTitle;
import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.utils.ButtonClickTimer;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.LogUtil;
import com.alfredbase.utils.ObjectFactory;
import com.alfredbase.utils.RxBus;
import com.alfredbase.utils.TextTypeFace;
import com.alfredposclient.R;
import com.alfredposclient.activity.kioskactivity.MainPageKiosk;
import com.alfredposclient.global.App;
import com.alfredposclient.global.UIHelp;
import com.alfredposclient.utils.AlertToDeviceSetting;

public class SettingView extends LinearLayout implements OnClickListener {

	private BaseActivity context;
	private DrawerLayout mDrawerLayout;
	final static String[] letters= {"a","b", "c","d","e","f","g","h","i","j"};
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
		findViewById(R.id.ll_close_subpos).setOnClickListener(this);
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
		findViewById(R.id.ll_sub_pos).setOnClickListener(this);
		findViewById(R.id.ll_sunmi).setOnClickListener(this);
		findViewById(R.id.linear_sunmi).setOnClickListener(this);
		SUNMIVisible();


		if (App.instance.countryCode != ParamConst.CHINA) {
        	findViewById(R.id.monthly_sale_report_container).setVisibility(View.GONE);
        }
		if(App.instance.isRevenueKiosk()){
			findViewById(R.id.ll_printer_qr_code).setVisibility(View.VISIBLE);
			if(App.instance.getPosType() == ParamConst.POS_TYPE_MAIN) {
				findViewById(R.id.ll_sub_pos).setVisibility(View.VISIBLE);
				findViewById(R.id.ll_close_subpos).setVisibility(View.GONE);

			}else{
				findViewById(R.id.ll_sub_pos).setVisibility(View.GONE);
				findViewById(R.id.ll_close_subpos).setVisibility(View.VISIBLE);
			}
		}else{
			findViewById(R.id.ll_printer_qr_code).setVisibility(View.GONE);
			findViewById(R.id.ll_sub_pos).setVisibility(View.GONE);
			findViewById(R.id.ll_close_subpos).setVisibility(View.GONE);
		}
		((TextView)findViewById(R.id.tv_app_version)).setText(context.getResources().getString(R.string.version) + App.instance.VERSION);
		initTextTypeFace();
	}
	
	public void initOptionsNoSessionOpen() {
		findViewById(R.id.ll_edit_settlement).setVisibility(
				View.GONE);
		findViewById(R.id.ll_close).setVisibility(View.GONE);
		if (App.instance.isSUNMIShow()){
			findViewById(R.id.linear_sunmi).setVisibility(VISIBLE);
			SUNMIGone();
		}else {
			findViewById(R.id.linear_sunmi).setVisibility(GONE);
			SUNMIGone();
		}


	}
	
	public void initOptionsSessionOpen() {
		findViewById(R.id.ll_edit_settlement).setVisibility(
				View.GONE);
		findViewById(R.id.ll_close).setVisibility(View.GONE);
		if (App.instance.isSUNMIShow()){
			findViewById(R.id.linear_sunmi).setVisibility(VISIBLE);
			SUNMIGone();
		}else {
			findViewById(R.id.linear_sunmi).setVisibility(INVISIBLE);
			SUNMIGone();
		}
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
//				UIHelp.startReprintBillHtml(context);
				UIHelp.startEditSettlementHtml(context);
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
				UIHelp.startXZReportActivty(context);
				break;
			}
			case R.id.ll_entplu: {
				UIHelp.startEntPluHtml(context);
				break;
			}
			case R.id.ll_voidplu: {
				UIHelp.startVoidPluHtml(context);
				break;
			}
			case R.id.ll_close: {
				// UIHelp.startOpenRestaruant(context);
				// context.overridePendingTransition(R.anim.slide_top_in,
				// R.anim.anim_null);
				context.finish();
				break;
			}
			case R.id.ll_close_subpos: {
				if (context instanceof MainPageKiosk) {
					((MainPageKiosk) context).tryToCloseSession();
				}
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
//				Map<String, String> map = new HashMap<String, String>();
//				map.put("companyId", CoreData.getInstance().getRestaurant().getCompanyId().intValue() + "");
//				map.put("restaurantId", CoreData.getInstance().getRestaurant().getId().intValue() + "");
//				map.put("tableId", "0");
//				map.put("tableName", "");
//				map.put("type", "2");
		        String restaurantId=getStringByInt( CoreData.getInstance().getRestaurant().getId().intValue()+"");
		        String tableId="a";
		        StringBuffer sb=new StringBuffer();
		        sb.append(getAbsoluteUrl(APIName.QC_DOWNLOAD)+"&"+restaurantId+"&"+tableId);

//				mDrawerLayout.closeDrawer(Gravity.END);
//				final String content = new Gson().toJson(map);
				final String content = sb.toString();
				DialogFactory.showQrCodeDialog(context, content, "", false,
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
				case R.id.ll_sunmi:
					UIHelp.startSunmiActivity(context);
					break;
				case R.id.linear_sunmi:
					UIHelp.startSunmiActivity(context);
					break;
				case R.id.ll_sub_pos:
					UIHelp.startSubPosManagePage(context);
					break;
			default:
				break;
			}
		}
	}

	private String getStringByInt(String id) {
		final String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
		StringBuffer sbf = new StringBuffer();
		char[] c = id.toCharArray();
		for (char ch : c) {
			int intNum = ch - '0';
			sbf.append(letters[intNum]);
		}
		return sbf.toString();
	}


	private String getAbsoluteUrl(String relativeUrl) {
		if (App.instance.isDebug) {
//			return "http://172.16.0.190:8087/alfred-api/" + relativeUrl;
			//  return "http://192.168.104.10:8083/alfred-api/" + relativeUrl;
			return "http://192.168.20.100:8083/alfred-api/" + relativeUrl;
		} else if (App.instance.isOpenLog) {

			return "http://139.224.17.126/" + relativeUrl;
		} else {

//			return "http://54.169.45.214/alfred-api/" + relativeUrl;52.77.208.125
			return "http://www.servedbyalfred.biz/" + relativeUrl;
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
		 textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_sunmi));
		 textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.text_sunmi));
	}

	public void SUNMIVisible(){
		findViewById(R.id.ll_sunmi).setVisibility(VISIBLE);
	}

	public void SUNMIGone(){
		findViewById(R.id.ll_sunmi).setVisibility(GONE);
	}
}
