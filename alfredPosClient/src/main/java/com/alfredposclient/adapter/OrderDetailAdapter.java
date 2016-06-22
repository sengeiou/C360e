package com.alfredposclient.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.TextTypeFace;
import com.alfredposclient.R;

public class OrderDetailAdapter extends BaseAdapter{
	private List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
	private LayoutInflater inflater;
	private Context context;
	private TextTypeFace textTypeFace;
	public OrderDetailAdapter(Context context, List<OrderDetail> orderDetailList) {
		this.context = context;
		this.orderDetailList = orderDetailList;
		inflater = LayoutInflater.from(context);
		textTypeFace = TextTypeFace.getInstance();
	}
	
	public void setList(List<OrderDetail> orderDetailList){
		this.orderDetailList = orderDetailList;
	}
	
	@Override
	public int getCount() {
		return orderDetailList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return orderDetailList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	
		
	@Override
	public View getView(int position, View currentView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (currentView == null) {
			currentView = inflater.inflate(R.layout.close_orderdetail_item, null);
			holder = new ViewHolder();
			holder.tv_item_name = (TextView) currentView.findViewById(R.id.tv_item_name);
			holder.tv_item_price = (TextView) currentView.findViewById(R.id.tv_item_price);
			holder.tv_item_qty = (TextView) currentView.findViewById(R.id.tv_item_qty);
			holder.tv_item_total = (TextView) currentView.findViewById(R.id.tv_item_total);
			
			textTypeFace.setTrajanProRegular(holder.tv_item_name);
			textTypeFace.setTrajanProRegular(holder.tv_item_price);
			textTypeFace.setTrajanProRegular(holder.tv_item_qty);
			textTypeFace.setTrajanProRegular(holder.tv_item_total);
			currentView.setTag(holder);
		} else {
			holder = (ViewHolder) currentView.getTag();
		}
		final OrderDetail orderDetail = this.orderDetailList
				.get(position);
		holder.tv_item_name.setText(orderDetail.getItemName());
		holder.tv_item_price.setText(BH.getBD(orderDetail.getItemPrice()).toString());
		holder.tv_item_qty.setText(String.valueOf(orderDetail.getItemNum().intValue()));
		holder.tv_item_total.setText(BH.getBD(orderDetail.getRealPrice()).toString());
		
		return currentView;
	}
	
	
	class ViewHolder{
		public TextView tv_item_name;
		public TextView tv_item_price;
		public TextView tv_item_qty;
		public TextView tv_item_total;
	} 
	
}
