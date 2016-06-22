package com.alfredposclient.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.alfredbase.ParamConst;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderSplit;
import com.alfredbase.utils.ColorUtils;
import com.alfredbase.utils.TextTypeFace;
import com.alfredposclient.R;
import com.alfredposclient.activity.MainPage;
import com.alfredposclient.global.App;

public class SelectOrderSplitDialog extends Dialog {
	private LayoutInflater inflater;
	private GridView gv_person_index;
	private Adapter adapter;
	private Handler handler;
	private Context context;
	private TextTypeFace textTypeFace;
	private List<OrderSplit> orderSplits = new ArrayList<OrderSplit>();

	public SelectOrderSplitDialog(Context context, Handler handler) {
		super(context, R.style.Dialog_transparent);
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.handler = handler;
		init();
	}

	private void init() {
		View contentView = View.inflate(getContext(),
				R.layout.select_groupid_dialog, null);
		setContentView(contentView);
		textTypeFace = TextTypeFace.getInstance();
		textTypeFace.setTrajanProRegular((TextView) contentView
				.findViewById(R.id.tv_clost_bill_title));
		gv_person_index = (GridView) contentView
				.findViewById(R.id.gv_person_index);
		adapter = new Adapter();
		gv_person_index.setAdapter(adapter);
		gv_person_index.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				OrderSplit orderSplit = orderSplits.get(arg2);
				if(orderSplit.getOrderStatus() != ParamConst.ORDER_STATUS_FINISHED){
					handler.sendMessage(handler.obtainMessage(MainPage.VIEW_EVENT_SHOW_CLOSE_SPLIT_BILL, arg1.getTag()));
					dismiss();
				}
				
			}
		});
//		setCancelable(false);
		setCanceledOnTouchOutside(false);
	}

	public void show(List<OrderSplit> orderSplits, Order order) {
		this.orderSplits = orderSplits;
		adapter.notifyDataSetChanged();
		super.show();
		App.instance.orderInPayment = order;
	}

	@Override
	public void dismiss() {
		super.dismiss();
		App.instance.orderInPayment = null;
	}

	private final class Adapter extends BaseAdapter {

		@Override
		public int getCount() {
			return orderSplits.size();
		}

		@Override
		public Object getItem(int arg0) {
			return orderSplits.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			arg1 = inflater.inflate(R.layout.item_person_index, null);
			OrderSplit orderSplit = orderSplits.get(arg0);
			RingTextView rtv_text = (RingTextView) arg1.findViewById(R.id.rtv_text);
			int groupId = orderSplit.getGroupId().intValue();
			arg1.setTag(orderSplit);
			if(orderSplit.getOrderStatus() == ParamConst.ORDER_STATUS_FINISHED){
				rtv_text.setDoneCircleColor(groupId);
			}else{
				rtv_text.setCircleColor(context.getResources().getColor(ColorUtils.ColorGroup.getColor(groupId)), groupId);
			}
			return arg1;
		}

	}
}
