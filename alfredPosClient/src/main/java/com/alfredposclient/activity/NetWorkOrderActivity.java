package com.alfredposclient.activity;


import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.ParamConst;
import com.alfredbase.javabean.temporaryforapp.TempModifierDetail;
import com.alfredbase.javabean.temporaryforapp.TempOrder;
import com.alfredbase.javabean.temporaryforapp.TempOrderDetail;
import com.alfredbase.store.sql.temporaryforapp.TempModifierDetailSQL;
import com.alfredbase.store.sql.temporaryforapp.TempOrderDetailSQL;
import com.alfredbase.store.sql.temporaryforapp.TempOrderSQL;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.TextTypeFace;
import com.alfredbase.utils.TimeUtil;
import com.alfredposclient.R;

public class NetWorkOrderActivity extends BaseActivity {
	
	private List<TempOrder> tempOrders = new ArrayList<TempOrder>();
	private List<TempOrderDetail> tempOrderDetails = new ArrayList<TempOrderDetail>();
	private ListView lv_order_list;
	private ListView lv_orderdetail_list;
	private TextTypeFace textTypeFace = TextTypeFace.getInstance();
	private LayoutInflater inflater;
	private int selectOrderItem = 0;
	private Button btn_check;
	private Button btn_delete;
	public static final int CHECK_REQUEST_CODE = 110;
	private TempOderAdapter tempOderAdapter;
	private TempOderDetailAdapter tempOderDetailAdapter;
	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_network_order);
		inflater = LayoutInflater.from(this);
		lv_order_list = (ListView) findViewById(R.id.lv_order_list);
		lv_orderdetail_list = (ListView) findViewById(R.id.lv_orderdetail_list);
		initData();
		initTextTypeFace();
		tempOderAdapter = new TempOderAdapter();
		lv_order_list.setAdapter(tempOderAdapter);
		tempOderDetailAdapter = new TempOderDetailAdapter();
		lv_orderdetail_list.setAdapter(tempOderDetailAdapter);
		btn_check = (Button) findViewById(R.id.btn_check);
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
				tempOderAdapter.notifyDataSetChanged();
				tempOderDetailAdapter.notifyDataSetChanged();
			}
		});
		
	}
	
	private void initData(){
		tempOrders = TempOrderSQL.getAllTempOrder();
		if(tempOrders.size() > 0){
			tempOrderDetails = TempOrderDetailSQL.getTempOrderDetailByAppOrderId(tempOrders.get(selectOrderItem).getAppOrderId());
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
			final TempOrder tempOrder = (TempOrder) v.getTag();
			if (tempOrder == null){
				return;
			}
			if (tempOrder.getStatus() == ParamConst.TEMPORDER_STATUS_UN_ACTIVTY) {
				DialogFactory.showOneButtonCompelDialog(context, "警告", "当前订单已删除", null);
			} else if (tempOrder.getStatus() == ParamConst.TEMPORDER_STATUS_UN_CHECKED) {
				checkOrder(tempOrder);
			} else { 
				DialogFactory.commonTwoBtnDialog(context, "警告", "当前订单已经下过单，确实继续下单?", "取消", "确定", null, new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						checkOrder(tempOrder);
					}
				});
			}
		}
			break;
		case R.id.btn_delete:{
			TempOrder tempOrder = (TempOrder) v.getTag();
			if (tempOrder == null){
				return;
			}
			tempOrder.setStatus(ParamConst.TEMPORDER_STATUS_UN_ACTIVTY);
			TempOrderSQL.updateTempOrder(tempOrder);
			refreshView();
		}
			break;
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
	
	private void refreshView(){
		initData();
		tempOderAdapter.notifyDataSetChanged();
		tempOderDetailAdapter.notifyDataSetChanged();
	}
	
	class TempOderAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return tempOrders.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return tempOrders.get(arg0);
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
			
			TempOrder tempOrder = tempOrders.get(arg0);
			if(arg0 == selectOrderItem){
				arg1.setBackgroundColor(NetWorkOrderActivity.this.getResources().getColor(R.color.white));
				btn_check.setTag(tempOrder);
				btn_delete.setTag(tempOrder);
			}else{
				arg1.setBackgroundColor(NetWorkOrderActivity.this.getResources().getColor(R.color.gray));
			}
			holder.tv_order_id.setText(tempOrder.getAppOrderId() + "");
			String statusStr = "";
			switch (tempOrder.getStatus()) {
			case ParamConst.TEMPORDER_STATUS_UN_CHECKED:
				statusStr = "未下单";
				break;
			case ParamConst.TEMPORDER_STATUS_CHECKED:
				statusStr = "已下单";
				break;
			case ParamConst.TEMPORDER_STATUS_UN_ACTIVTY:
				statusStr = "无效的";
				break;
			default:
				break;
			}
			holder.tv_order_status.setText(statusStr);
			holder.tv_order_type.setText(tempOrder.getSourceType() + "");
			holder.tv_place_time.setText(TimeUtil.getPrintDate(tempOrder.getPlaceOrderTime()));
			return arg1;
		}
		class HolderView {
			public TextView tv_order_id;
			public TextView tv_order_status;
			public TextView tv_order_type;
			public TextView tv_place_time;
		}
	} 
	
	
	class TempOderDetailAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return tempOrderDetails.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return tempOrderDetails.get(arg0);
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
			TempOrderDetail tempOrderDetail = tempOrderDetails.get(arg0);
			holder.tv_orderdetail_name.setText(tempOrderDetail.getItemName());
			holder.tv_orderdetail_qty.setText(tempOrderDetail.getItemCount() + "");
			List<TempModifierDetail> tempModifierDetails = TempModifierDetailSQL.getTempOrderDetailByOrderDetailId(tempOrderDetail.getOrderDetailId());
			StringBuffer modifierNames = new StringBuffer();
			for(TempModifierDetail tempModifierDetail : tempModifierDetails){
				if(modifierNames.length() != 0){
					modifierNames.append(",");
				}
				modifierNames.append(tempModifierDetail.getModifierName());
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
