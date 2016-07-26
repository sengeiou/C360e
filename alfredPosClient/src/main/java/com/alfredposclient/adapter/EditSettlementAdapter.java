package com.alfredposclient.adapter;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alfredbase.javabean.javabeanforhtml.EditSettlementInfo;
import com.alfredbase.utils.BH;
import com.puscene.posclient.R;
import com.puscene.posclient.global.App;


public class EditSettlementAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private Context context;
	private List<EditSettlementInfo> editSettlementInfos;
	private int selectorPosition = 0;
	public EditSettlementAdapter(Context context, List<EditSettlementInfo> editSettlementInfos) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		if (editSettlementInfos == null)
			editSettlementInfos = Collections.emptyList();
		this.editSettlementInfos = editSettlementInfos;

	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return editSettlementInfos.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return editSettlementInfos.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View currentView, ViewGroup arg2) {

		ViewHolder holder = null;
		if (currentView == null) {
			currentView = inflater.inflate(R.layout.payment_item, null);
			holder = new ViewHolder();
			holder.tv_bill_no = (TextView) currentView.findViewById(R.id.tv_bill_no);
			holder.tv_place_name = (TextView) currentView.findViewById(R.id.tv_place_name);
			holder.tv_total = (TextView) currentView.findViewById(R.id.tv_total);
			holder.tv_poeple = (TextView) currentView.findViewById(R.id.tv_poeple);
			holder.tv_time = (TextView) currentView.findViewById(R.id.tv_time);
			currentView.setTag(holder);
		} else {
			holder = (ViewHolder) currentView.getTag();
		}
		EditSettlementInfo editSettlementInfo = editSettlementInfos.get(position);
		String billNo = editSettlementInfo.getBillNo() + "";
		holder.tv_bill_no.setText(billNo);
		holder.tv_place_name.setText(editSettlementInfo.getPlaceName());
		holder.tv_total.setText(BH.getBD(editSettlementInfo.getTotalAmount()).toString());
		holder.tv_poeple.setText(editSettlementInfo.getUserName());
		holder.tv_time.setText(editSettlementInfo.getPaymentCreateTime());
		if(App.instance.isRevenueKiosk()){
			holder.tv_place_name.setVisibility(View.GONE);
		}
		return currentView;
	
	}
	
	class ViewHolder {
		public TextView tv_bill_no;
		public TextView tv_place_name;
		public TextView tv_total;
		public TextView tv_poeple;
		public TextView tv_time;
	}

}
