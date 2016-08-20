package com.alfredposclient.activity;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.LoadingDialog;
import com.alfredbase.ParamConst;
import com.alfredbase.PrinterLoadingDialog;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.temporaryforapp.AppOrder;
import com.alfredbase.javabean.temporaryforapp.AppOrderDetail;
import com.alfredbase.javabean.temporaryforapp.AppOrderModifier;
import com.alfredbase.javabean.temporaryforapp.TempOrder;
import com.alfredbase.store.sql.temporaryforapp.AppOrderDetailSQL;
import com.alfredbase.store.sql.temporaryforapp.AppOrderModifierSQL;
import com.alfredbase.store.sql.temporaryforapp.AppOrderSQL;
import com.alfredbase.store.sql.temporaryforapp.TempOrderSQL;
import com.alfredbase.utils.TextTypeFace;
import com.alfredbase.utils.TimeUtil;
import com.alfredposclient.R;
import com.alfredposclient.global.App;
import com.alfredposclient.global.SyncCentre;
import com.alfredposclient.global.UIHelp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NetWorkOrderActivity extends BaseActivity {

	public static final int REFRESH_APPORDER_SUCCESS = 101;
	public static final int REFRESH_APPORDER_FAILED = -101;
	
	private List<AppOrder> appOrders = new ArrayList<AppOrder>();
	private List<AppOrderDetail> appOrderDetails = new ArrayList<AppOrderDetail>();
	private ListView lv_order_list;
	private ListView lv_orderdetail_list;
	private TextTypeFace textTypeFace = TextTypeFace.getInstance();
	private LayoutInflater inflater;
	private int selectOrderItem = 0;
	private Button btn_check;
	private Button btn_delete;
	public static final int CHECK_REQUEST_CODE = 110;
	private AppOderAdapter appOderAdapter;
	private AppOderDetailAdapter appOderDetailAdapter;
	private int appOrderId = 0;
	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_network_order);
		loadingDialog = new LoadingDialog(this);
		inflater = LayoutInflater.from(this);
		lv_order_list = (ListView) findViewById(R.id.lv_order_list);
		lv_orderdetail_list = (ListView) findViewById(R.id.lv_orderdetail_list);
		initData();
		initTextTypeFace();
		appOderAdapter = new AppOderAdapter();
		lv_order_list.setAdapter(appOderAdapter);
		appOderDetailAdapter = new AppOderDetailAdapter();
		lv_orderdetail_list.setAdapter(appOderDetailAdapter);
		btn_check = (Button) findViewById(R.id.btn_check);
		if(App.instance.isRevenueKiosk()){
			btn_check.setVisibility(View.VISIBLE);
			btn_check.setText(getResources().getText(R.string.print_receipt));
		}else{
			btn_check.setVisibility(View.GONE);
		}
		btn_delete = (Button) findViewById(R.id.btn_delete);
		btn_check.setOnClickListener(this);
		btn_delete.setOnClickListener(this);
		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.btn_refresh).setOnClickListener(this);
		lv_order_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selectOrderItem = arg2;
				initData();
				appOderAdapter.notifyDataSetChanged();
				appOderDetailAdapter.notifyDataSetChanged();
			}
		});
		Intent intent = getIntent();
		if (!TextUtils.isEmpty(intent.getStringExtra("appOrderId"))) {
			appOrderId = Integer.parseInt(intent.getStringExtra("appOrderId"));
		}
		if (appOrderId != 0 ) {
			for (AppOrder appOrder : appOrders) {
				if (appOrder.getId().intValue() == appOrderId) {
					selectOrderItem = appOrders
							.indexOf(appOrder);
					lv_order_list.setSelection(selectOrderItem);
				}
			}
			appOrderId = 0;
		}
	}
	
	private void initData(){
		appOrders = AppOrderSQL.getAppOrderByOrderStatus(TimeUtil.getBeforeYesterday(App.instance.getBusinessDate()));
		if(appOrders.size() > 0){
			appOrderDetails = AppOrderDetailSQL.getAppOrderDetailByAppOrderId(appOrders.get(selectOrderItem).getId().intValue());
		}
	}
	
	private void initTextTypeFace() {
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.btn_back));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.btn_refresh));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_order_no));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_order_type));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_order_status));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_order_time));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_item_name));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_item_qty));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.btn_check));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.btn_delete));
		
	}
	
	@Override
	protected void handlerClickEvent(View v) {
		super.handlerClickEvent(v);
		switch (v.getId()) {
		case R.id.btn_check:{
			final AppOrder appOrder = (AppOrder)v.getTag();
			if(App.instance.isRevenueKiosk()){
				selectOrderItem = 0;
				if (appOrder == null) {
					return;
				}
				appOrder
						.setOrderStatus(ParamConst.APP_ORDER_STATUS_FINISH);
//				appOrder
//						.setDiningStatus(ParamConst.TEMP_ORDER_DINING_STATUS_FINISH);
				AppOrderSQL.updateAppOrder(appOrder);
				PrinterLoadingDialog printerLoadingDialog = new PrinterLoadingDialog(
						context);
				printerLoadingDialog.setTitle(context.getResources().getString(
						R.string.receipt_printing));
				printerLoadingDialog.showByTime(3000);
				App.instance.printerAppOrder(appOrder);
				App.instance.getSyncJob().checkAppOrderStatus(
						App.instance.getRevenueCenter().getId().intValue(),
						appOrder.getId().intValue(),
						appOrder.getOrderStatus().intValue(), "",
						App.instance.getBusinessDate().longValue(), appOrder.getOrderNo());
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						refreshView();
					}
				}, 3000);
			}

			/*
			final AppOrder appOrder = (AppOrder)v.getTag();
			Tables tables = CoreData.getInstance().getTables(appOrder.getTableId().intValue());
			DialogFactory.commonTwoBtnDialog(this, "Warning", "Please confirm '" + tables.getTableName() + "' has been cleared?",
					"No", "Yes", null, new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							PrinterLoadingDialog printerLoadingDialog = new PrinterLoadingDialog(context);
							printerLoadingDialog.setTitle("Loading");
							printerLoadingDialog.showByTime(3000);
							new Thread(new Runnable() {
								@Override
								public void run() {
									App.instance.appOrderTransforOrder(appOrder,
											AppOrderDetailSQL.getAppOrderDetailByAppOrderId(appOrder.getId().intValue()),
											AppOrderModifierSQL.getAppOrderModifierByAppOrderId(appOrder.getId().intValue()),
											AppOrderDetailTaxSQL.getAppOrderDetailTaxByAppOrderId(appOrder.getId().intValue()));
								}
							}).start();
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									appOderAdapter.notifyDataSetChanged();
									appOderDetailAdapter.notifyDataSetChanged();
								}
							}, 3001);
						}
					});


*/
		}
			break;
