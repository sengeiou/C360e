package com.alfredposclient.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.alfredbase.BaseActivity;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.MonthlySalesReport;
import com.alfredbase.javabean.PrinterTitle;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.LogUtil;
import com.alfredbase.utils.ObjectFactory;
import com.alfredbase.utils.SystemUtil;
import com.alfredbase.utils.ToastUtils;
import com.alfredposclient.R;
import com.alfredposclient.global.App;
import com.alfredposclient.global.JavaConnectJS;
import com.alfredposclient.global.SyncCentre;
import com.alfredposclient.global.WebViewConfig;

public class MonthlySalesReportHtml extends BaseActivity {
	private String TAG = MonthlySalesReportHtml.class.getSimpleName();
	private WebView web;
	private JavaConnectJS javaConnectJS;
	private Map<String, Object> map = new HashMap<String, Object>();
    private BaseActivity context;
    
	
	//report type for printing purpose
	public final static int LOAD_MONTYLY_SALES_REPORT_COMPLETE = 70;
	
	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_common_web);
		context = this;
		web = (WebView) findViewById(R.id.web);
		WebViewConfig.setDefaultConfig(web);
		javaConnectJS = new JavaConnectJS() {
			@Override
			@JavascriptInterface
			public void send(String action, String param) {
				LogUtil.i(TAG, "action" + action + "；param" + param);
				if (!TextUtils.isEmpty(action)) {
					if (JavaConnectJS.LOAD_MONTHLY_SALES_REPORT.equals(action)) {
						
						String month = null;
						JSONObject jsonObject;
						
						try {
							jsonObject = new JSONObject(param);
							month = jsonObject.optString("month");
							
							mHandler.sendMessage(mHandler.obtainMessage(
									JavaConnectJS.ACTION_LOAD_MONTHLY_SALES_REPORT, param));
							
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
					if (JavaConnectJS.CLICK_BACK.equals(action)) {
						mHandler.sendEmptyMessage(JavaConnectJS.ACTION_CLICK_BACK);
					}
					if (JavaConnectJS.CLICK_PRINT.equals(action)) {
						mHandler.sendMessage(mHandler.obtainMessage(JavaConnectJS.ACTION_CLICK_PRINT, param));
					}
				}
			}
		};
		web.addJavascriptInterface(javaConnectJS, "JavaConnectJS");
		if (SystemUtil.isZh(context)) {
			web.loadUrl(WebViewConfig.ROOT_DIRECTORY + "monthlyreport.html");
		}	
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			
			case JavaConnectJS.ACTION_LOAD_MONTHLY_SALES_REPORT: {
				JSONObject jsonObject;
				String str = (String) msg.obj;
				String month = null;
				RevenueCenter rc = App.instance.getRevenueCenter();
				if (rc!=null) {
					try {
						Map<String, Object> param = new HashMap<String, Object>();
						jsonObject = new JSONObject(str);
						month = jsonObject.optString("month");
						param.put("month", month);
						SyncCentre.getInstance().loadCloudMonthlySalesReport(context, param, mHandler);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			}
			case MonthlySalesReportHtml.LOAD_MONTYLY_SALES_REPORT_COMPLETE:
				Map<String, Object> param = (Map<String, Object>) msg.obj;
				System.out.println("123123123123123123");
				final String month = (String) param.get("date");
				System.out.println("123123123123123123=======");
				final List<MonthlySalesReport> data = (List<MonthlySalesReport>) param.get("salesData");
				System.out.println("123123123123123123+++++++");
				if (data.size()==0) {
					DialogFactory.alertDialog(context, "通知", "没有"+month+"月的销售数据");
				}else {
					DialogFactory.commonTwoBtnDialog(context, month, "需要打印该月的销售汇总嘛", "取消", "打印", null,
							new OnClickListener(){

								@Override
								public void onClick(View arg0) {
									PrinterTitle title = ObjectFactory
											.getInstance()
											.getPrinterTitleForReport(
													App.instance.getRevenueCenter().getId(),
													"",
													App.instance.getUser().getFirstName()
															+ App.instance.getUser().getLastName(), null,null);

									PrinterDevice cashierPrinter = App.instance.getCahierPrinter();
									String[] ym = month.split("-");
									App.instance.remotePrintMonthlySalesReport(cashierPrinter, title, 
															Integer.valueOf(ym[0]),
															Integer.valueOf(ym[1]),
															data);
									
								}
						
					});
					
				}
				
				break;
			case ResultCode.CONNECTION_FAILED:
				ToastUtils.showToast(context, "数据加载失败，请检查网络是否正常");
				break;
			case JavaConnectJS.ACTION_CLICK_BACK:
				MonthlySalesReportHtml.this.finish();
				break;				
			default:
				break;
			}
		};
	};

	@Override
	public void handlerClickEvent(View v) {
		super.handlerClickEvent(v);
	}

}
