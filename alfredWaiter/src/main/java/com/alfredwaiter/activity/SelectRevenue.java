package com.alfredwaiter.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.BaseApplication;
import com.alfredbase.LoadingDialog;
import com.alfredbase.ParamConst;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.model.WaiterDevice;
import com.alfredbase.store.Store;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.OrderDetailTaxSQL;
import com.alfredbase.store.sql.OrderModifierSQL;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredbase.utils.CommonUtil;
import com.alfredbase.utils.TextTypeFace;
import com.alfredwaiter.R;
import com.alfredwaiter.global.App;
import com.alfredwaiter.global.SyncCentre;
import com.alfredwaiter.global.UIHelp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectRevenue extends BaseActivity {
	public static final int HANDLER_PAIRING_COMPLETE = 1;
	public static final String CAN_SELECT_REVENUEID = "CAN_SELECT_REVENUEID";
	// public static final int HANDLER_CONN_ERROR = 4;
	// public static final int HANDLER_CONN_REFUSED = 5;
	// public static final String UNKNOW_ERROR =
	// "Network error: the main POS may be down";
	// public static final String CONN_TIMEOUT = "Network timeout";
	private ListView lv_revenue_centre;
	private RevenueListAdapter revenueListAdapter;
	private List<RevenueCenter> kitchens = new ArrayList<RevenueCenter>();
	private RevenueCenter revenue;
	private boolean doubleBackToExitPressedOnce = false;
	private TextTypeFace textTypeFace;
	
	public static final int SYNC_DATA_TAG = 2015;
	
	private int syncDataCount = 0;
	@Override
	protected void initView() {
		super.initView();
		loadingDialog = new LoadingDialog(context);
		setContentView(R.layout.activity_select_revenue);
		lv_revenue_centre = (ListView) findViewById(R.id.lv_revenue_centre);
		Intent intent = getIntent();
		HashMap<String, Object> map = (HashMap<String, Object>) intent
				.getSerializableExtra("revenueMap");
		kitchens = (List<RevenueCenter>) map.get("revenueCenters");
		revenue = (RevenueCenter) map.get("revenue");
		revenueListAdapter = new RevenueListAdapter(context, kitchens);
		lv_revenue_centre.setAdapter(revenueListAdapter);
		syncDataCount = 0;
		lv_revenue_centre.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				RevenueCenter revenueCenter = (RevenueCenter) parent
						.getItemAtPosition(position);
				if (revenueCenter.getId().intValue() == revenue.getId()
						.intValue()) {
					App.instance.setRevenueCenter(revenueCenter);
					Store.saveObject(context, Store.CURRENT_REVENUE_CENTER,
							revenueCenter);
					loadingDialog.setTitle(context.getResources().getString(R.string.loading));
					loadingDialog.show();
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							OrderSQL.deleteAllOrder();
							OrderDetailSQL.deleteAllOrderDetail();
							OrderModifierSQL.deleteAllOrderModifier();
							OrderDetailTaxSQL.deleteAllOrderDetailTax();
						}
					}).start();
					syncDataCount = 0;
					SyncData();
					getPlaces();
				} else {
					UIHelp.showToast(context,
							context.getResources().getString(R.string.link) + revenue.getRevName());

				}
			}
		});
		findViewById(R.id.iv_setting).setOnClickListener(this);
		
		((TextView)findViewById(R.id.tv_app_version)).setText(context.getResources().getString(R.string.version) + App.instance.VERSION);
		initTextTypeFace();
	}

	private void initTextTypeFace() {
		textTypeFace = TextTypeFace.getInstance();
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_app_version));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_select_rev_tips));
	}
	
	private void SyncData() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		SyncCentre.getInstance().syncCommonData(context,
				App.instance.getPairingIp(), parameters, handler);
	}

	private void getPlaces() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("revenueId", App.instance.getRevenueCenter().getId());
		SyncCentre.getInstance().getPlaceInfo(context,
				App.instance.getPairingIp(), parameters, handler);

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_PAIRING_COMPLETE: {
				loadingDialog.dismiss();
				UIHelp.startLogin(context);
				finish();
				break;
			}
			case TablesPage.HANDLER_GET_PLACE_INFO: {
				// 预留2秒让数据存下数据库
				BaseApplication.postHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Map<String, Object> parameters = new HashMap<String, Object>();
						WaiterDevice waiterDevice = new WaiterDevice();
						waiterDevice
								.setWaiterId(App.instance.getUser().getId());
						waiterDevice.setIP(CommonUtil.getLocalIpAddress());
						waiterDevice.setMac(CommonUtil
								.getLocalMacAddress(context));
						Store.saveObject(context, Store.WAITER_DEVICE,
								waiterDevice);
						parameters.put("device", waiterDevice);
						parameters.put("deviceType",
								ParamConst.DEVICE_TYPE_WAITER);
						SyncCentre.getInstance().pairingComplete(context,
								App.instance.getPairingIp(), parameters,
								handler);
					}
				}, 2 * 1000);
			}
				break;
			case ResultCode.CONNECTION_FAILED:
				loadingDialog.dismiss();
				UIHelp.showToast(context, ResultCode.getErrorResultStr(context,
						(Throwable) msg.obj, context.getResources().getString(R.string.revenue_center)));
				break;
			case SYNC_DATA_TAG:
				if(syncDataCount == 5){
					handler.sendEmptyMessage(TablesPage.HANDLER_GET_PLACE_INFO);
				}else{
					syncDataCount++;
				}
			break;
			default:
				break;
			}
		};
	};

	@Override
	public void handlerClickEvent(View v) {
		super.handlerClickEvent(v);
		switch (v.getId()) {
		case R.id.iv_setting: {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("visibilityMap", View.GONE);
			UIHelp.startSetting(context,map);
			break;
		}
		default:
			break;
		}
	}

	class RevenueListAdapter extends BaseAdapter {
		private List<RevenueCenter> revenueCentres;

		private LayoutInflater inflater;

		public RevenueListAdapter() {

		}

		public RevenueListAdapter(Context context,
				List<RevenueCenter> revenueCentres) {
			if (revenueCentres == null)
				revenueCentres = Collections.emptyList();
			this.revenueCentres = revenueCentres;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return revenueCentres.size();
		}

		@Override
		public Object getItem(int arg0) {
			return revenueCentres.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ViewHolder holder = null;
			if (arg1 == null) {
				arg1 = inflater.inflate(R.layout.item_kitchen, null);
				holder = new ViewHolder();
				holder.tv_text = (TextView) arg1.findViewById(R.id.tv_text);
				arg1.setTag(holder);
			} else {
				holder = (ViewHolder) arg1.getTag();
			}
			holder.tv_text.setText(revenueCentres.get(arg0).getRevName());
			textTypeFace.setTrajanProRegular(holder.tv_text);
			return arg1;
		}

		class ViewHolder {
			public TextView tv_text;
		}
	}

	@Override
	public void onBackPressed() {
		 if (doubleBackToExitPressedOnce) {
		        super.onBackPressed();
		        return;
		    }

		    this.doubleBackToExitPressedOnce = true;
		    UIHelp.showToast(this, context.getResources().getString(R.string.exit_program));

		BaseApplication.postHandler.postDelayed(new Runnable() {

		        @Override
		        public void run() {
		            doubleBackToExitPressedOnce=false;                       
		        }
		    }, 2000);
	}
}