//		case R.id.btn_delete:{
//			TempOrder tempOrder = (TempOrder) v.getTag();
//			if (tempOrder == null){
//				return;
//			}
//			tempOrder.setStatus(ParamConst.TEMPORDER_STATUS_UN_ACTIVTY);
//			TempOrderSQL.updateTempOrder(tempOrder);
//			refreshView();
//		}
//			break;
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_refresh:
			refreshView();
			break;
		default:
			break;
		}
	}
	
	private void checkOrder(TempOrder tempOrder){
		tempOrder.setStatus(ParamConst.TEMPORDER_STATUS_CHECKED);
		TempOrderSQL.updateTempOrder(tempOrder);
		Intent intent = this.getIntent();
		intent.putExtra("appOrderId", tempOrder.getAppOrderId());
		setResult(CHECK_REQUEST_CODE, intent);
		this.finish();
	}

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case REFRESH_APPORDER_SUCCESS:
					loadingDialog.dismiss();
					initData();
					appOderAdapter.notifyDataSetChanged();
					appOderDetailAdapter.notifyDataSetChanged();
					break;
				case REFRESH_APPORDER_FAILED:
					dismissLoadingDialog();
					initData();
					appOderAdapter.notifyDataSetChanged();
					appOderDetailAdapter.notifyDataSetChanged();
					UIHelp.showToast(context, ResultCode.getErrorResultStr(context,
							(Throwable) msg.obj, context.getResources().getString(R.string.server)));
					break;
				case RESULT_OK:
					initData();
					appOderAdapter.notifyDataSetChanged();
					appOderDetailAdapter.notifyDataSetChanged();
					dismissLoadingDialog();
					break;
			}
			super.handleMessage(msg);
		}
	};

	private void refreshView(){
		loadingDialog.setTitle("Loading");
		loadingDialog.show();
		SyncCentre.getInstance().getAllAppOrder(this, new HashMap<String, Object>(), handler);
	}


	@Override
	public void httpRequestAction(int action, Object obj) {
		if(action == RESULT_OK){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					loadingDialog.show();
				}
			});
			handler.sendEmptyMessage(RESULT_OK);
		}
		super.httpRequestAction(action, obj);

	}

	class AppOderAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return appOrders.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return appOrders.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			HolderView holder = null;
			if (arg1 == null) {
				arg1 = inflater.inflate(R.layout.temp_order_item, null);
				holder = new HolderView();
				holder.tv_order_id = (TextView) arg1.findViewById(R.id.tv_order_id);
				holder.tv_order_status = (TextView) arg1.findViewById(R.id.tv_order_status);
				holder.tv_order_type = (TextView) arg1.findViewById(R.id.tv_order_type);
				holder.tv_place_time = (TextView) arg1.findViewById(R.id.tv_place_time);
				textTypeFace.setTrajanProRegular(holder.tv_order_id);
				textTypeFace.setTrajanProRegular(holder.tv_order_type);
				textTypeFace.setTrajanProRegular(holder.tv_place_time);
				arg1.setTag(holder);
			} else {
				holder = (HolderView) arg1.getTag();
			}
			
			AppOrder appOrder = appOrders.get(arg0);
			if(arg0 == selectOrderItem){
				arg1.setBackgroundColor(NetWorkOrderActivity.this.getResources().getColor(R.color.white));
				btn_check.setTag(appOrder);
				btn_delete.setTag(appOrder);
//				if(appOrder.getTableType().intValue() == ParamConst.APP_ORDER_TABLE_STATUS_USED){
//					btn_check.setVisibility(View.VISIBLE);
//				}else{
//					btn_check.setVisibility(View.INVISIBLE);
//				}
			}else{
				arg1.setBackgroundColor(NetWorkOrderActivity.this.getResources().getColor(R.color.gray));
			}
			holder.tv_order_id.setText(appOrder.getId() + "");
			String statusStr = "";
			switch (appOrder.getOrderStatus().intValue()) {
			case ParamConst.APP_ORDER_STATUS_PAID:
				statusStr = getResources().getString(R.string.paid);
				break;
			case ParamConst.APP_ORDER_STATUS_KOTPRINTERD:
				statusStr = getResources().getString(R.string.making);
				break;
			case ParamConst.APP_ORDER_STATUS_KOTFINISH:
				statusStr = getResources().getString(R.string.completed);
				break;
			case ParamConst.APP_ORDER_STATUS_FINISH:
				statusStr = getResources().getString(R.string.finish);
				break;
			default:
				break;
			}
			holder.tv_order_status.setText(statusStr);
			holder.tv_order_type.setText("Online");
			holder.tv_place_time.setText(TimeUtil.getPrintDate(appOrder.getCreateTime()));
			return arg1;
		}
		class HolderView {
			public TextView tv_order_id;
			public TextView tv_order_status;
			public TextView tv_order_type;
			public TextView tv_place_time;
		}
	} 
	
	
	class AppOderDetailAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return appOrderDetails.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return appOrderDetails.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			HolderView holder = null;
			if (arg1 == null) {
				arg1 = inflater.inflate(R.layout.temp_orderdetail_item, null);
				holder = new HolderView();
				holder.tv_orderdetail_name = (TextView) arg1.findViewById(R.id.tv_orderdetail_name);
				holder.tv_orderdetail_qty = (TextView) arg1.findViewById(R.id.tv_orderdetail_qty);
				holder.tv_temp_modifier = (TextView) arg1.findViewById(R.id.tv_temp_modifier);
				textTypeFace.setTrajanProRegular(holder.tv_orderdetail_name);
				textTypeFace.setTrajanProRegular(holder.tv_orderdetail_qty);
				textTypeFace.setTrajanProRegular(holder.tv_temp_modifier);
				arg1.setTag(holder);
			} else {
				holder = (HolderView) arg1.getTag();
			}
			AppOrderDetail appOrderDetail = appOrderDetails.get(arg0);
			holder.tv_orderdetail_name.setText(appOrderDetail.getItemName());
			holder.tv_orderdetail_qty.setText(appOrderDetail.getItemNum() + "");
			List<AppOrderModifier> appOrderModifiers = AppOrderModifierSQL.getAppOrderModifierByOrderDetailId(appOrderDetail.getId().intValue());
			StringBuffer modifierNames = new StringBuffer();
			for(AppOrderModifier appOrderModifier : appOrderModifiers){
				if(modifierNames.length() != 0){
					modifierNames.append(",");
				}
				modifierNames.append(appOrderModifier.getModifierName());
			}
			holder.tv_temp_modifier.setText(modifierNames.toString());
			return arg1;
		}
		class HolderView {
			public TextView tv_orderdetail_name;
			public TextView tv_orderdetail_qty;
			public TextView tv_temp_modifier;
		}
	}
}
