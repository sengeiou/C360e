package com.alfredposclient.activity;

import java.util.Map;

import org.json.JSONObject;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.alfredbase.BaseActivity;
import com.alfredbase.ParamConst;
import com.alfredbase.VerifyDialog;
import com.alfredbase.javabean.Printer;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.utils.ButtonClickTimer;
import com.alfredbase.utils.ObjectFactory;
import com.alfredbase.utils.SystemUtil;
import com.alfredposclient.R;
import com.alfredposclient.global.App;
import com.alfredposclient.global.JavaConnectJS;
import com.alfredposclient.global.UIHelp;
import com.alfredposclient.global.WebViewConfig;

public class CashInOutHtml extends BaseActivity {
	private static final String ACTION_CONTINUE = "ACTION_CONTINUE";
	private static final String CashIn = "Cash In";
	private static final String defaultCash = "0.00";
	private WebView web;
	private JavaConnectJS javaConnectJS;
	private VerifyDialog dialog;
	
	private int type;
	private String cash;
	private String comment;
	private User user;

	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_common_web);
		dialog = new VerifyDialog(context, mHandler);
		web = (WebView) findViewById(R.id.web);
		WebViewConfig.setDefaultConfig(web);
		javaConnectJS = new JavaConnectJS() {
			@Override
			@JavascriptInterface
			public void send(String action, String param) {
				if (!ButtonClickTimer.canClick(web)) {
					return;
				}
				if (!TextUtils.isEmpty(action)) {
					if (JavaConnectJS.CLICK_BACK.equals(action)) {
						mHandler.sendEmptyMessage(JavaConnectJS.ACTION_CLICK_BACK);
					}
					if (JavaConnectJS.CLICK_CASH_SAVE.equals(action)) {
						mHandler.sendMessage(mHandler.obtainMessage(
								JavaConnectJS.ACTION_CLICK_CASH_SAVE, param));
					}
				}
			}
		};
		web.addJavascriptInterface(javaConnectJS, "JavaConnectJS");
		if (SystemUtil.isZh(context))
			web.loadUrl(WebViewConfig.ROOT_DIRECTORY + "cashinout_zh.html");
		else
			web.loadUrl(WebViewConfig.ROOT_DIRECTORY + "cashinout.html");
	}

	@Override
	public void handlerClickEvent(View v) {
		super.handlerClickEvent(v);
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case JavaConnectJS.ACTION_CLICK_BACK:
				CashInOutHtml.this.finish();
				break;
			case JavaConnectJS.ACTION_CLICK_CASH_SAVE:
				String str = (String) msg.obj;
				try {
					JSONObject jsonObject = new JSONObject(str);
					JSONObject saveDataJsonObject = jsonObject.getJSONObject("saveData");
					String typeString = saveDataJsonObject.optString("type");
					if (typeString.equals(CashIn)) {
						type = ParamConst.CASHINOUT_TYPE_IN;
					}else {
						type = ParamConst.CASHINOUT_TYPE_OUT;
					}
					cash = saveDataJsonObject.optString("cash");
					comment = saveDataJsonObject.optString("comment");
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!TextUtils.isEmpty(cash) && !cash.equals(defaultCash)) {
					dialog.show(ACTION_CONTINUE,null);
				}else {
					UIHelp.showToast(context, context.getResources().getString(R.string.cash_not_empty));
					return;
				}
				PrinterDevice printerDevice = App.instance.getCahierPrinter();
				App.instance.kickOutCashDrawer(printerDevice);
				break;
			case VerifyDialog.DIALOG_RESPONSE:
				Map<String, Object> map = (Map<String, Object>) msg.obj;
				user = (User) map.get("User");
				if (map.get("MsgObject").equals(ACTION_CONTINUE) && user!=null) {
					if (App.instance.countryCode == ParamConst.CHINA) {
						user.setUserName(user.getLastName()+user.getFirstName());
					}else {
						user.setUserName(user.getFirstName()+"."+user.getLastName());
					}
					ObjectFactory.getInstance().getCashInOut(App.instance.getRevenueCenter(),
							App.instance.getLastBusinessDate(),user,type,cash,comment);
					UIHelp.showToast(context, context.getResources().getString(R.string.save_success));
					mHandler.sendEmptyMessageDelayed(JavaConnectJS.ACTION_CLICK_BACK, 350);
				}
				break;
			default:
				break;
			}
		}
	};
	
}
