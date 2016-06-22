package com.alfredwaiter.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.Places;
import com.alfredbase.javabean.Tables;
import com.alfredwaiter.global.App;

public class TablesAdapter extends BaseAdapter {
	private Context context;
	private List<Tables> tableList;
	private Places places;
	public TablesAdapter(Context context, List<Tables> tableList,Places places) {
		this.context = context;
		this.tableList = tableList;
		this.places = places;
	}

	@Override
	public int getCount() {
		return tableList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return tableList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	public void setTableList(){
		this.tableList = CoreData
				.getInstance().getTableList(
						App.instance.getRevenueCenter().getId(),
						places.getId());
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		TextView textView = new TextView(context);
		AbsListView.LayoutParams params = new AbsListView.LayoutParams(100, 100);
		textView.setLayoutParams(params);
		if (tableList.get(arg0).getTableStatus() == ParamConst.TABLE_STATUS_DINING) {
			textView.setBackgroundColor(Color.parseColor("#000fff"));
		} else {
			textView.setBackgroundColor(Color.parseColor("#A4D3EE"));
		}
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(Color.parseColor("#000000"));
		textView.setText(tableList.get(arg0).getTableName());
		return textView;
	}

}
